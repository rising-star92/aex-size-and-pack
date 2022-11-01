package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.PackOptConstraintResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PackOptConstraintMapperTest {

    @InjectMocks
    PackOptConstraintMapper packOptConstraintMapper;

    @Mock
    PackOptConstraintResponseDTO packOptConstraintResponseDTO;

    private static final Integer finelineNbr=3470;
    private static final Long planId=471l;
    private static final String vendorName= "AbcD";
    private static final String factoryId = "S1D0027";
    private static final String styleNbr = "34_2816_2_19_2";
    private static final String ccId = "34_2816_2_19_2_CHARCOAL GREY HEATHER";
    private static final String colorCombination = "blue";

    @Test
    public void mapPackOptLvl2Test()
    {
        PackOptimizationResponse packOptimizationResponse = new PackOptimizationResponse();
        packOptConstraintResponseDTO=new PackOptConstraintResponseDTO();
        packOptConstraintResponseDTO.setChannelId(2);
        packOptConstraintResponseDTO.setPlanId(planId);
        packOptConstraintResponseDTO.setLvl2Nbr(12);
        packOptConstraintResponseDTO.setLvl3Nbr(13);
        packOptConstraintResponseDTO.setLvl4Nbr(11);
        packOptConstraintResponseDTO.setLvl0Nbr(123);
        packOptConstraintResponseDTO.setLvl1Nbr(234);
        packOptConstraintResponseDTO.setFinelineNbr(finelineNbr);
        packOptConstraintResponseDTO.setStyleNbr(styleNbr);
        packOptConstraintResponseDTO.setCcId(ccId);
        packOptConstraintResponseDTO.setCcMaxPacks(12);
        packOptConstraintResponseDTO.setStyleMaxPacks(43);
        packOptConstraintResponseDTO.setStyleColorCombination(colorCombination);
        packOptConstraintResponseDTO.setCcColorCombination(colorCombination);
        packOptConstraintResponseDTO.setStyleSupplierName(vendorName);
        packOptConstraintResponseDTO.setCcFactoryIds(factoryId);
        packOptConstraintMapper.mapPackOptLvl2(packOptConstraintResponseDTO,packOptimizationResponse,finelineNbr);
        assertNotNull(packOptimizationResponse);
        assertEquals(packOptimizationResponse.getPlanId(),planId);
        assertEquals(packOptimizationResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getStyleNbr(),styleNbr);
        assertEquals(packOptimizationResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getCcId(),ccId);
        assertEquals(packOptimizationResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getConstraints().getSupplierConstraints().getSupplierName(),vendorName);
        assertEquals(packOptimizationResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getConstraints().getSupplierConstraints().getFactoryIds(),factoryId);
    }
}
