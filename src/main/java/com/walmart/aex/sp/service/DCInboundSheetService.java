package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.DCInboundExcelResponse;
import com.walmart.aex.sp.dto.packoptimization.DCInboundResponse;
import com.walmart.aex.sp.dto.packoptimization.DCinboundReplenishment;
import com.walmart.aex.sp.dto.replenishment.DCInboundWorkbookResponse;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Slf4j
@Service
public class DCInboundSheetService {

    private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
    private final DCInboundSheetExporter dcInboundSheetExporter;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public DCInboundSheetService(CcSpReplnPkConsRepository ccSpReplnPkConsRepository, DCInboundSheetExporter dcInboundSheetExporter) {
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
        this.dcInboundSheetExporter = dcInboundSheetExporter;
    }

    public DCInboundWorkbookResponse getDcInboundWorkbook(Long planId, String channelDesc) {
        List<DCInboundExcelResponse> dcInboundData = getDCInboundData(planId,channelDesc);
        List<String> headers = getHeaders(dcInboundData);
        Workbook dcInboundWorkbook = dcInboundSheetExporter.generate(headers,dcInboundData);
        return new DCInboundWorkbookResponse(getDefaultFileName(), dcInboundWorkbook);
    }

    private String getDefaultFileName() {
        final String FILE_EXTENSION = ".xlsx";
        final String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        return DC_INBOUND_REPORT_NAME.concat(currentDateTime).concat(FILE_EXTENSION);
    }

    public List<DCInboundExcelResponse> getDCInboundData(Long planId, String channelDesc) {
        Integer channelId = CommonUtil.getChannelId(channelDesc);
        List<DCInboundResponse> response = ccSpReplnPkConsRepository.getDCInboundsByPlanIdAndChannelId(planId,channelId);
        return setDCInboundExcelSheetResponseDTO(response);
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
                dcInboundExcelResponse.setCcId(r.getCustomerChoice());
                dcInboundExcelResponse.setMerchMethodDesc(r.getMerchMethodDesc());
                dcInboundExcelResponse.setSizeDesc(r.getSizeDesc());
                dcInboundExcelResponse.setChannelDesc(r.getChannelDesc());
                if (r.getReplenishment() != null) {
                    try {
                        replenishmentDTO = Arrays.stream(objectMapper.readValue(r.getReplenishment(), DCinboundReplenishment[].class))
                              .sorted(Comparator.comparing(DCinboundReplenishment::getReplnWeek)).collect(Collectors.toList());

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

    private List<String> getHeaders(List<DCInboundExcelResponse> listDCInboundData) {
        List<String> headers = new ArrayList<>();

        headers.add(CATEGORY);
        headers.add(SUB_CATEGORY);
        headers.add(FINELINE);
        headers.add(STYLE);
        headers.add(CUSTOMER_CHOICE);
        headers.add(MERCH_METHOD);
        headers.add(SIZE);
        headers.add(CHANNEL);

        List<String> replenWeeks = listDCInboundData.stream().findFirst()
              .get().getReplenishment().stream()
              .map(DCinboundReplenishment::getReplnWeekDesc)
              .distinct()
              .collect(Collectors.toList());

        headers.addAll(replenWeeks);
        return headers;
    }
}


