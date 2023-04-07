package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.BumpSet;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.storedistribution.PackData;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionDTO;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionData;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;

import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
public class BigQueryStoreDistributionService {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private final BQFPService bqfpService;
	private final StrategyFetchService strategyFetchService;
	private final GraphQLService graphQLService;

	@ManagedConfiguration
	private GraphQLProperties graphQLProperties;

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public BigQueryStoreDistributionService(BQFPService bqfpService, StrategyFetchService strategyFetchService, GraphQLService graphQLService) {
		this.bqfpService = bqfpService;
		this.strategyFetchService = strategyFetchService;
		this.graphQLService = graphQLService;
	}

	public StoreDistributionData getStoreDistributionData(PackData packData) {
		StoreDistributionData storeDistributionData = new StoreDistributionData();
		List<StoreDistributionDTO> storeDistributionList = new ArrayList<>();

		try {
			String projectId = bigQueryConnectionProperties.getRFAProjectId();
			String parquetDatasetName = bigQueryConnectionProperties.getRFADataSetName();
			String parquetTable = bigQueryConnectionProperties.getRFACCStageTable();
			String assortmentMLProjectId = bigQueryConnectionProperties.getMLProjectId();
			String packOptDatasetName = bigQueryConnectionProperties.getMLDataSetName();
			String packOptTable = bigQueryConnectionProperties.getRFASPPackOptTableName();
			String parquetTableName = projectId + "." + parquetDatasetName + "." + parquetTable;
			String packOptOutputTableName = assortmentMLProjectId + "." + packOptDatasetName + "." + packOptTable;
			Long planId = packData.getPlanId();
			Integer fineline = packData.getFinelineNbr();
			Long inStoreWeek = packData.getInStoreWeek();
			String packId = packData.getPackId();
			String planAndFineline = planId + "_" + fineline + PERCENT;
			BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
			if (!Boolean.parseBoolean(bigQueryConnectionProperties.getStoreDistributionFeatureFlag())) {
				String storeDistributionQuery = getStoreDistributionQuery(parquetTableName, packOptOutputTableName,
						planAndFineline, planId, fineline, inStoreWeek, packId);
				QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(storeDistributionQuery).build();
				TableResult results = bigQuery.query(queryConfig);
				results.iterateAll().forEach(rows -> rows.forEach(row -> {
					try {
						StoreDistributionDTO storeDistributionDTO = objectMapper.readValue(row.getValue().toString(),
								StoreDistributionDTO.class);
						storeDistributionList.add(storeDistributionDTO);
					} catch (JsonProcessingException e) {
						log.error("Error while mapping the gcp table response data \n", e);
					}
				}));
			} else {
				String sqlQuery = getInitialSetQuery(parquetTableName, packOptOutputTableName,
						planAndFineline, planId, fineline, packId, inStoreWeek);

				QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(sqlQuery).build();
				TableResult results = bigQuery.query(queryConfig);
				results.iterateAll().forEach(rows -> rows.forEach(row -> {
					try {
						StoreDistributionDTO storeDistributionDTO = objectMapper.readValue(row.getValue().toString(),
								StoreDistributionDTO.class);
						storeDistributionList.add(storeDistributionDTO);
					} catch (JsonProcessingException e) {
						log.error("Error while mapping the gcp table response data \n", e);
					}
				}));
				if (storeDistributionList.isEmpty()) {
					sqlQuery = getBumpSetStoreDistributionQuery(parquetTableName, packOptOutputTableName,
							planAndFineline, planId, fineline, packId);
					if (StringUtils.isNotEmpty(sqlQuery)) {
						queryConfig = QueryJobConfiguration.newBuilder(sqlQuery).build();
						results = bigQuery.query(queryConfig);
						results.iterateAll().forEach(rows -> rows.forEach(row -> {
							try {
								StoreDistributionDTO storeDistributionDTO = objectMapper.readValue(row.getValue().toString(),
										StoreDistributionDTO.class);
								storeDistributionList.add(storeDistributionDTO);
							} catch (JsonProcessingException e) {
								log.error("Error while mapping the gcp table response data \n", e);
							}
						}));
						BQFPResponse bqfpResponse = bqfpService.getBqfpResponse(Math.toIntExact(planId), fineline);
						for (StoreDistributionDTO storeDistribution : storeDistributionList) {
							BumpSet bumpSet = BuyQtyCommonUtil.getBumpSet(bqfpResponse, storeDistribution.getProductFineline(), storeDistribution.getStyleNbr(),
									storeDistribution.getCc(), storeDistribution.getFixtureType(), storeDistribution.getClusterId());
							String bSInStoreWeek = BuyQtyCommonUtil.getInStoreWeek(bumpSet);
							if (StringUtils.isNotEmpty(bSInStoreWeek))
								storeDistribution.setInStoreWeek(Long.valueOf(bSInStoreWeek));
						}
						// Filtering result based on the inStoreWeek value passed down in request
						storeDistributionList.removeIf(store -> null == store.getInStoreWeek() || !store.getInStoreWeek().equals(packData.getInStoreWeek()));
					}
				}
			}
			if (!storeDistributionList.isEmpty()) {
				storeDistributionData.setStoreDistributionList(storeDistributionList);
				log.info("Query performed successfully.");
			}
		} catch (Exception e) {
			log.error("Exception details are ", e);
		}

		return storeDistributionData;
	}

