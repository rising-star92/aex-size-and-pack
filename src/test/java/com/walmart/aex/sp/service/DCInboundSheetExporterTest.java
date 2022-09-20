package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCInboundResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DCInboundSheetExporterTest {

    @InjectMocks
    @Spy
    private DCInboundSheetExporter dcInboundSheetExporter;

    @Mock
    CellStyle cellStyle;
    @Mock
    Row row;

    @Test
    void testAddReplenishmentUnitsCell() {
        int columnCount = 0;
        int currColCount = 0;
        List<String> headerList = new ArrayList<>();
        headerList.add("FYE2023WK44");
        headerList.add("FYE2023WK43");
        Mockito.doReturn(headerList).when(dcInboundSheetExporter).getHeaders();
        Mockito.doReturn("FYE2023WK43").when(dcInboundSheetExporter).getHeaderName(currColCount);
        Mockito.doReturn("FYE2023WK44").when(dcInboundSheetExporter).getHeaderName(++currColCount);
        Mockito.doNothing().when(dcInboundSheetExporter).createCell(row,1,100,cellStyle);
        dcInboundSheetExporter.addReplenishmentUnitsCell(cellStyle,getDCInboundExcelResponseDTO(),row,columnCount);
        assertTrue(currColCount==1);
    }
    private List<DCInboundExcelResponse> getDCInboundExcelResponseList(){
        List<DCInboundExcelResponse> dcInboundExcelResponseList = new ArrayList<>();
        dcInboundExcelResponseList.add(getDCInboundExcelResponseDTO());
        return dcInboundExcelResponseList;
    }
    private DCInboundExcelResponse getDCInboundExcelResponseDTO() {
        DCInboundExcelResponse dcInboundExcelResponse = new DCInboundExcelResponse();
        dcInboundExcelResponse = new DCInboundExcelResponse();
        dcInboundExcelResponse.setLvl3Desc("Dresses And Rompers Plus Womens");
        dcInboundExcelResponse.setLvl4Desc("Ls Dresses Plus Womens");
        dcInboundExcelResponse.setFinelineDesc("5147 - TS SWTHRT NECK MAXI DRESS");
        dcInboundExcelResponse.setMerchMethodDesc("FOLDED");
        dcInboundExcelResponse.setChannelDesc("Store");
        dcInboundExcelResponse.setCcId("34_5147_3_21_4_SEA TURTLE/DARK NAVY");
        dcInboundExcelResponse.setReplenishment(getDCinboundReplenishmentList());
        return dcInboundExcelResponse;
    }

    private List<DCinboundReplenishment> getDCinboundReplenishmentList() {
        List<DCinboundReplenishment> replenishment = new ArrayList<>();
        DCinboundReplenishment dcinboundReplenishment = new DCinboundReplenishment();
        dcinboundReplenishment.setReplnWeek(12245);
        dcinboundReplenishment.setReplnWeekDesc("FYE2023WK44");
        dcinboundReplenishment.setReplnUnits(4000);
        dcinboundReplenishment.setAdjReplnUnits(100);
        replenishment.add(dcinboundReplenishment);
        return replenishment;
    }
}
