package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCInboundResponse;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DCInboundSheetServiceTest {
    @InjectMocks
    private DCInboundSheetService dcInboundSheetService;
    @Mock
    DCInboundResponse dcInboundResponse;
    @Mock
    private CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
    private static final Long planId=471l;

    @Test
    void testGetDCInboundExcelSheet() throws JsonProcessingException {
        //Arrange
        DCInboundResponse dcInboundResponse = getDCInboundResponseDTO();
        List<DCInboundResponse> response = new ArrayList<>();
        response.add(dcInboundResponse);
        when(ccSpReplnPkConsRepository.getDCInboundsByPlanId(planId)).thenReturn(response);

        //Act
        List<DCInboundExcelResponse> dcInboundExcelData = dcInboundSheetService.getDCInboundExcelSheet(planId);

        // Assert
        assertNotNull(dcInboundExcelData);
        assertTrue(dcInboundExcelData.size()==1);
        assertTrue(dcInboundExcelData.get(0).getReplenishment().size()==1);
    }

    @Test
    void testGetDCInboundExcelSheetWhenReplenishmentObjectIsNull() throws JsonProcessingException {
        //Arrange
        dcInboundResponse = new DCInboundResponse();
        dcInboundResponse.setPlanId(planId);
        dcInboundResponse.setLvl0Nbr(50000);
        dcInboundResponse.setLvl0Desc("Apparel");
        dcInboundResponse.setLvl1Nbr(34);
        dcInboundResponse.setLvl1Desc("D34 - Womens Apparel");
        dcInboundResponse.setLvl2Nbr(6419);
        dcInboundResponse.setLvl2Desc("Plus Womens");
        dcInboundResponse.setLvl3Nbr(12228);
        dcInboundResponse.setLvl3Desc("Tops Plus Womens");
        dcInboundResponse.setLvl4Nbr(31508);
        dcInboundResponse.setLvl4Desc("Ls Tops Plus Womens");
        dcInboundResponse.setFinelineNbr(1021);
        dcInboundResponse.setFinelineDesc("5160 - TS TANK SHORT SET");
        dcInboundResponse.setStyleNbr("34_1021_2_21_2");
        dcInboundResponse.setCustomerChoice("34_1021_2_21_2_AURA ORANGE STENCIL");
        dcInboundResponse.setMerchMethodDesc("FOLDED");
        dcInboundResponse.setSizeDesc("1X");
        List<DCInboundResponse> response = new ArrayList<>();
        response.add(dcInboundResponse);
        when(ccSpReplnPkConsRepository.getDCInboundsByPlanId(planId)).thenReturn(response);

        //Act
        List<DCInboundExcelResponse> dcInboundExcelData = dcInboundSheetService.getDCInboundExcelSheet(planId);

        // Assert
        assertNotNull(dcInboundExcelData);
        assertTrue(dcInboundExcelData.size()==1);
        assertNull(dcInboundExcelData.get(0).getReplenishment());
    }

    @Test
    void testGetDcInboundExcelResponses() {
        //Arrange
        HttpServletResponse httpServletResponse = new MockHttpServletResponse();
        DCInboundResponse dcInboundResponse = getDCInboundResponseDTO();
        List<DCInboundResponse> response = new ArrayList<>();
        response.add(dcInboundResponse);
        when(ccSpReplnPkConsRepository.getDCInboundsByPlanId(planId)).thenReturn(response);
        //Act
        List<DCInboundExcelResponse> sheetData = dcInboundSheetService.getDcInboundExcelResponses(planId,httpServletResponse);

        // Assert
        assertNotNull(sheetData);
        assertTrue(sheetData.size()==1);
    }

    private DCInboundResponse getDCInboundResponseDTO() {
        dcInboundResponse = new DCInboundResponse();
        dcInboundResponse.setPlanId(planId);
        dcInboundResponse.setLvl0Nbr(50000);
        dcInboundResponse.setLvl0Desc("Apparel");
        dcInboundResponse.setLvl1Nbr(34);
        dcInboundResponse.setLvl1Desc("D34 - Womens Apparel");
        dcInboundResponse.setLvl2Nbr(6419);
        dcInboundResponse.setLvl2Desc("Plus Womens");
        dcInboundResponse.setLvl3Nbr(12228);
        dcInboundResponse.setLvl3Desc("Tops Plus Womens");
        dcInboundResponse.setLvl4Nbr(31508);
        dcInboundResponse.setLvl4Desc("Ls Tops Plus Womens");
        dcInboundResponse.setFinelineNbr(1021);
        dcInboundResponse.setFinelineDesc("5160 - TS TANK SHORT SET");
        dcInboundResponse.setStyleNbr("34_1021_2_21_2");
        dcInboundResponse.setCustomerChoice("34_1021_2_21_2_AURA ORANGE STENCIL");
        dcInboundResponse.setMerchMethodDesc("FOLDED");
        dcInboundResponse.setReplenishment("[{\"replnWeek\":12302,\"replnWeekDesc\":\"FYE2024WK02\",\"replnUnits\":null,\"adjReplnUnits\":2379,\"remainingUnits\":null}]");
        dcInboundResponse.setSizeDesc("1X");
        return dcInboundResponse;
    }

}