	private String getPlanDescById(Integer planId) throws SizeAndPackException {
		Map<String, String> headers = new HashMap<>();
		headers.put(WM_CONSUMER_ID, graphQLProperties.getPlanDefinitionConsumerId());
		headers.put(WM_SVC_NAME, graphQLProperties.getPlanDefinitionConsumerName());
		headers.put(WM_SVC_ENV, graphQLProperties.getPlanDefinitionConsumerEnv());
		Map<String, Object> data = new HashMap<>();
		data.put("planId", planId);
		GraphQLResponse graphQLResponse;
		try {
			graphQLResponse = graphQLService.post(graphQLProperties.getPlanDefinitionUrl(),
					graphQLProperties.getPlanDefinitionQuery(),
					headers, data);
			return graphQLResponse.getData().getGetPlanById().getPlanDesc();
		} catch (SizeAndPackException e) {
			log.error("An Exception occurred while fetching the result from LinePlanWeeks {}", e.getMessage());
			throw new SizeAndPackException("An Exception occurred while fetching the result from LinePlanWeeks");
		}
	}

	private String getBumpSetStoreDistributionQuery(String parquetTableName, String packOptOutputTableName, String planAndFineline, Long planId, Integer fineline, String packId) throws SizeAndPackException {
		StrategyVolumeDeviationResponse volumeDeviationResponse = strategyFetchService.getStrategyVolumeDeviation(planId, fineline);
		String planDesc = getPlanDescById(Math.toIntExact(planId));
		String season = planDesc.substring(0, 2);
		Integer fiscalYear = Integer.valueOf(planDesc.substring(planDesc.length() - 4));
		if (null != volumeDeviationResponse && !volumeDeviationResponse.getFinelines().isEmpty()) {
			FinelineVolumeDeviationDto finelineVolumeDeviationDto = volumeDeviationResponse.getFinelines().get(0);
			String analyticsQuery = getAnalyticsQueryByDeviation(finelineVolumeDeviationDto, season, fiscalYear);
			return getStoreDistributionBumpQuery(parquetTableName, packOptOutputTableName, planAndFineline, planId, fineline, packId, analyticsQuery);
		}
		return null;
	}

	private String getInitialSetQuery(String parquetTableName, String packOptOutputTableName,
									  String planAndFineline, Long planId, Integer fineline, String packId, Long inStoreWeek) {
		return "WITH MyTable AS " +
				"(select distinct productFineline, " +
				"RFA.finelineNbr, " +
				"RFA.styleNbr, " +
				"RFA.inStoreWeek, " +
				"SP.packId, " +
				"SP.store, " +
				"SP.packMultiplier " +
				"from (select fineline as finelineNbr, reverse( SUBSTR(REVERSE(trim(cc)), STRPOS(REVERSE(trim(cc)), \"_\")+1)) as styleNbr,trim(cc) as cc, CAST(store AS INTEGER) as store, min(week) as inStoreWeek " +
				"from `" + parquetTableName + "` as RFA where plan_id_partition=" + planId + " and fineline=" + fineline + " and final_alloc_space>0 " +
				"group by finelineNbr, styleNbr,cc,store having inStoreWeek=" + inStoreWeek + " order by finelineNbr, styleNbr,cc, inStoreWeek, store ) as RFA " +
				"join " +
				"(SELECT SP.ProductFineline as productFineline, trim(SP.ProductCustomerChoice)as cc, SP.store, SP.SPPackID as packId, SP.MerchMethod as merch_method, SP.size, SP.SPInitialSetPackMultiplier as packMultiplier " +
				"from `" + packOptOutputTableName + "` as SP where ProductFineline LIKE '" + planAndFineline + "' and SPInitialSetPackMultiplier>0 and " +
				"SP.SPPackID='" + packId + "') as SP on RFA.store = SP.store and RFA.cc = SP.cc " +
				"group by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier " +
				"order by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier) " +
				"SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable";
	}

