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
-- SQL q87.sql
select count(*)
from (select distinct c_last_name as l1, c_first_name as f1, d_date as d1
       from store_sales
        JOIN date_dim ON store_sales.ss_sold_date_sk = date_dim.d_date_sk
        JOIN customer ON store_sales.ss_customer_sk = customer.c_customer_sk
       where
         d_month_seq between 1193 and 1193+11
  ) t1
      LEFT OUTER JOIN
      ( select distinct c_last_name as l2, c_first_name as f2, d_date as d2
       from catalog_sales
        JOIN date_dim ON catalog_sales.cs_sold_date_sk = date_dim.d_date_sk
        JOIN customer ON catalog_sales.cs_bill_customer_sk = customer.c_customer_sk
       where
         d_month_seq between 1193 and 1193+11
  ) t2
      ON t1.l1 = t2.l2 and
       t1.f1 = t2.f2 and
       t1.d1 = t2.d2
      LEFT OUTER JOIN
      (select distinct c_last_name as l3, c_first_name as f3, d_date as d3
       from web_sales
        JOIN date_dim ON web_sales.ws_sold_date_sk = date_dim.d_date_sk
        JOIN customer ON web_sales.ws_bill_customer_sk = customer.c_customer_sk
       where
         d_month_seq between 1193 and 1193+11
  ) t3
      ON t1.l1 = t3.l3 and
       t1.f1 = t3.f3 and
       t1.d1 = t3.d3
WHERE
    l2 is null and
    l3 is null
