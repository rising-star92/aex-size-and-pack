package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessage;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.enums.MerchMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RFAValidationServiceImplTest {

    private APResponse apResponse;
    private CustomerChoiceDto customerChoiceDto;
    private List<MerchMethodsDto> merchMethodsDtoList;

    private ValidationResult validationResult;

    @InjectMocks
    private RFAValidationServiceImpl rfaValidationService;

    @BeforeEach
    void setUp() {

        customerChoiceDto = new CustomerChoiceDto();
        customerChoiceDto.setCcId("CC1");
        customerChoiceDto.setColorFamily("BLUE");

        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureType(FixtureTypeRollup.RACKS.getDescription());
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.RACKS.getCode());
        merchMethodsDto.setMerchMethod(MerchMethod.HANGING.getDescription());
        merchMethodsDto.setMerchMethodCode(MerchMethod.HANGING.getId());

        merchMethodsDtoList = List.of(merchMethodsDto);

        apResponse = new APResponse();
    }

    @Test
    void validateRFADataWithEmptyListTest() {
        apResponse.setRfaSizePackData(new ArrayList<>());
        validationResult = rfaValidationService.validateRFAData(merchMethodsDtoList, apResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessage.RFA_NOT_AVAILABLE.getId(), validationResult.getCodes().toArray()[0]);
    }

    @Test
    void validateRFADataWithCCNotAvailableTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC2");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("BLUE");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1));
        validationResult = rfaValidationService.validateRFAData(merchMethodsDtoList, apResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessage.RFA_CC_NOT_AVAILABLE.getId(), validationResult.getCodes().toArray()[0]);
    }

    @Test
    void validateRFADataWithMissingFixtureTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC1");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("BLUE");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1));
        validationResult = rfaValidationService.validateRFAData(merchMethodsDtoList, apResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessage.RFA_MISSING_FIXTURE.getId(), validationResult.getCodes().toArray()[0]);
    }

    @Test
    void validateRFADataWithMissingColorFamilyTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC1");
        rfaSizePackData1.setFixture_type("RACKS");
        rfaSizePackData1.setColor_family("BROWN");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1));
        validationResult = rfaValidationService.validateRFAData(merchMethodsDtoList, apResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessage.RFA_MISSING_COLOR_FAMILY.getId(), validationResult.getCodes().toArray()[0]);
    }

    @Test
    void validateRFADataWithDefaultColorFamilyTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC1");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("DEFAULT");

        RFASizePackData rfaSizePackData2 = new RFASizePackData();
        rfaSizePackData2.setCustomer_choice("CC1");
        rfaSizePackData2.setFixture_type("RACKS");
        rfaSizePackData2.setColor_family("DEFAULT");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1, rfaSizePackData2));
        validationResult = rfaValidationService.validateRFAData(merchMethodsDtoList, apResponse, "Style1", customerChoiceDto);
        assertEquals(0, validationResult.getCodes().size());
    }

    @Test
    void validateRFADataWithMissingFixtureAndColorFamilyTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC1");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("BROWN");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1));
        validationResult = rfaValidationService.validateRFAData(merchMethodsDtoList, apResponse, "Style1", customerChoiceDto);
        assertEquals(2, validationResult.getCodes().size());
        assertTrue(validationResult.getCodes().contains(AppMessage.RFA_MISSING_FIXTURE.getId()));
        assertTrue(validationResult.getCodes().contains(AppMessage.RFA_MISSING_COLOR_FAMILY.getId()));
    }
}