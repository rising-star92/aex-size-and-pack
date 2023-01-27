package com.walmart.aex.sp.properties;


import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "plmApiConfig")
public interface PLMServiceProperties {
    @Property(propertyName = "plm.baseUrl")
    String getPlmApiBaseURL();

    @Property(propertyName = "plm.consumerId")
    String getPlmConsumerId();

    @Property(propertyName = "plm.svcName")
    String getPlmServiceName();

    @Property(propertyName = "plm.env")
    String getPlmEnv();
}
