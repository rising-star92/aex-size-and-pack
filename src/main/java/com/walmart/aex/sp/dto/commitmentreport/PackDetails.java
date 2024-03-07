package com.walmart.aex.sp.dto.commitmentreport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackDetails {
	  private String packId;
	  private String packDescription;
	  private List<Metrics> metrics;
	  private String uuId;
	  private Integer bumpPackNbr;
	  private Set<String> groupingTypes = new HashSet<>();
}