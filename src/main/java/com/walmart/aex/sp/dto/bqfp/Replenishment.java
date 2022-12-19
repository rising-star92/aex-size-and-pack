package com.walmart.aex.sp.dto.bqfp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Replenishment {
   private Integer replnWeek;
   private String replnWeekDesc;
   private Long replnUnits;
   private Long adjReplnUnits;
   private Long remainingUnits;
   private Long dcInboundUnits;
   private Long dcInboundAdjUnits;

   public Replenishment(Integer replnWeek, String replnWeekDesc) {
      this.replnWeek = replnWeek;
      this.replnWeekDesc = replnWeekDesc;
   }
}
