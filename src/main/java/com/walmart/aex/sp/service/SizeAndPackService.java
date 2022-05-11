package com.walmart.aex.sp.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.*;
import com.walmart.aex.sp.repository.CustomerChoiceRepository;
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import com.walmart.aex.sp.repository.StylePlanRepository;
import com.walmart.aex.sp.repository.SubCatPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SizeAndPackService {

    @Autowired
    CustomerChoiceRepository customerChoiceRepository;

    @Autowired
    MerchCatPlanRepository merchCatPlanRepository;

    @Autowired
    StylePlanRepository stylePlanRepository;

    @Autowired
    SubCatPlanRepository subCatPlanRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SizeAndPackObjectMapper sizeAndPackObjectMapper;

    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";


    @Transactional
    public SizeAndPackResponse saveSizeAndPackData(PlanSizeAndPackDTO planSizeAndPackDTO) {
        SizeAndPackResponse sizeAndPackResponse = new SizeAndPackResponse();
        try {
            log.info("Received the payload from strategy listener for CLP & Analytics: {}", objectMapper.writeValueAsString(planSizeAndPackDTO));
        } catch (JsonProcessingException exp) {
            sizeAndPackResponse.setStatus(FAILED_STATUS);
            log.error("Couldn't parse the payload sent to Strategy Listener. Error: {}", exp.toString());
        }

        for (Lvl1 lvl1 : planSizeAndPackDTO.getLvl1List()) {
            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                for (Lvl3 lvl3 : lvl2.getLvl3List()) {
                    for (Lvl4 lvl4 : lvl3.getLvl4List()) {
                        for (Fineline fineline : lvl4.getFinelines()) {
                            for (Style style : fineline.getStyles()) {
                                customerChoiceRepository.save(sizeAndPackObjectMapper.mapCustChoice(planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl4, fineline, style));
                                merchCatPlanRepository.save(sizeAndPackObjectMapper.mapMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, fineline));
                                stylePlanRepository.save(sizeAndPackObjectMapper.mapStylePlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl4, fineline, style));
                                subCatPlanRepository.save(sizeAndPackObjectMapper.mapSubCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl4, fineline));
                            }
                        }
                    }
                }
            }
        }
        sizeAndPackResponse.setStatus("Success");

        return sizeAndPackResponse;

    }
}
