package com.walmart.aex.sp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdjustedDCInboundQty {
	Long adjustedDcInboundQty = 500L;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public String updatedAdjustedDcInboundQty(String replobject, Double VnpkWhpkRatio) {

		List<Replenishment> replobjectDTO = jsonStringToAdjustedDCInboundQtyMapper(replobject);
		replobjectDTO.forEach(replobj -> {
			int noOfVendorPacks = (int) Math.ceil(adjustedDcInboundQty / VnpkWhpkRatio);
			Long updatedAdjustedDcInboundQty = (long) (noOfVendorPacks * VnpkWhpkRatio);
			replobj.setDcInboundAdjUnits(updatedAdjustedDcInboundQty);

		});
		String updatedreplobjecteObj = adjustedDCInboundQtyMapperToJsonStringMapper(replobjectDTO);
		return updatedreplobjecteObj;
	}

	public List<Replenishment> jsonStringToAdjustedDCInboundQtyMapper(String replobject) {
		List<Replenishment> replobjectDTOs = new ArrayList<>();

		try {
			replobjectDTOs = Arrays.asList(objectMapper.readValue(replobject, Replenishment[].class));
		} catch (JsonMappingException e) {
			log.error("Error mapping json string replenishment object", e);

		} catch (JsonProcessingException e) {
			log.error("Error parsing replenishment object", e);
		}
		return replobjectDTOs;
	}

	public String adjustedDCInboundQtyMapperToJsonStringMapper(List<Replenishment> replobjectDTO) {
		String updatedReplobjecteObj = null;

		try {
			updatedReplobjecteObj = objectMapper.writeValueAsString(replobjectDTO);
		} catch (JsonProcessingException e) {
			log.error("Error parsing replenishment object", e);
		}
		return updatedReplobjecteObj;
	}
}