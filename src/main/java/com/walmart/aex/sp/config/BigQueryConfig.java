package com.walmart.aex.sp.config;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BigQueryConfig {

   @Bean
   BigQuery bigQuery() {
      return BigQueryOptions.getDefaultInstance().getService();
   }
}
