package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.service.DCInboundSheetExporter;
import com.walmart.aex.sp.service.DCInboundSheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/size-and-pack/v1")
public class DCInboundSheetController {


    private final DCInboundSheetService dcInboundSheetService;

    public DCInboundSheetController(DCInboundSheetService dcInboundSheetService) {
        this.dcInboundSheetService = dcInboundSheetService;
    }

    @CrossOrigin
    @GetMapping("/dcInboundExportExcel/{planId}/{channelDesc}")
    public void getDCInbountExcelSheet(@PathVariable("planId") Long planId, @PathVariable("channelDesc") String channelDesc,HttpServletResponse response) throws IOException {
        List<DCInboundExcelResponse> sheetData = dcInboundSheetService.getDcInboundExcelResponses(planId, channelDesc,response);
        DCInboundSheetExporter excelExporter = new DCInboundSheetExporter(sheetData);
        excelExporter.export(response);
    }

}
