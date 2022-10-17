package com.walmart.aex.sp.dto.commitmentreport;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metrics {
	  private String size;
	  private Integer ratio;
	  private Integer quantity;
	  private String ccId;
	  private String merchMethod;
}