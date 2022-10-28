package com.walmart.aex.sp.dto.currentlineplan;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lvl4 implements Serializable {
   private Integer lvl4Nbr;
   private String lvl4Name;
   private List<Fineline> finelines;
}
