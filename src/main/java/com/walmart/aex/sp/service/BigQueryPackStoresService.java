package com.walmart.aex.sp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.cr.storepacks.PackDetailsVolumeResponse;
import com.walmart.aex.sp.dto.cr.storepacks.PackStoreDTO;
import com.walmart.aex.sp.dto.cr.storepacks.StoreMetrics;
import com.walmart.aex.sp.dto.cr.storepacks.StylePack;
import com.walmart.aex.sp.dto.cr.storepacks.StylePackVolume;
import com.walmart.aex.sp.dto.cr.storepacks.VolumeFixtureAllocation;
import com.walmart.aex.sp.dto.cr.storepacks.VolumeFixtureMetrics;
import com.walmart.aex.sp.dto.isVolume.FinelineVolume;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import com.walmart.aex.sp.properties.GraphQLProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import io.strati.libs.commons.collections.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryPackStoresService 
{
	 private final ObjectMapper objectMapper;

	 private final BigQuery bigQuery;
	 
	 private final StrategyFetchService strategyFetchService;
	 
	 @ManagedConfiguration
	 BigQueryConnectionProperties bigQueryConnectionProperties;

	 @ManagedConfiguration
	 private GraphQLProperties graphQLProperties;

	public BigQueryPackStoresService(ObjectMapper objectMapper, BigQuery bigQuery, StrategyFetchService strategyFetchService) {
		this.objectMapper = objectMapper;
		this.bigQuery = bigQuery;
		this.strategyFetchService = strategyFetchService;
	}

	public PackDetailsVolumeResponse getPackStoreDetailsByVolumeCluster(Long planId,
																							  FinelineVolume request) throws SizeAndPackException
	 {
	     QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(findSqlQuery(planId, 
	    		 request, getVolumeDeviation(planId, request.getFinelineNbr()))).build();
	     List<PackStoreDTO> packStoreDTOs = new ArrayList<>();
	     try 
	     {
			TableResult tableResult = bigQuery.query(queryConfig);
			Iterator<FieldValueList> iterator = tableResult.iterateAll().iterator();
			while(iterator.hasNext())
			{
				for(FieldValue fieldValue : iterator.next())
				{
						PackStoreDTO packStoreDTO = objectMapper.readValue(
								fieldValue.getValue().toString(), PackStoreDTO.class);
						packStoreDTOs.add(packStoreDTO);
				}
			}
		 }
	     catch (JsonMappingException e)
	     {
				log.error("Exception occurred while mapping the pack store gcp table response data", e);
				throw new SizeAndPackException("Exception occurred while mapping the pack store gcp table response data", e);
		 }
	     catch (JsonProcessingException e) 
		 {
				log.error("Exception occurred while processing the pack store gcp table response data", e);
				throw new SizeAndPackException("Exception occurred while processing the pack store gcp table response data", e);
		 }
	     catch (JobException e) 
		 {
		    	log.error("The query for fetching store packs failed", e);
		    	throw new SizeAndPackException("The query for fetching store packs failed", e);
	     } 
	     catch (InterruptedException e) 
	     {
				log.error("Thread to fetch store packs interrupted while waiting for query to complete", e);
				throw new SizeAndPackException("Thread to fetch store packs interrupted while waiting for query to complete", e);
		 }
		 return PackDetailsVolumeResponse.builder()
	    		 .finelineNbr(request.getFinelineNbr())
	    		 .stylePackVolumes(getPackDetailsVolumeResponse(createVolumeFixtureMetrics(packStoreDTOs))).build();
	 }
	 
	 private String getVolumeDeviation(Long planId, Integer finelineNbr) throws SizeAndPackException
	 {
		 StrategyVolumeDeviationResponse volumeDeviationResponse = strategyFetchService
				 .getStrategyVolumeDeviation(planId, finelineNbr);
		 if (null != volumeDeviationResponse && CollectionUtils.isNotEmpty(volumeDeviationResponse
				 .getFinelines())) 
		 {
             FinelineVolumeDeviationDto finelineVolumeDeviationDto = volumeDeviationResponse.getFinelines().get(0);
             String volumeDeviationLevel = finelineVolumeDeviationDto.getVolumeDeviationLevel();
             if (StringUtils.isNotEmpty(volumeDeviationLevel)) 
             {
                 return volumeDeviationLevel;
             } 
             log.error("Exception occurred while fetching Strategy Volume Deviation level for plan id {} and fineline {}", planId, finelineNbr);
             throw new SizeAndPackException("Exception occurred while fetching Strategy Volume Deviation level for plan id " + planId);
         }
		 else
		 {
             log.error("Exception occurred while fetching Strategy Volume Deviation Response for plan id {} and fineline {}", planId, finelineNbr);
             throw new SizeAndPackException("Exception occurred while fetching Strategy Volume Deviation Response for plan id " + planId);
         }
	 }
	 
	 private String findSqlQuery(Long planId, FinelineVolume request, String volumeDeviationLevel) 
	 {
	        String tableNameSp = getProjectIdSp();
	        String tableNameCc = getProjectIdCc();

	        if (volumeDeviationLevel.equals(VdLevelCode.CATEGORY.getDescription())) 
	        {
	            return getStorePacksByVolumeCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl3Nbr(), bigQueryConnectionProperties.getAnalyticsData(),
	            		request.getInterval(),request.getFiscalYear());
	        } 
            if (volumeDeviationLevel.equals(VdLevelCode.SUB_CATEGORY.getDescription())) 
            {
	            return geStorePacksByVolumeSubCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl4Nbr(), bigQueryConnectionProperties.getAnalyticsData(),
	            		request.getInterval(),request.getFiscalYear(),request.getLvl3Nbr());
	        }
            if (volumeDeviationLevel.equals(VdLevelCode.FINELINE.getDescription())) 
            {
	            return getStorePacksByVolumeFinelineClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),
	            		request.getFiscalYear(),request.getLvl3Nbr(),request.getLvl4Nbr());
	        }
	        throw new RuntimeException("Invalid volume deviation level found : " +volumeDeviationLevel +  
	        		", Fineline, Category and Subcategory are valid values");
	 }
	 
	 
	 private Map<StylePack, Map<VolumeFixtureAllocation, List<StoreMetrics>>> createVolumeFixtureMetrics(List<PackStoreDTO> packStoreDTOs)
	 {
		 Map<StylePack, Map<VolumeFixtureAllocation, List<StoreMetrics>>> volFixtureMetrics = new HashMap<>();
		 for(PackStoreDTO packStoreDTO : packStoreDTOs)
	     {
	    	 Integer isQty = packStoreDTO.getIsQuantity();
	    	 Integer bsQty = packStoreDTO.getBsQuantity();
	    	 // Stores have allocated space in RFA but don't have any quantities
	    	 if(isQty == null && bsQty == null)
	    	 {
	    		 populateVolumeFixtureMetrics(volFixtureMetrics, packStoreDTO, 0, 0);
	    	 }
	    	 if(isQty!=null && isQty > 0)
	    	 {
	    		 populateVolumeFixtureMetrics(volFixtureMetrics, packStoreDTO, 
	    				 packStoreDTO.getIsQuantity(), packStoreDTO.getInitialSetPackMultiplier());
	    	 }
	    	 else if(bsQty!=null && bsQty > 0)
	    	 {
	    		 populateVolumeFixtureMetrics(volFixtureMetrics, packStoreDTO, 
	    				 packStoreDTO.getBsQuantity(), packStoreDTO.getBumpSetPackMultiplier());
	    	 }
	     }
		 return volFixtureMetrics;
	 }
	 
	 private void populateVolumeFixtureMetrics(Map<StylePack, 
			 Map<VolumeFixtureAllocation, List<StoreMetrics>>> volFixtureMetrics, PackStoreDTO packStoreDTO, 
			 Integer qty, Integer multiplier)
	 {
		 volFixtureMetrics.computeIfAbsent(new StylePack(packStoreDTO.getStyleNbr(),
    			 packStoreDTO.getPackId()), x -> new HashMap<>()).computeIfAbsent(
    					 VolumeFixtureAllocation.builder().ccId(packStoreDTO.getCc())
    					 .volumeClusterId(packStoreDTO.getClusterId())
    					 .fixtureType(packStoreDTO.getFixtureType())
    					 .fixtureAllocation(new BigDecimal(packStoreDTO.getFixtureAllocation())
    							 ).build(), y -> new ArrayList<>()).add(StoreMetrics.builder()
    									 .multiplier(multiplier)
    									 .store(packStoreDTO.getStore())
    									 .qty(qty).build());
	 }
	 
	 private List<StylePackVolume> getPackDetailsVolumeResponse(Map<StylePack, 
			 Map<VolumeFixtureAllocation, List<StoreMetrics>>> volFixtureMetrics)
	 {
		 List<StylePackVolume> stylePackVolumes = new ArrayList<>();
	     for(Map.Entry<StylePack, Map<VolumeFixtureAllocation, List<StoreMetrics>>> entry : 
	    	 volFixtureMetrics.entrySet())
	     {
	    	 StylePack stylePack = entry.getKey();
	    	 List<VolumeFixtureMetrics> volFixtureAllocationMetrics = new ArrayList<>();
	    	 for(Map.Entry<VolumeFixtureAllocation, List<StoreMetrics>> e : entry.getValue().entrySet())
	    	 {
	    		 List<StoreMetrics> storeMetrics = e.getValue();
	    		 VolumeFixtureAllocation volumeFixtureAllocation = e.getKey();
	    		 volFixtureAllocationMetrics.add(VolumeFixtureMetrics.builder()
	    				 .ccId(volumeFixtureAllocation.getCcId())
	    				 .fixtureAllocation(volumeFixtureAllocation.getFixtureAllocation())
	    				 .fixtureType(volumeFixtureAllocation.getFixtureType())
	    				 .volumeClusterId(volumeFixtureAllocation.getVolumeClusterId())
	    				 .quantity(storeMetrics.stream().mapToInt(StoreMetrics::getQty).sum())
	    				 .stores(storeMetrics)
	    				 .build());
	    	 }
	    	 stylePackVolumes.add(StylePackVolume.builder().styleId(stylePack.getStyleId())
	    			 .packId(stylePack.getPackId()).metrics(volFixtureAllocationMetrics).build());
	     }
		 return stylePackVolumes;
	 }
	 
	 private String getProjectIdSp() 
	 {
	        String projectId = bigQueryConnectionProperties.getMLProjectId();
	        String datasetName = bigQueryConnectionProperties.getMLDataSetName();
	        String tableNameSp = bigQueryConnectionProperties.getRFASPPackOptTableName();
	        return projectId + "." + datasetName + "." + tableNameSp;
	 }
	 
	 private String getProjectIdCc() 
	 {
	        String rfaProjectId = bigQueryConnectionProperties.getRFAProjectId();
	        String rfaDatasetName = bigQueryConnectionProperties.getRFADataSetName();
	        String tableNameCc = bigQueryConnectionProperties.getRFACCStageTable();
	        return rfaProjectId + "." + rfaDatasetName + "." + tableNameCc;
	 }
	 
	 private String getStorePacksByVolumeFinelineClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, String analyticsData, String interval, 
			 Integer fiscalYear, Integer catNbr, Integer subCatNbr)
	 {
		 String prodFineline = planId + "_" + finelineNbr + "%";
         return "WITH MyTable AS (\n" +
               "select distinct\n" +
               "SP.productFineline,\n" +
               "RFA.fineline,\n" +
               "RFA.cc,\n" +
               "RFA.style_nbr AS styleNbr,\n" +
               "SUM(SP.is_quantity) AS isQuantity ,\n" +
               "SUM(SP.bs_quantity) AS bsQuantity ,\n" +
               "RFA.store,\n" +
               "CL.clusterId,\n" +
               "RFA.fixtureAllocation,\n" +
               "RFA.fixtureType,\n" +
               "SP.packId,\n" +
               "SP.initialSetPackMultiplier,\n" +
               "SP.bumpSetPackMultiplier\n" +
               "from (\n" +
               " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week, "
               + " cc_week.fineline as fineline, allocated as fixtureAllocation, final_pref as fixtureType from (" +
               "(select fineline, store, allocated, final_pref from ("+
               "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
               "(select * from " + ccTableName  +
               " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
               " where rw_nbr = 1) fl_alloc" +
               " inner join " +
               " (select store, fineline, style_nbr,cc, min(week) as in_store_week" +
               " FROM " + ccTableName+
               " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, style_nbr,cc) cc_week" +
               " on fl_alloc.fineline = cc_week.fineline" +
               " and fl_alloc.store = cc_week.store )" +
               ") "+
               "as RFA left outer join "+
               "(\n" +
               "SELECT SP.ProductFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as packId, "
               + "SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity, SP.SPInitialSetPackMultiplier "
               + "as initialSetPackMultiplier, SP.SPBumpSetPackMultiplier as bumpSetPackMultiplier\n" +
               "FROM `" + spTableName + "` AS SP where ProductFineline like '" + prodFineline +
               "') as SP\n" +
               "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
               "join (\n" +
               "select store_nbr as store,cluster_id  as clusterId from `" + analyticsData + ".svg_fl_cluster` where dept_catg_nbr = " + catNbr + " and dept_subcatg_nbr = " + subCatNbr + 
               " and fineline_nbr = " + finelineNbr + " and season = '"+interval+"' and fiscal_year = " +fiscalYear + " \n" +
               ") as CL\n" +
               "on RFA.store = CL.store\n" +
               "GROUP BY SP.productFineline, SP.packId, RFA.fineline, RFA.in_store_week,RFA.style_nbr, RFA.cc, CL.clusterId,RFA.store ,RFA.fixtureAllocation, RFA.fixtureType, SP.initialSetPackMultiplier, SP.bumpSetPackMultiplier order by "
               + "RFA.style_nbr,RFA.cc,CL.clusterId,RFA.store, RFA.fixtureAllocation, RFA.fixtureType, SP.packId, SP.initialSetPackMultiplier, SP.bumpSetPackMultiplier\n" +
               ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
	 }
	 
	 private String getStorePacksByVolumeCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer catNbr, String analyticsData, String interval, 
			 Integer fiscalYear)
	 {
		 String prodFineline = planId + "_" + finelineNbr + "%";
         return "WITH MyTable AS (\n" +
               "select distinct\n" +
               "SP.productFineline,\n" +
               "RFA.fineline,\n" +
               "RFA.cc,\n" +
               "RFA.style_nbr AS styleNbr,\n" +
               "SUM(SP.is_quantity) AS isQuantity ,\n" +
               "SUM(SP.bs_quantity) AS bsQuantity ,\n" +
               "RFA.store,\n" +
               "CL.clusterId,\n" +
               "RFA.fixtureAllocation,\n" +
               "RFA.fixtureType,\n" +
               "SP.packId,\n" +
               "SP.initialSetPackMultiplier,\n" +
               "SP.bumpSetPackMultiplier\n" +
               "from (\n" +
               " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week, "
               + " cc_week.fineline as fineline, allocated as fixtureAllocation, final_pref as fixtureType from (" +
               "(select fineline, store, allocated, final_pref from ("+
               "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
               "(select * from " + ccTableName  +
               " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
               " where rw_nbr = 1) fl_alloc" +
               " inner join " +
               " (select store, fineline, style_nbr,cc, min(week) as in_store_week" +
               " FROM " + ccTableName+
               " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, style_nbr,cc) cc_week" +
               " on fl_alloc.fineline = cc_week.fineline" +
               " and fl_alloc.store = cc_week.store )" +
               ") "+
               "as RFA left outer join "+
               "(\n" +
               "SELECT SP.ProductFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as packId, "
               + "SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity, \n" 
               + " SP.SPInitialSetPackMultiplier as initialSetPackMultiplier, " 
               + "SP.SPBumpSetPackMultiplier as bumpSetPackMultiplier\n" +
               "FROM `" + spTableName + "` AS SP where ProductFineline like '" + prodFineline +
               "') as SP\n" +
               "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
               "join (select scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_category_cluster` scc join `"+analyticsData+".svg_category` sc on sc.cluster_id = scc.cluster_id "
               		+ "and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_catg_nbr = " + catNbr +
               		" and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + ") as CL\n" +
               "on RFA.store = CL.store\n" +
               "GROUP BY SP.productFineline, SP.packId, RFA.fineline, RFA.in_store_week,RFA.style_nbr, RFA.cc, CL.clusterId,RFA.store ,RFA.fixtureAllocation, RFA.fixtureType, SP.initialSetPackMultiplier, SP.bumpSetPackMultiplier order by "
               + "RFA.style_nbr,RFA.cc,CL.clusterId,RFA.store, RFA.fixtureAllocation, RFA.fixtureType, SP.packId, SP.initialSetPackMultiplier, SP.bumpSetPackMultiplier\n" +
               ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
	 }
	 
	 private String geStorePacksByVolumeSubCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer subCatNbr, String analyticsData, String interval, 
			 Integer fiscalYear, Integer catNbr)
	 {
		 String prodFineline = planId + "_" + finelineNbr + "%";
         return "WITH MyTable AS (\n" +
               "select distinct\n" +
               "SP.productFineline,\n" +
               "RFA.fineline,\n" +
               "RFA.cc,\n" +
               "RFA.style_nbr AS styleNbr,\n" +
               "SUM(SP.is_quantity) AS isQuantity ,\n" +
               "SUM(SP.bs_quantity) AS bsQuantity ,\n" +
               "RFA.store,\n" +
               "CL.clusterId,\n" +
               "RFA.fixtureAllocation,\n" +
               "RFA.fixtureType,\n" +
               "SP.packId,\n" +
               "SP.initialSetPackMultiplier,\n" +
               "SP.bumpSetPackMultiplier\n" +
               "from (\n" +
               " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week, "
               + " cc_week.fineline as fineline, allocated as fixtureAllocation, final_pref as fixtureType from (" +
               "(select fineline, store, allocated, final_pref from ("+
               "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
               "(select * from " + ccTableName  +
               " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
               " where rw_nbr = 1) fl_alloc" +
               " inner join " +
               " (select store, fineline, style_nbr,cc, min(week) as in_store_week" +
               " FROM " + ccTableName+
               " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, style_nbr,cc) cc_week" +
               " on fl_alloc.fineline = cc_week.fineline" +
               " and fl_alloc.store = cc_week.store )" +
               ") "+
               "as RFA left outer join "+
               "(\n" +
               "SELECT SP.ProductFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as packId, "
               + "SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity, \n" 
               + " SP.SPInitialSetPackMultiplier as initialSetPackMultiplier, " 
               + "SP.SPBumpSetPackMultiplier as bumpSetPackMultiplier\n" +
               "FROM `" + spTableName + "` AS SP where ProductFineline like '" + prodFineline +
               "') as SP\n" +
               "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
               "join (select scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_subcategory_cluster` scc join `"+analyticsData+".svg_subcategory` sc on sc.cluster_id = scc.cluster_id "
               		+ "and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.dept_subcatg_nbr = scc.dept_subcatg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year "
               		+ "where sc.dept_catg_nbr = " + catNbr + " and sc.dept_subcatg_nbr = " + subCatNbr + " and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + ") as CL\n" +
               "on RFA.store = CL.store\n" +
               "GROUP BY SP.productFineline, SP.packID, RFA.fineline, RFA.in_store_week,RFA.style_nbr, RFA.cc, CL.clusterId,RFA.store ,RFA.fixtureAllocation, RFA.fixtureType, SP.initialSetPackMultiplier, SP.bumpSetPackMultiplier order by "
               + "RFA.style_nbr,RFA.cc,CL.clusterId,RFA.store, RFA.fixtureAllocation, RFA.fixtureType, SP.packId, SP.initialSetPackMultiplier, SP.bumpSetPackMultiplier\n" +
               ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
	 }
}
