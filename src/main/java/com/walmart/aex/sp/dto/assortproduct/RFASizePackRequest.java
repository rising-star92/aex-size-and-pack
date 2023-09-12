package com.walmart.aex.sp.dto.assortproduct;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RFASizePackRequest {

    private Integer plan_id;
    private Integer fiscal_year;
    private Integer seasonCode;
    private Integer rpt_lvl_0_nbr;
    private Integer rpt_lvl_1_nbr;
    private Integer rpt_lvl_2_nbr;
    private Integer rpt_lvl_3_nbr;
    private Integer rpt_lvl_4_nbr;
    private Integer fineline_nbr;

    private Integer like_fineline_nbr;
    private Integer like_lvl1_nbr;
    private List<ColorDefinition> colors;


}
