package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitSetBumpPackDTO;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitSetBumpPackData;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryInitSetBpPkQtyService {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public InitSetBumpPackData fetchInitialSetBumpPackDataFromGCP(BuyQtyRequest request) {
		InitSetBumpPackData initSetBpPkData = new InitSetBumpPackData();
		List<InitSetBumpPackDTO> initSetBpPkDTOList = new ArrayList<>();

		try {
			String projectId = bigQueryConnectionProperties.getRFAProjectId();
			String datasetName = bigQueryConnectionProperties.getRFADataSetName();
			String tableName = bigQueryConnectionProperties.getRFASPPackOptTableName();
			String projectIdDatasetNameTableName = projectId + "." + datasetName + "." + tableName;
			Long planId = request.getPlanId();
			Integer finelineNbr = request.getFinelineNbr();
			String planAndFineline = String.valueOf(planId) + "_" + String.valueOf(finelineNbr);
			String initSetBpPkQtyRFAQuery = getInitSetBpPkGCPQuery(projectIdDatasetNameTableName, planAndFineline);
			BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(initSetBpPkQtyRFAQuery).build();
			TableResult results = bigQuery.query(queryConfig);
			results.iterateAll().forEach(rows -> rows.forEach(row -> {
				try {
					InitSetBumpPackDTO initSetBpPkDto = objectMapper.readValue(row.getValue().toString(),
							InitSetBumpPackDTO.class);
					initSetBpPkDTOList.add(initSetBpPkDto);
					initSetBpPkData.setInitSetBpPkQtyDTOList(initSetBpPkDTOList);
				} catch (JsonProcessingException e) {
					log.error("Error while mapping the gcp table response data \n", e);
				}
			}));
			log.info("Query performed successfully.");
		} catch (Exception e) {
			log.error("Exception details are ", e);
		}
		log.info("results: {}", initSetBpPkData);
		return initSetBpPkData;
	}

	private String getInitSetBpPkGCPQuery(String projectIdDatasetNameTableName, String planAndFineline) {
		return "WITH MyTable AS ( SELECT ProductFineline as planAndFineline, reverse( SUBSTR(REVERSE(ProductCustomerChoice), STRPOS(REVERSE(ProductCustomerChoice), \"_\")+1)) as styleNbr,ProductCustomerChoice as customerChoice, "
				+ "MerchMethod as merchMethodDesc, size, sum(SPPackInitialSetOutput)+sum(SPPackBumpOutput) as finalInitialSetQty, sum(SPPackBumpOutput) as bumpPackQty  FROM `"
				+ projectIdDatasetNameTableName + "`"
				+ " group by planAndFineline, styleNbr, customerChoice, merchMethodDesc, size "
				+ "order by planAndFineline, styleNbr, customerChoice, merchMethodDesc, size) "
				+ "SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable where gcpTable.planAndFineline = '"
				+ planAndFineline + "'";
	}
}
