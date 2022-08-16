package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCInboundResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.DC_INBOUND_HEADER_KEY;
import static com.walmart.aex.sp.util.SizeAndPackConstants.DC_INBOUND_REPORT_NAME;

@Slf4j
@Service
public class DCInboundSheetService {

    private final MerchCatPlanRepository merchCatPlanRepository;
    private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public DCInboundSheetService(MerchCatPlanRepository merchCatPlanRepository, CcSpReplnPkConsRepository ccSpReplnPkConsRepository) {
        this.merchCatPlanRepository = merchCatPlanRepository;
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
    }

    public List<DCInboundExcelResponse> getDCInboundExcelSheet(Long planId) {
        List<DCInboundResponse> response = ccSpReplnPkConsRepository.getDCInboundsByPlanId(planId);
        List<DCInboundExcelResponse> dcInboundExcelData = setDCInboundExcelSheetResponseDTO(response);
        return dcInboundExcelData;
    }

    public List<DCInboundExcelResponse> setDCInboundExcelSheetResponseDTO(List<DCInboundResponse> response) {
        List<DCInboundExcelResponse> dcInboundExcelResponses = new ArrayList<>();
        List<DCinboundReplenishment> replenishmentDTO = new ArrayList<>();
        if (response != null) {
            for (DCInboundResponse r : response) {
                DCInboundExcelResponse dcInboundExcelResponse = new DCInboundExcelResponse();
                dcInboundExcelResponse.setLvl3Desc(r.getLvl3Desc());
                dcInboundExcelResponse.setLvl4Desc(r.getLvl4Desc());
                dcInboundExcelResponse.setFinelineDesc(r.getFinelineDesc());
                dcInboundExcelResponse.setStyleNbr(r.getStyleNbr());
                dcInboundExcelResponse.setCcId(r.getCcId());
                dcInboundExcelResponse.setMerchMethodDesc(r.getMerchMethodDesc());
                dcInboundExcelResponse.setSizeDesc(r.getSizeDesc());

                if (r.getReplenishment() != null) {
                    try {
                        replenishmentDTO = Arrays.asList(objectMapper.readValue(r.getReplenishment(), DCinboundReplenishment[].class));
                        dcInboundExcelResponse.setReplenishment(replenishmentDTO);
                    } catch (JsonProcessingException jsonProcessingException) {
                        log.error("Error parsing replenishment object: ", jsonProcessingException);
                        throw new CustomException("Error parsing replenishment object:");
                    }

                }
                dcInboundExcelResponses.add(dcInboundExcelResponse);
            }

        }
        return dcInboundExcelResponses;
    }
    public List<DCInboundExcelResponse> getDcInboundExcelResponses(Long planId, HttpServletResponse response) {
        response.setContentType(String.valueOf(ContentType.APPLICATION_OCTET_STREAM));
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = DC_INBOUND_HEADER_KEY;
        String headerValue = "attachment; filename=" + DC_INBOUND_REPORT_NAME + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        List<DCInboundExcelResponse> sheetData = getDCInboundExcelSheet(planId);
        return sheetData;
    }
//"application/octet-stream"
}
