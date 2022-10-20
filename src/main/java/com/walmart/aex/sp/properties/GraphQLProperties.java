package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "graphQlConfig")
public interface GraphQLProperties {
    @Property(propertyName = "size.profile.url")
    String getSizeProfileUrl();

    @Property(propertyName = "size.profile.query")
    String getSizeProfileQuery();

    @Property(propertyName = "size.profile.consumer.id")
    String getSizeProfileConsumerId();

    @Property(propertyName = "size.profile.consumer.name")
    String getSizeProfileConsumerName();

    @Property(propertyName = "size.profile.consumer.env")
    String getSizeProfileConsumerEnv();

    @Property(propertyName = "size.profile.assortproduct.url")
    String getAssortProductUrl();

    @Property(propertyName = "size.profile.assortproduct.query")
    String getAssortProductRFAQuery();

    @Property(propertyName = "size.profile.assortproduct.consumer.id")
    String getAssortProductConsumerId();

    @Property(propertyName = "size.profile.assortproduct.consumer.name")
    String getAssortProductConsumerName();

    @Property(propertyName = "size.profile.assortproduct.consumer.env")
    String getAssortProductConsumerEnv();

    @Property(propertyName = "size.profile.all.cc.query")
    String getAllCcSizeProfileQuery();

    @Property(propertyName = "buyQty.finelines.size.query")
    String getBuyQtyFinelinesSizeQuery();

    @Property(propertyName = "buyQty.style.cc.size.query")
    String getBuyQtyStyleCcSizeQuery();

    @Property(propertyName = "buy.quantification.weeks.url")
    String getRfaWeeksUrl();

    @Property(propertyName = "buy.quantification.weeks.query")
    String getRfaWeeksQuery();

}
