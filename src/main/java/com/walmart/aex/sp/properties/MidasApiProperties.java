package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "midasConfig")
public interface MidasApiProperties {

   @Property(propertyName = "midas.baseURL")
   String getMidasApiBaseURL();

   @Property(propertyName = "midas.header.consumer")
   String getMidasHeaderConsumer();

   @Property(propertyName = "midas.header.signatureKeyVersion")
   String getMidasHeaderSignatureKeyVersion();

   @Property(propertyName = "midas.header.signatureAuthFlag")
   String getMidasHeaderSignatureAuthFlag();

   @Property(propertyName = "midas.header.signatureTS")
   String getMidasHeaderSignatureTS();

   @Property(propertyName = "midas.header.tenant")
   String getMidasHeaderTenant();

   @Property(propertyName = "midas.historicalSizeMetricsQuery")
   String getMidasHistoricalMetricsQuery();

   @Property(propertyName = "midas.colorFamiliesQuery")
   String getMidasColorFamiliesQuery();
}
