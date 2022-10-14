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
}
