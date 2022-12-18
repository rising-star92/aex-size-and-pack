package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.bqfp.BQFPRequest;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.BumpSet;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetPackRequest;
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponse;
import com.walmart.aex.sp.dto.isVolume.*;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

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
            String projectIdDatasetNameTableNameSp = getProjectIdSp();
            String projectIdDatasetNameTableNameCc = getProjectIdCc();

            String spQueryStringIs = getSizePackIntialSetQueryString(projectIdDatasetNameTableNameCc, projectIdDatasetNameTableNameSp, rfaSizePackRequest.getPlanId(), rfaSizePackRequest.getFinelineNbr());
            String spQueryStringBs = getSizePackBumpSetQueryString(projectIdDatasetNameTableNameSp, rfaSizePackRequest.getPlanId(), rfaSizePackRequest.getFinelineNbr());
            BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
            QueryJobConfiguration queryConfigIs = QueryJobConfiguration.newBuilder(spQueryStringIs).build();
            TableResult resultsIs = bigQuery.query(queryConfigIs);
            resultsIs.iterateAll().forEach(rows -> rows.forEach(row -> {
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
            resultsBs.iterateAll().forEach(rows -> rows.forEach(row -> {
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
        } catch (Exception e) {
            log.error("Query not performed \n" + e.getMessage());
        }

        return rfaInitialSetBumpSetResponses;
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

    public String getInStoreWeek(Integer planId, Integer finelineNbr) {
        BQFPRequest bqfpRequest = new BQFPRequest();
        bqfpRequest.setPlanId(Long.valueOf(planId));
        bqfpRequest.setFinelineNbr(finelineNbr);
        bqfpRequest.setChannel("1");

        BQFPResponse response = requireNonNull(bqfpService.getBuyQuantityUnits(bqfpRequest), "flow plan response can't be null or empty");
        List<BumpSet> bumpList = Optional.ofNullable(response)
                .map(styles -> styles.getStyles())
                .map(style -> style.get(0))
                .map(cc -> cc.getCustomerChoices())
                .map(customerChoice -> customerChoice.get(0))
                .map(fixtures -> fixtures.getFixtures())
                .map(fixture -> fixture.get(0))
                .map(clusters -> clusters.getClusters())
                .map(cluster -> cluster.get(0))
                .map(Cluster::getBumpList)
                .orElse(Collections.emptyList());

        if (null != bumpList && !bumpList.isEmpty()) {
            if (null != bumpList.get(0)) {
                return formatWeekDesc(bumpList.get(0).getWeekDesc());
            }
        }
        return null;
    }

    public String formatWeekDesc(String input) {
        requireNonNull(input, "input is required and missing.");

        StringBuilder weekDesc = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c >= '0' && c <= '9') {
                weekDesc.append(c);
            }
        }
        return weekDesc.toString();
    }

    private String getSizePackIntialSetQueryString(String ccTableName, String spTableName, Integer planId, Integer finelineNbr) {
        String prodFineline = planId + "_" + finelineNbr;
        return "WITH MyTable AS ( select distinct reverse( SUBSTR(REVERSE(RFA.cc), STRPOS(REVERSE(RFA.cc), \"_\")+1)) as style_id,RFA.in_store_week, RFA.cc, SP.merch_method, SP.pack_id,SP.size, (SP.initialpack_ratio) AS initialpack_ratio, SUM(SP.is_quantity) AS is_quantity from (select trim(cc) as cc,CAST(store AS INTEGER) as store,min(week) as in_store_week FROM `" + ccTableName + "`as RFA where plan_id_partition=" + planId + " and fineline=" + finelineNbr + " and final_alloc_space>0 group by cc,store order by cc, in_store_week, store )as RFA join (SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as pack_id, SP.MerchMethod as merch_method, SP.size, SP.SPBumpSetPackSizeRatio as bumppack_ratio, SP.SPInitialSetPackSizeRatio as initialpack_ratio, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPInitialSetPackSizeRatio >0 ) as SP on RFA.store = SP.store and RFA.cc = SP.cc GROUP BY RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id ,initialpack_ratio order by RFA.in_store_week,RFA.cc,SP.merch_method,SP.size,SP.pack_id,initialpack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
    }

    private String getSizePackBumpSetQueryString(String spTableName, Integer planId, Integer finelineNbr) {
        String prodFineline = planId + "_" + finelineNbr;
        return "WITH MyTable AS ( select distinct reverse( SUBSTR(REVERSE(ProductCustomerChoice), STRPOS(REVERSE(ProductCustomerChoice), \"_\")+1)) as style_id, SP.ProductCustomerChoice as cc, SP.MerchMethod AS merch_method, SP.SPPackID as pack_id,SP.Size as size, (SP.SPBumpSetPackSizeRatio) AS bumppack_ratio, SUM(SP.SPPackBumpOutput) AS bs_quantity FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPBumpSetPackSizeRatio>0 GROUP BY style_id,cc,merch_method,size,pack_id,bumppack_ratio order by style_id,cc,merch_method,size,pack_id,bumppack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
    }

    private String getISByVolumeFinelineClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, String analyticsData,String interval, Integer fiscalYear) {
        String prodFineline = planId + "_" + finelineNbr;
          /*
        Min week is added as a join condition to RFA. This is to prevent any inconsistent allocations in RFA.
        For example- CC 1 in store 400 could have only 1 Fixture type and allocation. RFA , as of 14 Dec 2021, could allocate a new fixture
        type and allocation mid season. This is not an expected behaviour. To shield us from this inconsistency, adding a min week check.
         */
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "RFA.in_store_week,\n" +
                "RFA.cc,\n" +
                "SUM(SP.is_quantity) AS is_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +                               
				 " select distinct trim(cc_week.cc) as cc, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
					"(select fineline, store, allocated, final_pref from ("+
					"select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
					"(select * from " + ccTableName  +
					" where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
					" where rw_nbr = 1) fl_alloc" +
					" inner join " +
					" (select store, fineline, cc, min(week) as in_store_week" +
					" FROM " + ccTableName+
					" where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, cc) cc_week" + 
					" on fl_alloc.fineline = cc_week.fineline" + 
					" and fl_alloc.store = cc_week.store )" +
					")"+
					"as RFA join "+               
                "(\n" +
                "SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPInitialSetPackSizeRatio >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select store_nbr as store,cluster_id  as clusterId from `" + analyticsData + ".svg_fl_cluster` where fineline_nbr = " + finelineNbr + " and season = '"+interval+"' and fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.in_store_week,RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }

    private String getISByVolumeSubCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer subCatNbr, String analyticsData, String interval, Integer fiscalYear) {
        String prodFineline = planId + "_" + finelineNbr;
          /*
        Min week is added as a join condition to RFA. This is to prevent any inconsistent allocations in RFA.
        For example- CC 1 in store 400 could have only 1 Fixture type and allocation. RFA , as of 14 Dec 2021, could allocate a new fixture
        type and allocation mid season. This is not an expected behaviour. To shield us from this inconsistency, adding a min week check.
         */
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "RFA.in_store_week,\n" +
                "RFA.cc,\n" +
                "SUM(SP.is_quantity) AS is_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +          
                " select distinct trim(cc_week.cc) as cc, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
        		"(select fineline, store, allocated, final_pref from ("+
        		"select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
        		"(select * from " + ccTableName  +
        		" where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
        		" where rw_nbr = 1) fl_alloc" +
        		" inner join " +
        		" (select store, fineline, cc, min(week) as in_store_week" +
        		" FROM " + ccTableName+
        		" where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, cc) cc_week" + 
        		" on fl_alloc.fineline = cc_week.fineline" + 
        		" and fl_alloc.store = cc_week.store )" +
        		")"+
        		"as RFA join "+
                "(\n" +
                "SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPInitialSetPackSizeRatio >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select distinct scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_subcategory_cluster` scc join `"+analyticsData+".svg_subcategory` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.dept_subcatg_nbr = scc.dept_subcatg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_subcatg_nbr = " + subCatNbr + " and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.in_store_week,RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }

    private String getISByVolumeCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer catNbr, String analyticsData,String interval, Integer fiscalYear) {
        String prodFineline = planId + "_" + finelineNbr;
        /*
        Min week is added as a join condition to RFA. This is to prevent any inconsistent allocations in RFA.
        For example- CC 1 in store 400 could have only 1 Fixture type and allocation. RFA , as of 14 Dec 2021, could allocate a new fixture
        type and allocation mid season. This is not an expected behaviour. To shield us from this inconsistency, adding a min week check.
         */
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "RFA.in_store_week,\n" +
                "RFA.cc,\n" +
                "SUM(SP.is_quantity) AS is_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +  
                "from (\n" +  
                " select distinct trim(cc_week.cc) as cc, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
                		"(select fineline, store, allocated, final_pref from ("+
                		"select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
                		"(select * from " + ccTableName  +
                		" where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
                		" where rw_nbr = 1) fl_alloc" +
                		" inner join " +
                		" (select store, fineline, cc, min(week) as in_store_week" +
                		" FROM " + ccTableName+
                		" where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, cc) cc_week" + 
                		" on fl_alloc.fineline = cc_week.fineline" + 
                		" and fl_alloc.store = cc_week.store )" +
                		")"+
                		"as RFA join "+
                "(\n" +
                "SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPInitialSetPackSizeRatio >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_category_cluster` scc join `"+analyticsData+".svg_category` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_catg_nbr = " + catNbr +" and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.in_store_week,RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }
    /*
        TODO add weeks back in the query when consistent RFA allocation is available. 
        Need to revisit when there are mulitple bump weeks . This is a very point in time solution to use only 1 bump week
        Change for S4
         */
    private String getBumpQTYVolumeSubCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer subCatNbr, String analyticsData,String interval, Integer fiscalYear, Integer week) {
        String prodFineline = planId + "_" + finelineNbr;
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                week + " as in_store_week,\n" +
                "RFA.cc,\n" +
                "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
           	    " select distinct trim(cc_week.cc) as cc, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
				"(select fineline, store, allocated, final_pref from ("+
				"select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
				"(select * from " + ccTableName  +
				" where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
				" where rw_nbr = 1) fl_alloc" +
				" inner join " +
				" (select store, fineline, cc, min(week) as in_store_week" +
				" FROM " + ccTableName+
				" where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, cc) cc_week" + 
				" on fl_alloc.fineline = cc_week.fineline" + 
				" and fl_alloc.store = cc_week.store )" +
				")"+
				"as RFA join "+ 
                "(\n" +
                "SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPPackBumpOutput >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select distinct scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_subcategory_cluster` scc join `"+analyticsData+".svg_subcategory` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.dept_subcatg_nbr = scc.dept_subcatg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_subcatg_nbr = " + subCatNbr + " and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }
    /*
    TODO add weeks back in the query when consistent RFA allocation is available.
    Need to revisit when there are mulitple bump weeks . This is a very point in time solution to use only 1 bump week
    Change for S4
     */
    private String getBumpQTYVolumeCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer catNbr, String analyticsData,String interval, Integer fiscalYear, Integer week) {
        String prodFineline = planId + "_" + finelineNbr;
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                week + " as in_store_week,\n" +
                "RFA.cc,\n" +
                "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
           	    " select distinct trim(cc_week.cc) as cc, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
				"(select fineline, store, allocated, final_pref from ("+
				"select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
				"(select * from " + ccTableName  +
				" where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
				" where rw_nbr = 1) fl_alloc" +
				" inner join " +
				" (select store, fineline, cc, min(week) as in_store_week" +
				" FROM " + ccTableName+
				" where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, cc) cc_week" + 
				" on fl_alloc.fineline = cc_week.fineline" + 
				" and fl_alloc.store = cc_week.store )" +
				")"+
				"as RFA join "+ 
                "(\n" +
                "SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPPackBumpOutput >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_category_cluster` scc join `"+analyticsData+".svg_category` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_catg_nbr = " + catNbr +" and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }

    /*
   TODO  add weeks back in the query when consistent RFA allocation is available.
    Need to revisit when there are mulitple bump weeks . This is a very point in time solution to use only 1 bump week
    Change for S4
     */
    private String getBumpQTYVolumeFinelineClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr,  String analyticsData,String interval, Integer fiscalYear, Integer week) {
        String prodFineline = planId + "_" + finelineNbr;
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                week + " as in_store_week,\n" +
                "RFA.cc,\n" +
                "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
           	 	" select distinct trim(cc_week.cc) as cc, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
				"(select fineline, store, allocated, final_pref from ("+
				"select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
				"(select * from " + ccTableName  +
				" where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
				" where rw_nbr = 1) fl_alloc" +
				" inner join " +
				" (select store, fineline, cc, min(week) as in_store_week" +
				" FROM " + ccTableName+
				" where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, cc) cc_week" + 
				" on fl_alloc.fineline = cc_week.fineline" + 
				" and fl_alloc.store = cc_week.store )" +
				")"+
				"as RFA join "+ 
                "(\n" +
                "SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPPackBumpOutput >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select store_nbr as store,cluster_id  as clusterId from `" + analyticsData + ".svg_fl_cluster` where fineline_nbr = " + finelineNbr + " and season = '"+interval+"' and fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }

    public List<InitialSetVolumeResponse> getInitialAndBumpSetDetailsByVolumeCluster(Long planId, FinelineVolume request) throws InterruptedException {
        String sqlQuery = findSqlQuery(planId, request);
        BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
        QueryJobConfiguration queryConfigIs = QueryJobConfiguration.newBuilder(sqlQuery).build();
        TableResult resultsIs = bigQuery.query(queryConfigIs);
        HashMap<VolumeQueryId, List<JsonNode>> uniqueRows = new HashMap<>();

        resultsIs.iterateAll().forEach(rows -> rows.forEach(row -> {
            try {
                JsonNode node = objectMapper.readTree(row.getStringValue());
                VolumeQueryId nodeId = new VolumeQueryId(node.get("cc").textValue(), node.get("clusterId").intValue(), node.get("in_store_week").intValue(),node.get("fixtureType").textValue(),node.get("fixtureAllocation").floatValue());
                if (uniqueRows.containsKey(nodeId)) {
                    uniqueRows.get(nodeId).add(node);
                } else {
                    List<JsonNode> nodes = new ArrayList<>();
                    nodes.add(node);
                    uniqueRows.put(nodeId, nodes);
                }
            } catch (JsonProcessingException e) {
                log.error("Error Occurred while fetching IS Information", e);
            }
        }));

        try{
            String bsInstoreweek = getInStoreWeek(planId.intValue(), request.getFinelineNbr());
            sqlQuery = findBumpSqlQuery(planId, request,bsInstoreweek);
            bigQuery = BigQueryOptions.getDefaultInstance().getService();
            queryConfigIs = QueryJobConfiguration.newBuilder(sqlQuery).build();
            resultsIs = bigQuery.query(queryConfigIs);

            resultsIs.iterateAll().forEach(rows -> rows.forEach(row -> {
                try {
                    JsonNode node = objectMapper.readTree(row.getStringValue());
                    VolumeQueryId nodeId = new VolumeQueryId(node.get("cc").textValue(), node.get("clusterId").intValue(), node.get("in_store_week").intValue(),node.get("fixtureType").textValue(),node.get("fixtureAllocation").floatValue());
                    if (uniqueRows.containsKey(nodeId)) {
                        uniqueRows.get(nodeId).add(node);
                    } else {
                        List<JsonNode> nodes = new ArrayList<>();
                        nodes.add(node);
                        uniqueRows.put(nodeId, nodes);
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }));
        }catch (Exception e){
            log.error("Error Occured while fetch Bump Pack information", e);
        }
        return formatUniqueRows(request, uniqueRows);
    }

    private String findBumpSqlQuery(Long planId, FinelineVolume request, String bsInstoreweek) {
        String tableNameSp = getProjectIdSp();
        String tableNameCc = getProjectIdCc();

        if (request.getVolumeDeviationLevel().equals(VdLevelCode.CATEGORY.getDescription())) {
            return getBumpQTYVolumeCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl3Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear(), Integer.parseInt(bsInstoreweek));
        } else if (request.getVolumeDeviationLevel().equals(VdLevelCode.SUB_CATEGORY.getDescription())) {
            return getBumpQTYVolumeSubCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl4Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear(), Integer.parseInt(bsInstoreweek));
        } else if (request.getVolumeDeviationLevel().equals(VdLevelCode.FINELINE.getDescription())) {
            return getBumpQTYVolumeFinelineClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear(), Integer.parseInt(bsInstoreweek));
        }
        throw new RuntimeException("Invalid Deviation Level, Fineline, Subcategory, Category are valid values");
    }

    private List<InitialSetVolumeResponse> formatUniqueRows(FinelineVolume request, HashMap<VolumeQueryId, List<JsonNode>> uniqueRows) {
        List<InitialSetVolumeResponse> initialSetVolumeResponseList = new ArrayList<>();
        uniqueRows.keySet().forEach(key -> {
            String styleId = key.getCc().substring(0, key.getCc().lastIndexOf("_"));
            InitialSetVolumeResponse initialSetVolume = initialSetVolumeResponseList.stream().filter(initialSetVolumeResponse -> initialSetVolumeResponse.getStyleId().equals(styleId) && initialSetVolumeResponse.getFinelineNbr() == request.getFinelineNbr()).findFirst().orElse(new InitialSetVolumeResponse());
            if (initialSetVolume.getStyleId() == null) {
                initialSetVolume.setStyleId(styleId);
                initialSetVolume.setFinelineNbr(request.getFinelineNbr());
                List<CustomerChoicesVolume> customerChoicesVolumes = new ArrayList<>();
                CustomerChoicesVolume customerChoicesVolume = getCustomerChoicesVolume(uniqueRows, key);

                customerChoicesVolumes.add(customerChoicesVolume);
                initialSetVolume.setCustomerChoices(customerChoicesVolumes);
                initialSetVolumeResponseList.add(initialSetVolume);
            } else {
                CustomerChoicesVolume customerChoicesVolume1 = initialSetVolume.getCustomerChoices().stream().filter(customerChoicesVolume2 -> customerChoicesVolume2.getCcId().equals(key.getCc())).findFirst().orElse(new CustomerChoicesVolume());
                if (customerChoicesVolume1.getCcId() == null) {
                    customerChoicesVolume1.setCcId(key.getCc());
                    CustomerChoicesVolume customerChoicesVolume = getCustomerChoicesVolume(uniqueRows, key);
                    initialSetVolume.getCustomerChoices().add(customerChoicesVolume);
                } else {
                    IsPlan isPlan = customerChoicesVolume1.getIsPlans().stream().filter(isPlan1 -> Objects.equals(isPlan1.getInStoreWeek(), key.getInStoreWeek())).findFirst().orElse(new IsPlan());
                    if (isPlan.getMetrics() == null) {
                        IsPlan newIsPlan = getIsPlan(uniqueRows, key);
                        customerChoicesVolume1.getIsPlans().add(newIsPlan);
                    } else {
                        MetricsVolume metricsVolume = getMetricsVolume(uniqueRows, key);
                        isPlan.getMetrics().add(metricsVolume);
                    }
                }
            }
        });
        return initialSetVolumeResponseList;
    }

    private CustomerChoicesVolume getCustomerChoicesVolume(HashMap<VolumeQueryId, List<JsonNode>> uniqueRows, VolumeQueryId key) {
        CustomerChoicesVolume customerChoicesVolume = new CustomerChoicesVolume();
        customerChoicesVolume.setCcId(key.getCc());

        List<IsPlan> isPlanList = new ArrayList<>();
        isPlanList.add(getIsPlan(uniqueRows, key));
        customerChoicesVolume.setIsPlans(isPlanList);
        return customerChoicesVolume;
    }

    private IsPlan getIsPlan(HashMap<VolumeQueryId, List<JsonNode>> uniqueRows, VolumeQueryId key) {
        IsPlan isPlan = new IsPlan();
        isPlan.setInStoreWeek(key.getInStoreWeek());

        MetricsVolume metricsVolume = getMetricsVolume(uniqueRows, key);
        List<MetricsVolume> metricsVolumes = new ArrayList<>();
        metricsVolumes.add(metricsVolume);
        isPlan.setMetrics(metricsVolumes);
        return isPlan;
    }

    private MetricsVolume getMetricsVolume(HashMap<VolumeQueryId, List<JsonNode>> uniqueRows, VolumeQueryId key) {
        MetricsVolume metricsVolume = new MetricsVolume();
        List<JsonNode> currentRows = uniqueRows.get(key);
        HashMap<Integer,StoreDetail> storeMap = new HashMap<>();
        currentRows.stream().forEach(jsonNode -> {
            Integer store =  jsonNode.get("store").intValue();
            Integer qty = Optional.ofNullable(jsonNode.get("is_quantity")).orElse(jsonNode.get("bs_quantity")).intValue() ;
            if(storeMap.containsKey(store)){
                StoreDetail storeDetail = storeMap.get(store);
                storeDetail.setQty(storeDetail.getQty() + qty);
                storeMap.put(store,storeDetail);
            } else {
                storeMap.put(store,new StoreDetail(store,qty));
            }
        });
        metricsVolume.setStores(new ArrayList<>(storeMap.values()));
        metricsVolume.setFixtureAllocation(currentRows.get(0).get("fixtureAllocation").decimalValue());
        metricsVolume.setFixtureType(currentRows.get(0).get("fixtureType").textValue());
        metricsVolume.setQuantity(currentRows.stream().flatMapToInt(jsonNode -> IntStream.of(Optional.ofNullable(jsonNode.get("is_quantity")).orElse(jsonNode.get("bs_quantity")).intValue())).sum());
        metricsVolume.setVolumeClusterId(key.getClusterId());
        return metricsVolume;
    }

    private String findSqlQuery(Long planId, FinelineVolume request) {
        String tableNameSp = getProjectIdSp();
        String tableNameCc = getProjectIdCc();

        if (request.getVolumeDeviationLevel().equals(VdLevelCode.CATEGORY.getDescription())) {
            return getISByVolumeCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl3Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear());
        } else if (request.getVolumeDeviationLevel().equals(VdLevelCode.SUB_CATEGORY.getDescription())) {
            return getISByVolumeSubCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl4Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear());
        } else if (request.getVolumeDeviationLevel().equals(VdLevelCode.FINELINE.getDescription())) {
            return getISByVolumeFinelineClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear());
        }
        throw new RuntimeException("Invalid Deviation Level, Fineline, Subcategory, Category are valid values");
    }
}