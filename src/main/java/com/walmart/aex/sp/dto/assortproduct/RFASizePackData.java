package com.walmart.aex.sp.dto.assortproduct;

import lombok.Data;

@Data
public class RFASizePackData {
   private Integer rpt_lvl_0_nbr;
   private Integer rpt_lvl_1_nbr;
   private Integer rpt_lvl_2_nbr;
   private Integer rpt_lvl_3_nbr;
   private Integer rpt_lvl_4_nbr;
   private Integer fineline_nbr;
   private String style_nbr;
   private String customer_choice;
   private String fixture_type;
   private Float fixture_group;
   private String color_family;
   private Integer size_cluster_id;
   private Integer volume_group_cluster_id;
   private String store_list;
   private Integer store_cnt;
   private Long plan_id_partition;
}
