package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

@Data
public class Replenishment {
   private Integer replnWeek;
   private String replnWeekDesc;
   private Long replnUnits;
   private Long adjReplnUnits;
   private Long remainingUnits;
}
