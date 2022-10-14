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
import com.walmart.aex.sp.dto.initialsetqty.RFAInitialSetDTO;
import com.walmart.aex.sp.dto.initialsetqty.RFAInitialSetData;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryInitialSetQtyService {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public RFAInitialSetData fetchInitialSetDataFromGCP(BuyQtyRequest request) {
		RFAInitialSetData rfaInitialSetData = new RFAInitialSetData();
		List<RFAInitialSetDTO> rfaInitialSetDTOList = new ArrayList<>();

		try {
			String projectId = bigQueryConnectionProperties.getRFAProjectId();
			String datasetName = bigQueryConnectionProperties.getRFADataSetName();
			String tableName = bigQueryConnectionProperties.getRFASPPackOptTableName();
			String projectIdDatasetNameTableName = projectId + "." + datasetName + "." + tableName;
			Long planId = request.getPlanId();
			Integer finelineNbr = request.getFinelineNbr();
			String planAndFineline = String.valueOf(planId) + "_" + String.valueOf(finelineNbr);
			String initSetQtyRFAQuery = getInitialSetGCPQuery(projectIdDatasetNameTableName, planAndFineline);
			BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(initSetQtyRFAQuery).build();
			TableResult results = bigQuery.query(queryConfig);
			results.iterateAll().forEach(rows -> rows.forEach(row -> {
				try {
					RFAInitialSetDTO rfaInitSetData = objectMapper.readValue(row.getValue().toString(),
							RFAInitialSetDTO.class);
					rfaInitialSetDTOList.add(rfaInitSetData);
					rfaInitialSetData.setRfaInitialSetQtyData(rfaInitialSetDTOList);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					log.error("Error while mapping rfa data \n" + e.toString());
				}
			}));
			log.info("Query performed successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception details are ", e);
		}
		log.info("results: {}", rfaInitialSetData);
		return rfaInitialSetData;
	}

	private String getInitialSetGCPQuery(String projectIdDatasetNameTableName, String planAndFineline) {
		return "WITH MyTable AS ( SELECT ProductFineline as planAndFineline, reverse( SUBSTR(REVERSE(ProductCustomerChoice), STRPOS(REVERSE(ProductCustomerChoice), \"_\")+1)) as styleNbr,ProductCustomerChoice as customerChoice, "
				+ "MerchMethod as merchMethodDesc, size, sum(SPPackInitialSetOutput)+sum(SPPackBumpOutput) as finalInitialSetQty  FROM `"
				+ projectIdDatasetNameTableName + "`"
				+ " group by planAndFineline, styleNbr, customerChoice, merchMethodDesc, size "
				+ "order by planAndFineline, styleNbr, customerChoice, merchMethodDesc, size) "
				+ "SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable where rfaTable.planAndFineline = '"
				+ planAndFineline + "'";
	}
}
