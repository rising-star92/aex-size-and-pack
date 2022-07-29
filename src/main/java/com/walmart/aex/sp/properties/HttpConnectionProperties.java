package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;


@Configuration(configName = "httpConfig")
public interface HttpConnectionProperties {

    @Property(propertyName = "http.connectTimeout")
    int getConnectTimeout();

    @Property(propertyName = "http.connectionRequestTimeout")
    int getConnectionRequestTimeout();

    @Property(propertyName = "http.readTimeout")
    int getReadTimeout();
}
