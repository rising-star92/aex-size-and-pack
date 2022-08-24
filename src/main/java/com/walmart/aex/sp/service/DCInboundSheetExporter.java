package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Slf4j
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
        headerList.add(CHANNEL);
        for (DCInboundExcelResponse obj:listDCInboundData) {
            List<DCinboundReplenishment> rep = obj.getReplenishment();
            for(DCinboundReplenishment r: rep){
                replWeekList.add(r.getReplnWeekDesc());
            }
        }
        // Sorting replenishment week desc
        List<String> rwList = new ArrayList<String>();
        for (String x : replWeekList)
            rwList.add(x);
        Collections.sort(rwList);
        headerList.addAll(rwList);
        return headerList;
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet(DC_INBOUND_EXCEL_SHEET_NAME);
        Row row = sheet.createRow(ZERO);
        int column_num = ZERO;

        for (String colName : getHeaders()) {
            createCell(row, column_num++, colName,null);
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

        for (DCInboundExcelResponse dcInboundData : listDCInboundData) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = ZERO;

            createCell(row, columnCount++, dcInboundData.getLvl3Desc(), null);
            createCell(row, columnCount++, dcInboundData.getLvl4Desc(), null);
            createCell(row, columnCount++, dcInboundData.getFinelineDesc(), null);
            createCell(row, columnCount++, dcInboundData.getStyleNbr(), null);
            createCell(row, columnCount++, dcInboundData.getCcId(), null);
            createCell(row, columnCount++, dcInboundData.getMerchMethodDesc(), null);
            createCell(row, columnCount++, dcInboundData.getSizeDesc(), null);
            createCell(row, columnCount++, dcInboundData.getChannelDesc(), null);
            while(columnCount < getHeaders().size()){
                for(DCinboundReplenishment r: dcInboundData.getReplenishment()){
                    int currColCount = columnCount++;
                    if(getHeaderName(currColCount).equalsIgnoreCase(r.getReplnWeekDesc()))
                        createCell(row, currColCount, r.getAdjReplnUnits(), null);
                }
            }
        }
    }

    private String getHeaderName(int columnCount)
    {
        Row row = sheet.getRow(ZERO);
        Cell cell = row.getCell(columnCount);
        return cell.getStringCellValue();
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
