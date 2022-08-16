package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuyQuantitiesDto {

	   private Integer isUnits;
	    private List<Integer> storeList;
	    private Integer sizeCluster;
	    private Integer volumeCluster;
	    private List<BumpSetDto>bumpSets;
	   
	  @Override
	    public String toString() {
	        return "{" +
	                "\"isUnits\":" + isUnits +
	                ", \"storeList\":\"" + storeList + '\"' +
	                 ", \"sizeCluster\":\"" + sizeCluster + '\"' +
	                ", \"volumeCluster\":\"" + volumeCluster + '\"' +
	                 ", \"bumpSets\":\"" + bumpSets.toString() + '\"' +
	                '}';
	    }

}
