--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--
-- SQL q88.sql
select  *
from
 (select count(*) h8_30_to_9
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 8
     and time_dim.t_minute >= 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s1,
 (select count(*) h9_to_9_30
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 9
     and time_dim.t_minute < 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s2,
 (select count(*) h9_30_to_10
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 9
     and time_dim.t_minute >= 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s3,
 (select count(*) h10_to_10_30
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 10
     and time_dim.t_minute < 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s4,
 (select count(*) h10_30_to_11
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 10
     and time_dim.t_minute >= 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s5,
 (select count(*) h11_to_11_30
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 11
     and time_dim.t_minute < 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s6,
 (select count(*) h11_30_to_12
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 11
     and time_dim.t_minute >= 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s7,
 (select count(*) h12_to_12_30
 from store_sales
 join household_demographics on store_sales.ss_hdemo_sk = household_demographics.hd_demo_sk
 join time_dim on store_sales.ss_sold_time_sk = time_dim.t_time_sk
 join store on store_sales.ss_store_sk = store.s_store_sk
 where time_dim.t_hour = 12
     and time_dim.t_minute < 30
     and ((household_demographics.hd_dep_count = 3 and household_demographics.hd_vehicle_count<=3+2) or
          (household_demographics.hd_dep_count = 0 and household_demographics.hd_vehicle_count<=0+2) or
          (household_demographics.hd_dep_count = 1 and household_demographics.hd_vehicle_count<=1+2))
     and store.s_store_name = 'ese') s8
