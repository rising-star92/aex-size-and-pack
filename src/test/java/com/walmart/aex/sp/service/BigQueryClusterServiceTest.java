package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.bigquery.BigQuery;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BigQueryClusterServiceTest {
    @InjectMocks
    private BigQueryClusterService bigQueryClusterService;
    @Mock
    private BigQueryConnectionProperties bigQueryConnectionProperties;
    @Mock
    private BigQuery bigQuery;
    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        bigQueryClusterService = new BigQueryClusterService(new ObjectMapper(),bigQuery);
        ReflectionTestUtils.setField(bigQueryClusterService, "bigQueryConnectionProperties", bigQueryConnectionProperties);
        setProperties();
    }

    private void setProperties() {
        lenient().when(bigQueryConnectionProperties.getDESizeClusterFeatureFlag()).thenReturn("true");
        lenient().when(bigQueryConnectionProperties.getRFAProjectId()).thenReturn("wmt-e12743607538928aa17e0e22f9");
        lenient().when(bigQueryConnectionProperties.getAnalyticsData()).thenReturn("analytics_data_prod");
        lenient().when(bigQueryConnectionProperties.getRFADataSetName()).thenReturn("commitment_report_rfa_output_prod");
        lenient().when(bigQueryConnectionProperties.getRFACCStageTable()).thenReturn("rfa_cc_out_parquet");
        lenient().when(bigQueryConnectionProperties.getSizeClusterStoreFl()).thenReturn("size_clust_store_fineline");
        lenient().when(bigQueryConnectionProperties.getSizeCluster()).thenReturn("sc_cluster");
        lenient().when(bigQueryConnectionProperties.getSizeColorCluster()).thenReturn("sco_cluster");
        lenient().when(bigQueryConnectionProperties.getFinelineVolumeCluster()).thenReturn("svg_fl_cluster");
        lenient().when(bigQueryConnectionProperties.getSubCategoryVolumeCluster()).thenReturn("svg_subcategory_cluster");
        lenient().when(bigQueryConnectionProperties.getCategoryVolumeCluster()).thenReturn("svg_category_cluster");

    }
    @Test
    void generateQueryWhenVolumeDeviationLevelIsFinelineTest(){
        String volumeDeviationLevel = "Fineline";
        String expectedQuery = getExpectedQueryForFineline();
        String response = bigQueryClusterService.generateQuery(volumeDeviationLevel);
        Assert.assertEquals(expectedQuery,response);
    }
    @Test
    void generateQueryWhenVolumeDeviationLevelIsSubCategoryTest(){
        String volumeDeviationLevel = "Sub_Category";
        String expectedQuery = getExpectedQueryForSubCategory();
        String response = bigQueryClusterService.generateQuery(volumeDeviationLevel);
        Assert.assertEquals(expectedQuery,response);
    }
    @Test
    void generateQueryWhenVolumeDeviationLevelIsCategoryTest(){
        String volumeDeviationLevel = "Category";
        String expectedQuery = getExpectedQueryForCategory();
        String response = bigQueryClusterService.generateQuery(volumeDeviationLevel);
        Assert.assertEquals(expectedQuery,response);
    }

    private String getExpectedQueryForFineline() {
        return "WITH MyTable AS ( \n" +
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
                "), cc_color_families AS (\n" +
                "    SELECT JSON_EXTRACT_SCALAR(cc_color_families_json, '$.cc') AS cc,\n" +
                "        JSON_EXTRACT_SCALAR(cc_color_families_json, '$.color_family_desc') AS color_family_desc\n" +
                "    FROM data,\n" +
                "        UNNEST(JSON_EXTRACT_ARRAY(json_array)) AS cc_color_families_json\n" +
                ")SELECT rpt_lvl_0_nbr,\n" +
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
                "            COALESCE(size_clus.color_family, 'DEFAULT') AS color_family,\n" +
                "            COALESCE(size_clus.cluster_id, 1) AS size_cluster_id,\n" +
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
                "                                FROM `wmt-e12743607538928aa17e0e22f9.commitment_report_rfa_output_prod.rfa_cc_out_parquet`,\n" +
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
                "                FROM `wmt-e12743607538928aa17e0e22f9.analytics_data_prod.svg_fl_cluster` AS svg_fl_clus,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE svg_fl_clus.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND svg_fl_clus.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND svg_fl_clus.season = h.season_code\n" +
                "                    AND svg_fl_clus.fiscal_year = h.fiscal_year\n" +
                "            ) AS fl_clus ON CAST(rfa_output.store AS INT64) = fl_clus.store_nbr\n" +
                "            LEFT JOIN (\n" +
                "                SELECT sc_store_fl.fineline_nbr,\n" +
                "                    sc_store_fl.dept_nbr,\n" +
                "                    sc_store_fl.store_nbr,\n" +
                "                    sc_store_fl.cluster_id,\n" +
                "                    sc_store_fl.dept_catg_nbr,\n" +
                "                    sc_store_fl.dept_subcatg_nbr,\n" +
                "                    sc_store_fl.color_family,\n" +
                "                    sc_store_fl.fiscal_year,\n" +
                "                    sc_store_fl.season\n" +
                "                FROM `wmt-e12743607538928aa17e0e22f9.analytics_data_prod.size_clust_store_fineline` AS sc_store_fl,\n" +
                "                    plan_hierarchy AS h\n" +
                "                WHERE sc_store_fl.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
                "                    AND sc_store_fl.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
                "                    AND sc_store_fl.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
                "                    AND sc_store_fl.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
                "                    AND sc_store_fl.season = h.season_code\n" +
                "                    AND sc_store_fl.fiscal_year = h.fiscal_year\n" +
                "            ) AS size_clus ON CAST(rfa_output.store AS INT64) = size_clus.store_nbr\n" +
                "            AND size_clus.fineline_nbr = COALESCE(rfa_output.like_fineline_nbr, rfa_output.fineline)\n" +
                "            AND rfa_output.fiscal_year = size_clus.fiscal_year\n" +
                "            AND rfa_output.season_code = size_clus.season\n" +
                "            LEFT JOIN (\n" +
                "                SELECT *\n" +
                "                FROM cc_color_families\n" +
                "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
                "        WHERE UPPER(TRIM(size_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
                "        OR size_clus.fineline_nbr is NULL\n" +
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
                "    volume_group_cluster_id ) SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable;";
    }

    private String getExpectedQueryForSubCategory(){return "WITH MyTable AS ( \n" +
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
            "), cc_color_families AS (\n" +
            "    SELECT JSON_EXTRACT_SCALAR(cc_color_families_json, '$.cc') AS cc,\n" +
            "        JSON_EXTRACT_SCALAR(cc_color_families_json, '$.color_family_desc') AS color_family_desc\n" +
            "    FROM data,\n" +
            "        UNNEST(JSON_EXTRACT_ARRAY(json_array)) AS cc_color_families_json\n" +
            ")SELECT rpt_lvl_0_nbr,\n" +
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
            "            COALESCE(size_clus.color_family, 'DEFAULT') AS color_family,\n" +
            "            COALESCE(size_clus.cluster_id, 1) AS size_cluster_id,\n" +
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
            "                                FROM `wmt-e12743607538928aa17e0e22f9.commitment_report_rfa_output_prod.rfa_cc_out_parquet`,\n" +
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
            "                FROM `wmt-e12743607538928aa17e0e22f9.analytics_data_prod.svg_subcategory_cluster` AS svg_subcatg_clus,\n" +
            "                    plan_hierarchy AS h\n" +
            "                WHERE svg_subcatg_clus.dept_subcatg_nbr = h.rpt_lvl_4_nbr\n" +
            "                    AND svg_subcatg_clus.dept_nbr = h.rpt_lvl_1_nbr\n" +
            "                    AND svg_subcatg_clus.season = h.season_code\n" +
            "                    AND svg_subcatg_clus.fiscal_year = h.fiscal_year\n" +
            "            ) AS subcatg_clus ON CAST(rfa_output.store AS INT64) = subcatg_clus.store_nbr\n" +
            "            LEFT JOIN (\n" +
            "                SELECT sc_store_fl.fineline_nbr,\n" +
            "                    sc_store_fl.store_nbr,\n" +
            "                    sc_store_fl.cluster_id,\n" +
            "                    sc_store_fl.dept_catg_nbr,\n" +
            "                    sc_store_fl.dept_subcatg_nbr,\n" +
            "                    sc_store_fl.color_family,\n" +
            "                    sc_store_fl.fiscal_year,\n" +
            "                    sc_store_fl.season\n" +
            "                FROM `wmt-e12743607538928aa17e0e22f9.analytics_data_prod.size_clust_store_fineline` AS sc_store_fl,\n" +
            "                    plan_hierarchy AS h\n" +
            "                WHERE sc_store_fl.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
            "                    AND sc_store_fl.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
            "                    AND sc_store_fl.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
            "                    AND sc_store_fl.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
            "                    AND sc_store_fl.season = h.season_code\n" +
            "                    AND sc_store_fl.fiscal_year = h.fiscal_year\n" +
            "            ) AS size_clus ON CAST(rfa_output.store AS INT64) = size_clus.store_nbr\n" +
            "            AND size_clus.fineline_nbr = COALESCE(\n" +
            "                rfa_output.like_fineline_nbr,\n" +
            "                rfa_output.fineline\n" +
            "            )\n" +
            "            AND size_clus.dept_subcatg_nbr = COALESCE(\n" +
            "                rfa_output.like_rpt_lvl_4_nbr,\n" +
            "                rfa_output.rpt_lvl_4_nbr\n" +
            "            )\n" +
            "            AND rfa_output.fiscal_year = size_clus.fiscal_year\n" +
            "            AND rfa_output.season_code = size_clus.season\n" +
            "            LEFT JOIN (\n" +
            "                SELECT *\n" +
            "                FROM cc_color_families\n" +
            "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
            "        WHERE UPPER(TRIM(size_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
            "        OR size_clus.fineline_nbr is NULL\n" +
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
            "    volume_group_cluster_id ) SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable;";}

    private String getExpectedQueryForCategory(){
        return "WITH MyTable AS ( \n" +
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
            "), cc_color_families AS (\n" +
            "    SELECT JSON_EXTRACT_SCALAR(cc_color_families_json, '$.cc') AS cc,\n" +
            "        JSON_EXTRACT_SCALAR(cc_color_families_json, '$.color_family_desc') AS color_family_desc\n" +
            "    FROM data,\n" +
            "        UNNEST(JSON_EXTRACT_ARRAY(json_array)) AS cc_color_families_json\n" +
            ")SELECT rpt_lvl_0_nbr,\n" +
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
            "            COALESCE(size_clus.color_family, 'DEFAULT') AS color_family,\n" +
            "            COALESCE(size_clus.cluster_id, 1) AS size_cluster_id,\n" +
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
            "                                FROM `wmt-e12743607538928aa17e0e22f9.commitment_report_rfa_output_prod.rfa_cc_out_parquet`,\n" +
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
            "                FROM `wmt-e12743607538928aa17e0e22f9.analytics_data_prod.svg_category_cluster` AS svg_catg_clus,\n" +
            "                    plan_hierarchy AS h\n" +
            "                WHERE svg_catg_clus.dept_catg_nbr = h.rpt_lvl_3_nbr\n" +
            "                    AND svg_catg_clus.dept_nbr = h.rpt_lvl_1_nbr\n" +
            "                    AND svg_catg_clus.season = h.season_code\n" +
            "                    AND svg_catg_clus.fiscal_year = h.fiscal_year\n" +
            "            ) AS catg_clus ON CAST(rfa_output.store AS INT64) = catg_clus.store_nbr\n" +
            "            LEFT JOIN (\n" +
            "                SELECT sc_store_fl.fineline_nbr,\n" +
            "                    sc_store_fl.dept_nbr,\n" +
            "                    sc_store_fl.store_nbr,\n" +
            "                    sc_store_fl.cluster_id,\n" +
            "                    sc_store_fl.dept_catg_nbr,\n" +
            "                    sc_store_fl.dept_subcatg_nbr,\n" +
            "                    sc_store_fl.color_family,\n" +
            "                    sc_store_fl.fiscal_year,\n" +
            "                    sc_store_fl.season\n" +
            "                FROM `wmt-e12743607538928aa17e0e22f9.analytics_data_prod.size_clust_store_fineline` AS sc_store_fl,\n" +
            "                    plan_hierarchy AS h\n" +
            "                WHERE sc_store_fl.fineline_nbr = COALESCE(h.like_fineline_nbr, h.fineline_nbr)\n" +
            "                    AND sc_store_fl.dept_nbr = COALESCE(h.like_rpt_lvl_1_nbr, h.rpt_lvl_1_nbr)\n" +
            "                    AND sc_store_fl.dept_catg_nbr = COALESCE(h.like_rpt_lvl_3_nbr, h.rpt_lvl_3_nbr)\n" +
            "                    AND sc_store_fl.dept_subcatg_nbr = COALESCE(h.like_rpt_lvl_4_nbr, h.rpt_lvl_4_nbr)\n" +
            "                    AND sc_store_fl.season = h.season_code\n" +
            "                    AND sc_store_fl.fiscal_year = h.fiscal_year\n" +
            "            ) AS size_clus ON CAST(rfa_output.store AS INT64) = size_clus.store_nbr\n" +
            "            AND size_clus.dept_catg_nbr = COALESCE(\n" +
            "                rfa_output.like_rpt_lvl_3_nbr,\n" +
            "                rfa_output.rpt_lvl_3_nbr\n" +
            "                )\n" +
            "            AND size_clus.fineline_nbr = COALESCE(\n" +
            "                rfa_output.like_fineline_nbr,\n" +
            "                rfa_output.fineline\n" +
            "            )\n" +
            "            AND rfa_output.fiscal_year = size_clus.fiscal_year\n" +
            "            AND rfa_output.season_code = size_clus.season\n" +
            "            LEFT JOIN (\n" +
            "                SELECT *\n" +
            "                FROM cc_color_families\n" +
            "            ) AS cc_color ON rfa_output.cc = cc_color.cc\n" +
            "        WHERE UPPER(TRIM(size_clus.color_family)) = UPPER(TRIM(cc_color.color_family_desc))\n" +
            "        OR size_clus.fineline_nbr is NULL\n" +
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
            "    volume_group_cluster_id) SELECT TO_JSON_STRING(gcpTable) AS json FROM MyTable AS gcpTable;";}
}
