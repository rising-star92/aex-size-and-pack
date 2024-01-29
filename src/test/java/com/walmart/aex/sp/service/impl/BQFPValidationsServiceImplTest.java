package com.walmart.aex.sp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessageText;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.enums.MerchMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BQFPValidationsServiceImplTest {

    BQFPValidationsServiceImpl bqfpValidationsService = new BQFPValidationsServiceImpl();
    ObjectMapper mapper = new ObjectMapper();

    ValidationResult actualValidationResults;

    @Test
    void test_validateNoValidationErrors() throws IOException {
        List<MerchMethodsDto> merchMethodsDtos = new ArrayList<>();
        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureType(FixtureTypeRollup.RACKS.getDescription());
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.RACKS.getCode());
        merchMethodsDto.setMerchMethod(MerchMethod.HANGING.getDescription());
        merchMethodsDto.setMerchMethodCode(MerchMethod.HANGING.getId());
        merchMethodsDtos.add(merchMethodsDto);

        final String path = "/plan72fineline4440";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));

        StyleDto styleDto = buyQtyResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0);
        CustomerChoiceDto customerChoiceDto = styleDto.getCustomerChoices().get(0);

        actualValidationResults = bqfpValidationsService.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);

        assertEquals(new HashSet<>(), actualValidationResults.getCodes());
    }

    @Test
    void test_validateMissingISData() throws IOException {
        List<MerchMethodsDto> merchMethodsDtos = new ArrayList<>();
        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureType(FixtureTypeRollup.RACKS.getDescription());
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.RACKS.getCode());
        merchMethodsDto.setMerchMethod(MerchMethod.HANGING.getDescription());
        merchMethodsDto.setMerchMethodCode(MerchMethod.HANGING.getId());
        merchMethodsDtos.add(merchMethodsDto);

        final String path = "/plan72fineline4440";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponse"));

        bqfpResponse.getStyles().get(0).getCustomerChoices().get(0).getFixtures().get(0).setClusters(new ArrayList<>());

        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));

        StyleDto styleDto = buyQtyResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0);
        CustomerChoiceDto customerChoiceDto = styleDto.getCustomerChoices().get(0);

        actualValidationResults = bqfpValidationsService.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);

        assertEquals(Set.of(AppMessageText.BQFP_MISSING_IS_DATA.getId()), actualValidationResults.getCodes());
    }

    @Test
    void test_validateMissingISandBSandReplnQty() throws IOException {
        List<MerchMethodsDto> merchMethodsDtos = new ArrayList<>();
        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureType(FixtureTypeRollup.RACKS.getDescription());
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.RACKS.getCode());
        merchMethodsDto.setMerchMethod(MerchMethod.HANGING.getDescription());
        merchMethodsDto.setMerchMethodCode(MerchMethod.HANGING.getId());
        merchMethodsDtos.add(merchMethodsDto);

        final String path = "/plan72fineline4440";
        BQFPResponse bqfpResponse = bqfpResponseFromJson(path.concat("/BQFPResponseMissingBuyQty"));
        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson(path.concat("/BuyQtyResponse"));

        StyleDto styleDto = buyQtyResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0);
        CustomerChoiceDto customerChoiceDto = styleDto.getCustomerChoices().get(0);

        actualValidationResults = bqfpValidationsService.missingBuyQuantity(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);

        assertEquals(Set.of(AppMessageText.BQFP_MISSING_IS_QUANTITIES.getId(),
                AppMessageText.BQFP_MISSING_REPLENISHMENT_QUANTITIES.getId(),
                AppMessageText.BQFP_MISSING_BUMPSET_QUANTITIES.getId(),
                AppMessageText.BQFP_MISSING_BUMPSET_WEEKS.getId()), actualValidationResults.getCodes());
    }

    BQFPResponse bqfpResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BQFPResponse.class);
    }

    BuyQtyResponse buyQtyResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BuyQtyResponse.class);
    }

    String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }
}
