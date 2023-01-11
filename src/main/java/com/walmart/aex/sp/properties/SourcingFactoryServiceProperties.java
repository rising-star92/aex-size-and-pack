package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;
@Configuration(configName = "sourcingFactoryServiceConfig")
public interface SourcingFactoryServiceProperties {

    @Property(propertyName = "sourcingFactory.url")
    String getUrl();

    @Property(propertyName = "sourcingFactory.consumerId")
    String getConsumerId();

    @Property(propertyName = "sourcingFactory.name")
    String getServiceName();

    @Property(propertyName = "sourcingFactory.apiTokenKey")
    String getApiTokenKey();

    @Property(propertyName = "sourcingFactory.env")
    String getEnv();

}
