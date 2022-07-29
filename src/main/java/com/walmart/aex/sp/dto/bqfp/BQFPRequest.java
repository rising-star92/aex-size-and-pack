package com.walmart.aex.sp.dto.bqfp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BQFPRequest {

   private Integer planId;
   private String channel;
   private Integer finelineNbr;
}
