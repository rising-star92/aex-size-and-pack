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

	@Property(propertyName = "analytics.data.catg.volume.cluster")
	String getCategoryVolumeCluster();

	@Property(propertyName = "analytics.data.subcatg.volume.cluster")
	String getSubCategoryVolumeCluster();

	@Property(propertyName = "analytics.data.fineline.volume.cluster")
	String getFinelineVolumeCluster();

	@Property(propertyName = "analytics.data.size.cluster")
	String getSizeCluster();

	@Property(propertyName = "analytics.data.size.color.cluster")
	String getSizeColorCluster();

	@Property(propertyName = "de.size.cluster.table.feature.flag")
	String getDESizeClusterFeatureFlag();

	@Property(propertyName = "analytics.data.size.cluster.store.fineline")
	String getSizeClusterStoreFl();

	@Property(propertyName = "po.distribution.override.datasetName")
	String getPODistributionOverrideDatasetName();

	@Property(propertyName = "po.distribution.override.tableName")
	String getPODistributionOverrideTableName();

	@Property(propertyName = "po.distribution.override.planIds")
	String getPODistributionOverridePlanIds();

}
