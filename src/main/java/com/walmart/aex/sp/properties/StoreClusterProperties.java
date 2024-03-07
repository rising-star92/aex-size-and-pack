package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "storeClusterConfig")
public interface StoreClusterProperties {

    @Property(propertyName = "store.cluster.url")
    String getStoreClusterUrl();

}
