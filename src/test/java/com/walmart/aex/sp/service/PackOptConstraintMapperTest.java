package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PackOptConstraintMapperTest {

    @InjectMocks
    private PackOptConstraintMapper packOptConstraintMapper;

    private static final Integer finelineNbr = 3470;
    private static final Long planId = 471L;
    private static final Integer vendorNbr6 = 123456;
    private static final Integer gsmSupplierId = 12345678;
    private static final Integer vendorNbr9 = 123456789;
    private static final String factoryId = "S1D0027";
    private static final String styleNbr = "34_2816_2_19_2";
    private static final String ccId = "34_2816_2_19_2_CHARCOAL GREY HEATHER";
    private static final String ccId_2 = "34_2956_1_18_1_BLUE SAPPHIRE";
    private static final String colorCombination = "4-22-1";

    @Test
    void test_packOptDetails() {
        PackOptimizationResponse packOptimizationResponse = new PackOptimizationResponse();
        PackOptimizationResponse actual = packOptConstraintMapper.packOptDetails(Collections.singletonList(getFineLines().get(0)));
        assertNotNull(packOptimizationResponse);
        assertEquals(styleNbr, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getStyleNbr());
        assertEquals(ccId, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getCcId());
        assertEquals("PUMA", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getSupplierName());
        assertEquals(vendorNbr9, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getVendorNumber6());
        assertEquals(vendorNbr6, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getVendorNumber9());
        assertEquals(gsmSupplierId, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getGsmSupplierNumber());
        assertEquals("123 - XYZ", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
    }

    @Test
    void test_packOptDetailsNonNull() {
        CustomerChoice customerChoice = new CustomerChoice();
        customerChoice.setCcId(ccId);

        Style style = new Style();
        style.setStyleNbr(styleNbr);
        style.setCustomerChoices(List.of(customerChoice));

        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(finelineNbr);
        fineline.setStyles(List.of(style));

        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(11);
        lvl4.setFinelines(List.of(fineline));

        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(13);
        lvl3.setLvl4List(List.of(lvl4));

        PackOptimizationResponse packOptimizationResponse = new PackOptimizationResponse();
        packOptimizationResponse.setPlanId(planId);
        packOptimizationResponse.setLvl0Nbr(123);
        packOptimizationResponse.setLvl1Nbr(234);
        packOptimizationResponse.setLvl2Nbr(12);
        packOptimizationResponse.setChannel("store");
        packOptimizationResponse.setLvl3List(List.of(lvl3));

        PackOptimizationResponse actual = packOptConstraintMapper.packOptDetails(Collections.singletonList(getFineLines().get(0)));
        assertEquals(finelineNbr, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        assertEquals(styleNbr, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getStyleNbr());
        assertEquals(ccId, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getCcId());

    }

    @Test
    void test_packOptDetailsShouldReturnConsolidatedSupplierNamesForFineLines() {
        List<FineLineMapperDto> requestFineLines = getFineLines();
        PackOptimizationResponse actual = packOptConstraintMapper.packOptDetails(requestFineLines);
        assertEquals(finelineNbr, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        assertEquals("PUMA", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getSupplierName());
        assertEquals("INDIA, USA", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getConstraints().getColorCombinationConstraints().getCountryOfOrigin());
        assertEquals("INDIA, USA", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getConstraints().getColorCombinationConstraints().getPortOfOrigin());
    }

    @Test
    void test_packOptDetailsShouldReturnConsolidatedSupplierNamesForStyles() {
        List<FineLineMapperDto> requestFineLines = getFineLines();
        PackOptimizationResponse actual = packOptConstraintMapper.packOptDetails(requestFineLines);
        assertEquals(finelineNbr, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        assertEquals("PUMA", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getSuppliers().get(0).getSupplierName());
        assertEquals("INDIA, USA", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getCountryOfOrigin());
        assertEquals("INDIA, USA", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getPortOfOrigin());
    }

    @Test
    void test_packOptDetailsShouldReturnConsolidatedFactoryNames() {
        List<FineLineMapperDto> requestFineLines = getFineLines();
        PackOptimizationResponse actual = packOptConstraintMapper.packOptDetails(requestFineLines);
        assertEquals(finelineNbr, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        assertEquals("123 - XYZ, 234 - ABC", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
        assertEquals("123 - XYZ, 234 - ABC", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
        assertEquals("123 - XYZ", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
        assertEquals("234 - ABC", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(1).getConstraints().getColorCombinationConstraints().getFactoryId());
    }

    @Test
    void test_packOptDetailsShouldReturnConsolidatedFactoryNamesIfFactoryNameIsEmptyThenAddFactoryId() {
        List<FineLineMapperDto> requestFineLines = getFineLinesForFactoryId();
        PackOptimizationResponse actual = packOptConstraintMapper.packOptDetails(requestFineLines);
        assertEquals(finelineNbr, actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        assertEquals("123, 234", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
        assertEquals("123, 234", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
        assertEquals("123", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
        assertEquals("234", actual.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(1).getConstraints().getColorCombinationConstraints().getFactoryId());
    }

    private List<FineLineMapperDto> getFineLinesForFactoryId() {
        FineLineMapperDto fineLineMapperDto = new FineLineMapperDto();
        fineLineMapperDto.setChannelId(2);
        fineLineMapperDto.setPlanId(planId);
        fineLineMapperDto.setLvl2Nbr(12);
        fineLineMapperDto.setLvl3Nbr(13);
        fineLineMapperDto.setLvl4Nbr(11);
        fineLineMapperDto.setLvl0Nbr(123);
        fineLineMapperDto.setLvl1Nbr(234);
        fineLineMapperDto.setFineLineNbr(finelineNbr);
        fineLineMapperDto.setStyleNbr(styleNbr);
        fineLineMapperDto.setCcId(ccId);
        fineLineMapperDto.setCcMaxPacks(12);
        fineLineMapperDto.setStyleMaxPacks(43);
        fineLineMapperDto.setStyleColorCombination(colorCombination);
        fineLineMapperDto.setCcColorCombination(colorCombination);
        fineLineMapperDto.setCcFactoryIds(factoryId);
        fineLineMapperDto.setCcSinglePackIndicator(1);
        fineLineMapperDto.setStyleSinglePackIndicator(1);
        fineLineMapperDto.setCcSupplierName("PUMA");
        fineLineMapperDto.setCcVendorNumber9(vendorNbr9);
        fineLineMapperDto.setCcVendorNumber6(vendorNbr6);
        fineLineMapperDto.setCcGsmSupplierNumber(gsmSupplierId);
        fineLineMapperDto.setCcFactoryIds("123");
        fineLineMapperDto.setCcCountryOfOrigin("INDIA");
        fineLineMapperDto.setCcPortOfOrigin("INDIA");

        FineLineMapperDto fineLineMapperDto1 = new FineLineMapperDto();
        fineLineMapperDto1.setChannelId(2);
        fineLineMapperDto1.setPlanId(planId);
        fineLineMapperDto1.setLvl2Nbr(12);
        fineLineMapperDto1.setLvl3Nbr(13);
        fineLineMapperDto1.setLvl4Nbr(11);
        fineLineMapperDto1.setLvl0Nbr(123);
        fineLineMapperDto1.setLvl1Nbr(234);
        fineLineMapperDto1.setFineLineNbr(finelineNbr);
        fineLineMapperDto1.setStyleNbr(styleNbr);
        fineLineMapperDto1.setCcId(ccId_2);
        fineLineMapperDto1.setCcSupplierName("NIKE");
        fineLineMapperDto1.setCcFactoryIds("234");
        fineLineMapperDto1.setCcCountryOfOrigin("USA");
        fineLineMapperDto1.setCcPortOfOrigin("USA");
        return Arrays.asList(fineLineMapperDto, fineLineMapperDto1);
    }

    private List<FineLineMapperDto> getFineLines() {
        FineLineMapperDto fineLineMapperDto = new FineLineMapperDto();
        fineLineMapperDto.setChannelId(2);
        fineLineMapperDto.setPlanId(planId);
        fineLineMapperDto.setLvl2Nbr(12);
        fineLineMapperDto.setLvl3Nbr(13);
        fineLineMapperDto.setLvl4Nbr(11);
        fineLineMapperDto.setLvl0Nbr(123);
        fineLineMapperDto.setLvl1Nbr(234);
        fineLineMapperDto.setFineLineNbr(finelineNbr);
        fineLineMapperDto.setStyleNbr(styleNbr);
        fineLineMapperDto.setCcId(ccId);
        fineLineMapperDto.setCcMaxPacks(12);
        fineLineMapperDto.setStyleMaxPacks(43);
        fineLineMapperDto.setStyleColorCombination(colorCombination);
        fineLineMapperDto.setCcColorCombination(colorCombination);
        fineLineMapperDto.setCcFactoryIds(factoryId);
        fineLineMapperDto.setCcSinglePackIndicator(1);
        fineLineMapperDto.setStyleSinglePackIndicator(1);
        fineLineMapperDto.setCcSupplierName("PUMA");
        fineLineMapperDto.setCcVendorNumber9(vendorNbr9);
        fineLineMapperDto.setCcVendorNumber6(vendorNbr6);
        fineLineMapperDto.setCcGsmSupplierNumber(gsmSupplierId);
        fineLineMapperDto.setCcFactoryIds("123");
        fineLineMapperDto.setCcFactoryName("XYZ");
        fineLineMapperDto.setCcCountryOfOrigin("INDIA");
        fineLineMapperDto.setCcPortOfOrigin("INDIA");

        FineLineMapperDto fineLineMapperDto1 = new FineLineMapperDto();
        fineLineMapperDto1.setChannelId(2);
        fineLineMapperDto1.setPlanId(planId);
        fineLineMapperDto1.setLvl2Nbr(12);
        fineLineMapperDto1.setLvl3Nbr(13);
        fineLineMapperDto1.setLvl4Nbr(11);
        fineLineMapperDto1.setLvl0Nbr(123);
        fineLineMapperDto1.setLvl1Nbr(234);
        fineLineMapperDto1.setFineLineNbr(finelineNbr);
        fineLineMapperDto1.setStyleNbr(styleNbr);
        fineLineMapperDto1.setCcId(ccId_2);
        fineLineMapperDto1.setCcSupplierName("NIKE");
        fineLineMapperDto1.setCcFactoryIds("234");
        fineLineMapperDto1.setCcFactoryName("ABC");
        fineLineMapperDto1.setCcCountryOfOrigin("USA");
        fineLineMapperDto1.setCcPortOfOrigin("USA");

        return Arrays.asList(fineLineMapperDto, fineLineMapperDto1);
    }
}
