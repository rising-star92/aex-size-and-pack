package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class StoreObjectDto {

	private List<BuyQuantitiesDto> buyQuantities;
	

	 
	}



