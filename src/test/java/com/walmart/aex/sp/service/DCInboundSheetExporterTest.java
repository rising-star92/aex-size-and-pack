package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.CATEGORY;
import static com.walmart.aex.sp.util.SizeAndPackConstants.CHANNEL;
import static com.walmart.aex.sp.util.SizeAndPackConstants.CUSTOMER_CHOICE;
import static com.walmart.aex.sp.util.SizeAndPackConstants.DC_INBOUND_EXCEL_SHEET_NAME;
import static com.walmart.aex.sp.util.SizeAndPackConstants.FINELINE;
import static com.walmart.aex.sp.util.SizeAndPackConstants.MERCH_METHOD;
import static com.walmart.aex.sp.util.SizeAndPackConstants.SIZE;
import static com.walmart.aex.sp.util.SizeAndPackConstants.STYLE;
import static com.walmart.aex.sp.util.SizeAndPackConstants.SUB_CATEGORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DCInboundSheetExporterTest {

    private DCInboundSheetExporter dcInboundSheetExporter;

    private final String lvl3Desc = "Dresses And Rompers Plus Womens";
    private final String lvl4Desc = "Ls Dresses Plus Womens";
    private final String finelineDesc = "5147 - TS SWTHRT NECK MAXI DRESS";
    private final String styleDesc = "Test Style";
    private final String merchMethodDesc = "FOLDED";
    private final String channelDesc = "Store";

    //cc 1
    private final String ccDesc = "34_5147_3_21_4_SEA TURTLE/DARK NAVY";
    private final String colorFamilyDesc = "Grey";
    private final String colorName = "BLACK CRYSTAL TIE DYE";

    private final String sizeDesc = "XL";
    private final Integer replenWk = 12245;
    private final String replenWkDesc = "FYE2023WK44";
    private final int replenUnits = 100;

    //cc 2

    private final String cc2Desc = "34_5147_3_21_4_YELLOW";
    private final String cc2ColorFamilyDesc = "Yellow";
    private final String cc2ColorName = "SCHOOL BUS YELLOW";

    private final String cc2SizeDesc = "M";
    private final Integer cc2ReplenWk = 12246;
    private final String cc2ReplenWkDesc = "FYE2023WK46";
    private final int cc2ReplenUnits = 100;

    @BeforeEach
    public void init() {
        dcInboundSheetExporter = new DCInboundSheetExporter();
    }

    @Test
    void testAddReplenishmentUnitsCell() {

        List<String> headers = new ArrayList<>(SizeAndPackConstants.DC_INBOUND_REPORT_DEFAULT_HEADERS);
        headers.add(replenWkDesc);
        headers.add(cc2ReplenWkDesc);

        List<Object> dataRow = List.of(lvl3Desc, lvl4Desc, finelineDesc, styleDesc, ccDesc, colorName, colorFamilyDesc, merchMethodDesc, sizeDesc, channelDesc, replenUnits, 0);
        List<Object> cc2DataRow = List.of(lvl3Desc, lvl4Desc, finelineDesc, styleDesc, cc2Desc, cc2ColorName, cc2ColorFamilyDesc, merchMethodDesc, cc2SizeDesc, channelDesc, 0, cc2ReplenUnits);
        try (Workbook workbook = dcInboundSheetExporter.generate(headers, getDCInboundExcelResponseList())) {
            Row headerRow = workbook.getSheet(DC_INBOUND_EXCEL_SHEET_NAME).getRow(0);
            Row bodyRow = workbook.getSheet(DC_INBOUND_EXCEL_SHEET_NAME).getRow(1);
            Row cc2BodyRow = workbook.getSheet(DC_INBOUND_EXCEL_SHEET_NAME).getRow(2);
            for (int i = 0; i < headers.size(); i++) {
                assertEquals(headers.get(i), headerRow.getCell(i).getStringCellValue());

                if (bodyRow.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
                    assertEquals(dataRow.get(i), (int) bodyRow.getCell(i).getNumericCellValue());
                    assertEquals(cc2DataRow.get(i), (int) cc2BodyRow.getCell(i).getNumericCellValue());
                }
                else {
                    assertEquals(dataRow.get(i), bodyRow.getCell(i).getStringCellValue());
                    assertEquals(cc2DataRow.get(i), cc2BodyRow.getCell(i).getStringCellValue());
                }
            }
        } catch (IOException e) {
            fail();
        }
    }

    private List<DCInboundExcelResponse> getDCInboundExcelResponseList(){
        return Arrays.asList(getDCInboundExcelResponseDTO(), getDCInboundExcelResponseDTO2());
    }
    private DCInboundExcelResponse getDCInboundExcelResponseDTO() {
        DCInboundExcelResponse dcInboundExcelResponse = new DCInboundExcelResponse();
        dcInboundExcelResponse.setLvl3Desc(lvl3Desc);
        dcInboundExcelResponse.setLvl4Desc(lvl4Desc);
        dcInboundExcelResponse.setFinelineDesc(finelineDesc);
        dcInboundExcelResponse.setStyleNbr(styleDesc);
        dcInboundExcelResponse.setMerchMethodDesc(merchMethodDesc);
        dcInboundExcelResponse.setChannelDesc(channelDesc);
        dcInboundExcelResponse.setCcId(ccDesc);
        dcInboundExcelResponse.setSizeDesc(sizeDesc);
        dcInboundExcelResponse.setColorName(colorName);
        dcInboundExcelResponse.setColorFamilyDesc(colorFamilyDesc);
        dcInboundExcelResponse.setReplenishment(getDCinboundReplenishmentList(replenWk,replenWkDesc,replenUnits));
        return dcInboundExcelResponse;
    }

    private DCInboundExcelResponse getDCInboundExcelResponseDTO2() {
        DCInboundExcelResponse dcInboundExcelResponse = new DCInboundExcelResponse();
        dcInboundExcelResponse.setLvl3Desc(lvl3Desc);
        dcInboundExcelResponse.setLvl4Desc(lvl4Desc);
        dcInboundExcelResponse.setFinelineDesc(finelineDesc);
        dcInboundExcelResponse.setStyleNbr(styleDesc);
        dcInboundExcelResponse.setMerchMethodDesc(merchMethodDesc);
        dcInboundExcelResponse.setChannelDesc(channelDesc);
        dcInboundExcelResponse.setCcId(cc2Desc);
        dcInboundExcelResponse.setSizeDesc(cc2SizeDesc);
        dcInboundExcelResponse.setColorName(cc2ColorName);
        dcInboundExcelResponse.setColorFamilyDesc(cc2ColorFamilyDesc);
        dcInboundExcelResponse.setReplenishment(getDCinboundReplenishmentList(cc2ReplenWk, cc2ReplenWkDesc, cc2ReplenUnits));
        return dcInboundExcelResponse;
    }

    private List<DCinboundReplenishment> getDCinboundReplenishmentList(int replenWk, String replenWkDesc, int replenUnits) {
        List<DCinboundReplenishment> replenishment = new ArrayList<>();
        DCinboundReplenishment dcinboundReplenishment = new DCinboundReplenishment();
        dcinboundReplenishment.setReplnWeek(replenWk);
        dcinboundReplenishment.setReplnWeekDesc(replenWkDesc);
        dcinboundReplenishment.setReplnUnits(4000);
        dcinboundReplenishment.setAdjReplnUnits(replenUnits);
        replenishment.add(dcinboundReplenishment);
        return replenishment;
    }
}