	private String getStoreDistributionBumpQuery(String parquetTableName, String packOptOutputTableName, String planAndFineline, Long planId, Integer fineline, String packId, String analyticsQuery) {
		return "WITH MyTable AS " +
				"(select distinct productFineline, " +
				"RFA.finelineNbr, " +
				"RFA.styleNbr, " +
				"RFA.inStoreWeek, " +
				"SP.packId, " +
				"SP.store, " +
				"SP.packMultiplier, " +
				"CL.clusterId, " +
				"RFA.cc, " +
				"RFA.fixtureAllocation, " +
				"RFA.fixtureType " +
				"from " +
				"(select fineline as finelineNbr, reverse( SUBSTR(REVERSE(trim(cc)), STRPOS(REVERSE(trim(cc)), \"_\")+1)) as styleNbr, trim(cc) as cc, CAST(store AS INTEGER) as store, min(week) as inStoreWeek, " +
				"allocated as fixtureAllocation, final_pref as fixtureType from `" + parquetTableName + "` as RFA where plan_id_partition=" + planId + " and fineline=" + fineline + " and final_alloc_space>0 " +
				"group by finelineNbr, styleNbr, cc, store, fixtureAllocation, fixtureType order by finelineNbr, styleNbr, cc, store, fixtureAllocation, fixtureType ) as RFA " +
				"join " +
				"(SELECT SP.ProductFineline as productFineline, trim(SP.ProductCustomerChoice)as cc, SP.store, SP.SPPackID as packId, SP.MerchMethod as merch_method, SP.size, SP.SPBumpSetPackMultiplier as packMultiplier " +
				"from `" + packOptOutputTableName + "` as SP where ProductFineline LIKE '" + planAndFineline + "' and SPBumpSetPackMultiplier>0 and SP.SPPackID='" + packId + "') as SP " +
				"on RFA.store = SP.store and RFA.cc = SP.cc " +
				"join (" + analyticsQuery + ") as CL on RFA.store = CL.store\n" +
				"group by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier, CL.clusterId, RFA.cc, RFA.fixtureAllocation, RFA.fixtureType " +
				"order by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier, CL.clusterId, RFA.cc, RFA.fixtureAllocation, RFA.fixtureType) " +
				"SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable";
	}

	private String getAnalyticsQueryByDeviation(FinelineVolumeDeviationDto finelineVolumeDeviationDto, String season, Integer fiscalYear) throws SizeAndPackException {
		if (finelineVolumeDeviationDto.getVolumeDeviationLevel().equals(VdLevelCode.CATEGORY.getDescription()))
			return "select cc.store_nbr as store, cc.cluster_id as clusterId from `" + bigQueryConnectionProperties.getAnalyticsData() + ".svg_category_cluster` cc " +
					"join `" + bigQueryConnectionProperties.getAnalyticsData() + ".svg_category` c on c.cluster_id = cc.cluster_id and c.dept_nbr = cc.dept_nbr and " +
					"c.dept_catg_nbr = cc.dept_catg_nbr and c.season = cc.season and c.fiscal_year = cc.fiscal_year " +
					"where c.dept_catg_nbr = " + finelineVolumeDeviationDto.getLvl3Nbr() + " and  c.season = '" + season + "' and c.fiscal_year = " + fiscalYear;

		else if (finelineVolumeDeviationDto.getVolumeDeviationLevel().equals(VdLevelCode.SUB_CATEGORY.getDescription()))
			return "select distinct scc.store_nbr as store,scc.cluster_id as clusterId from `" + bigQueryConnectionProperties.getAnalyticsData() + ".svg_subcategory_cluster` scc " +
					"join `" + bigQueryConnectionProperties.getAnalyticsData() + ".svg_subcategory` sc on sc.cluster_id = scc.cluster_id and sc.dept_nbr = scc.dept_nbr and " +
					"sc.dept_catg_nbr = scc.dept_catg_nbr and sc.dept_subcatg_nbr = scc.dept_subcatg_nbr and sc.season = scc.season and sc.fiscal_year = scc.fiscal_year " +
					"where sc.dept_catg_nbr = " + finelineVolumeDeviationDto.getLvl3Nbr() + " and sc.dept_subcatg_nbr = " + finelineVolumeDeviationDto.getLvl4Nbr() +
					" and  sc.season = '" + season + "' and sc.fiscal_year = " + fiscalYear;

		else if (finelineVolumeDeviationDto.getVolumeDeviationLevel().equals(VdLevelCode.FINELINE.getDescription()))
			return "select store_nbr as store,cluster_id as clusterId from `" + bigQueryConnectionProperties.getAnalyticsData() + ".svg_fl_cluster` " +
					"where dept_catg_nbr = " + finelineVolumeDeviationDto.getLvl3Nbr() + " and dept_subcatg_nbr = " + finelineVolumeDeviationDto.getLvl4Nbr() +
					" and fineline_nbr = " + finelineVolumeDeviationDto.getFinelineId() + " and season = '" + season + "' and fiscal_year = " + fiscalYear;

		throw new SizeAndPackException("Invalid Deviation Level, Fineline, Subcategory, Category are valid values");
	}

