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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BigQueryPostProcessingService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @ManagedConfiguration
    BigQueryConnectionProperties bigQueryConnectionProperties;

    public List<RFASizePackData> fetchRFASizePackData(RFASizePackRequest request, String volumeDeviationLevel) throws InterruptedException, JsonProcessingException {
        String gcpQuery = generateQuery(request, volumeDeviationLevel);
        BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();
        QueryJobConfiguration queryConfigIs = QueryJobConfiguration.newBuilder(gcpQuery).build();
        TableResult resultsIs = bigQuery.query(queryConfigIs);
        List<RFASizePackData> results = new ArrayList<>();
        resultsIs.iterateAll().forEach(rows -> rows.forEach(row -> {
            RFASizePackData rowValue = null;
            try {
                rowValue = objectMapper.readValue(row.getValue().toString(), RFASizePackData.class);
            } catch (JsonProcessingException e) {
                log.error("Exception ", e);
            }
            results.add(rowValue);
        }));
        return results;
    }

    private String generateQuery(RFASizePackRequest request, String volumeDeviationLevel) throws JsonProcessingException {
        String colorJson = "WITH data AS ( SELECT" + objectMapper.writeValueAsString(request.getColors()) + " AS json_array ),";
        String planHierarchy = " plan_hierarchy AS ( select " +
                request.getPlan_id() + " AS plan_id " +
                request.getRpt_lvl_0_nbr() + " AS rpt_lvl_0_nbr " +
                request.getRpt_lvl_1_nbr() + " AS rpt_lvl_1_nbr " +
                request.getRpt_lvl_2_nbr() + " AS rpt_lvl_2_nbr " +
                request.getRpt_lvl_3_nbr() + " AS rpt_lvl_3_nbr " +
                request.getRpt_lvl_4_nbr() + " AS rpt_lvl_4_nbr " +
                request.getFineline_nbr() + " AS fineline_nbr " +
                request.getLike_fineline_nbr() + " AS like_fineline_nbr " +
                request.getLike_lvl1_nbr() + " AS like_lvl1_nbr " +
                request.getFiscal_year() + " AS fiscal_year " +
                request.getSeasonCode() + " AS season_code " +
                "), ";
        StringBuilder queryBuilder = new StringBuilder(colorJson + planHierarchy);
        if (volumeDeviationLevel.equalsIgnoreCase("fineline")) {
            queryBuilder.append(findFineLineQuery());
        } else if (volumeDeviationLevel.equalsIgnoreCase("subcategory")) {
            queryBuilder.append(findSubCatQuery());

        } else {
            queryBuilder.append(findCatQuery());
        }
        return queryBuilder.toString();
    }

    private String findCatQuery() {
        return "";
    }

    private String findSubCatQuery() {
        return "";
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
                "                FROM `" + bigQueryConnectionProperties.getRFAProjectId() + "." + bigQueryConnectionProperties.getAnalyticsData() + "sc_cluster` AS sc_clus,\n" +
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
                "    volume_group_cluster_id;";
    }
}
