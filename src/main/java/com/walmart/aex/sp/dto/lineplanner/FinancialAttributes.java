package com.walmart.aex.sp.dto.lineplanner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.walmart.aex.sp.dto.bqfp.WeeksDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinancialAttributes implements Serializable {
   private WeeksDTO transactableEnd;
   private WeeksDTO transactableStart;
}
