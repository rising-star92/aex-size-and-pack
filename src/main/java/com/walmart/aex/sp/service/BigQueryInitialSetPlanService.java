package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetPackRequest;
import com.walmart.aex.sp.dto.commitmentreport.RFASizePackDataForCom;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryInitialSetPlanService {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public List<RFASizePackDataForCom> getInitialAndBumpSetDetails(InitialSetPackRequest rfaSizePackRequest) {
		List<RFASizePackDataForCom> rfaSizePackDataForComs = new ArrayList<>();
	     try {
	         String projectId = bigQueryConnectionProperties.getRFAProjectId();
	         String datasetName = bigQueryConnectionProperties.getRFADataSetNameStage();
	         String tableNameSp = bigQueryConnectionProperties.getRFASPPackOptTableName();
	         String tableNameCc = bigQueryConnectionProperties.getRFACCStageTable();
	         String projectIdDatasetNameTableNameSp = projectId + "." + datasetName + "." + tableNameSp;
	         String projectIdDatasetNameTableNameCc = projectId + "." + datasetName + "." + tableNameCc;
	         String spQueryString=getSizePackOptQueryString(projectIdDatasetNameTableNameCc, projectIdDatasetNameTableNameSp, rfaSizePackRequest.getPlanId(),rfaSizePackRequest.getFinelineNbr());
	         BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
	         QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(spQueryString).build();
	         TableResult results = bigQuery.query(queryConfig);
	         results.iterateAll().forEach(rows -> rows.forEach(row ->{
	        	 	try {
						RFASizePackDataForCom rfaSizePackDataForCom = objectMapper.readValue(row.getValue().toString(), RFASizePackDataForCom.class);
						rfaSizePackDataForComs.add(rfaSizePackDataForCom);
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	             
	         }));
	                
	         log.info("Query performed successfully.");
	     }catch (Exception e) {
	         log.error("Query not performed \n" + e);
	     }
	     log.info("results: {}",rfaSizePackDataForComs);
	   
	     return rfaSizePackDataForComs;
	 }
 
	  private String getSizePackOptQueryString(String ccTableName, String spTableName,Integer planId,Integer finelineNbr){
	  	 String prodFineline = planId+"_"+finelineNbr;
		 return  "WITH MyTable AS ( select distinct reverse( SUBSTR(REVERSE(RFA.cc), STRPOS(REVERSE(RFA.cc), \"_\")+1)) as style_id,RFA.in_store_week, RFA.cc, SP.merch_method, SP.pack_id,SP.size, (SP.initialpack_ratio) AS initialpack_ratio, SUM(SP.is_quantity) AS is_quantity from (select trim(cc) as cc,CAST(store AS INTEGER) as store,min(week) as in_store_week FROM `"+ccTableName+"`as RFA where plan_id_partition="+planId+" and fineline="+finelineNbr+" and final_alloc_space>0 group by cc,store order by cc, in_store_week, store )as RFA join (SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as pack_id, SP.MerchMethod as merch_method, SP.size, SP.SPBumpSetPackSizeRatio as bumppack_ratio, SP.SPInitialSetPackSizeRatio as initialpack_ratio, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity FROM `"+spTableName+"` AS SP where ProductFineline = '"+prodFineline+"') as SP on RFA.store = SP.store and RFA.cc = SP.cc GROUP BY RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id ,initialpack_ratio order by RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id,initialpack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
	  }
}