package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "integrationHubServiceConfig")
public interface IntegrationHubServiceProperties {
    @Property(propertyName = "integrationhub.url")
    String getUrl();

    @Property(propertyName = "integrationhub.sizeAndPackBaseUrl")
    String getSizeAndPackUrl();

    @Property(propertyName = "integrationhub.env")
    String getEnv();


}
