package com.walmart.aex.sp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class AdjustedDCInboundQtyTest {

	@InjectMocks
	@Spy
	AdjustedDCInboundQty adjustedDCInboundQty;
	
	@Mock
	private ObjectMapper objectMapper;
	 
	    @Test
	    void testUpdatedAdjustedDcInboundQty(){    	

	    	String replenishment="[{\"replnWeek\":null,\"replnWeekDesc\":\"FYE2024WK01\",\"replnUnits\":null,\"adjReplnUnits\":500,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":500}]";
	    	Integer vnpkCnt=5;

	    	List<Replenishment> replobjectDTO = new ArrayList<>();
			try {
				objectMapper = new ObjectMapper();
				replobjectDTO = Arrays.asList(objectMapper.readValue(replenishment, Replenishment[].class));

			} catch (JsonProcessingException e) {
				log.error("Error parsing replenishment object", e);
			}

	    	adjustedDCInboundQty.updatedAdjustedDcInboundQty(replobjectDTO, vnpkCnt);


	    	assertEquals(500,replobjectDTO.get(0).getDcInboundAdjUnits());
	    }}