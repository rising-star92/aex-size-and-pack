package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackRequest;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BigQueryClusterService {

    private final ObjectMapper objectMapper ;
    @ManagedConfiguration
    BigQueryConnectionProperties bigQueryConnectionProperties;

    BigQueryClusterService (ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<RFASizePackData> fetchRFASizePackData(RFASizePackRequest request, String volumeDeviationLevel) throws InterruptedException, JsonProcessingException {
        String gcpQuery = generateQuery(request, volumeDeviationLevel);
        BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
        QueryJobConfiguration queryConfigIs = QueryJobConfiguration.newBuilder(gcpQuery).build();
        TableResult resultsIs = bigQuery.query(queryConfigIs);
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

    private String generateQuery(RFASizePackRequest request, String volumeDeviationLevel) throws JsonProcessingException {
        String queryParams = "WITH MyTable AS ( \n" +
                        "WITH data AS (\n" +
                        "    SELECT 'in_colors' AS json_array\n" +
                        "),\n" +
                        "plan_hierarchy AS (\n" +
                        "    SELECT in_plan_id AS plan_id,\n" +
                        "        in_rpt_lvl_0_nbr AS rpt_lvl_0_nbr,\n" +
                        "       in_rpt_lvl_1_nbr AS rpt_lvl_1_nbr,\n" +
                        "        in_rpt_lvl_2_nbr AS rpt_lvl_2_nbr,\n" +
                        "        in_rpt_lvl_3_nbr AS rpt_lvl_3_nbr,\n" +
                        "        in_rpt_lvl_4_nbr AS rpt_lvl_4_nbr,\n" +
                        "        in_fineline_nbr AS fineline_nbr,\n" +
                        "        in_like_fineline_nbr AS like_fineline_nbr,\n" +
                        "        in_like_rpt_lvl_1_nbr AS like_rpt_lvl_1_nbr,\n" +
                        "        in_fiscal_year AS fiscal_year,\n" +
                        "        \"in_season_code\" AS season_code\n" +
                        "), ";
        String query =  queryParams
                .replace("in_colors",objectMapper.writeValueAsString(request.getColors()))
                .replace("in_plan_id", String.valueOf(request.getPlan_id()))
                .replace("in_rpt_lvl_0_nbr", String.valueOf(request.getRpt_lvl_0_nbr()))
                .replace("in_rpt_lvl_1_nbr", String.valueOf(request.getRpt_lvl_1_nbr()))
                .replace("in_rpt_lvl_2_nbr", String.valueOf(request.getRpt_lvl_2_nbr()))
                .replace("in_rpt_lvl_3_nbr", String.valueOf(request.getRpt_lvl_3_nbr()))
                .replace("in_rpt_lvl_4_nbr", String.valueOf(request.getRpt_lvl_4_nbr()))
                .replace("in_fineline_nbr", String.valueOf(request.getFineline_nbr()))
                .replace("in_like_fineline_nbr", String.valueOf(request.getLike_fineline_nbr()))
                .replace("in_like_rpt_lvl_1_nbr", String.valueOf(request.getLike_lvl1_nbr()))
                .replace("in_fiscal_year", String.valueOf(request.getFiscal_year()))
                .replace("in_season_code", String.valueOf(request.getSeasonCode()));

        if (volumeDeviationLevel.equalsIgnoreCase("fineline")) {
            query += findFineLineQuery();
        } else if (volumeDeviationLevel.equalsIgnoreCase("subcategory")) {
            query+=findSubCatQuery();
        } else {
            query+=findCatQuery();
        }
        query+=") SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable;";
        return query;
    }

    private String findCatQuery() {
        return "cc_color_families AS (\n" +
                "    SELECT JSON_EXTRACT_SCALAR(cc_color_families_json, '$.cc') AS cc,\n" +
                "        JSON_EXTRACT_SCALAR(cc_color_families_json, '$.color_family_desc') AS color_family_desc\n" +
                "    FROM data,\n" +
                "        UNNEST(JSON_EXTRACT_ARRAY(json_array)) AS cc_color_families_json\n" +
                ")\n" +
                "SELECT rpt_lvl_0_nbr,\n" +
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
                "    STRING_AGG(DISTINCT CAST(store_nbr AS STRING), ',') AS store_list,\n" +
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
                "                                    h.like_rpt_lvl_1_nbr,\n" +
                "                                    h.fiscal_year,\n" +
                "                                    h.season_code,\n" +
                "                                    week,\n" +
                "                                    store,\n" +
                "                                    h.rpt_lvl_0_nbr,\n" +
                "                                    h.rpt_lvl_1_nbr,\n" +
                "                                    h.rpt_lvl_2_nbr,\n" +
                "                                    h.rpt_lvl_3_nbr,\n" +
                "                                    h.rpt_lvl_4_nbr,\n" +
                "                                    h.like_fineline_nbr,\n" +
                "                                    dept,\n" +
                "                                    fineline,\n" +
                "                                    final_pref,\n" +
                "                                    allocated,\n" +
                "                                    TRIM(style_nbr) AS style_nbr,\n" +
                "                                    TRIM(cc) AS cc,\n" +
                "                                    final_alloc_space\n" +
                "                                FROM `"+ bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getRFADataSetName() + "." + bigQueryConnectionProperties.getRFACCStageTable()  +"`,\n" +
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
                "                    svg_fl_clus.dept_catg_nbr,\n" +
                "                    svg_fl_clus.store_nbr,\n" +
                "                    svg_fl_clus.cluster_id,\n" +
                "                    svg_fl_clus.fiscal_year,\n" +
                "                    svg_fl_clus.season\n" +
                "                FROM `"+ bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() +".svg_category_cluster` AS svg_fl_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE svg_fl_clus.dept_catg_nbr = h.rpt_lvl_3_nbr\n" +
                "                    AND svg_fl_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND svg_fl_clus.season = h.season_code\n" +
                "                    AND svg_fl_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS fl_clus ON CAST(rfa_output.store AS INT64) = fl_clus.store_nbr\n" +
                "            AND rfa_output.rpt_lvl_3_nbr = fl_clus.dept_catg_nbr\n" +
                "            JOIN (\n" +
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
                "                FROM `"+ bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() +".sc_cluster` AS sc_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sc_clus.dept_catg_nbr = h.rpt_lvl_3_nbr\n" +
                "                    AND sc_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sc_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
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
                "                FROM `"+ bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() +".sco_cluster` AS sco_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sco_clus.dept_catg_nbr = h.rpt_lvl_3_nbr\n" +
                "                    AND sco_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    and sco_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sco_clus.season = h.season_code\n" +
                "                    AND sco_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS all_clus ON CAST(rfa_output.store AS INT64) = all_clus.store_nbr\n" +
                "            AND all_clus.dept_catg_nbr = rfa_output.rpt_lvl_3_nbr\n" +
                "            AND all_clus.fineline_nbr = COALESCE(\n" +
                "                rfa_output.like_fineline_nbr,\n" +
                "                rfa_output.fineline\n" +
                "            )\n" +
                "            AND rfa_output.fiscal_year = all_clus.fiscal_year\n" +
                "            AND rfa_output.season_code = all_clus.season\n" +
                "            JOIN (\n" +
                "                SELECT *\n" +
                "                FROM cc_color_families\n" +
                "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
                "        WHERE UPPER(TRIM(all_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
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

    private String findSubCatQuery() {
        return "cc_color_families AS (\n" +
                "    SELECT JSON_EXTRACT_SCALAR(cc_color_families_json, '$.cc') AS cc,\n" +
                "        JSON_EXTRACT_SCALAR(cc_color_families_json, '$.color_family_desc') AS color_family_desc\n" +
                "    FROM data,\n" +
                "        UNNEST(JSON_EXTRACT_ARRAY(json_array)) AS cc_color_families_json\n" +
                ")\n" +
                "SELECT rpt_lvl_0_nbr,\n" +
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
                "    STRING_AGG(DISTINCT CAST(store_nbr AS STRING), ',') AS store_list,\n" +
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
                "                                    h.like_rpt_lvl_1_nbr,\n" +
                "                                    h.fiscal_year,\n" +
                "                                    h.season_code,\n" +
                "                                    week,\n" +
                "                                    store,\n" +
                "                                    h.rpt_lvl_0_nbr,\n" +
                "                                    h.rpt_lvl_1_nbr,\n" +
                "                                    h.rpt_lvl_2_nbr,\n" +
                "                                    h.rpt_lvl_3_nbr,\n" +
                "                                    h.rpt_lvl_4_nbr,\n" +
                "                                    h.like_fineline_nbr,\n" +
                "                                    dept,\n" +
                "                                    fineline,\n" +
                "                                    final_pref,\n" +
                "                                    allocated,\n" +
                "                                    TRIM(style_nbr) AS style_nbr,\n" +
                "                                    TRIM(cc) AS cc,\n" +
                "                                    final_alloc_space\n" +
                "                                FROM `" + bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getRFADataSetName() + "." + bigQueryConnectionProperties.getRFACCStageTable()  +"`,\n" +
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
                "                    svg_fl_clus.dept_subcatg_nbr,\n" +
                "                    svg_fl_clus.store_nbr,\n" +
                "                    svg_fl_clus.cluster_id,\n" +
                "                    svg_fl_clus.fiscal_year,\n" +
                "                    svg_fl_clus.season\n" +
                "                FROM `"+ bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() +".svg_subcategory_cluster` AS svg_fl_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE svg_fl_clus.dept_subcatg_nbr = h.rpt_lvl_4_nbr\n" +
                "                    AND svg_fl_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND svg_fl_clus.season = h.season_code\n" +
                "                    AND svg_fl_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS fl_clus ON CAST(rfa_output.store AS INT64) = fl_clus.store_nbr\n" +
                "            AND rfa_output.rpt_lvl_4_nbr = fl_clus.dept_subcatg_nbr\n" +
                "            JOIN (\n" +
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
                "                FROM `"+ bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() +".sc_cluster` AS sc_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sc_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sc_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sc_clus.season = h.season_code\n" +
                "                    AND sc_clus.fiscal_year = h.fiscal_year\n" +
                "                    AND sc_clus.dept_subcatg_nbr = h.rpt_lvl_4_nbr\n" +
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
                "                FROM `"+ bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() +".sco_cluster` AS sco_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sco_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    and sco_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sco_clus.season = h.season_code\n" +
                "                    AND sco_clus.fiscal_year = h.fiscal_year\n" +
                "                    AND sco_clus.dept_subcatg_nbr = h.rpt_lvl_4_nbr\n" +
                "            ) AS all_clus ON CAST(rfa_output.store AS INT64) = all_clus.store_nbr\n" +
                "            AND all_clus.fineline_nbr = COALESCE(\n" +
                "                rfa_output.like_fineline_nbr,\n" +
                "                rfa_output.fineline\n" +
                "            )\n" +
                "            AND rfa_output.fiscal_year = all_clus.fiscal_year\n" +
                "            AND rfa_output.season_code = all_clus.season\n" +
                "            AND rfa_output.rpt_lvl_4_nbr = all_clus.dept_subcatg_nbr\n" +
                "            JOIN (\n" +
                "                SELECT *\n" +
                "                FROM cc_color_families\n" +
                "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
                "        WHERE UPPER(TRIM(all_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
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

    private String findFineLineQuery() {
        return "cc_color_families AS (\n" +
                "    SELECT JSON_EXTRACT_SCALAR(cc_color_families_json, '$.cc') AS cc,\n" +
                "        JSON_EXTRACT_SCALAR(cc_color_families_json, '$.color_family_desc') AS color_family_desc\n" +
                "    FROM data,\n" +
                "        UNNEST(JSON_EXTRACT_ARRAY(json_array)) AS cc_color_families_json\n" +
                ")\n" +
                "SELECT rpt_lvl_0_nbr,\n" +
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
                "    STRING_AGG(DISTINCT CAST(store_nbr AS STRING), ',') AS store_list,\n" +
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
                "                                    h.like_rpt_lvl_1_nbr,\n" +
                "                                    h.fiscal_year,\n" +
                "                                    h.season_code,\n" +
                "                                    week,\n" +
                "                                    store,\n" +
                "                                    h.rpt_lvl_0_nbr,\n" +
                "                                    h.rpt_lvl_1_nbr,\n" +
                "                                    h.rpt_lvl_2_nbr,\n" +
                "                                    h.rpt_lvl_3_nbr,\n" +
                "                                    h.rpt_lvl_4_nbr,\n" +
                "                                    h.like_fineline_nbr,\n" +
                "                                    dept,\n" +
                "                                    fineline,\n" +
                "                                    final_pref,\n" +
                "                                    allocated,\n" +
                "                                    TRIM(style_nbr) AS style_nbr,\n" +
                "                                    TRIM(cc) AS cc,\n" +
                "                                    final_alloc_space\n" +
                "                                FROM `" + bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getRFADataSetName() + "." + bigQueryConnectionProperties.getRFACCStageTable() + "`,\n" +
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
                "                FROM `" + bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() + ".svg_fl_cluster` AS svg_fl_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE svg_fl_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND svg_fl_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND svg_fl_clus.season = h.season_code\n" +
                "                    AND svg_fl_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS fl_clus ON CAST(rfa_output.store AS INT64) = fl_clus.store_nbr\n" +
                "            JOIN (\n" +
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
                "                FROM `" + bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() + ".sc_cluster` AS sc_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sc_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sc_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
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
                "                FROM `" + bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() + ".sco_cluster` AS sco_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sco_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    and sco_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sco_clus.season = h.season_code\n" +
                "                    AND sco_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS all_clus ON CAST(rfa_output.store AS INT64) = all_clus.store_nbr\n" +
                "            AND all_clus.fineline_nbr = COALESCE(\n" +
                "                rfa_output.like_fineline_nbr,\n" +
                "                rfa_output.fineline\n" +
                "            )\n" +
                "            AND rfa_output.fiscal_year = all_clus.fiscal_year\n" +
                "            AND rfa_output.season_code = all_clus.season\n" +
                "            JOIN (\n" +
                "                SELECT *\n" +
                "                FROM cc_color_families\n" +
                "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
                "        WHERE UPPER(TRIM(all_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
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
