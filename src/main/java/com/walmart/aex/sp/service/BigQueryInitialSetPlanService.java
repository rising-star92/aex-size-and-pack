package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.StoreClusterMap;
import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetPackRequest;
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponse;
import com.walmart.aex.sp.dto.currentlineplan.LikeAssociation;
import com.walmart.aex.sp.dto.isVolume.*;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.CommonGCPUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BigQueryInitialSetPlanService {

    private final ObjectMapper objectMapper;
    private final BQFPService bqfpService;
    private final StrategyFetchService strategyFetchService;
    private final BigQuery bigQuery;
    private final LinePlanService linePlanService;
    private final StoreClusterService storeClusterService;
    @ManagedConfiguration
    BigQueryConnectionProperties bigQueryConnectionProperties;

    @ManagedConfiguration
    private GraphQLProperties graphQLProperties;

    public BigQueryInitialSetPlanService(ObjectMapper objectMapper, BQFPService bqfpService,
                                         StrategyFetchService strategyFetchService, LinePlanService linePlanService,
                                         BigQuery bigQuery, StoreClusterService storeClusterService) {
        this.objectMapper = objectMapper;
        this.bqfpService = bqfpService;
        this.strategyFetchService = strategyFetchService;
        this.linePlanService = linePlanService;
        this.bigQuery = bigQuery;
        this.storeClusterService = storeClusterService;
    }

    public List<RFAInitialSetBumpSetResponse> getInitialAndBumpSetDetails(InitialSetPackRequest rfaSizePackRequest) {
        List<RFAInitialSetBumpSetResponse> rfaInitialSetBumpSetResponses = new ArrayList<>();
        List<RFAInitialSetBumpSetResponse> rfaInitialSetBumpSetResponseBs = new ArrayList<>();
        try {
            String projectIdDatasetNameTableNameSp = getProjectIdSp();
            String projectIdDatasetNameTableNameCc = getProjectIdCc();

            String spQueryStringIs = getSizePackIntialSetQueryString(projectIdDatasetNameTableNameCc, projectIdDatasetNameTableNameSp, rfaSizePackRequest.getPlanId(), rfaSizePackRequest.getFinelineNbr());
            String spQueryStringBs = getSizePackBumpSetQueryString(projectIdDatasetNameTableNameSp, rfaSizePackRequest.getPlanId(), rfaSizePackRequest.getFinelineNbr());
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
            BQFPResponse bqfpResponse = bqfpService.getBqfpResponse(rfaSizePackRequest.getPlanId(), rfaSizePackRequest.getFinelineNbr());

            for (RFAInitialSetBumpSetResponse rfaBumpSetResponse : rfaInitialSetBumpSetResponseBs) {
                RFAInitialSetBumpSetResponse rfaRes = new RFAInitialSetBumpSetResponse();
                BumpSet bp = BuyQtyCommonUtil.getBumpSet(bqfpResponse, rfaBumpSetResponse.getProduct_fineline(), rfaBumpSetResponse.getStyle_id(), rfaBumpSetResponse.getCc(), null, null);
                rfaRes.setIn_store_week(BuyQtyCommonUtil.getInStoreWeek(bp));
                rfaRes.setBumpPackNbr(bp.getBumpPackNbr());
                rfaRes.setStyle_id(rfaBumpSetResponse.getStyle_id());
                rfaRes.setCc(rfaBumpSetResponse.getCc());
                rfaRes.setMerch_method(rfaBumpSetResponse.getMerch_method());
                rfaRes.setPack_id(rfaBumpSetResponse.getPack_id());
                rfaRes.setSize(rfaBumpSetResponse.getSize());
                rfaRes.setBumppack_ratio(rfaBumpSetResponse.getBumppack_ratio());
                rfaRes.setBs_quantity(rfaBumpSetResponse.getBs_quantity());
                rfaRes.setUuid(rfaBumpSetResponse.getUuid());
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

    private String getSizePackIntialSetQueryString(String ccTableName, String spTableName, Integer planId, Integer finelineNbr) {
        String prodFineline = planId + "_" + finelineNbr;
        return "WITH MyTable AS ( select product_fineline, reverse( SUBSTR(REVERSE(RFA.cc), STRPOS(REVERSE(RFA.cc), \"_\")+1)) as style_id,RFA.in_store_week, RFA.cc, SP.merch_method, SP.pack_id, SP.UUID AS uuid, SP.size, ARRAY_AGG(DISTINCT SP.store) as stores, (SP.initialpack_ratio) AS initialpack_ratio, SUM(SP.is_quantity) AS is_quantity from (select trim(cc) as cc,CAST(store AS INTEGER) as store,min(week) as in_store_week FROM `" + ccTableName + "`as RFA where plan_id_partition=" + planId + " and fineline=" + finelineNbr + " and final_alloc_space>0 group by cc,store order by cc, in_store_week, store )as RFA join (SELECT SP.ProductFineline as product_fineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackID as pack_id, SP.MerchMethod as merch_method, SP.UUID as uuid, SP.size, SP.SPBumpSetPackSizeRatio as bumppack_ratio, SP.SPInitialSetPackSizeRatio as initialpack_ratio, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity FROM `" + spTableName + "` AS SP where SP.ProductFineline = '" + prodFineline + "' and SPInitialSetPackSizeRatio >0 ) as SP on RFA.store = SP.store and RFA.cc = SP.cc GROUP BY product_fineline,RFA.in_store_week,RFA.cc,SP.merch_method, uuid, SP.size,SP.pack_id ,initialpack_ratio order by product_fineline,RFA.in_store_week,RFA.cc,SP.merch_method,SP.size, uuid, SP.pack_id,initialpack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
    }

    private String getSizePackBumpSetQueryString(String spTableName, Integer planId, Integer finelineNbr) {
        String prodFineline = planId + "_" + finelineNbr + PERCENT;
        return "WITH MyTable AS ( select distinct SP.ProductFineline as product_fineline, reverse( SUBSTR(REVERSE(ProductCustomerChoice), STRPOS(REVERSE(ProductCustomerChoice), \"_\")+1)) as style_id, SP.ProductCustomerChoice as cc, SP.MerchMethod AS merch_method, SP.UUID AS uuid, SP.SPPackID as pack_id,SP.Size as size, (SP.SPBumpSetPackSizeRatio) AS bumppack_ratio, SUM(SP.SPPackBumpOutput) AS bs_quantity FROM `" + spTableName + "` AS SP where SP.ProductFineline LIKE '" + prodFineline + "' and SPBumpSetPackSizeRatio>0 GROUP BY product_fineline,style_id,cc,merch_method,size,pack_id,uuid,bumppack_ratio order by product_fineline,style_id,cc,merch_method,size,pack_id,uuid,bumppack_ratio ) SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable";
    }

    private String getISByVolumeFinelineClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, String interval, Integer fiscalYear, Integer catNbr, Integer subCatNbr, LikeAssociation likeAssociation) {
        String prodFineline = planId + "_" + finelineNbr;
        String finelineTable = CommonGCPUtil.formatTable(bigQueryConnectionProperties.getAnalyticsData(), bigQueryConnectionProperties.getFinelineVolumeCluster());
          /*
        Min week is added as a join condition to RFA. This is to prevent any inconsistent allocations in RFA.
        For example- CC 1 in store 400 could have only 1 Fixture type and allocation. RFA , as of 14 Dec 2021, could allocate a new fixture
        type and allocation mid season. This is not an expected behaviour. To shield us from this inconsistency, adding a min week check.
        Adding filter on dept_subcatg_nbr, dept_subcatg_nbr to get records for dept.
         */
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "RFA.in_store_week,\n" +
                "RFA.cc,\n" +
                "RFA.style_nbr,\n" +
                "SUM(SP.is_quantity) AS is_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
                " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
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
                "as RFA join "+
                "(\n" +
                "SELECT trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackInitialSetOutput as is_quantity, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline = '" + prodFineline + "' and SPInitialSetPackSizeRatio >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                CommonGCPUtil.getFinelineVolumeClusterQuery(finelineTable, catNbr, subCatNbr, finelineNbr, interval, fiscalYear, likeAssociation) +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week,RFA.style_nbr, RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.in_store_week,RFA.style_nbr,RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }

    private String getISByVolumeSubCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer subCatNbr, String analyticsData, String interval, Integer fiscalYear, Integer catNbr) {
        String prodFineline = planId + "_" + finelineNbr;
          /*
        Min week is added as a join condition to RFA. This is to prevent any inconsistent allocations in RFA.
        For example- CC 1 in store 400 could have only 1 Fixture type and allocation. RFA , as of 14 Dec 2021, could allocate a new fixture
        type and allocation mid season. This is not an expected behaviour. To shield us from this inconsistency, adding a min week check.
        Adding filter on dept_catg_nbr to get  records for dept.
         */
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "RFA.in_store_week,\n" +
                "RFA.cc,\n" +
                "RFA.style_nbr,\n" +
                "SUM(SP.is_quantity) AS is_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
                " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
                "(select fineline, store, allocated, final_pref from ("+
                "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
                "(select * from " + ccTableName  +
                " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
                " where rw_nbr = 1) fl_alloc" +
                " inner join " +
                " (select store, fineline, style_nbr,cc, min(week) as in_store_week" +
                " FROM " + ccTableName+
                " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, style_nbr, cc) cc_week" +
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
                "select distinct scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_subcategory_cluster` scc join `"+analyticsData+".svg_subcategory` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.dept_subcatg_nbr = scc.dept_subcatg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_catg_nbr = " + catNbr + " and sc.dept_subcatg_nbr = " + subCatNbr + " and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY RFA.in_store_week, RFA.style_nbr, RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.in_store_week, RFA.style_nbr, RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
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
                "RFA.style_nbr,\n" +
                "RFA.cc,\n" +
                "SUM(SP.is_quantity) AS is_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
                " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store,cc_week.in_store_week,  allocated as fixtureAllocation,  final_pref as fixtureType from (" +
                "(select fineline, store, allocated, final_pref from ("+
                "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
                "(select * from " + ccTableName  +
                " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
                " where rw_nbr = 1) fl_alloc" +
                " inner join " +
                " (select store, fineline, style_nbr, cc, min(week) as in_store_week" +
                " FROM " + ccTableName+
                " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, style_nbr, cc) cc_week" +
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
                "GROUP BY RFA.in_store_week, RFA.style_nbr, RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.in_store_week,RFA.style_nbr, RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }
    /*
        TODO add weeks back in the query when consistent RFA allocation is available.
        Need to revisit when there are mulitple bump weeks . This is a very point in time solution to use only 1 bump week
        Change for S4
        Adding filter on dept_catg_nbr to get  records for dept.
         */
    private String getBumpQTYVolumeSubCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer subCatNbr, String analyticsData,String interval, Integer fiscalYear, Integer catNbr) {
        String prodFineline = planId + "_" + finelineNbr + PERCENT;
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "SP.productFineline,\n" +
                "RFA.cc,\n" +
                "RFA.style_nbr,\n" +
                "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
                " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr,cast (cc_week.store as INT64) as store, allocated as fixtureAllocation,  final_pref as fixtureType from (" +
                "(select fineline, store, allocated, final_pref from ("+
                "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
                "(select * from " + ccTableName  +
                " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
                " where rw_nbr = 1) fl_alloc" +
                " inner join " +
                " (select store, fineline, style_nbr, cc" +
                " FROM " + ccTableName+
                " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, style_nbr, cc) cc_week" +
                " on fl_alloc.fineline = cc_week.fineline" +
                " and fl_alloc.store = cc_week.store )" +
                ")"+
                "as RFA join "+
                "(\n" +
                "SELECT SP.ProductFineline as productFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline LIKE '" + prodFineline + "' and SPPackBumpOutput >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select distinct scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_subcategory_cluster` scc join `"+analyticsData+".svg_subcategory` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.dept_subcatg_nbr = scc.dept_subcatg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_catg_nbr = " + catNbr + " and sc.dept_subcatg_nbr = " + subCatNbr + " and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY SP.productFineline,RFA.style_nbr,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.style_nbr,RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }
    /*
    TODO add weeks back in the query when consistent RFA allocation is available.
    Need to revisit when there are mulitple bump weeks . This is a very point in time solution to use only 1 bump week
    Change for S4
     */
    private String getBumpQTYVolumeCatClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr, Integer catNbr, String analyticsData,String interval, Integer fiscalYear) {
        String prodFineline = planId + "_" + finelineNbr + PERCENT;
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "SP.productFineline,\n" +
                "RFA.cc,\n" +
                "RFA.style_nbr,\n" +
                "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
                " select distinct trim(cc_week.cc) as cc,trim(cc_week.style_nbr) as style_nbr, cast (cc_week.store as INT64) as store, allocated as fixtureAllocation,  final_pref as fixtureType from (" +
                "(select fineline, store, allocated, final_pref from ("+
                "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
                "(select * from " + ccTableName  +
                " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
                " where rw_nbr = 1) fl_alloc" +
                " inner join " +
                " (select store, fineline, style_nbr, cc" +
                " FROM " + ccTableName+
                " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline, style_nbr, cc) cc_week" +
                " on fl_alloc.fineline = cc_week.fineline" +
                " and fl_alloc.store = cc_week.store )" +
                ")"+
                "as RFA join "+
                "(\n" +
                "SELECT SP.ProductFineline as productFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline LIKE '" + prodFineline + "' and SPPackBumpOutput >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                "select scc.store_nbr as store,scc.cluster_id  as clusterId  from `" + analyticsData + ".svg_category_cluster` scc join `"+analyticsData+".svg_category` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and sc.dept_catg_nbr = scc.dept_catg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year where sc.dept_catg_nbr = " + catNbr +" and  sc.season = '"+interval+"' and sc.fiscal_year = " +fiscalYear + " \n" +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY SP.productFineline,RFA.style_nbr,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.style_nbr,RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }

    /*
   TODO  add weeks back in the query when consistent RFA allocation is available.
    Need to revisit when there are mulitple bump weeks . This is a very point in time solution to use only 1 bump week
    Change for S4
    Adding filter on dept_subcatg_nbr, dept_subcatg_nbr to get  records for dept.
     */
    private String getBumpQTYVolumeFinelineClusterQuery(String ccTableName, String spTableName, Integer planId, Integer finelineNbr,String interval, Integer fiscalYear, Integer catNbr, Integer subCatNbr, LikeAssociation likeAssociation) {
        String prodFineline = planId + "_" + finelineNbr + PERCENT;
        String finelineTable = bigQueryConnectionProperties.getAnalyticsData() + "." + bigQueryConnectionProperties.getFinelineVolumeCluster();
        return "WITH MyTable AS (\n" +
                "select distinct\n" +
                "SP.productFineline,\n" +
                "RFA.cc,\n" +
                "RFA.style_nbr,\n" +
                "SUM(SP.bs_quantity) AS bs_quantity ,\n" +
                "CL.store,\n" +
                "CL.clusterId,\n" +
                "RFA.fixtureAllocation,\n" +
                "RFA.fixtureType\n" +
                "from (\n" +
                " select distinct trim(cc_week.cc) as cc, trim(cc_week.style_nbr) as style_nbr,cast (cc_week.store as INT64) as store, allocated as fixtureAllocation,  final_pref as fixtureType from (" +
                "(select fineline, store, allocated, final_pref from ("+
                "select fineline, store, week, allocated, final_pref, row_number() over(PARTITION BY fineline, store order by week) as rw_nbr from "+
                "(select * from " + ccTableName  +
                " where plan_id_partition =" +planId+ " and final_alloc_space > 0 and fineline =" + finelineNbr +"))" +
                " where rw_nbr = 1) fl_alloc" +
                " inner join " +
                " (select store, fineline, style_nbr,cc" +
                " FROM " + ccTableName+
                " where plan_id_partition =" + planId+ " and final_alloc_space > 0 and fineline=" +finelineNbr + " group by store, fineline,style_nbr, cc) cc_week" +
                " on fl_alloc.fineline = cc_week.fineline" +
                " and fl_alloc.store = cc_week.store )" +
                ")"+
                "as RFA join "+
                "(\n" +
                "SELECT SP.ProductFineline as productFineline, trim(SP.ProductCustomerChoice) as cc,SP.store, SP.SPPackBumpOutput as bs_quantity\n" +
                "FROM `" + spTableName + "` AS SP where ProductFineline LIKE '" + prodFineline + "' and SPPackBumpOutput >0\n" +
                ") as SP\n" +
                "on RFA.store = SP.store and RFA.cc = SP.cc\n" +
                "join (\n" +
                CommonGCPUtil.getFinelineVolumeClusterQuery(finelineTable, catNbr, subCatNbr, finelineNbr, interval, fiscalYear, likeAssociation) +
                ") as CL\n" +
                "on RFA.store = CL.store\n" +
                "GROUP BY SP.productFineline,RFA.style_nbr,RFA.cc, CL.clusterId,CL.store ,RFA.fixtureAllocation, RFA.fixtureType order by RFA.style_nbr,RFA.cc,CL.clusterId,CL.store, RFA.fixtureAllocation, RFA.fixtureType\n" +
                ") SELECT TO_JSON_STRING(rfaTable) AS json FROM MyTable AS rfaTable\n";
    }

    public List<InitialSetVolumeResponse> getInitialAndBumpSetDetailsByVolumeCluster(Long planId, FinelineVolume request) throws InterruptedException, SizeAndPackException {
//      TODO: Remove this code when commit report is ready with the change, i.e. volu,e deviation should be always fetched from the Strategy Service
        String volumeDeviationLevel = request.getVolumeDeviationLevel();
        String sqlQuery = null;
        LikeAssociation likeAssociation = linePlanService.getLikeAssociation(planId, request.getFinelineNbr());
        if(null == volumeDeviationLevel || volumeDeviationLevel.isBlank()){
            StrategyVolumeDeviationResponse volumeDeviationResponse = strategyFetchService.getStrategyVolumeDeviation(planId, request.getFinelineNbr());
            if (null != volumeDeviationResponse && !volumeDeviationResponse.getFinelines().isEmpty()) {
                FinelineVolumeDeviationDto finelineVolumeDeviationDto = volumeDeviationResponse.getFinelines().get(0);
                volumeDeviationLevel = finelineVolumeDeviationDto.getVolumeDeviationLevel();
                if (StringUtils.isNotEmpty(volumeDeviationLevel)) {
                    sqlQuery = findSqlQuery(planId, request, volumeDeviationLevel, likeAssociation);
                } else {
                    log.error("Error Occurred while fetching Strategy Volume Deviation level for plan ID {} and fineline {}", planId, request.getFinelineNbr());
                    throw new SizeAndPackException("Error Occurred while fetching Strategy Volume Deviation level for plan ID " + planId);
                }
            }else{
                log.error("Error Occurred while fetching Strategy Volume Deviation Response for plan ID {} and fineline {}", planId, request.getFinelineNbr());
                throw new SizeAndPackException("Error Occurred while fetching Strategy Volume Deviation Response for plan ID " + planId);
            }
        }else{
            sqlQuery = findSqlQuery(planId, request,volumeDeviationLevel, likeAssociation);
        }

        log.debug("sqlQuery: {}", sqlQuery);
        QueryJobConfiguration queryConfigIs = QueryJobConfiguration.newBuilder(sqlQuery).build();
        TableResult resultsIs = bigQuery.query(queryConfigIs);
        HashMap<VolumeQueryId, List<VolumeClusterDTO>> uniqueRows = new HashMap<>();

        resultsIs.iterateAll().forEach(rows -> rows.forEach(row -> {
            try {
                VolumeClusterDTO volumeClusterDTO = objectMapper.readValue(row.getStringValue(), VolumeClusterDTO.class);
                VolumeQueryId volumeQueryId = new VolumeQueryId(volumeClusterDTO.getCc(), volumeClusterDTO.getStyle_nbr(), volumeClusterDTO.getClusterId(), volumeClusterDTO.getIn_store_week(), volumeClusterDTO.getFixtureType(), volumeClusterDTO.getFixtureAllocation());
                if (uniqueRows.containsKey(volumeQueryId)) {
                    uniqueRows.get(volumeQueryId).add(volumeClusterDTO);
                } else {
                    List<VolumeClusterDTO> volumeClusterDTOList = new ArrayList<>();
                    volumeClusterDTOList.add(volumeClusterDTO);
                    uniqueRows.put(volumeQueryId, volumeClusterDTOList);
                }
            } catch (JsonProcessingException e) {
                log.error("Error Occurred while fetching IS Information", e);
            }
        }));

        try{
            BQFPResponse bqfpResponse = bqfpService.getBqfpResponse(planId.intValue(), request.getFinelineNbr());
            if (StringUtils.isNotEmpty(volumeDeviationLevel)) {
                sqlQuery = findBumpSqlQuery(planId, request, volumeDeviationLevel, likeAssociation);
                log.debug("sqlQuery: {}", sqlQuery);
            } else {
                log.error("Error Occurred while fetching Strategy Volume Deviation level for plan ID {} and fineline {}", planId, request.getFinelineNbr());
                throw new SizeAndPackException("Error Occurred while fetching Strategy Volume Deviation level for plan ID " + planId);
            }
            queryConfigIs = QueryJobConfiguration.newBuilder(sqlQuery).build();
            resultsIs = bigQuery.query(queryConfigIs);
            resultsIs.iterateAll().forEach(rows -> rows.forEach(row -> {
                try {
                    VolumeClusterDTO volumeClusterDTO = objectMapper.readValue(row.getStringValue(), VolumeClusterDTO.class);
                    BumpSet bp = BuyQtyCommonUtil.getBumpSet(bqfpResponse, volumeClusterDTO.getProductFineline(), volumeClusterDTO.getStyle_nbr(), volumeClusterDTO.getCc(), volumeClusterDTO.getFixtureType(), volumeClusterDTO.getClusterId());
                    if (StringUtils.isNotEmpty(bp.getWeekDesc())) {
                        String bsInstoreweek = BuyQtyCommonUtil.getInStoreWeek(bp);
                        VolumeQueryId volumeQueryId = new VolumeQueryId(volumeClusterDTO.getCc(), volumeClusterDTO.getStyle_nbr(), volumeClusterDTO.getClusterId(), Integer.valueOf(bsInstoreweek), volumeClusterDTO.getFixtureType(), volumeClusterDTO.getFixtureAllocation());
                        if (uniqueRows.containsKey(volumeQueryId)) {
                            uniqueRows.get(volumeQueryId).add(volumeClusterDTO);
                        } else {
                            List<VolumeClusterDTO> volumeClusterDTOList = new ArrayList<>();
                            volumeClusterDTOList.add(volumeClusterDTO);
                            uniqueRows.put(volumeQueryId, volumeClusterDTOList);
                        }
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }));
        }catch (Exception e){
            log.error("Error Occured while fetch Bump Pack information", e);
        }

        StoreClusterMap storeClusterMap = storeClusterService.fetchPOStoreClusterGrouping(request.getInterval(),
                String.valueOf(request.getFiscalYear()));

        return formatUniqueRows(request, uniqueRows, storeClusterMap);
    }

    private String findBumpSqlQuery(Long planId, FinelineVolume request, String volumeDeviationLevel, LikeAssociation likeAssociation) {
        String tableNameSp = getProjectIdSp();
        String tableNameCc = getProjectIdCc();

        if (volumeDeviationLevel.equals(VdLevelCode.CATEGORY.getDescription())) {
            return getBumpQTYVolumeCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl3Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear());
        } else if (volumeDeviationLevel.equals(VdLevelCode.SUB_CATEGORY.getDescription())) {
            return getBumpQTYVolumeSubCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl4Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear(),request.getLvl3Nbr());
        } else if (volumeDeviationLevel.equals(VdLevelCode.FINELINE.getDescription())) {
            log.debug("LikeFineline details - planId: {} | originalFineline: {} | likeFineline: {}", planId, request.getFinelineNbr(), null != likeAssociation ? likeAssociation.getId() : null);
            return getBumpQTYVolumeFinelineClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getInterval(), request.getFiscalYear(), request.getLvl3Nbr(), request.getLvl4Nbr(), likeAssociation);
        }
        throw new RuntimeException("Invalid Deviation Level, Fineline, Subcategory, Category are valid values");
    }

    private List<InitialSetVolumeResponse> formatUniqueRows(FinelineVolume request, HashMap<VolumeQueryId,
            List<VolumeClusterDTO>> uniqueRows, StoreClusterMap storeClusterMap) {
        List<InitialSetVolumeResponse> initialSetVolumeResponseList = new ArrayList<>();
        uniqueRows.keySet().forEach(key -> {
            String styleId = key.getStyleNbr();
            InitialSetVolumeResponse initialSetVolume = initialSetVolumeResponseList.stream().filter(initialSetVolumeResponse -> initialSetVolumeResponse.getStyleId().equals(styleId) && initialSetVolumeResponse.getFinelineNbr() == request.getFinelineNbr()).findFirst().orElse(new InitialSetVolumeResponse());
            if (initialSetVolume.getStyleId() == null) {
                initialSetVolume.setStyleId(styleId);
                initialSetVolume.setFinelineNbr(request.getFinelineNbr());
                List<CustomerChoicesVolume> customerChoicesVolumes = new ArrayList<>();
                CustomerChoicesVolume customerChoicesVolume = getCustomerChoicesVolume(uniqueRows, key, storeClusterMap);

                customerChoicesVolumes.add(customerChoicesVolume);
                initialSetVolume.setCustomerChoices(customerChoicesVolumes);
                initialSetVolumeResponseList.add(initialSetVolume);
            } else {
                CustomerChoicesVolume customerChoicesVolume1 = initialSetVolume.getCustomerChoices().stream().filter(customerChoicesVolume2 -> customerChoicesVolume2.getCcId().equals(key.getCc())).findFirst().orElse(new CustomerChoicesVolume());
                if (customerChoicesVolume1.getCcId() == null) {
                    customerChoicesVolume1.setCcId(key.getCc());
                    CustomerChoicesVolume customerChoicesVolume = getCustomerChoicesVolume(uniqueRows, key, storeClusterMap);
                    initialSetVolume.getCustomerChoices().add(customerChoicesVolume);
                } else {
                    IsPlan isPlan = customerChoicesVolume1.getIsPlans().stream().filter(isPlan1 -> Objects.equals(isPlan1.getInStoreWeek(), key.getInStoreWeek())).findFirst().orElse(new IsPlan());
                    if (isPlan.getMetrics() == null) {
                        IsPlan newIsPlan = getIsPlan(uniqueRows, key, storeClusterMap);
                        customerChoicesVolume1.getIsPlans().add(newIsPlan);
                    } else {
                        MetricsVolume metricsVolume = getMetricsVolume(uniqueRows, key, storeClusterMap);
                        isPlan.getMetrics().add(metricsVolume);
                    }
                }
            }
        });
        return initialSetVolumeResponseList;
    }

    private CustomerChoicesVolume getCustomerChoicesVolume(HashMap<VolumeQueryId, List<VolumeClusterDTO>> uniqueRows,
                                                           VolumeQueryId key, StoreClusterMap storeClusterMap) {
        CustomerChoicesVolume customerChoicesVolume = new CustomerChoicesVolume();
        customerChoicesVolume.setCcId(key.getCc());

        List<IsPlan> isPlanList = new ArrayList<>();
        isPlanList.add(getIsPlan(uniqueRows, key, storeClusterMap));
        customerChoicesVolume.setIsPlans(isPlanList);
        return customerChoicesVolume;
    }

    private IsPlan getIsPlan(HashMap<VolumeQueryId, List<VolumeClusterDTO>> uniqueRows, VolumeQueryId key,
                             StoreClusterMap storeClusterMap) {
        IsPlan isPlan = new IsPlan();
        isPlan.setInStoreWeek(key.getInStoreWeek());

        MetricsVolume metricsVolume = getMetricsVolume(uniqueRows, key, storeClusterMap);
        List<MetricsVolume> metricsVolumes = new ArrayList<>();
        metricsVolumes.add(metricsVolume);
        isPlan.setMetrics(metricsVolumes);
        return isPlan;
    }

    private MetricsVolume getMetricsVolume(HashMap<VolumeQueryId, List<VolumeClusterDTO>> uniqueRows, VolumeQueryId key,
                                           StoreClusterMap storeClusterMap) {
        MetricsVolume metricsVolume = new MetricsVolume();
        List<VolumeClusterDTO> currentRows = uniqueRows.get(key);
        HashMap<Integer,StoreDetail> storeMap = new HashMap<>();
        currentRows.forEach(volumeClusterDTO -> {
            Integer store = volumeClusterDTO.getStore();
            Integer qty = Optional.ofNullable(volumeClusterDTO.getIs_quantity()).orElse(volumeClusterDTO.getBs_quantity()) ;
            if(storeMap.containsKey(store)){
                StoreDetail storeDetail = storeMap.get(store);
                storeDetail.setQty(storeDetail.getQty() + qty);
                storeMap.put(store,storeDetail);
            } else {
                storeMap.put(store,new StoreDetail(store, Optional.ofNullable(storeClusterMap)
                        .map(storeCluster -> storeCluster.getKey(store))
                        .orElse(null), qty));
            }
        });
        metricsVolume.setStores(new ArrayList<>(storeMap.values()));
        metricsVolume.setFixtureAllocation(BigDecimal.valueOf(currentRows.get(0).getFixtureAllocation()));
        metricsVolume.setFixtureType(currentRows.get(0).getFixtureType());
        metricsVolume.setQuantity(currentRows.stream().flatMapToInt(volumeClusterDTO -> IntStream.of(Optional.ofNullable(volumeClusterDTO.getIs_quantity()).orElse(volumeClusterDTO.getBs_quantity()))).sum());
        metricsVolume.setVolumeClusterId(key.getClusterId());
        return metricsVolume;
    }

    private String findSqlQuery(Long planId, FinelineVolume request, String volumeDeviationLevel, LikeAssociation likeAssociation) {
        String tableNameSp = getProjectIdSp();
        String tableNameCc = getProjectIdCc();

        if (volumeDeviationLevel.equals(VdLevelCode.CATEGORY.getDescription())) {
            return getISByVolumeCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl3Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear());
        } else if (volumeDeviationLevel.equals(VdLevelCode.SUB_CATEGORY.getDescription())) {
            return getISByVolumeSubCatClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getLvl4Nbr(), bigQueryConnectionProperties.getAnalyticsData(),request.getInterval(),request.getFiscalYear(),request.getLvl3Nbr());
        } else if (volumeDeviationLevel.equals(VdLevelCode.FINELINE.getDescription())) {
            log.debug("LikeFineline details - planId: {} | originalFineline: {} | likeFineline: {}", planId, request.getFinelineNbr(), null != likeAssociation ? likeAssociation.getId() : null);
            return getISByVolumeFinelineClusterQuery(tableNameCc, tableNameSp, Math.toIntExact(planId), request.getFinelineNbr(), request.getInterval(), request.getFiscalYear(), request.getLvl3Nbr(), request.getLvl4Nbr(), likeAssociation);
        }
        throw new RuntimeException("Invalid Deviation Level, Fineline, Subcategory, Category are valid values");
    }
}