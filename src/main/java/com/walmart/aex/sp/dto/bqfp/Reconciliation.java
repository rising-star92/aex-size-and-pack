package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

@Data
public class Reconciliation {
   private Integer remaningUnits;
   private Float weeklySellThrough;
   private Integer receiptUnit;
   private Long total;
}
