package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.DC_INBOUND_EXCEL_SHEET_NAME;
import static com.walmart.aex.sp.util.SizeAndPackConstants.DEFAULT_FONT_HEIGHT;
import static com.walmart.aex.sp.util.SizeAndPackConstants.ZERO;

@Slf4j
@Service
public class DCInboundSheetExporter {

    public DCInboundSheetExporter() {
    }

    public Workbook generate(List<String> headers, List<DCInboundExcelResponse> listDCInboundData) {
        final Workbook workbook = createWorkbook();
        final Sheet sheet = createDefaultSheet(workbook);

        //Write headers and style to sheet
        CellStyle headerStyle = createHeaderStyle(workbook);
        writeHeaderLine(sheet, headerStyle, headers);

        //Write body to sheet
        CellStyle bodyStyle = createBodyStyle(workbook);
        writeDataLines(sheet, bodyStyle, headers, listDCInboundData);

        formatSheet(sheet, headers.size());
        return workbook;
    }

    private Workbook createWorkbook() {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        workbook.setCompressTempFiles(true);
        return workbook;
    }

    private Sheet createDefaultSheet(Workbook workbook) {
        SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(DC_INBOUND_EXCEL_SHEET_NAME);
        //Needed so columns can be auto-sized later
        sheet.trackAllColumnsForAutoSizing();
        return sheet;
    }

    private void formatSheet(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++){
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook){
        CellStyle style = createDefaultStyle(workbook);
        style.setFont(createHeaderFont(workbook));
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createBodyStyle(Workbook workbook) {
        CellStyle style = createDefaultStyle(workbook);
        Font font = createDefaultFont(workbook);
        style.setFont(font);
        return style;
    }

    private CellStyle createDefaultStyle(Workbook workbook) {
        return workbook.createCellStyle();
    }

    private Font createHeaderFont(Workbook workbook) {
        Font font = createDefaultFont(workbook);
        font.setBold(true);
        return font;
    }

    private Font createDefaultFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT.shortValue());
        return font;
    }

    private void writeHeaderLine(Sheet sheet, CellStyle style, List<String> headers) {
        Row row = sheet.createRow(ZERO);
        int columnNum = ZERO;

        for (String colName : headers) {
            createCell(row, columnNum++, colName,style);
        }
    }

    void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }
        else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(Sheet sheet, CellStyle style, List<String> headers, List<DCInboundExcelResponse> listDCInboundData) {
        int rowCount = 1;

        for (DCInboundExcelResponse dcInboundData : listDCInboundData) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = ZERO;

            createCell(row, columnCount++, dcInboundData.getLvl3Desc(), style);
            createCell(row, columnCount++, dcInboundData.getLvl4Desc(), style);
            createCell(row, columnCount++, dcInboundData.getFinelineDesc(), style);
            createCell(row, columnCount++, dcInboundData.getStyleNbr(), style);
            createCell(row, columnCount++, dcInboundData.getCcId(), style);
            createCell(row, columnCount++, dcInboundData.getColorName(), style);
            createCell(row, columnCount++, dcInboundData.getColorFamilyDesc(), style);
            createCell(row, columnCount++, dcInboundData.getMerchMethodDesc(), style);
            createCell(row, columnCount++, dcInboundData.getSizeDesc(), style);
            createCell(row, columnCount++, dcInboundData.getChannelDesc(), style);

            addReplenishmentUnitsCell(row, columnCount, style, headers, dcInboundData);
        }


    }

    void addReplenishmentUnitsCell(Row row, int columnCount, CellStyle style, List<String> headers, DCInboundExcelResponse dcInboundData) {
        for (DCinboundReplenishment r : dcInboundData.getReplenishment()) {
            int currColCount = columnCount;
            while (currColCount < headers.size()) {
                if (headers.get(currColCount).equalsIgnoreCase(r.getReplnWeekDesc())) {
                    createCell(row, currColCount, r.getAdjReplnUnits(), style);
                } else {
                    if (row.getCell(currColCount) == null) {
                        //Fill with zero if we have no week value
                        createCell(row, currColCount, ZERO, style);
                    }
                }
                currColCount++;
            }
        }
    }
}

