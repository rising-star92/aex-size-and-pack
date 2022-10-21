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
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponse;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryInitialSetPlanService {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public List<RFAInitialSetBumpSetResponse> getInitialAndBumpSetDetails(InitialSetPackRequest rfaSizePackRequest) {
		List<RFAInitialSetBumpSetResponse> rfaInitialSetBumpSetResponses = new ArrayList<>();
		List<RFAInitialSetBumpSetResponse> rfaInitialSetBumpSetResponseBs = new ArrayList<>();
	     try {
	         String projectId = bigQueryConnectionProperties.getRFAProjectId();
	         String datasetName = bigQueryConnectionProperties.getRFADataSetName();
	         String tableNameSp = bigQueryConnectionProperties.getRFASPPackOptTableName();
	         String tableNameCc = bigQueryConnectionProperties.getRFACCStageTable();
	         String projectIdDatasetNameTableNameSp = projectId + "." + datasetName + "." + tableNameSp;
	         String projectIdDatasetNameTableNameCc = projectId + "." + datasetName + "." + tableNameCc;
	         String spQueryStringIs=getSizePackIntialSetQueryString(projectIdDatasetNameTableNameCc, projectIdDatasetNameTableNameSp, rfaSizePackRequest.getPlanId(),rfaSizePackRequest.getFinelineNbr());
	         String spQueryStringBs=getSizePackBumpSetQueryString(projectIdDatasetNameTableNameSp, rfaSizePackRequest.getPlanId(),rfaSizePackRequest.getFinelineNbr());
	         BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
	         QueryJobConfiguration queryConfigIs = QueryJobConfiguration.newBuilder(spQueryStringIs).build();
	         TableResult resultsIs = bigQuery.query(queryConfigIs);
	         resultsIs.iterateAll().forEach(rows -> rows.forEach(row ->{
	        	 	try {
						RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponseIss = objectMapper.readValue(row.getValue().toString(), RFAInitialSetBumpSetResponse.class);
						rfaInitialSetBumpSetResponses.add(rfaInitialSetBumpSetResponseIss);
					} catch (JsonMappingException e) {
						log.error("JsonMappingException \n" + e.getMessage());
					} catch (JsonProcessingException e) {
						log.error("JsonProcessingException \n" + e.getMessage());
					}	             
	         }));
	         QueryJobConfiguration queryConfigBs = QueryJobConfiguration.newBuilder(spQueryStringBs).build();
	         TableResult resultsBs = bigQuery.query(queryConfigBs);
	         resultsBs.iterateAll().forEach(rows -> rows.forEach(row ->{
	        	 	try {
						RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponseBss = objectMapper.readValue(row.getValue().toString(), RFAInitialSetBumpSetResponse.class);
						rfaInitialSetBumpSetResponseBs.add(rfaInitialSetBumpSetResponseBss);
					} catch (JsonMappingException e) {
						log.error("JsonMappingException \n" + e.getMessage());
					} catch (JsonProcessingException e) {
						log.error("JsonProcessingException \n" + e.getMessage());
					}	             
	         }));
	         for (RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse : rfaInitialSetBumpSetResponses) {
				for (RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse2 : rfaInitialSetBumpSetResponseBs) {
					if(rfaInitialSetBumpSetResponse2.getPack_id().equalsIgnoreCase(rfaInitialSetBumpSetResponse.getPack_id())) {
						RFAInitialSetBumpSetResponse rfaRes = new RFAInitialSetBumpSetResponse();
						rfaRes.setIn_store_week(rfaInitialSetBumpSetResponse.getIn_store_week());
						rfaRes.setStyle_id(rfaInitialSetBumpSetResponse2.getStyle_id());
						rfaRes.setCc(rfaInitialSetBumpSetResponse.getCc());
						rfaRes.setMerch_method(rfaInitialSetBumpSetResponse2.getMerch_method());
						rfaRes.setPack_id(rfaInitialSetBumpSetResponse2.getPack_id());
						rfaRes.setSize(rfaInitialSetBumpSetResponse.getStyle_id());
						rfaRes.setBumppack_ratio(rfaInitialSetBumpSetResponse2.getBumppack_ratio());
						rfaRes.setIs_quantity(rfaInitialSetBumpSetResponse2.getIs_quantity());
						rfaRes.setInitialpack_ratio(rfaInitialSetBumpSetResponse.getInitialpack_ratio());
						rfaInitialSetBumpSetResponses.add(rfaRes);
					}
				}
			}
	         
	         log.info("Query performed successfully.");
	     }catch (Exception e) {
	         log.error("Query not performed \n" + e.getMessage());
	     }
	     log.info("resultsIS: {}",rfaInitialSetBumpSetResponses);
	   
	     return rfaInitialSetBumpSetResponses;
	 }
 
	  private String getSizePackIntialSetQueryString(String ccTableName, String spTableName,Integer planId,Integer finelineNbr){
	  	 String prodFineline = planId+"_"+finelineNbr;
		 return  "WITH MyTable AS ( select distinct reverse( SUBSTR(REVERSE(RFA.cc), STRPOS(REVERSE(RFA.cc), \"_\")+1)) as style_id,RFA.in_store_week, RFA.cc, SP.merch_method, SP.pack_id,SP.size, (SP.initialpack_ratio) AS initialpack_ratio, SUM(SP.is_quantity) AS is_quantity from (select trim(cc) as cc,CAST(store AS INTEGER) as store,min(week) as in_store_week FROM `"+ccTableName+"`as RFA where plan_id_partition="+planId+" and fineline="+finelineNbr+" and final_alloc_space>0 group by cc,store order by cc, in_store_week, store )as RFA join (SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as pack_id, SP.MerchMethod as merch_method, SP.size, SP.SPBumpSetPackSizeRatio as bumppack_ratio, SP.SPInitialSetPackSizeRatio as initialpack_ratio, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity FROM `"+spTableName+"` AS SP where ProductFineline = '"+prodFineline+"') as SP on RFA.store = SP.store and RFA.cc = SP.cc GROUP BY RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id ,initialpack_ratio order by RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id,initialpack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
	  }
	  
	  private String getSizePackBumpSetQueryString(String spTableName,Integer planId,Integer finelineNbr){
	  	 String prodFineline = planId+"_"+finelineNbr;
		 return  "WITH MyTable AS ( select distinct reverse( SUBSTR(REVERSE(ProductCustomerChoice), STRPOS(REVERSE(ProductCustomerChoice), \"_\")+1)) as style_id, SP.ProductCustomerChoice as customer_choice, SP.MerchMethod AS merch_method, SP.SPPackID as pack_id,SP.Size as size, (SP.SPBumpSetPackSizeRatio) AS bumppack_ratio, SUM(SP.SPPackBumpOutput) AS is_quantity FROM `"+spTableName+"` AS SP where ProductFineline = '"+prodFineline+"' and SPBumpSetPackSizeRatio>0 GROUP BY style_id,customer_choice,merch_method,size,pack_id,bumppack_ratio order by style_id,customer_choice,merch_method,size,pack_id,bumppack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
	  }
}