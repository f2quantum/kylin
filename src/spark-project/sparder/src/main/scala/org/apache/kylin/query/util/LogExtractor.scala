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

package org.apache.kylin.query.util

import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}
import org.apache.kylin.common.util.{AddressUtil, ClusterConstant, FileSystemUtil, HadoopUtil}
import org.apache.kylin.common.{KapConfig, KylinConfig, KylinConfigBase}
import org.slf4j.LoggerFactory

object ExtractFactory {
  def create: ILogExtractor = {
    if (KapConfig.wrap(KylinConfig.getInstanceFromEnv).isCloud) {
      CloudLogExtractor
    } else if (KylinConfig.getInstanceFromEnv.getMicroServiceMode != null) {
      K8sLogExtractor
    } else {
      HadoopLogExtractor
    }
  }
}

trait ILogExtractor {
  val ROLL_LOG_DIR_NAME_PREFIX = "eventlog_v2_"

  def getValidSparderApps(startTime: Long, endTime: Long, host: String): scala.List[FileStatus] = {
    val logDir = getSparderEvenLogDir
    val fs = HadoopUtil.getFileSystem(logDir)
    HadoopUtil.getFileSystem(logDir).listStatus(new Path(logDir)).toList
      .filter(fileStatus => filterApps(fileStatus, startTime, endTime, fs))
  }

  def getSparderEvenLogDir(): String = {
    KapConfig.wrap(KylinConfig.getInstanceFromEnv).getSparkConf.get("spark.eventLog.dir") + "/" + AddressUtil.getLocalServerInfo
  }

  def filterApps(fileStatus: FileStatus, startTime: Long, endTime: Long, fs: FileSystem): Boolean
}

object CloudLogExtractor extends ILogExtractor {
  private val log = LoggerFactory.getLogger(CloudLogExtractor.getClass)

  override def filterApps(fileStatus: FileStatus, startTime: Long, endTime: Long, fs: FileSystem): Boolean = {
    var valid = false
    try {
      val fileInfo = fileStatus.getPath.getName.split("#")
      val fileStatuses: Array[FileStatus] = FileSystemUtil.listStatus(fs, new Path(fileStatus.getPath.toUri));
      if (!fileStatuses.isEmpty) {
        val maxModifyTime = fileStatuses.map(f => f.getModificationTime).max
        valid = fileInfo.length == 2 && fileInfo(1).toLong <= endTime && maxModifyTime >= startTime
      }
    } catch {
      case e: Exception =>
        log.error("Check sparder appId time range failed.", e)
    }
    valid
  }
}

object HadoopLogExtractor extends ILogExtractor {

  private val log = LoggerFactory.getLogger(HadoopLogExtractor.getClass)

  override def filterApps(fileStatus: FileStatus, startTime: Long, endTime: Long, fs: FileSystem): Boolean = {
    var valid = false
    try {
      val fileInfo = fileStatus.getPath.getName.split("#")
      valid = fileInfo.length == 2 && fileInfo(1).toLong <= endTime && fileStatus.getModificationTime >= startTime
    } catch {
      case e: Exception =>
        log.error("Check sparder appId time range failed.", e)
    }
    valid
  }
}

object K8sLogExtractor extends ILogExtractor {
  private val log = LoggerFactory.getLogger(K8sLogExtractor.getClass)

  override def filterApps(fileStatus: FileStatus, startTime: Long, endTime: Long, fs: FileSystem): Boolean = {
    var valid = false
    try {
      val fileInfo = fileStatus.getPath.getName.split("#")
      val fileStatuses: Array[FileStatus] = FileSystemUtil.listStatus(fs, new Path(fileStatus.getPath.toUri))
      if (!fileStatuses.isEmpty) {
        val maxModifyTime = fileStatuses.map(f => f.getModificationTime).max
        valid = fileInfo.length == 2 && fileInfo(1).toLong <= endTime && maxModifyTime >= startTime
      }
    } catch {
      case e: Exception =>
        log.error("Check sparder appId time range failed.", e)
    }
    valid
  }

  def getServiceSparderEventLogDir(service: String): String = {
    KapConfig.wrap(KylinConfig.getInstanceFromEnv).getSparkConf.get("spark.eventLog.dir") + "/" + service
  }

  override def getSparderEvenLogDir(): String = {
    val service = KylinConfig.getInstanceFromEnv.getApplicationConfig(KylinConfigBase.SERVER_NAME_STRING)
    getServiceSparderEventLogDir(service) + "/" + AddressUtil.getLocalServerInfo
  }

  override def getValidSparderApps(startTime: Long, endTime: Long, host: String): scala.List[FileStatus] = {
    val logDir = getServiceSparderEventLogDir(ClusterConstant.QUERY)
    val fs = HadoopUtil.getFileSystem(logDir)
    fs.listStatus(new Path(logDir))
      .flatMap(status => {
        if (host != null && !host.equals(status.getPath.getName)) {
          Array[FileStatus]()
        } else {
          fs.listStatus(status.getPath)
        }
      }).toList
      .filter(fileStatus => filterApps(fileStatus, startTime, endTime, fs))
  }
}