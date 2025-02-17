/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kylin.metadata.cube.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.kylin.guava30.shaded.common.collect.Lists;
import org.apache.kylin.guava30.shaded.common.collect.Maps;
import org.apache.kylin.guava30.shaded.common.collect.Sets;
import org.apache.kylin.metadata.cube.model.IndexEntity;
import org.apache.kylin.metadata.cube.model.IndexPlan;
import org.apache.kylin.metadata.cube.model.LayoutEntity;
import org.apache.kylin.metadata.model.NDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexPlanReduceUtil {
    private static final Logger log = LoggerFactory.getLogger(IndexPlanReduceUtil.class);

    private IndexPlanReduceUtil() {
    }

    public static Map<LayoutEntity, LayoutEntity> collectIncludedLayouts(List<LayoutEntity> inputLayouts,
            boolean isGarbageCleaning) {
        Map<LayoutEntity, LayoutEntity> redundantMap = Maps.newHashMap();
        Set<LayoutEntity> tableIndexGroup = Sets.newHashSet();
        Map<List<Integer>, Set<LayoutEntity>> aggIndexDimGroup = Maps.newHashMap();
        inputLayouts.forEach(layout -> {
            // layout.getOrderedDimensions() maybe more better, but difficult to cover with simple UT
            if (IndexEntity.isTableIndex(layout.getId())) {
                tableIndexGroup.add(layout);
            } else {
                List<Integer> aggIndexDims = layout.getColOrder().stream()
                        .filter(idx -> idx < NDataModel.MEASURE_ID_BASE).collect(Collectors.toList());
                aggIndexDimGroup.putIfAbsent(aggIndexDims, Sets.newHashSet());
                aggIndexDimGroup.get(aggIndexDims).add(layout);
            }
        });

        List<LayoutEntity> tableIndexsShareSameDims = descSortByColOrderSize(Lists.newArrayList(tableIndexGroup));
        redundantMap.putAll(findIncludedLayoutMap(tableIndexsShareSameDims, isGarbageCleaning));

        aggIndexDimGroup.forEach((dims, layouts) -> {
            List<LayoutEntity> aggIndexsShareSameDims = descSortByColOrderSize(Lists.newArrayList(layouts));
            redundantMap.putAll(findIncludedLayoutMap(aggIndexsShareSameDims, isGarbageCleaning));
        });

        return redundantMap;
    }

    public static List<Set<LayoutEntity>> collectSameDimAggLayouts(List<LayoutEntity> inputLayouts) {
        List<Set<LayoutEntity>> sameDimAggLayouts = Lists.newArrayList();
        Map<List<Integer>, Set<LayoutEntity>> aggLayoutDimGroup = Maps.newHashMap();
        inputLayouts.stream().filter(layout -> !layout.isBase() && !IndexEntity.isTableIndex(layout.getId()))
                .forEach(layout -> {
                    List<Integer> aggLayoutDims = new ArrayList<>();
                    for (Integer idx : layout.getColOrder()) {
                        if (idx < NDataModel.MEASURE_ID_BASE) {
                            aggLayoutDims.add(idx);
                        }
                    }
                    aggLayoutDimGroup.putIfAbsent(aggLayoutDims, Sets.newHashSet());
                    aggLayoutDimGroup.get(aggLayoutDims).add(layout);
                });

        sameDimAggLayouts.addAll(
                aggLayoutDimGroup.values().stream().filter(layouts -> layouts.size() > 1).collect(Collectors.toSet()));
        return sameDimAggLayouts;
    }

    public static IndexPlan mergeSameDimLayout(IndexPlan indexPlan, List<Set<LayoutEntity>> sameDimLayouts) {
        IndexPlan.IndexPlanUpdateHandler updateHandler = indexPlan.createUpdateHandler();
        for (Set<LayoutEntity> layoutEntities : sameDimLayouts) {
            Set<Integer> colOrder = Sets.newLinkedHashSet();
            List<Integer> allColOrders = Lists.newArrayList();
            List<Integer> shardByCol = Lists.newArrayList();
            for (LayoutEntity layoutEntity : layoutEntities) {
                colOrder.addAll(layoutEntity.getColOrder());
                allColOrders.addAll(layoutEntity.getColOrder());
                shardByCol = layoutEntity.getShardByColumns();
            }

            LayoutEntity mergedLayout = indexPlan.createLayout(Lists.newArrayList(colOrder), true, false, shardByCol);
            log.info("merge colOrders: {} into {}", allColOrders, mergedLayout.getColOrder());
            updateHandler.add(mergedLayout, true, false);
        }

        return updateHandler.complete();
    }

    /**
     * Collect a redundant map from included layout to reserved layout.
     * @param sortedLayouts sorted by layout's colOrder
     * @param isGarbageCleaning if true for gc, otherwise for auto-modeling tailor layout
     */
    private static Map<LayoutEntity, LayoutEntity> findIncludedLayoutMap(List<LayoutEntity> sortedLayouts,
            boolean isGarbageCleaning) {
        Map<LayoutEntity, LayoutEntity> includedMap = Maps.newHashMap();
        if (sortedLayouts.size() <= 1) {
            return includedMap;
        }

        for (int i = 0; i < sortedLayouts.size(); i++) {
            LayoutEntity target = sortedLayouts.get(i);
            if (includedMap.containsKey(target)) {
                continue;
            }
            for (int j = i + 1; j < sortedLayouts.size(); j++) {
                LayoutEntity current = sortedLayouts.get(j);
                // In the process of garbage cleaning all existing layouts were taken into account,
                // but in the process of propose only layouts with status of inProposing were taken into account.
                if ((!isGarbageCleaning && !current.isInProposing())
                        || (target.getColOrder().size() == current.getColOrder().size())
                        || includedMap.containsKey(current)
                        || !Objects.equals(current.getShardByColumns(), target.getShardByColumns())) {
                    continue;
                }

                if (isContained(current, target)) {
                    includedMap.put(current, target);
                }
            }
        }
        return includedMap;
    }

    /**
     * When two layouts comes from the same group, judge whether the current is contained by the target.
     * For AggIndex, only need to judge measures included; for TableIndex, compare colOrder.
     */
    private static boolean isContained(LayoutEntity current, LayoutEntity target) {
        boolean isTableIndex = IndexEntity.isTableIndex(target.getId());
        if (isTableIndex) {
            return isSubPartColOrder(current.getColOrder(), target.getColOrder());
        }
        Set<Integer> currentMeasures = Sets.newHashSet(current.getIndex().getMeasures());
        Set<Integer> targetMeasures = Sets.newHashSet(target.getIndex().getMeasures());
        return targetMeasures.containsAll(currentMeasures);
    }

    /**
     * Check whether current sequence is a part of target sequence.
     */
    public static boolean isSubPartColOrder(List<Integer> curSeq, List<Integer> targetSeq) {
        int i = 0;
        int j = 0;
        while (i < curSeq.size() && j < targetSeq.size()) {
            if (curSeq.get(i).intValue() == targetSeq.get(j).intValue()) {
                i++;
            }
            j++;
        }
        return i == curSeq.size() && j <= targetSeq.size();
    }

    // sort layout first to get a stable result for problem diagnosis
    public static List<LayoutEntity> descSortByColOrderSize(List<LayoutEntity> allLayouts) {
        allLayouts.sort((o1, o2) -> {
            if (o2.getColOrder().size() - o1.getColOrder().size() == 0) {
                return (int) (o1.getId() - o2.getId());
            }
            return o2.getColOrder().size() - o1.getColOrder().size();
        }); // desc by colOrder size
        return allLayouts;
    }
}
