package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCInboundResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;
import static org.apache.poi.ss.util.CellUtil.createCell;

public class DCInboundSheetExporter {
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<DCInboundExcelResponse> listDCInboundData;

    public DCInboundSheetExporter(List<DCInboundExcelResponse> listDCInboundData) {
        this.listDCInboundData = listDCInboundData;
        workbook = new XSSFWorkbook();
    }

    private List<String> getHeaders(){
        List<String> headerList = new ArrayList<>();
        Set<String> replWeekList = new HashSet<>();
        headerList.add(CATEGORY);
        headerList.add(SUB_CATEGORY);
        headerList.add(FINELINE);
        headerList.add(STYLE);
        headerList.add(CUSTOMER_CHOICE);
        headerList.add(MERCH_METHOD);
        headerList.add(SIZE);
        for (DCInboundExcelResponse obj:listDCInboundData) {
            List<DCinboundReplenishment> rep = obj.getReplenishment();
            for(DCinboundReplenishment r: rep){
                replWeekList.add(r.getReplnWeekDesc());
            }
        }
        headerList.addAll(replWeekList);
        return headerList;
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet(DC_INBOUND_EXCEL_SHEET_NAME);

        Row row = sheet.createRow(ZERO);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(HEADER_FONT_HEIGHT);
        style.setFont(font);
        int column_num = ZERO;

        for(String colName : getHeaders()){
            createCell(row,column_num ++ , colName, style);
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
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

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(ROW_FONT_HEIGHT);
        style.setFont(font);

        for (DCInboundExcelResponse dcInboundData : listDCInboundData) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = ZERO;

            createCell(row, columnCount++, dcInboundData.getLvl3Desc(), style);
            createCell(row, columnCount++, dcInboundData.getLvl4Desc(), style);
            createCell(row, columnCount++, dcInboundData.getFinelineDesc(), style);
            createCell(row, columnCount++, dcInboundData.getStyleNbr(), style);
            createCell(row, columnCount++, dcInboundData.getCcId(), style);
            createCell(row, columnCount++, dcInboundData.getMerchMethodDesc(), style);
            createCell(row, columnCount++, dcInboundData.getSizeDesc(), style);

        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }
    }
