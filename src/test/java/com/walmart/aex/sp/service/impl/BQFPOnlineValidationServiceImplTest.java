package com.walmart.aex.sp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessage;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BQFPOnlineValidationServiceImplTest {

    @InjectMocks
    BqfpOnlineValidationServiceImpl bqfpOnlineValidationServiceImpl;
    ObjectMapper mapper = new ObjectMapper();
    ValidationResult actualValidationResults;

    @Test
    void test_validateNoValidationErrors() throws IOException {
        List<MerchMethodsDto> merchMethodsDtos = new ArrayList<>();
        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.ONLINE_FIXTURE.getCode());
        merchMethodsDto.setMerchMethod("ONLINE_MERCH_METHOD");
        merchMethodsDto.setMerchMethodCode(0);
        merchMethodsDtos.add(merchMethodsDto);

        final String path = "/plan12fineline5160";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        FinelineDto finelineDto = finelineDtoFromJson(path.concat("/fineLineDto"));

        StyleDto styleDto = finelineDto.getStyles().stream().filter(style -> style.getStyleNbr().equalsIgnoreCase("34_5160_3_24_003")).findFirst().orElse(new StyleDto());
        CustomerChoiceDto customerChoiceDto = styleDto.getCustomerChoices().stream().filter(cc -> cc.getCcId().equalsIgnoreCase("34_5160_3_24_003_COOL MULTI")).findFirst().orElse(new CustomerChoiceDto());

        actualValidationResults = bqfpOnlineValidationServiceImpl.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);

        assertEquals(new HashSet<>(), actualValidationResults.getCodes());
    }

    @Test
    void test_validateWithValidationErrors() throws IOException {
        List<MerchMethodsDto> merchMethodsDtos = new ArrayList<>();
        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.ONLINE_FIXTURE.getCode());
        merchMethodsDto.setMerchMethod("ONLINE_MERCH_METHOD");
        merchMethodsDto.setMerchMethodCode(0);
        merchMethodsDtos.add(merchMethodsDto);

        final String path = "/plan12fineline5160";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        FinelineDto finelineDto = finelineDtoFromJson(path.concat("/fineLineDto"));

        StyleDto styleDto = finelineDto.getStyles().stream().filter(style -> style.getStyleNbr().equalsIgnoreCase("34_5160_3_24_002")).findFirst().orElse(new StyleDto());
        CustomerChoiceDto customerChoiceDto = styleDto.getCustomerChoices().stream().filter(cc -> cc.getCcId().equalsIgnoreCase("34_5160_3_24_002_BLACK SOOT")).findFirst().orElse(new CustomerChoiceDto());

        actualValidationResults = bqfpOnlineValidationServiceImpl.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);

        assertEquals(Set.of(AppMessage.BQFP_MISSING_REPLN_UNITS.getId()), actualValidationResults.getCodes());
    }

    @Test
    void test_validateWithNegativeErrors() throws IOException {
        List<MerchMethodsDto> merchMethodsDtos = new ArrayList<>();
        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.ONLINE_FIXTURE.getCode());
        merchMethodsDto.setMerchMethod("ONLINE_MERCH_METHOD");
        merchMethodsDto.setMerchMethodCode(0);
        merchMethodsDtos.add(merchMethodsDto);

        final String path = "/plan12fineline5160";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponseWithNegativeValues"));
        FinelineDto finelineDto = finelineDtoFromJson(path.concat("/fineLineDto"));

        StyleDto styleDto = finelineDto.getStyles().stream().filter(style -> style.getStyleNbr().equalsIgnoreCase("34_5160_3_24_002")).findFirst().orElse(new StyleDto());
        CustomerChoiceDto customerChoiceDto = styleDto.getCustomerChoices().stream().filter(cc -> cc.getCcId().equalsIgnoreCase("34_5160_3_24_002_NA")).findFirst().orElse(new CustomerChoiceDto());

        actualValidationResults = bqfpOnlineValidationServiceImpl.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);

        assertEquals(Set.of(AppMessage.BQFP_REPLN_NEGATIVE_UNITS.getId()), actualValidationResults.getCodes());
    }

    BQFPResponse bqfpResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BQFPResponse.class);
    }

    FinelineDto finelineDtoFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), FinelineDto.class);
    }

    String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }
}