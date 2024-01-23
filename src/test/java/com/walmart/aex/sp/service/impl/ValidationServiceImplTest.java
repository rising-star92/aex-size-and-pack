package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    private APResponse apResponse;
    private BQFPResponse bqfpResponse;
    private CustomerChoiceDto customerChoiceDto;

    @Mock
    private RFAValidationServiceImpl rfaValidationService;
    @InjectMocks
    private ValidationServiceImpl validationService;

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
        apResponse.setRfaSizePackData(new ArrayList<>());
    }

    @Test
    void validateCalculateBuyQuantityInputData() {
        Mockito.when(rfaValidationService.validateRFAData(apResponse, bqfpResponse, "Style1", customerChoiceDto)).thenReturn(ValidationResult.builder().messages(List.of(AppMessageText.RFA_NOT_AVAILABLE.getId())).build());
        ValidationResult validationResult = validationService.validateCalculateBuyQuantityInputData(apResponse, bqfpResponse, "Style1", customerChoiceDto);
        assertEquals(1, validationResult.getMessages().size());
        assertEquals(AppMessageText.RFA_NOT_AVAILABLE.getId(), validationResult.getMessages().get(0));
    }
}