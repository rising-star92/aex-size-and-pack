package com.walmart.aex.sp.dto.bqfp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialSet {
   private Integer weeksOfSale;
   private Long initialSetUnitsPerFix;
   private Long totalInitialSetUnits;
}
