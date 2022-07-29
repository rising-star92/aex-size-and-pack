package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

@Data
public class Metrics {
   private String inStoredate;
   private String markdownDate;
   private Integer sellingWeeks;
   private Integer minPresentationUnits;
   private Integer maxPresentationUnits;
   private Integer totalStoresAllocated;
   private Float storeProductivity;
}
