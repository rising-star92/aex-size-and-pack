package com.walmart.aex.sp.service.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.UpdatePackOptStatusRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.constraints.AssertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PackOptConstraintMapperHelperTest {
    @InjectMocks
    PackOptConstraintMapperHelper packOptConstraintMapperHelper;
    @Mock
    private ObjectMapper objectMapper;
    private static final Integer finelineNbr = 3470;
    private static final Long planId = 471L;
    private static final Integer vendorNbr6 = 123456;
    private static final Integer gsmSupplierId = 12345678;
    private static final Integer vendorNbr9 = 123456789;
    private static final String factoryId = "S1D0027";
    private static final String styleNbr = "34_2816_2_19_2";
    private static final String ccId = "34_2816_2_19_2_CHARCOAL GREY HEATHER";
    private static final String colorCombination = "4-22-1";

    @Test
    void test_getRunStatusLongDescriptions() {
        FineLineMapperDto fineLineMapperDto = getFineLinesMapperDto();
        Map<Integer, Map<Integer, String>> finelineBumpStatusMap = new HashMap<>();
        List<String> result = packOptConstraintMapperHelper.getRunStatusLongDescriptions(fineLineMapperDto,finelineBumpStatusMap);
        assertNotNull(result);
        assertEquals("Fineline failed, please contact the support team",result.get(0));
    }

    @Test
    void test_getRunStatusLongDescriptionsWhenRunStatusCodeIsEligibleForPrefix() throws JsonProcessingException {
        FineLineMapperDto fineLineMapperDto = getFineLinesMapperDto();
        fineLineMapperDto.setChildRunStatusCode(15);
        fineLineMapperDto.setChildReturnMessage("{\n" +
                "    \"statusCode\": 15,\n" +
                "    \"statusDesc\": \"BUMP_SET_CC_VALUE_ERROR_MSG\",\n" +
                "    \"statusLongDesc\": \"Fineline has a bump set with less than 6 units. Please adjust units or consider combining colors.\"\n" +
                "}");
        Map<Integer, Map<Integer, String>> finelineBumpStatusMap = new HashMap<>();
        UpdatePackOptStatusRequest updatePackOptStatusRequest = new UpdatePackOptStatusRequest();
        updatePackOptStatusRequest.setStatusCode(15);
        updatePackOptStatusRequest.setStatusDesc("BUMP_SET_CC_VALUE_ERROR_MSG");
        updatePackOptStatusRequest.setStatusLongDesc("Fineline has a bump set with less than 6 units. Please adjust units or consider combining colors.");
        when(objectMapper.readValue(fineLineMapperDto.getChildReturnMessage(),UpdatePackOptStatusRequest.class)).thenReturn(updatePackOptStatusRequest);
        List<String> result = packOptConstraintMapperHelper.getRunStatusLongDescriptions(fineLineMapperDto,finelineBumpStatusMap);
        assertNotNull(result);
        assertEquals("Bump Pack 1 : Fineline has a bump set with less than 6 units. Please adjust units or consider combining colors.",result.get(0));
    }

    private FineLineMapperDto getFineLinesMapperDto() {
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
        fineLineMapperDto.setCcFactoryId(factoryId);
        fineLineMapperDto.setCcSinglePackIndicator(1);
        fineLineMapperDto.setStyleSinglePackIndicator(1);
        fineLineMapperDto.setCcSupplierName("PUMA");
        fineLineMapperDto.setCcVendorNumber9(vendorNbr9);
        fineLineMapperDto.setCcVendorNumber6(vendorNbr6);
        fineLineMapperDto.setCcGsmSupplierNumber(gsmSupplierId);
        fineLineMapperDto.setCcFactoryId("123");
        fineLineMapperDto.setCcPortOfOrigin("USA");
        fineLineMapperDto.setChildRunStatusCode(13);
        fineLineMapperDto.setBumpPackNbr(1);
        fineLineMapperDto.setRunStatusCode(101);
        fineLineMapperDto.setChildRunStatusCodeDesc("Fineline failed, please contact the support team");
        return fineLineMapperDto;
    }




}
