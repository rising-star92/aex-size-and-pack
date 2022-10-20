package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "bqfpServiceConfig")
public interface BQFPServiceProperties {

   @Property(propertyName = "bqfp.url")
   String getUrl();

   @Property(propertyName = "bqfp.consumerId")
   String getConsumerId();

   @Property(propertyName = "bqfp.env")
   String getEnv();

   @Property(propertyName = "bqfp.svcName")
   String getSvcName();

   @Property(propertyName = "flow.plan.url")
   String getFlowPlanUrl();

   @Property(propertyName = "flow.plan.finelineMetricsWeek.query")
   String getFlowPlanFinelineWeeks();

}
