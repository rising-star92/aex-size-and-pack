package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.service.DCInboundSheetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.walmart.aex.sp.util.SizeAndPackConstants.DC_INBOUND_HEADER_KEY;
import static com.walmart.aex.sp.util.SizeAndPackConstants.DC_INBOUND_REPORT_NAME;

@RestController
@Slf4j
@RequestMapping("/size-and-pack/v1")
public class DCInboundSheetController {


    private final DCInboundSheetService dcInboundSheetService;

    public DCInboundSheetController(DCInboundSheetService dcInboundSheetService) {
        this.dcInboundSheetService = dcInboundSheetService;
    }

    @CrossOrigin
    @GetMapping("/dcInboundExportExcel")
    public void getDCInbountExcelSheet(@RequestParam("planId") Long planId, @RequestParam("channelDesc") String channelDesc,HttpServletResponse response) throws IOException {
//        String headerKey = DC_INBOUND_HEADER_KEY;
//        String headerValue = "attachment; filename=" + DC_INBOUND_REPORT_NAME + currentDateTime + ".xlsx";
//        DC sheetData = dcInboundSheetService.getDcInboundWorkbook(planId, channelDesc);
//        //DCInboundSheetExporter excelExporter = new DCInboundSheetExporter(sheetData);
//        response.setContentType(String.valueOf(ContentType.APPLICATION_OCTET_STREAM));
//        response.setHeader(headerKey, headerValue);
//        excelExporter.export(response);
    }

}
