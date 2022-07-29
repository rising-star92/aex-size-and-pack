package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

@Data
public class BumpSet {
   private Float unitPct;
   private Long units;
   private String weekDesc;
   private Integer weeksOfSale;
   private Integer wmYearWeek;
   private Integer bumpPackNbr;
}
