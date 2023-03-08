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

    @Property(propertyName = "buy.qty.finelines.size.query")
    String getBuyQtyFinelinesSizeQuery();

    @Property(propertyName = "buy.qty.style.cc.size.query")
    String getBuyQtyStyleCcSizeQuery();

    @Property(propertyName = "buy.qty.rfa.consumer.id")
    String getBuyQtyRfaConsumerId();

    @Property(propertyName = "buy.qty.rfa.consumer.name")
    String getBuyQtyRfaConsumerName();

    @Property(propertyName = "buy.qty.rfa.consumer.env")
    String getBuyQtyRfaConsumerEnv();

    @Property(propertyName = "buy.qty.line.plan.consumer.id")
    String getBuyQtyLinePlanConsumerId();

    @Property(propertyName = "buy.qty.line.plan.consumer.name")
    String getBuyQtyLinePlanConsumerName();

    @Property(propertyName = "buy.qty.line.plan.consumer.env")
    String getBuyQtyLinePlanConsumerEnv();

    @Property(propertyName = "buy.qty.rfa.weeks.url")
    String getRfaWeeksUrl();

    @Property(propertyName = "buy.qty.rfa.weeks.query")
    String getRfaWeeksQuery();

    @Property(propertyName = "buy.qty.line.plan.weeks.query")
    String getLinePlanWeeksQuery();

    @Property(propertyName = "buy.qty.line.plan.weeks.url")
    String getLinePlanWeeksUrl();

    @Property(propertyName = "strategy.vg.deviation.level")
    String getStrategyVolumeDeviationLevel();

    @Property(propertyName = "cbam.plan.definition.url")
    String getPlanDefinitionUrl();

    @Property(propertyName = "cbam.plan.definition.query")
    String getPlanDefinitionQuery();

    @Property(propertyName = "cbam.plan.definition.consumer.id")
    String getPlanDefinitionConsumerId();

    @Property(propertyName = "cbam.plan.definition.consumer.name")
    String getPlanDefinitionConsumerName();

    @Property(propertyName = "cbam.plan.definition.consumer.env")
    String getPlanDefinitionConsumerEnv();
}
