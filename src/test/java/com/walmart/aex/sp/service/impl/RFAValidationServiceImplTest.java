package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.CustomerChoice;
import com.walmart.aex.sp.dto.bqfp.Fixture;
import com.walmart.aex.sp.dto.bqfp.Style;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.enums.AppMessageText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RFAValidationServiceImplTest {

    private APResponse apResponse;
    private BQFPResponse bqfpResponse;
    private CustomerChoiceDto customerChoiceDto;

    private ValidationResult validationResult;

    @InjectMocks
    private RFAValidationServiceImpl rfaValidationService;

    @BeforeEach
    void setUp() {
        Fixture fixture1 = new Fixture();
        fixture1.setFixtureType("WALLS");

        Fixture fixture2 = new Fixture();
        fixture2.setFixtureType("RACKS");

        CustomerChoice cc = new CustomerChoice();
        cc.setCcId("CC1");
        cc.setFixtures(List.of(fixture1, fixture2));

        Style style = new Style();
        style.setStyleId("Style1");
        style.setCustomerChoices(List.of(cc));

        bqfpResponse = new BQFPResponse();
        bqfpResponse.setStyles(List.of(style));

        customerChoiceDto = new CustomerChoiceDto();
        customerChoiceDto.setCcId("CC1");
        customerChoiceDto.setColorFamily("BLUE");

        apResponse = new APResponse();
    }

    @Test
    void validateRFADataWithEmptyListTest() {
        apResponse.setRfaSizePackData(new ArrayList<>());
        validationResult = rfaValidationService.validateRFAData(apResponse, bqfpResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessageText.RFA_NOT_AVAILABLE.getId(), validationResult.getCodes().get(0));
    }

    @Test
    void validateRFADataWithCCNotAvailableTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC2");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("BLUE");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1));
        validationResult = rfaValidationService.validateRFAData(apResponse, bqfpResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessageText.RFA_CC_NOT_AVAILABLE.getId(), validationResult.getCodes().get(0));
    }

    @Test
    void validateRFADataWithMissingFixtureTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC1");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("BLUE");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1));
        validationResult = rfaValidationService.validateRFAData(apResponse, bqfpResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessageText.RFA_MISSING_FIXTURE.getId(), validationResult.getCodes().get(0));
    }

    @Test
    void validateRFADataWithMissingColorFamilyTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC1");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("BROWN");

        RFASizePackData rfaSizePackData2 = new RFASizePackData();
        rfaSizePackData2.setCustomer_choice("CC1");
        rfaSizePackData2.setFixture_type("RACKS");
        rfaSizePackData2.setColor_family("BROWN");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1, rfaSizePackData2));
        validationResult = rfaValidationService.validateRFAData(apResponse, bqfpResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessageText.RFA_MISSING_COLOR_FAMILY.getId(), validationResult.getCodes().get(0));
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
        validationResult = rfaValidationService.validateRFAData(apResponse, bqfpResponse, "Style1", customerChoiceDto);
        assertEquals(0, validationResult.getCodes().size());
    }

    @Test
    void validateRFADataWithMissingFixtureAndColorFamilyTest() {
        RFASizePackData rfaSizePackData1 = new RFASizePackData();
        rfaSizePackData1.setCustomer_choice("CC1");
        rfaSizePackData1.setFixture_type("WALLS");
        rfaSizePackData1.setColor_family("BROWN");

        apResponse.setRfaSizePackData(List.of(rfaSizePackData1));
        validationResult = rfaValidationService.validateRFAData(apResponse, bqfpResponse, "Style1", customerChoiceDto);
        assertEquals(2, validationResult.getCodes().size());
        assertTrue(validationResult.getCodes().contains(AppMessageText.RFA_MISSING_FIXTURE.getId()));
        assertTrue(validationResult.getCodes().contains(AppMessageText.RFA_MISSING_COLOR_FAMILY.getId()));
    }
}