package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "buyQtyConfig")
public interface BuyQtyProperties {
    @Property(propertyName = "initialSet.constraint")
    Integer getInitialThreshold();

    @Property(propertyName = "replenishment.constraint")
    Integer getReplenishmentThreshold();

    @Property(propertyName = "s3Plans2024.constraint")
    String getPlanIds();
}
