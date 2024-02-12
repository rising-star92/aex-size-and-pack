package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.CustomerChoice;
import com.walmart.aex.sp.dto.bqfp.Fixture;
import com.walmart.aex.sp.dto.bqfp.Style;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessage;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.enums.MerchMethod;
import com.walmart.aex.sp.service.BqfpValidationsService;
import com.walmart.aex.sp.service.RFAValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    private APResponse apResponse;
    private BQFPResponse bqfpResponse;
    private CustomerChoiceDto customerChoiceDto;
    private StyleDto styleDto;
    private List<MerchMethodsDto> merchMethodsDtoList;

    @Mock
    private BqfpValidationsService bqfpValidationsService;
    @Mock
    private RFAValidationService rfaValidationService;
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

        styleDto = new StyleDto();
        styleDto.setStyleNbr("Style1");
        styleDto.setCustomerChoices(List.of(customerChoiceDto));

        apResponse = new APResponse();
        apResponse.setRfaSizePackData(new ArrayList<>());

        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setFixtureType(FixtureTypeRollup.RACKS.getDescription());
        merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.RACKS.getCode());
        merchMethodsDto.setMerchMethod(MerchMethod.HANGING.getDescription());
        merchMethodsDto.setMerchMethodCode(MerchMethod.HANGING.getId());

        merchMethodsDtoList = List.of(merchMethodsDto);
    }

    @Test
    void validateCalculateBuyQuantityInputData() {
        Mockito.when(bqfpValidationsService.missingBuyQuantity(merchMethodsDtoList, bqfpResponse, styleDto, customerChoiceDto)).thenReturn(ValidationResult.builder().codes(new HashSet<>()).build());
        Mockito.when(rfaValidationService.validateRFAData(merchMethodsDtoList, apResponse, "Style1", customerChoiceDto)).thenReturn(ValidationResult.builder().codes(Set.of(AppMessage.RFA_NOT_AVAILABLE.getId())).build());
        ValidationResult validationResult = validationService.validateCalculateBuyQuantityInputData(merchMethodsDtoList, apResponse, bqfpResponse, styleDto, customerChoiceDto);
        assertEquals(1, validationResult.getCodes().size());
        assertEquals(AppMessage.RFA_NOT_AVAILABLE.getId(), validationResult.getCodes().toArray()[0]);
    }
}