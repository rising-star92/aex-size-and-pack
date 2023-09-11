package com.walmart.aex.sp.properties;

import io.strati.ccm.utils.client.annotation.Configuration;
import io.strati.ccm.utils.client.annotation.Property;

@Configuration(configName = "bigQueryConfig")
public interface BigQueryConnectionProperties {

	@Property(propertyName = "rfa.projectId")
	String getRFAProjectId();

	@Property(propertyName = "rfa.datasetName")
	String getRFADataSetName();
	
	@Property(propertyName = "rfa.table.sp.packoptimization.output")
	String getRFASPPackOptTableName();
	
	@Property(propertyName = "rfa.table.sp.cc.output")
	String getRFACCStageTable();
	
	@Property(propertyName = "ml.projectId")
	String getMLProjectId();

	@Property(propertyName = "ml.datasetName")
	String getMLDataSetName();

	@Property(propertyName = "analytics_data")
	String getAnalyticsData();

	@Property(propertyName = "store.distribution.feature.flag")
	String getStoreDistributionFeatureFlag();
	
	@Property(propertyName = "pack.store.feature.flag")
	String getPackStoreFeatureFlag();
}
