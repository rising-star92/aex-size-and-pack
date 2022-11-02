package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
import com.walmart.aex.sp.dto.bqfp.BQFPRequest;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.BumpSet;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import static java.util.Objects.requireNonNull;

@Service
@Slf4j
public class BigQueryInitialSetPlanService {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private final BQFPService bqfpService;

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public BigQueryInitialSetPlanService(BQFPService bqfpService) {
		this.bqfpService = bqfpService;
	}


	public List<RFAInitialSetBumpSetResponse> getInitialAndBumpSetDetails(InitialSetPackRequest rfaSizePackRequest) {
		List<RFAInitialSetBumpSetResponse> rfaInitialSetBumpSetResponses = new ArrayList<>();
		List<RFAInitialSetBumpSetResponse> rfaInitialSetBumpSetResponseBs = new ArrayList<>();
	     try {
	         String projectId = bigQueryConnectionProperties.getMLProjectId();
	         String datasetName = bigQueryConnectionProperties.getMLDataSetName();
	         String tableNameSp = bigQueryConnectionProperties.getRFASPPackOptTableName();
	         String rfaProjectId = bigQueryConnectionProperties.getRFAProjectId();
	         String rfaDatasetName = bigQueryConnectionProperties.getRFADataSetName();
	         String tableNameCc = bigQueryConnectionProperties.getRFACCStageTable();
	         String projectIdDatasetNameTableNameSp = projectId + "." + datasetName + "." + tableNameSp;
	         String projectIdDatasetNameTableNameCc = rfaProjectId + "." + rfaDatasetName + "." + tableNameCc;
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
	         List<RFAInitialSetBumpSetResponse> rfaBsRes = new ArrayList<>();
			 String bsInstoreweek = getInStoreWeek(rfaSizePackRequest.getPlanId(), rfaSizePackRequest.getFinelineNbr());

			 for (RFAInitialSetBumpSetResponse rfaBumpSetResponse : rfaInitialSetBumpSetResponseBs) {
						RFAInitialSetBumpSetResponse rfaRes = new RFAInitialSetBumpSetResponse();
				 		rfaRes.setIn_store_week(bsInstoreweek);
				 		rfaRes.setStyle_id(rfaBumpSetResponse.getStyle_id());
						rfaRes.setCc(rfaBumpSetResponse.getCc());
						rfaRes.setMerch_method(rfaBumpSetResponse.getMerch_method());
						rfaRes.setPack_id(rfaBumpSetResponse.getPack_id());
						rfaRes.setSize(rfaBumpSetResponse.getSize());
						rfaRes.setBumppack_ratio(rfaBumpSetResponse.getBumppack_ratio());
						rfaRes.setBs_quantity(rfaBumpSetResponse.getBs_quantity());
						rfaBsRes.add(rfaRes);
				}
	         rfaInitialSetBumpSetResponses.addAll(rfaBsRes);
	         log.info("Query performed successfully.");
	     }catch (Exception e) {
	         log.error("Query not performed \n" + e.getMessage());
	     }
	    // log.info("resultsIS: {}",rfaInitialSetBumpSetResponses);
	   
	     return rfaInitialSetBumpSetResponses;
	 }
	public String getInStoreWeek(Integer planId, Integer finelineNbr){
		BQFPRequest bqfpRequest = new BQFPRequest();
		bqfpRequest.setPlanId(Long.valueOf(planId));
		bqfpRequest.setFinelineNbr(finelineNbr);
		bqfpRequest.setChannel("1");

		BQFPResponse response = requireNonNull(bqfpService.getBuyQuantityUnits(bqfpRequest), "flow plan response can't be null or empty");
		List<BumpSet> bumpList = Optional.ofNullable(response)
				.map( styles -> styles.getStyles())
				.map(style -> style.get(0))
				.map(cc -> cc.getCustomerChoices())
				.map(customerChoice -> customerChoice.get(0))
				.map(fixtures -> fixtures.getFixtures())
				.map(fixture -> fixture.get(0))
				.map(clusters -> clusters.getClusters())
				.map(cluster -> cluster.get(0))
				.map(Cluster::getBumpList)
				.orElse(Collections.emptyList());

		if( null!= bumpList && !bumpList.isEmpty()){
			if( null!=bumpList.get(0) ){
				return formatWeekDesc(bumpList.get(0).getWeekDesc());
			}
		}
		return null;
	}

	public String formatWeekDesc(String input){
		requireNonNull(input, "input is required and missing.");

		StringBuilder weekDesc = new StringBuilder();

		for(char c: input.toCharArray()){
			if(c >='0' && c<='9'){
				weekDesc.append(c);
			}
		}
		return weekDesc.toString();
	}

	private String getSizePackIntialSetQueryString(String ccTableName, String spTableName,Integer planId,Integer finelineNbr){
	  	 String prodFineline = planId+"_"+finelineNbr;
		 return  "WITH MyTable AS ( select distinct reverse( SUBSTR(REVERSE(RFA.cc), STRPOS(REVERSE(RFA.cc), \"_\")+1)) as style_id,RFA.in_store_week, RFA.cc, SP.merch_method, SP.pack_id,SP.size, (SP.initialpack_ratio) AS initialpack_ratio, SUM(SP.is_quantity) AS is_quantity from (select trim(cc) as cc,CAST(store AS INTEGER) as store,min(week) as in_store_week FROM `"+ccTableName+"`as RFA where plan_id_partition="+planId+" and fineline="+finelineNbr+" and final_alloc_space>0 group by cc,store order by cc, in_store_week, store )as RFA join (SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as pack_id, SP.MerchMethod as merch_method, SP.size, SP.SPBumpSetPackSizeRatio as bumppack_ratio, SP.SPInitialSetPackSizeRatio as initialpack_ratio, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity FROM `"+spTableName+"` AS SP where ProductFineline = '"+prodFineline+"' and SPInitialSetPackSizeRatio >0 ) as SP on RFA.store = SP.store and RFA.cc = SP.cc GROUP BY RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id ,initialpack_ratio order by RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id,initialpack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
	  }
	  
	  private String getSizePackBumpSetQueryString(String spTableName,Integer planId,Integer finelineNbr){
	  	 String prodFineline = planId+"_"+finelineNbr;
		 return  "WITH MyTable AS ( select distinct reverse( SUBSTR(REVERSE(ProductCustomerChoice), STRPOS(REVERSE(ProductCustomerChoice), \"_\")+1)) as style_id, SP.ProductCustomerChoice as cc, SP.MerchMethod AS merch_method, SP.SPPackID as pack_id,SP.Size as size, (SP.SPBumpSetPackSizeRatio) AS bumppack_ratio, SUM(SP.SPPackBumpOutput) AS bs_quantity FROM `"+spTableName+"` AS SP where ProductFineline = '"+prodFineline+"' and SPBumpSetPackSizeRatio>0 GROUP BY style_id,cc,merch_method,size,pack_id,bumppack_ratio order by style_id,cc,merch_method,size,pack_id,bumppack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
	  }
}