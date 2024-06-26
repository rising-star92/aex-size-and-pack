package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;
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

	private final ObjectMapper objectMapper;
	private final BigQuery bigQuery;

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public BigQueryInitSetBpPkQtyService(ObjectMapper objectMapper, BigQuery bigQuery) {
		this.objectMapper = objectMapper;
		this.bigQuery = bigQuery;
	}

	public InitSetBumpPackData fetchInitialSetBumpPackDataFromGCP(BuyQtyRequest request) {
		InitSetBumpPackData initSetBpPkData = new InitSetBumpPackData();
		List<InitSetBumpPackDTO> initSetBpPkDTOList = new ArrayList<>();

		try {
			String projectIdDatasetNameTableNameSp = getProjectIdSp();
			String projectIdDatasetNameTableNameRFA = getProjectIdCc();

			Long planId = request.getPlanId();
			Integer finelineNbr = request.getFinelineNbr();
			String initSetBpPkQtyRFAQuery = getInitSetBpPkGCPQuery(projectIdDatasetNameTableNameRFA,projectIdDatasetNameTableNameSp, planId,finelineNbr);
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
			Thread.currentThread().interrupt();
		}
		log.info("results: {}", initSetBpPkData);
		return initSetBpPkData;
	}

	private String getInitSetBpPkGCPQuery(String rfaTableName,String projectIdDatasetNameTableName,Long planId, Integer finelineNbr) {
		String planAndFineline = planId + "_" + finelineNbr + PERCENT;
		// TODO: Find a way to not use RFA for getting style_nbr. This is temporary
		return "WITH MyTable AS (select PackOpt.*, RFA.style_nbr as styleNbr from (select distinct trim(style_nbr) as style_nbr, trim(cc) as cc FROM " +
				"`"+ rfaTableName +"` " +
				"where season = "+ planId +" and fineline="+ finelineNbr +") as RFA " +
				"join " +
				"(SELECT ProductFineline as planAndFineline,trim(ProductCustomerChoice) as customerChoice, MerchMethod as merchMethodDesc, size, sum(SPPackInitialSetOutput) as finalInitialSetQty, sum(SPPackBumpOutput) as bumpPackQty  FROM" +
				"`"+projectIdDatasetNameTableName+"` sp where sp.ProductFineline LIKE '"+ planAndFineline +"' " +
				"group by planAndFineline, customerChoice, merchMethodDesc, size ) as PackOpt on PackOpt.customerChoice=RFA.cc order by planAndFineline, style_nbr, customerChoice, merchMethodDesc, size )" +
				"SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable;";


	}
	private String getProjectIdSp() {
		String projectId = bigQueryConnectionProperties.getMLProjectId();
		String datasetName = bigQueryConnectionProperties.getMLDataSetName();
		String tableNameSp = bigQueryConnectionProperties.getRFASPPackOptTableName();
		return projectId + "." + datasetName + "." + tableNameSp;
	}

	private String getProjectIdCc() {
		String rfaProjectId = bigQueryConnectionProperties.getRFAProjectId();
		String rfaDatasetName = bigQueryConnectionProperties.getRFADataSetName();
		String tableNameCc = bigQueryConnectionProperties.getRFACCStageTable();
		return rfaProjectId + "." + rfaDatasetName + "." + tableNameCc;
	}
}
