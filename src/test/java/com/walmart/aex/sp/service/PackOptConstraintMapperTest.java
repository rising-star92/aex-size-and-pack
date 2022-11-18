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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PackOptConstraintMapperTest {

    @InjectMocks
    private PackOptConstraintMapper packOptConstraintMapper;

    private static final Integer finelineNbr = 3470;
    private static final Long planId = 471l;
    private static final String vendorName = "AbcD";
    private static final String factoryId = "S1D0027";
    private static final String styleNbr = "34_2816_2_19_2";
    private static final String ccId = "34_2816_2_19_2_CHARCOAL GREY HEATHER";
    private static final String colorCombination = "4-22-1";

    @Test
    void mapPackOptLvl2Test() {
        PackOptimizationResponse packOptimizationResponse = new PackOptimizationResponse();
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
        fineLineMapperDto.setStyleSupplierName(vendorName);
        fineLineMapperDto.setCcFactoryIds(factoryId);
        fineLineMapperDto.setCcSinglePackIndicator(1);
        fineLineMapperDto.setStyleSinglePackIndicator(1);
        List<Lvl3> actual = packOptConstraintMapper.mapPackOptLvl3(fineLineMapperDto, packOptimizationResponse);
        assertNotNull(packOptimizationResponse);
        assertEquals(styleNbr, actual.get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getStyleNbr());
        assertEquals(ccId, actual.get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getCcId());
        assertEquals(vendorName, actual.get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getColorCombinationConstraints().getSupplierName());
        assertEquals(factoryId, actual.get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getConstraints().getColorCombinationConstraints().getFactoryId());
    }

    @Test
    void mapPackOptLvl2TestNonNull() {
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
        List<Lvl3> actual = packOptConstraintMapper.mapPackOptLvl3(fineLineMapperDto, packOptimizationResponse);
        assertEquals(finelineNbr, actual.get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        assertEquals(styleNbr, actual.get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getStyleNbr());
        assertEquals(ccId, actual.get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getCcId());

    }
}
