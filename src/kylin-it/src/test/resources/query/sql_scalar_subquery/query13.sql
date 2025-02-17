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


select DATES.D_DATEKEY,
       count(DATES.D_DAYOFWEEK) min_dayofweek,
       DT.DATE_TIME,
       DATES.D_DATE
from SSB.DATES
         inner join
     (select '1995-03-29' as DATE_TIME
      union
      select '1995-04-01' as DATE_TIME
      union all
      select '1995-03-29' as DATE_TIME
      union all
      select '1995-04-01' as DATE_TIME
      union all
      select '1995-05-17' as DATE_TIME) DT
     on DT.DATE_TIME = DATES.D_DATEKEY
group by DATES.D_DATE, date_time, DATES.D_DATEKEY