	private String getStoreDistributionQuery(String parquetTableName, String packOptOutputTableName,
											 String planAndFineline, Long planId, Integer fineline, Long inStoreWeek, String packId) {
		return "WITH MyTable AS ((select distinct productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier from "
				+ "(select fineline as finelineNbr, reverse( SUBSTR(REVERSE(trim(cc)), STRPOS(REVERSE(trim(cc)), \"_\")+1)) as styleNbr,trim(cc) as cc, CAST(store AS INTEGER) as store, min(week) as inStoreWeek "
				+ "from `" + parquetTableName + "` as RFA where plan_id_partition=" + planId + " and fineline="
				+ fineline + " and final_alloc_space>0"
				+ " group by finelineNbr, styleNbr,cc,store having inStoreWeek=" + inStoreWeek
				+ " order by finelineNbr, styleNbr,cc, inStoreWeek, store ) as RFA " + "join "
				+ "(SELECT SP.ProductFineline as productFineline, trim(SP.ProductCustomerChoice)as cc, SP.store, SP.SPPackID as packId, SP.MerchMethod as merch_method, SP.size, "
				+ "SP.SPInitialSetPackMultiplier as packMultiplier from `" + packOptOutputTableName
				+ "` as SP where ProductFineline LIKE '" + planAndFineline + "' and "
				+ "SPInitialSetPackMultiplier>0 and SP.SPPackID='" + packId
				+ "') as SP on RFA.store = SP.store and RFA.cc = SP.cc "
				+ "group by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier "
				+ "order by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier) "
				+ "UNION ALL" +
				" (select distinct productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier from "
				+ "(select fineline as finelineNbr, reverse( SUBSTR(REVERSE(trim(cc)), STRPOS(REVERSE(trim(cc)), \"_\")+1)) as styleNbr,trim(cc) as cc, CAST(store AS INTEGER) as store, min(week) as inStoreWeek "
				+ "from `" + parquetTableName + "` as RFA where plan_id_partition=" + planId + " and fineline="
				+ fineline + " and final_alloc_space>0"
				+ " group by finelineNbr, styleNbr,cc,store "
				+ "order by finelineNbr, styleNbr,cc, inStoreWeek, store ) as RFA " + "join "
				+ "(SELECT SP.ProductFineline as productFineline, trim(SP.ProductCustomerChoice)as cc, SP.store, SP.SPPackID as packId, SP.MerchMethod as merch_method, SP.size, "
				+ "SP.SPBumpSetPackMultiplier as packMultiplier from `" + packOptOutputTableName
				+ "` as SP where ProductFineline LIKE '" + planAndFineline + "' and "
				+ "SPBumpSetPackMultiplier>0 and SP.SPPackID='" + packId
				+ "') as SP on RFA.store = SP.store and RFA.cc = SP.cc "
				+ "group by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier "
				+ "order by productFineline, RFA.finelineNbr, RFA.styleNbr, RFA.inStoreWeek, SP.packId, SP.store, SP.packMultiplier)) "
				+ "SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable";
	}

}
