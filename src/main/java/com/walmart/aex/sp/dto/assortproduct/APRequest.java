package com.walmart.aex.sp.dto.assortproduct;

import lombok.Data;

@Data
public class APRequest {
   private Long planId;
   private String volumeDeviationLevel;
   private Integer finelineNbr;
}
