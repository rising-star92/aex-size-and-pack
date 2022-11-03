package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.commitmentreport.InitialBumpSetResponse;
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponses;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class InitialSetPlanMapperTest {

	@InjectMocks
	InitialSetPlanMapper initialSetPlanMapper;
	
	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void getInitialBumpSetDataTest() {
		RFAInitialSetBumpSetResponses rfaInitialSetBumpSetResponses = getInitialBumpSetFromRFAResponse();
		InitialBumpSetResponse initialBumpSetResponse = new InitialBumpSetResponse();
		Optional.of(rfaInitialSetBumpSetResponses.getRfaInitialSetBumpSetResponses()).stream().flatMap(Collection::stream).forEach(
				intialSetResponseOne -> initialSetPlanMapper.mapInitialSetPlan(intialSetResponseOne, initialBumpSetResponse, 5141));
		
		assertNotNull(initialBumpSetResponse);

		Integer fineline = initialBumpSetResponse.getFinelineNbr();
		String styleId = initialBumpSetResponse.getIntialSetStyles().get(0).getStyleId();

		assertEquals(5141, fineline);
		assertEquals("34_5141_4_21_11", styleId);

	}

	private RFAInitialSetBumpSetResponses getInitialBumpSetFromRFAResponse() {
		RFAInitialSetBumpSetResponses rfaResponse = null;
		try {
			rfaResponse = mapper.readValue(readJsonFileAsString("RFAInitialBumpSetResponse"), RFAInitialSetBumpSetResponses.class);
		} catch (JsonMappingException e) {
			log.error("JsonMappingException \n" + e.getMessage());
		} catch (JsonProcessingException e) {
			log.error("JsonProcessingException \n" + e.getMessage());
		} catch (IOException e) {
			log.error("IOException \n" + e.getMessage());
		}
		return rfaResponse;
	}
	
	private String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }

}
