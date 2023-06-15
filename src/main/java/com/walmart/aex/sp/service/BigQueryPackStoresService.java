package com.walmart.aex.sp.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.cr.storepacks.PackDetailsVolumeResponse;
import com.walmart.aex.sp.dto.isVolume.FinelineVolume;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import com.walmart.aex.sp.properties.GraphQLProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BigQueryPackStoresService 
{
	 private static final ObjectMapper objectMapper = new ObjectMapper();
	 
	 private final StrategyFetchService strategyFetchService;
	 
	 @ManagedConfiguration
	 BigQueryConnectionProperties bigQueryConnectionProperties;

	 @ManagedConfiguration
	 private GraphQLProperties graphQLProperties;
	 
	 public BigQueryPackStoresService(StrategyFetchService strategyFetchService)
	 {
		 this.strategyFetchService = strategyFetchService;
	 }
	 
	 public PackDetailsVolumeResponse getPackStoreDetailsByVolumeCluster(Long planId, FinelineVolume request) throws SizeAndPackException
	 {
		 Integer finelineNbr = request.getFinelineNbr();
		 StrategyVolumeDeviationResponse volumeDeviationResponse = strategyFetchService.getStrategyVolumeDeviation(planId, finelineNbr);
		 String sqlQuery = "";
		 if (null != volumeDeviationResponse && !volumeDeviationResponse.getFinelines().isEmpty()) 
		 {
             FinelineVolumeDeviationDto finelineVolumeDeviationDto = volumeDeviationResponse.getFinelines().get(0);
             String volumeDeviationLevel = finelineVolumeDeviationDto.getVolumeDeviationLevel();
             if (StringUtils.isNotEmpty(volumeDeviationLevel)) 
             {
                 sqlQuery = findSqlQuery(planId, request, volumeDeviationLevel);
             } 
             else 
             {
                 log.error("Error Occurred while fetching Strategy Volume Deviation level for plan ID {} and fineline {}", planId, finelineNbr);
                 throw new SizeAndPackException("Error Occurred while fetching Strategy Volume Deviation level for plan ID " + planId);
             }
         }
		 else
		 {
             log.error("Error Occurred while fetching Strategy Volume Deviation Response for plan ID {} and fineline {}", planId, finelineNbr);
             throw new SizeAndPackException("Error Occurred while fetching Strategy Volume Deviation Response for plan ID " + planId);
         }
		 BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
	     QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(sqlQuery).build();
	     try 
	     {
			TableResult tableResult = bigQuery.query(queryConfig);
		 }
	     catch (JobException e) 
		    {
		    	log.error("The query for fetching store packs completed unsuccessfully", e);
			} 
	     catch (InterruptedException e) 
	     {
				// TODO Auto-generated catch block
				log.error("Thread to fetch store packs interrupted while waiting for query to complete", e);
		  }
		   
		 return null;
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
	        throw new RuntimeException("Invalid Deviation Level, Fineline, Subcategory, Category are valid values");
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
		 String prodFineline = planId + "_" + finelineNbr;
         return "WITH MyTable AS (\n" +
               "select distinct\n" +
               "SP.productFineline,\n" +
               "RFA.fineline,\n" +
               "RFA.cc,\n" +
               "RFA.style_nbr,\n" +
               "SUM(SP.is_quantity) AS is_quantity ,\n" +
               "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
               "RFA.store,\n" +
               "CL.clusterId,\n" +
               "RFA.fixtureAllocation,\n" +
               "RFA.fixtureType\n" +
               "SP.SPPackID,\n" +
               "from (\n" +
               " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week, "
               + "allocated as fixtureAllocation, final_pref as fixtureType from (" +
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
               ")"+
               "as RFA left outer join "+
               "(\n" +
               "SELECT SP.ProductFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity\n" +
               "FROM `" + spTableName + "` AS SP where ProductFineline like '" + prodFineline +
               ") as SP\n" +
               "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
               "join (\n" +
               "select store_nbr as store,cluster_id  as clusterId from `" + analyticsData + ".svg_fl_cluster` where dept_catg_nbr = " + catNbr + " and dept_subcatg_nbr = " + subCatNbr + 
               " and fineline_nbr = " + finelineNbr + " and season = '"+interval+"' and fiscal_year = " +fiscalYear + " \n" +
               ") as CL\n" +
               "on RFA.store = CL.store\n" +
               "GROUP BY SP.productFineline, SP.SPPackID, RFA.fineline, RFA.in_store_week,RFA.style_nbr, RFA.cc, CL.clusterId,RFA.store ,RFA.fixtureAllocation, RFA.fixtureType order by "
               + "RFA.in_store_week,RFA.style_nbr,RFA.cc,CL.clusterId,RFA.store, RFA.fixtureAllocation, RFA.fixtureType, SP.SPPackID\n" +
               ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
	 }
	 
	 private String getStorePacksByVolumeCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer catNbr, String analyticsData, String interval, 
			 Integer fiscalYear)
	 {
		 String prodFineline = planId + "_" + finelineNbr;
         return "WITH MyTable AS (\n" +
               "select distinct\n" +
               "SP.productFineline,\n" +
               "RFA.fineline,\n" +
               "RFA.cc,\n" +
               "RFA.style_nbr,\n" +
               "SUM(SP.is_quantity) AS is_quantity ,\n" +
               "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
               "RFA.store,\n" +
               "CL.clusterId,\n" +
               "RFA.fixtureAllocation,\n" +
               "RFA.fixtureType\n" +
               "SP.SPPackID,\n" +
               "from (\n" +
               " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week, "
               + "allocated as fixtureAllocation, final_pref as fixtureType from (" +
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
               ")"+
               "as RFA left outer join "+
               "(\n" +
               "SELECT SP.ProductFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity\n" +
               "FROM `" + spTableName + "` AS SP where ProductFineline like '" + prodFineline +
               ") as SP\n" +
               "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
               "join (select scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_category_cluster` scc join `"+analyticsData+".svg_category` sc on sc.cluster_id = scc.cluster_id "
               		+ "and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_catg_nbr = " + catNbr +
               		" and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + ") as CL\n" +
               "on RFA.store = CL.store\n" +
               "GROUP BY SP.productFineline, SP.SPPackID, RFA.fineline, RFA.in_store_week,RFA.style_nbr, RFA.cc, CL.clusterId,RFA.store ,RFA.fixtureAllocation, RFA.fixtureType order by "
               + "RFA.in_store_week,RFA.style_nbr,RFA.cc,CL.clusterId,RFA.store, RFA.fixtureAllocation, RFA.fixtureType, SP.SPPackID\n" +
               ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
	 }
	 
	 private String geStorePacksByVolumeSubCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer subCatNbr, String analyticsData, String interval, 
			 Integer fiscalYear, Integer catNbr)
	 {
		 String prodFineline = planId + "_" + finelineNbr;
         return "WITH MyTable AS (\n" +
               "select distinct\n" +
               "SP.productFineline,\n" +
               "RFA.fineline,\n" +
               "RFA.cc,\n" +
               "RFA.style_nbr,\n" +
               "SUM(SP.is_quantity) AS is_quantity ,\n" +
               "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
               "RFA.store,\n" +
               "CL.clusterId,\n" +
               "RFA.fixtureAllocation,\n" +
               "RFA.fixtureType\n" +
               "SP.SPPackID,\n" +
               "from (\n" +
               " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week, "
               + "allocated as fixtureAllocation, final_pref as fixtureType from (" +
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
               ")"+
               "as RFA left outer join "+
               "(\n" +
               "SELECT SP.ProductFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity\n" +
               "FROM `" + spTableName + "` AS SP where ProductFineline like '" + prodFineline +
               ") as SP\n" +
               "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
               "join (select scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_subcategory_cluster` scc join `"+analyticsData+".svg_subcategory` sc on sc.cluster_id = scc.cluster_id "
               		+ "and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.dept_subcatg_nbr = scc.dept_subcatg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year "
               		+ "where sc.dept_catg_nbr = " + catNbr + " and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + ") as CL\n" +
               "on RFA.store = CL.store\n" +
               "GROUP BY SP.productFineline, SP.SPPackID, RFA.fineline, RFA.in_store_week,RFA.style_nbr, RFA.cc, CL.clusterId,RFA.store ,RFA.fixtureAllocation, RFA.fixtureType order by "
               + "RFA.in_store_week,RFA.style_nbr,RFA.cc,CL.clusterId,RFA.store, RFA.fixtureAllocation, RFA.fixtureType, SP.SPPackID\n" +
               ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
	 }
}
