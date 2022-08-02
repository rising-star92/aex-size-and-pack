package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

@Data
public class Replenishment {
   private Integer wmYearWeek;
   private String weekDesc;
   private Long replenishUnits;
}
