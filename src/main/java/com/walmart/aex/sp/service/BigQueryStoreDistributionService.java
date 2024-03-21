package com.walmart.aex.sp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.cloud.bigquery.QueryParameterValue;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.BumpSet;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.currentlineplan.LikeAssociation;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.CommonGCPUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
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

	private final ObjectMapper objectMapper;
	private final BQFPService bqfpService;
	private final StrategyFetchService strategyFetchService;
	private final GraphQLService graphQLService;
	private LinePlanService linePlanService;
	private final BigQuery bigQuery;

	@ManagedConfiguration
	private GraphQLProperties graphQLProperties;

	@ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

	public BigQueryStoreDistributionService(ObjectMapper objectMapper, BQFPService bqfpService, StrategyFetchService strategyFetchService, GraphQLService graphQLService, LinePlanService linePlanService, BigQuery bigQuery) {
		this.objectMapper = objectMapper;
		this.bqfpService = bqfpService;
		this.strategyFetchService = strategyFetchService;
		this.graphQLService = graphQLService;
		this.linePlanService = linePlanService;
		this.bigQuery = bigQuery;
	}

	public StoreDistributionData getStoreDistributionData(PackData packData, String groupingType) {
		StoreDistributionData storeDistributionData = new StoreDistributionData();
		List<StoreDistributionDTO> storeDistributionList;

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
			String poDistrOverrideDatsetName = bigQueryConnectionProperties.getPODistributionOverrideDatasetName();
			String poDistrOverrideTableName = bigQueryConnectionProperties.getPODistributionOverrideTableName();

			List<Long> poDistrOverriddenPlanIds = getPoDistributionOverridePlanIds();

			QueryJobConfiguration queryConfig;

			if (poDistrOverriddenPlanIds.contains(packData.getPlanId())) {
				final String tableName = CommonGCPUtil.formatTable(projectId, poDistrOverrideDatsetName, poDistrOverrideTableName);
				final String poOverrideQuery = getPoDistributionOverriddenQuery(tableName);

				queryConfig = QueryJobConfiguration.newBuilder(poOverrideQuery)
						.addNamedParameter("planId", QueryParameterValue.int64(packData.getPlanId()))
						.addNamedParameter("finelineNbr", QueryParameterValue.int64(packData.getFinelineNbr()))
						.addNamedParameter("packId", QueryParameterValue.string(packData.getPackId()))
						.addNamedParameter("inStoreWeek", QueryParameterValue.int64(packData.getInStoreWeek()))
						.build();

				log.debug("PO Override Query: {}", queryConfig.getQuery());
				storeDistributionList = processResults(bigQuery.query(queryConfig));
			} else {

				String sqlQuery = getInitialSetQuery(parquetTableName, packOptOutputTableName,
						planAndFineline, planId, fineline, packId, inStoreWeek);
				log.debug("InitialSet Query: {}", sqlQuery);
				queryConfig = QueryJobConfiguration.newBuilder(sqlQuery).build();
				storeDistributionList = processResults(bigQuery.query(queryConfig));

				if (storeDistributionList.isEmpty()) {
					sqlQuery = getBumpSetStoreDistributionQuery(parquetTableName, packOptOutputTableName,
							planAndFineline, planId, fineline, packId);
					log.debug("BumpSet Query: {}", sqlQuery);
					if (StringUtils.isNotEmpty(sqlQuery)) {
						queryConfig = QueryJobConfiguration.newBuilder(sqlQuery).build();
						storeDistributionList = processResults(bigQuery.query(queryConfig));

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
			if (!storeDistributionList.isEmpty())
				storeDistributionData.setStoreDistributionList(storeDistributionList);

		} catch (Exception e) {
			log.error("Exception details are ", e);
		}

		return storeDistributionData;
	}

	private List<Long> getPoDistributionOverridePlanIds() {
		try {
			return Arrays.asList(objectMapper.readValue(bigQueryConnectionProperties.getPODistributionOverridePlanIds(), Long[].class));
		} catch (Exception e) {
			log.error("Unable to deserialize po.distribution.override.planIds: {}", bigQueryConnectionProperties.getPODistributionOverridePlanIds());
			return new ArrayList<>();
		}
	}

	private List<StoreDistributionDTO> processResults(TableResult results) {
		List<StoreDistributionDTO> storeDistributionList = new ArrayList<>();

		if (results != null)
			results.iterateAll().forEach(rows -> rows.forEach(row -> {
				try {
					StoreDistributionDTO storeDistributionDTO = objectMapper.readValue(row.getValue().toString(),
							StoreDistributionDTO.class);
					storeDistributionList.add(storeDistributionDTO);
				} catch (JsonProcessingException e) {
					log.error("Error while mapping the gcp table response data \n", e);
				}
			}));

		return storeDistributionList;
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
		if (StringUtils.isNotEmpty(planDesc) && planDesc.length() > 3) {
			String season = planDesc.substring(0, 2);
			Integer fiscalYear = Integer.valueOf(planDesc.substring(planDesc.length() - 4));
			if (null != volumeDeviationResponse && !volumeDeviationResponse.getFinelines().isEmpty()) {
				FinelineVolumeDeviationDto finelineVolumeDeviationDto = volumeDeviationResponse.getFinelines().get(0);
				log.debug("planId: {}, finelineNbr: {}, volumeDeviation: {}", planId, fineline, finelineVolumeDeviationDto.getVolumeDeviationLevel());
				String analyticsQuery = getAnalyticsQueryByDeviation(finelineVolumeDeviationDto, season, fiscalYear);
				return getStoreDistributionBumpQuery(parquetTableName, packOptOutputTableName, planAndFineline, planId, fineline, packId, analyticsQuery);
			}
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

		else if (finelineVolumeDeviationDto.getVolumeDeviationLevel().equals(VdLevelCode.FINELINE.getDescription())) {
			String finelineTable = CommonGCPUtil.formatTable(bigQueryConnectionProperties.getAnalyticsData(), bigQueryConnectionProperties.getFinelineVolumeCluster());
			LikeAssociation likeAssociation = linePlanService.getLikeAssociation(finelineVolumeDeviationDto.getPlanId(), finelineVolumeDeviationDto.getFinelineId());
			log.debug("LikeFineline details - planId: {} | originalFineline: {} | likeFineline: {}", finelineVolumeDeviationDto.getPlanId(), finelineVolumeDeviationDto.getFinelineId(), null != likeAssociation ? likeAssociation.getId() : null);
			return CommonGCPUtil.getFinelineVolumeClusterQuery(finelineTable,
							finelineVolumeDeviationDto.getLvl3Nbr(),
							finelineVolumeDeviationDto.getLvl4Nbr(),
							finelineVolumeDeviationDto.getFinelineId(),
							season, fiscalYear, likeAssociation);
		}

		throw new SizeAndPackException("Invalid Deviation Level, Fineline, Subcategory, Category are valid values");
	}

	private String getPoDistributionOverriddenQuery(String tableName) {
		return "WITH\n" +
				"MyTable AS (\n" +
				"SELECT DISTINCT \n" +
				"productFineline,\n" +
				"fineline as finelineNbr,\n" +
				"style_nbr as styleNbr,\n" +
				"po_in_store_dates as inStoreWeek,\n" +
				"pack_id as packId,\n" +
				"store,\n" +
				"no_of_packs as packMultiplier FROM `" + tableName + "` \n" +
				"where plan_id = @planId and fineline = @finelineNbr and po_in_store_dates = @inStoreWeek and trim(pack_id) = @packId)\n" +
				"SELECT\n" +
				"  TO_JSON_STRING(gcpTable) AS json\n" +
				"FROM\n" +
				"  MyTable AS gcpTable";
	}

}
