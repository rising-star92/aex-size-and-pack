package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Slf4j
@Service
public class DCInboundSheetExporter {
//    private SXSSFWorkbook workbook;
//    private SXSSFSheet sheet;
//    private List<DCInboundExcelResponse> listDCInboundData;
//
//    private List<String> headers;

    public DCInboundSheetExporter() {
        SXSSFWorkbook workbook;
        SXSSFSheet sheet;

//        this.listDCInboundData = listDCInboundData;
//        this.headers = new ArrayList<>();
//        workbook = new SXSSFWorkbook();
//        workbook.setCompressTempFiles(true);

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
        font.setFontHeightInPoints(HEADER_FONT_HEIGHT.shortValue());
        return font;
    }

//    List<String> getHeaders(){
//        List<String> headerList = new ArrayList<>();
////        Set<String> replWeekList = listDCInboundData.stream().findFirst()
////              .get().getReplenishment().stream().map(DCinboundReplenishment::getReplnWeekDesc).collect(Collectors.toSet());
//        List<String> rwList = listDCInboundData.stream().findFirst()
//              .get().getReplenishment().stream()
//              .map(DCinboundReplenishment::getReplnWeekDesc)
//              .distinct()
//              .collect(Collectors.toList());;
//        headerList.add(CATEGORY);
//        headerList.add(SUB_CATEGORY);
//        headerList.add(FINELINE);
//        headerList.add(STYLE);
//        headerList.add(CUSTOMER_CHOICE);
//        headerList.add(MERCH_METHOD);
//        headerList.add(SIZE);
//        headerList.add(CHANNEL);
//
////        for (DCInboundExcelResponse obj:listDCInboundData) {
////            List<DCinboundReplenishment> rep = obj.getReplenishment();
////            for(DCinboundReplenishment r: rep){
////                replWeekList.add(r.getReplnWeekDesc());
////            }
////        }
//        // Sorting replenishment week desc
//        //List<String> rwList = new ArrayList<>(replWeekList);
//        //Collections.sort(rwList);
//        headerList.addAll(rwList);
//        //headers.addAll(rwList);
//        headers = headerList;
//        return headerList;
//    }

    private void writeHeaderLine(Sheet sheet, CellStyle style, List<String> headers) {
        Row row = sheet.createRow(ZERO);
        int column_num = ZERO;

        for (String colName : headers) {
            createCell(row, column_num++, colName,style);
        }

        //TODO do we need this? seems redundant
        //row.getCell(ZERO).setCellStyle(style);
    }

    void createCell(Row row, int columnCount, Object value, CellStyle style) {
        //BAD BUG BELOW
        //sheet.autoSizeColumn(columnCount);
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
//
//        CellStyle style = workbook.createCellStyle();
//        Font font = workbook.createFont();
//        font.setFontHeightInPoints((short)14);
//        style.setFont(font);

        for (DCInboundExcelResponse dcInboundData : listDCInboundData) {
            long rowTime = System.currentTimeMillis();
            Row row = sheet.createRow(rowCount++);
            int columnCount = ZERO;

            createCell(row, columnCount++, dcInboundData.getLvl3Desc(), style);
            createCell(row, columnCount++, dcInboundData.getLvl4Desc(), style);
            createCell(row, columnCount++, dcInboundData.getFinelineDesc(), style);
            createCell(row, columnCount++, dcInboundData.getStyleNbr(), style);
            createCell(row, columnCount++, dcInboundData.getCcId(), style);
            createCell(row, columnCount++, dcInboundData.getMerchMethodDesc(), style);
            createCell(row, columnCount++, dcInboundData.getSizeDesc(), style);
            createCell(row, columnCount++, dcInboundData.getChannelDesc(), style);

            addReplenishmentUnitsCell(row, columnCount, style, headers, dcInboundData);
            log.info("ROW TIME: {}", System.currentTimeMillis()-rowTime);
        }
        log.info("ROW COUNT: {}", rowCount);


    }

    void addReplenishmentUnitsCell(Row row, int columnCount, CellStyle style, List<String> headers, DCInboundExcelResponse dcInboundData) {
        for (DCinboundReplenishment r : dcInboundData.getReplenishment()) {
            int currColCount = columnCount;
            while (currColCount < headers.size()) {
                if (headers.get(currColCount).equalsIgnoreCase(r.getReplnWeekDesc())) {
                    createCell(row, currColCount, r.getAdjReplnUnits(), style);
                    break;
                }
                currColCount++;
            }
        }
    }

//    String getHeaderName(int columnCount)
//    {
//        return headers.get(columnCount);
////        Row row = sheet.getRow(ZERO);
////        Cell cell = row.getCell(columnCount);
////        return cell.getStringCellValue();
//    }

    public void export(HttpServletResponse response) throws IOException {
//        //TODO move to controller/service layer
//        writeHeaderLine();
//        writeDataLines();
//
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        workbook.close();
//
//        outputStream.close();

    }
}

