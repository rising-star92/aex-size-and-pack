package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.*;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackRequest;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BigQueryClusterService {

    private final ObjectMapper objectMapper ;
    private final BigQuery bigQuery;
    @ManagedConfiguration
    BigQueryConnectionProperties bigQueryConnectionProperties;

    public BigQueryClusterService(ObjectMapper objectMapper, BigQuery bigQuery) {
        this.objectMapper = objectMapper;
        this.bigQuery = bigQuery;
    }

    public List<RFASizePackData> fetchRFASizePackData(RFASizePackRequest request, String volumeDeviationLevel) throws InterruptedException, JsonProcessingException {
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(generateQuery(volumeDeviationLevel))
                .addNamedParameter("colors", QueryParameterValue.string(objectMapper.writeValueAsString(request.getColors())))
                .addNamedParameter("planId", QueryParameterValue.int64(request.getPlan_id()))
                .addNamedParameter("lvl0", QueryParameterValue.int64(request.getRpt_lvl_0_nbr()))
                .addNamedParameter("lvl1", QueryParameterValue.int64(request.getRpt_lvl_1_nbr()))
                .addNamedParameter("lvl2", QueryParameterValue.int64(request.getRpt_lvl_2_nbr()))
                .addNamedParameter("lvl3", QueryParameterValue.int64(request.getRpt_lvl_3_nbr()))
                .addNamedParameter("lvl4", QueryParameterValue.int64(request.getRpt_lvl_4_nbr()))
                .addNamedParameter("finelineNbr", QueryParameterValue.int64(request.getFineline_nbr()))
                .addNamedParameter("likeFinelineNbr", QueryParameterValue.int64(request.getLike_fineline_nbr()))
                .addNamedParameter("likeLvl1", QueryParameterValue.int64(request.getLike_lvl1_nbr()))
                .addNamedParameter("likeLvl3", QueryParameterValue.int64(request.getLike_lvl3_nbr()))
                .addNamedParameter("likeLvl4", QueryParameterValue.int64(request.getLike_lvl4_nbr()))
                .addNamedParameter("fiscalYear", QueryParameterValue.int64(request.getFiscal_year()))
                .addNamedParameter("seasonCode", QueryParameterValue.string(request.getSeasonCode()))
                .build();
        TableResult resultsIs = bigQuery.query(queryConfig);
        List<RFASizePackData> results = new ArrayList<>();
        resultsIs.iterateAll().forEach(rows -> rows.forEach(row -> {
            try {
                results.add(objectMapper.readValue(row.getValue().toString(), RFASizePackData.class));
            } catch (JsonProcessingException e) {
                log.error("Exception ", e);
            }
        }));
        return results;
    }

    private String generateQuery(String volumeDeviationLevel)  {
        String analyticsDataset = bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData();
        String rfaCcTable = bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getRFADataSetName() + "." + bigQueryConnectionProperties.getRFACCStageTable();
        String sizeClusterTable = analyticsDataset + "." + bigQueryConnectionProperties.getSizeCluster();
        String sizeColorClusterTable = analyticsDataset + "." + bigQueryConnectionProperties.getSizeColorCluster();
        String queryParams = "WITH MyTable AS ( \n" +
                        "WITH data AS (\n" +
                        "    SELECT @colors AS json_array\n" +
                        "),\n" +
                        "plan_hierarchy AS (\n" +
                        "    SELECT @planId AS plan_id,\n" +
                        "        @lvl0 AS rpt_lvl_0_nbr,\n" +
                        "        @lvl1 AS rpt_lvl_1_nbr,\n" +
                        "        @lvl2 AS rpt_lvl_2_nbr,\n" +
                        "        @lvl3 AS rpt_lvl_3_nbr,\n" +
                        "        @lvl4 AS rpt_lvl_4_nbr,\n" +
                        "        @finelineNbr AS fineline_nbr,\n" +
                        "        @likeLvl1 AS like_rpt_lvl_1_nbr,\n" +
                        "        @likeLvl3 AS like_rpt_lvl_3_nbr,\n" +
                        "        @likeLvl4 AS like_rpt_lvl_4_nbr,\n" +
                        "        @likeFinelineNbr AS like_fineline_nbr,\n" +
                        "        @fiscalYear AS fiscal_year,\n" +
                        "        @seasonCode AS season_code\n" +
                        "), " +
                "cc_color_families AS (\n" +
                "    SELECT JSON_EXTRACT_SCALAR(cc_color_families_json, '$.cc') AS cc,\n" +
                "        JSON_EXTRACT_SCALAR(cc_color_families_json, '$.color_family_desc') AS color_family_desc\n" +
                "    FROM data,\n" +
                "        UNNEST(JSON_EXTRACT_ARRAY(json_array)) AS cc_color_families_json\n" +
                ")";

        if (volumeDeviationLevel.equalsIgnoreCase(VdLevelCode.FINELINE.getDescription())) {
            String finelineClusterTable = analyticsDataset + "." + bigQueryConnectionProperties.getFinelineVolumeCluster();
            queryParams += findFinelineQuery(rfaCcTable, sizeClusterTable, sizeColorClusterTable, finelineClusterTable);
        } else if (volumeDeviationLevel.equalsIgnoreCase(VdLevelCode.SUB_CATEGORY.getDescription())) {
            String subCategoryClusterTable = analyticsDataset + "." + bigQueryConnectionProperties.getSubCategoryVolumeCluster();
            queryParams += findSubCatQuery(rfaCcTable, sizeClusterTable, sizeColorClusterTable, subCategoryClusterTable);
        } else {
            String categoryClusterTable = analyticsDataset + "." + bigQueryConnectionProperties.getCategoryVolumeCluster();
            queryParams += findCatQuery(rfaCcTable, sizeClusterTable, sizeColorClusterTable, categoryClusterTable);
        }
        queryParams += ") SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable;";
        return queryParams;
    }

    /**
     * Fineline SVG query
     * When likeFL is available apply it in filter clause on SVG and SC tables
     */
    private String findFinelineQuery(String rfaCcTable, String sizeClusterTable, String sizeColorClusterTable, String finelineClusterTable) {
        return  "SELECT rpt_lvl_0_nbr,\n" +
                "    rpt_lvl_1_nbr,\n" +
                "    rpt_lvl_2_nbr,\n" +
                "    rpt_lvl_3_nbr,\n" +
                "    rpt_lvl_4_nbr,\n" +
                "    fineline_nbr,\n" +
                "    style_nbr,\n" +
                "    customer_choice,\n" +
                "    fixture_type,\n" +
                "    fixture_group,\n" +
                "    color_family,\n" +
                "    size_cluster_id,\n" +
                "    volume_group_cluster_id,\n" +
                "    CONCAT('[', STRING_AGG(DISTINCT CAST(store_nbr AS STRING), ', '), ']') AS store_list,\n" +
                "    COUNT(DISTINCT store_nbr) AS store_cnt\n" +
                "FROM(\n" +
                "        SELECT rfa_output.rpt_lvl_0_nbr,\n" +
                "            rfa_output.rpt_lvl_1_nbr,\n" +
                "            rfa_output.rpt_lvl_2_nbr,\n" +
                "            rfa_output.rpt_lvl_3_nbr,\n" +
                "            rfa_output.rpt_lvl_4_nbr,\n" +
                "            rfa_output.fineline AS fineline_nbr,\n" +
                "            rfa_output.style_nbr,\n" +
                "            rfa_output.cc AS customer_choice,\n" +
                "            rfa_output.final_pref AS fixture_type,\n" +
                "            rfa_output.final_alloc_space AS fixture_group,\n" +
                "            COALESCE(all_clus.color_family, 'DEFAULT') AS color_family,\n" +
                "            COALESCE(all_clus.cluster_id, 1) AS size_cluster_id,\n" +
                "            fl_clus.cluster_id AS volume_group_cluster_id,\n" +
                "            fl_clus.store_nbr\n" +
                "        FROM (\n" +
                "                SELECT *\n" +
                "                FROM (\n" +
                "                        SELECT RANK() OVER (\n" +
                "                                PARTITION BY store,\n" +
                "                                fineline,\n" +
                "                                cc\n" +
                "                                ORDER BY week\n" +
                "                            ) row_nbr,\n" +
                "                            *\n" +
                "                        FROM (\n" +
                "                                SELECT plan_id_partition,\n" +
                "                                    h.fiscal_year,\n" +
                "                                    h.season_code,\n" +
                "                                    week,\n" +
                "                                    store,\n" +
                "                                    h.rpt_lvl_0_nbr,\n" +
                "                                    h.rpt_lvl_1_nbr,\n" +
                "                                    h.rpt_lvl_2_nbr,\n" +
                "                                    h.rpt_lvl_3_nbr,\n" +
                "                                    h.rpt_lvl_4_nbr,\n" +
                "                                    h.like_rpt_lvl_1_nbr,\n" +
                "                                    h.like_fineline_nbr,\n" +
                "                                    dept,\n" +
                "                                    fineline,\n" +
                "                                    final_pref,\n" +
                "                                    allocated,\n" +
                "                                    TRIM(style_nbr) AS style_nbr,\n" +
                "                                    TRIM(cc) AS cc,\n" +
                "                                    final_alloc_space\n" +
                "                                FROM `" + rfaCcTable + "`,\n" +
                "                                    plan_hierarchy AS h\n" +
                "                                WHERE plan_id_partition = h.plan_id\n" +
                "                                    AND fineline = h.fineline_nbr\n" +
                "                                    AND final_alloc_space > 0\n" +
                "                            )\n" +
                "                    ) AS rfa_cc_output\n" +
                "                WHERE rfa_cc_output.row_nbr = 1\n" +
                "            ) AS rfa_output\n" +
                "            JOIN (\n" +
                "                SELECT svg_fl_clus.dept_nbr,\n" +
                "                    svg_fl_clus.fineline_nbr,\n" +
                "                    svg_fl_clus.store_nbr,\n" +
                "                    svg_fl_clus.cluster_id,\n" +
                "                    svg_fl_clus.fiscal_year,\n" +
                "                    svg_fl_clus.season\n" +
                "                FROM `" + finelineClusterTable + "` AS svg_fl_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE svg_fl_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND svg_fl_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND svg_fl_clus.season = h.season_code\n" +
                "                    AND svg_fl_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS fl_clus ON CAST(rfa_output.store AS INT64) = fl_clus.store_nbr\n" +
                "            LEFT JOIN (\n" +
                "                SELECT sc_clus.fineline_nbr,\n" +
                "                    sc_clus.dept_nbr,\n" +
                "                    sc_clus.store_nbr,\n" +
                "                    sc_clus.cluster_id,\n" +
                "                    sc_clus.dept_catg_nbr,\n" +
                "                    sc_clus.dept_subcatg_nbr,\n" +
                "                    \"default\" AS color_family,\n" +
                "                    sc_clus.dept,\n" +
                "                    sc_clus.fiscal_year,\n" +
                "                    sc_clus.season\n" +
                "                FROM `" + sizeClusterTable + "` AS sc_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sc_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sc_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sc_clus.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
                "                    AND sc_clus.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
                "                    AND sc_clus.season = h.season_code\n" +
                "                    AND sc_clus.fiscal_year = h.fiscal_year\n" +
                "                UNION ALL\n" +
                "                SELECT sco_clus.fineline_nbr,\n" +
                "                    sco_clus.dept_nbr,\n" +
                "                    sco_clus.store_nbr,\n" +
                "                    sco_clus.cluster_id,\n" +
                "                    sco_clus.dept_catg_nbr,\n" +
                "                    sco_clus.dept_subcatg_nbr,\n" +
                "                    sco_clus.color_family,\n" +
                "                    sco_clus.dept,\n" +
                "                    sco_clus.fiscal_year,\n" +
                "                    sco_clus.season\n" +
                "                FROM `" + sizeColorClusterTable + "` AS sco_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sco_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sco_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sco_clus.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
                "                    AND sco_clus.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
                "                    AND sco_clus.season = h.season_code\n" +
                "                    AND sco_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS all_clus ON CAST(rfa_output.store AS INT64) = all_clus.store_nbr\n" +
                "            AND all_clus.fineline_nbr = COALESCE(rfa_output.like_fineline_nbr, rfa_output.fineline)\n" +
                "            AND rfa_output.fiscal_year = all_clus.fiscal_year\n" +
                "            AND rfa_output.season_code = all_clus.season\n" +
                "            LEFT JOIN (\n" +
                "                SELECT *\n" +
                "                FROM cc_color_families\n" +
                "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
                "        WHERE UPPER(TRIM(all_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
                "        OR all_clus.fineline_nbr is NULL\n" +
                "    )\n" +
                "GROUP BY rpt_lvl_0_nbr,\n" +
                "    rpt_lvl_1_nbr,\n" +
                "    rpt_lvl_2_nbr,\n" +
                "    rpt_lvl_3_nbr,\n" +
                "    rpt_lvl_4_nbr,\n" +
                "    fineline_nbr,\n" +
                "    style_nbr,\n" +
                "    customer_choice,\n" +
                "    fixture_type,\n" +
                "    fixture_group,\n" +
                "    color_family,\n" +
                "    size_cluster_id,\n" +
                "    volume_group_cluster_id ";
    }

    /**
     * Sub Category SVG query
     * When likeFL is available apply it in filter clause only on SC tables
     */
    private String findSubCatQuery(String rfaCcTable, String sizeClusterTable, String sizeColorClusterTable, String subCategoryClusterTable) {
        return "SELECT rpt_lvl_0_nbr,\n" +
                "    rpt_lvl_1_nbr,\n" +
                "    rpt_lvl_2_nbr,\n" +
                "    rpt_lvl_3_nbr,\n" +
                "    rpt_lvl_4_nbr,\n" +
                "    fineline_nbr,\n" +
                "    style_nbr,\n" +
                "    customer_choice,\n" +
                "    fixture_type,\n" +
                "    fixture_group,\n" +
                "    color_family,\n" +
                "    size_cluster_id,\n" +
                "    volume_group_cluster_id,\n" +
                "    CONCAT('[', STRING_AGG(DISTINCT CAST(store_nbr AS STRING), ', '), ']') AS store_list,\n" +
                "    COUNT(DISTINCT store_nbr) AS store_cnt\n" +
                "FROM(\n" +
                "        SELECT rfa_output.rpt_lvl_0_nbr,\n" +
                "            rfa_output.rpt_lvl_1_nbr,\n" +
                "            rfa_output.rpt_lvl_2_nbr,\n" +
                "            rfa_output.rpt_lvl_3_nbr,\n" +
                "            rfa_output.rpt_lvl_4_nbr,\n" +
                "            rfa_output.fineline AS fineline_nbr,\n" +
                "            rfa_output.style_nbr,\n" +
                "            rfa_output.cc AS customer_choice,\n" +
                "            rfa_output.final_pref AS fixture_type,\n" +
                "            rfa_output.final_alloc_space AS fixture_group,\n" +
                "            COALESCE(all_clus.color_family, 'DEFAULT') AS color_family,\n" +
                "            COALESCE(all_clus.cluster_id, 1) AS size_cluster_id,\n" +
                "            subcatg_clus.cluster_id AS volume_group_cluster_id,\n" +
                "            subcatg_clus.store_nbr\n" +
                "        FROM (\n" +
                "                SELECT *\n" +
                "                FROM (\n" +
                "                        SELECT RANK() OVER (\n" +
                "                                PARTITION BY store,\n" +
                "                                fineline,\n" +
                "                                cc\n" +
                "                                ORDER BY week\n" +
                "                            ) row_nbr,\n" +
                "                            *\n" +
                "                        FROM (\n" +
                "                                SELECT plan_id_partition,\n" +
                "                                    h.fiscal_year,\n" +
                "                                    h.season_code,\n" +
                "                                    week,\n" +
                "                                    store,\n" +
                "                                    h.rpt_lvl_0_nbr,\n" +
                "                                    h.rpt_lvl_1_nbr,\n" +
                "                                    h.rpt_lvl_2_nbr,\n" +
                "                                    h.rpt_lvl_3_nbr,\n" +
                "                                    h.rpt_lvl_4_nbr,\n" +
                "                                    h.like_rpt_lvl_1_nbr,\n" +
                "                                    h.like_rpt_lvl_4_nbr,\n" +
                "                                    h.like_fineline_nbr,\n" +
                "                                    dept,\n" +
                "                                    fineline,\n" +
                "                                    final_pref,\n" +
                "                                    allocated,\n" +
                "                                    TRIM(style_nbr) AS style_nbr,\n" +
                "                                    TRIM(cc) AS cc,\n" +
                "                                    final_alloc_space\n" +
                "                                FROM `" + rfaCcTable + "`,\n" +
                "                                    plan_hierarchy AS h\n" +
                "                                WHERE plan_id_partition = h.plan_id\n" +
                "                                    AND fineline = h.fineline_nbr\n" +
                "                                    AND final_alloc_space > 0\n" +
                "                            )\n" +
                "                    ) AS rfa_cc_output\n" +
                "                WHERE rfa_cc_output.row_nbr = 1\n" +
                "            ) AS rfa_output\n" +
                "            JOIN (\n" +
                "                SELECT svg_subcatg_clus.dept_nbr,\n" +
                "                    svg_subcatg_clus.dept_subcatg_nbr,\n" +
                "                    svg_subcatg_clus.store_nbr,\n" +
                "                    svg_subcatg_clus.cluster_id,\n" +
                "                    svg_subcatg_clus.fiscal_year,\n" +
                "                    svg_subcatg_clus.season\n" +
                "                FROM `" + subCategoryClusterTable + "` AS svg_subcatg_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE svg_subcatg_clus.dept_subcatg_nbr = h.rpt_lvl_4_nbr\n" +
                "                    AND svg_subcatg_clus.dept_nbr = h.rpt_lvl_1_nbr\n" +
                "                    AND svg_subcatg_clus.season = h.season_code\n" +
                "                    AND svg_subcatg_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS subcatg_clus ON CAST(rfa_output.store AS INT64) = subcatg_clus.store_nbr\n" +
                "            LEFT JOIN (\n" +
                "                SELECT sc_clus.fineline_nbr,\n" +
                "                    sc_clus.dept_nbr,\n" +
                "                    sc_clus.store_nbr,\n" +
                "                    sc_clus.cluster_id,\n" +
                "                    sc_clus.dept_catg_nbr,\n" +
                "                    sc_clus.dept_subcatg_nbr,\n" +
                "                    \"default\" AS color_family,\n" +
                "                    sc_clus.dept,\n" +
                "                    sc_clus.fiscal_year,\n" +
                "                    sc_clus.season\n" +
                "                FROM `" + sizeClusterTable + "` AS sc_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sc_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sc_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sc_clus.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
                "                    AND sc_clus.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
                "                    AND sc_clus.season = h.season_code\n" +
                "                    AND sc_clus.fiscal_year = h.fiscal_year\n" +
                "                UNION ALL\n" +
                "                SELECT sco_clus.fineline_nbr,\n" +
                "                    sco_clus.dept_nbr,\n" +
                "                    sco_clus.store_nbr,\n" +
                "                    sco_clus.cluster_id,\n" +
                "                    sco_clus.dept_catg_nbr,\n" +
                "                    sco_clus.dept_subcatg_nbr,\n" +
                "                    sco_clus.color_family,\n" +
                "                    sco_clus.dept,\n" +
                "                    sco_clus.fiscal_year,\n" +
                "                    sco_clus.season\n" +
                "                FROM `" + sizeColorClusterTable + "` AS sco_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sco_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sco_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sco_clus.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
                "                    AND sco_clus.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
                "                    AND sco_clus.season = h.season_code\n" +
                "                    AND sco_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS all_clus ON CAST(rfa_output.store AS INT64) = all_clus.store_nbr\n" +
                "            AND all_clus.fineline_nbr = COALESCE(\n" +
                "                rfa_output.like_fineline_nbr,\n" +
                "                rfa_output.fineline\n" +
                "            )\n" +
                "            AND all_clus.dept_subcatg_nbr = COALESCE(\n" +
                "                rfa_output.like_rpt_lvl_4_nbr,\n" +
                "                rfa_output.rpt_lvl_4_nbr\n" +
                "            )\n" +
                "            AND rfa_output.fiscal_year = all_clus.fiscal_year\n" +
                "            AND rfa_output.season_code = all_clus.season\n" +
                "            LEFT JOIN (\n" +
                "                SELECT *\n" +
                "                FROM cc_color_families\n" +
                "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
                "        WHERE UPPER(TRIM(all_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
                "        OR all_clus.fineline_nbr is NULL\n" +
                "    )\n" +
                "GROUP BY rpt_lvl_0_nbr,\n" +
                "    rpt_lvl_1_nbr,\n" +
                "    rpt_lvl_2_nbr,\n" +
                "    rpt_lvl_3_nbr,\n" +
                "    rpt_lvl_4_nbr,\n" +
                "    fineline_nbr,\n" +
                "    style_nbr,\n" +
                "    customer_choice,\n" +
                "    fixture_type,\n" +
                "    fixture_group,\n" +
                "    color_family,\n" +
                "    size_cluster_id,\n" +
                "    volume_group_cluster_id ";
    }

    /**
     * Category SVG query
     * When likeFL is available apply it in filter clause only on SC tables
     */
    private String findCatQuery(String rfaCcTable, String sizeClusterTable, String sizeColorClusterTable, String categoryClusterTable) {
        return "SELECT rpt_lvl_0_nbr,\n" +
                "    rpt_lvl_1_nbr,\n" +
                "    rpt_lvl_2_nbr,\n" +
                "    rpt_lvl_3_nbr,\n" +
                "    rpt_lvl_4_nbr,\n" +
                "    fineline_nbr,\n" +
                "    style_nbr,\n" +
                "    customer_choice,\n" +
                "    fixture_type,\n" +
                "    fixture_group,\n" +
                "    color_family,\n" +
                "    size_cluster_id,\n" +
                "    volume_group_cluster_id,\n" +
                "    CONCAT('[', STRING_AGG(DISTINCT CAST(store_nbr AS STRING), ', '), ']') AS store_list,\n" +
                "    COUNT(DISTINCT store_nbr) AS store_cnt\n" +
                "FROM(\n" +
                "        SELECT rfa_output.rpt_lvl_0_nbr,\n" +
                "            rfa_output.rpt_lvl_1_nbr,\n" +
                "            rfa_output.rpt_lvl_2_nbr,\n" +
                "            rfa_output.rpt_lvl_3_nbr,\n" +
                "            rfa_output.rpt_lvl_4_nbr,\n" +
                "            rfa_output.fineline AS fineline_nbr,\n" +
                "            rfa_output.style_nbr,\n" +
                "            rfa_output.cc AS customer_choice,\n" +
                "            rfa_output.final_pref AS fixture_type,\n" +
                "            rfa_output.final_alloc_space AS fixture_group,\n" +
                "            COALESCE(all_clus.color_family, 'DEFAULT') AS color_family,\n" +
                "            COALESCE(all_clus.cluster_id, 1) AS size_cluster_id,\n" +
                "            catg_clus.cluster_id AS volume_group_cluster_id,\n" +
                "            catg_clus.store_nbr\n" +
                "        FROM (\n" +
                "                SELECT *\n" +
                "                FROM (\n" +
                "                        SELECT RANK() OVER (\n" +
                "                                PARTITION BY store,\n" +
                "                                fineline,\n" +
                "                                cc\n" +
                "                                ORDER BY week\n" +
                "                            ) row_nbr,\n" +
                "                            *\n" +
                "                        FROM (\n" +
                "                                SELECT plan_id_partition,\n" +
                "                                    h.fiscal_year,\n" +
                "                                    h.season_code,\n" +
                "                                    week,\n" +
                "                                    store,\n" +
                "                                    h.rpt_lvl_0_nbr,\n" +
                "                                    h.rpt_lvl_1_nbr,\n" +
                "                                    h.rpt_lvl_2_nbr,\n" +
                "                                    h.rpt_lvl_3_nbr,\n" +
                "                                    h.rpt_lvl_4_nbr,\n" +
                "                                    h.like_rpt_lvl_1_nbr,\n" +
                "                                    h.like_rpt_lvl_3_nbr,\n" +
                "                                    h.like_fineline_nbr,\n" +
                "                                    dept,\n" +
                "                                    fineline,\n" +
                "                                    final_pref,\n" +
                "                                    allocated,\n" +
                "                                    TRIM(style_nbr) AS style_nbr,\n" +
                "                                    TRIM(cc) AS cc,\n" +
                "                                    final_alloc_space\n" +
                "                                FROM `" + rfaCcTable + "`,\n" +
                "                                    plan_hierarchy AS h\n" +
                "                                WHERE plan_id_partition = h.plan_id\n" +
                "                                    AND fineline = h.fineline_nbr\n" +
                "                                    AND final_alloc_space > 0\n" +
                "                            )\n" +
                "                    ) AS rfa_cc_output\n" +
                "                WHERE rfa_cc_output.row_nbr = 1\n" +
                "            ) AS rfa_output\n" +
                "            JOIN (\n" +
                "                SELECT svg_catg_clus.dept_nbr,\n" +
                "                    svg_catg_clus.dept_catg_nbr,\n" +
                "                    svg_catg_clus.store_nbr,\n" +
                "                    svg_catg_clus.cluster_id,\n" +
                "                    svg_catg_clus.fiscal_year,\n" +
                "                    svg_catg_clus.season\n" +
                "                FROM `" +  categoryClusterTable + "` AS svg_catg_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE svg_catg_clus.dept_catg_nbr = h.rpt_lvl_3_nbr\n" +
                "                    AND svg_catg_clus.dept_nbr = h.rpt_lvl_1_nbr\n" +
                "                    AND svg_catg_clus.season = h.season_code\n" +
                "                    AND svg_catg_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS catg_clus ON CAST(rfa_output.store AS INT64) = catg_clus.store_nbr\n" +
                "            LEFT JOIN (\n" +
                "                SELECT sc_clus.fineline_nbr,\n" +
                "                    sc_clus.dept_nbr,\n" +
                "                    sc_clus.store_nbr,\n" +
                "                    sc_clus.cluster_id,\n" +
                "                    sc_clus.dept_catg_nbr,\n" +
                "                    sc_clus.dept_subcatg_nbr,\n" +
                "                    \"default\" AS color_family,\n" +
                "                    sc_clus.dept,\n" +
                "                    sc_clus.fiscal_year,\n" +
                "                    sc_clus.season\n" +
                "                FROM `" + sizeClusterTable + "` AS sc_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sc_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sc_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sc_clus.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
                "                    AND sc_clus.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
                "                    AND sc_clus.season = h.season_code\n" +
                "                    AND sc_clus.fiscal_year = h.fiscal_year\n" +
                "                UNION ALL\n" +
                "                SELECT sco_clus.fineline_nbr,\n" +
                "                    sco_clus.dept_nbr,\n" +
                "                    sco_clus.store_nbr,\n" +
                "                    sco_clus.cluster_id,\n" +
                "                    sco_clus.dept_catg_nbr,\n" +
                "                    sco_clus.dept_subcatg_nbr,\n" +
                "                    sco_clus.color_family,\n" +
                "                    sco_clus.dept,\n" +
                "                    sco_clus.fiscal_year,\n" +
                "                    sco_clus.season\n" +
                "                FROM `" + sizeColorClusterTable + "` AS sco_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sco_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sco_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sco_clus.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
                "                    AND sco_clus.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
                "                    AND sco_clus.season = h.season_code\n" +
                "                    AND sco_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS all_clus ON CAST(rfa_output.store AS INT64) = all_clus.store_nbr\n" +
                "            AND all_clus.dept_catg_nbr = COALESCE(\n" +
                "                rfa_output.like_rpt_lvl_3_nbr,\n" +
                "                rfa_output.rpt_lvl_3_nbr\n" +
                "                )\n" +
                "            AND all_clus.fineline_nbr = COALESCE(\n" +
                "                rfa_output.like_fineline_nbr,\n" +
                "                rfa_output.fineline\n" +
                "            )\n" +
                "            AND rfa_output.fiscal_year = all_clus.fiscal_year\n" +
                "            AND rfa_output.season_code = all_clus.season\n" +
                "            LEFT JOIN (\n" +
                "                SELECT *\n" +
                "                FROM cc_color_families\n" +
                "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
                "        WHERE UPPER(TRIM(all_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
                "        OR all_clus.fineline_nbr is NULL\n" +
                "    )\n" +
                "GROUP BY rpt_lvl_0_nbr,\n" +
                "    rpt_lvl_1_nbr,\n" +
                "    rpt_lvl_2_nbr,\n" +
                "    rpt_lvl_3_nbr,\n" +
                "    rpt_lvl_4_nbr,\n" +
                "    fineline_nbr,\n" +
                "    style_nbr,\n" +
                "    customer_choice,\n" +
                "    fixture_type,\n" +
                "    fixture_group,\n" +
                "    color_family,\n" +
                "    size_cluster_id,\n" +
                "    volume_group_cluster_id";
    }
}
