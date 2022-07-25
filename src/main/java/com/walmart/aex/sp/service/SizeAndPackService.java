package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyQuantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyQuantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyQuantity.FetchFineLineResponse;
import com.walmart.aex.sp.dto.planHierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planHierarchy.SizeAndPackResponse;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SizeAndPackService {

    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";

    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
    private final SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
    private final BuyQuantityMapper buyQunatityMapper;

    public SizeAndPackService(SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository, BuyQuantityMapper buyQunatityMapper,
                              SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository) {
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.buyQunatityMapper = buyQunatityMapper;
        this.spCustomerChoiceChannelFixtureRepository = spCustomerChoiceChannelFixtureRepository;
    }

    public FetchFineLineResponse fetchFinelineBuyQnty(BuyQtyRequest buyQtyRequest) {
        FetchFineLineResponse fetchFineLineResponse = new FetchFineLineResponse();
        try {
            List<BuyQntyResponseDTO> buyQntyResponseDTOS = spFineLineChannelFixtureRepository
                    .getBuyQntyByPlanChannel(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()));
            Optional.of(buyQntyResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(buyQntyResponseDTO -> buyQunatityMapper
                            .mapBuyQntyLvl2Sp(buyQntyResponseDTO, fetchFineLineResponse, null));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline Buy Qunatities :", e);
            throw new CustomException("Failed to fetch Fineline Buy Qunatities, due to" + e);
        }
        log.info("Fetch Buy Qty Fineline response: {}", fetchFineLineResponse);
        return fetchFineLineResponse;
    }

    public FetchFineLineResponse fetchCcBuyQnty(BuyQtyRequest buyQtyRequest, Integer finelineNbr) {
        FetchFineLineResponse fetchFineLineResponse = new FetchFineLineResponse();
        try {
            List<BuyQntyResponseDTO> buyQntyResponseDTOS = spCustomerChoiceChannelFixtureRepository
                    .getBuyQntyByPlanChannelFineline(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()), finelineNbr);
            Optional.of(buyQntyResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(buyQntyResponseDTO -> buyQunatityMapper
                            .mapBuyQntyLvl2Sp(buyQntyResponseDTO, fetchFineLineResponse, finelineNbr));
        } catch (Exception e) {
            log.error("Exception While fetching CC Buy Qunatities :", e);
            throw new CustomException("Failed to fetch CC Buy Qunatities, due to" + e);
        }
        log.info("Fetch Buy Qty CC response: {}", fetchFineLineResponse);
        return fetchFineLineResponse;
    }


//    @Transactional
//    public SizeAndPackResponse saveSizeAndPackData(PlanSizeAndPackDTO planSizeAndPackDTO) {
//        SizeAndPackResponse sizeAndPackResponse = new SizeAndPackResponse();
//        try {
//            log.info("Received the payload from strategy listener for CLP & Analytics: {}", objectMapper.writeValueAsString(planSizeAndPackDTO));
//        } catch (JsonProcessingException exp) {
//            sizeAndPackResponse.setStatus(FAILED_STATUS);
//            log.error("Couldn't parse the payload sent to Strategy Listener. Error: {}", exp.toString());
//        }
//
//        for (Lvl1 lvl1 : planSizeAndPackDTO.getLvl1List()) {
//            for (Lvl2 lvl2 : lvl1.getLvl2List()) {
//                for (Lvl3 lvl3 : lvl2.getLvl3List()) {
//                    for (Lvl4 lvl4 : lvl3.getLvl4List()) {
//                        for (Fineline fineline : lvl4.getFinelines()) {
//                            for (Style style : fineline.getStyles()) {
//                                customerChoiceRepository.save(sizeAndPackObjectMapper.mapCustChoice(planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl4, fineline, style));
//                                merchCatPlanRepository.save(sizeAndPackObjectMapper.mapMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, fineline));
//                                stylePlanRepository.save(sizeAndPackObjectMapper.mapStylePlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl4, fineline, style));
//                                subCatPlanRepository.save(sizeAndPackObjectMapper.mapSubCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl4, fineline));
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        sizeAndPackResponse.setStatus("Success");
//
//        return sizeAndPackResponse;
//
//        return null;
//  }




}
