package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.planhierarchy.Lvl1;
import com.walmart.aex.sp.dto.planhierarchy.Lvl2;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.SizeAndPackResponse;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class SizeAndPackService {

    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";

    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
    private final SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
    private final SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;

    private final MerchCatPlanRepository merchCatPlanRepository;
    private final BuyQuantityMapper buyQuantityMapper;

    private final SizeAndPackObjectMapper sizeAndPackObjectMapper;

    private final RestApiService restApiService;

    @ManagedConfiguration
    GraphQLProperties graphQLProperties;

    final ObjectMapper objectMapper = new ObjectMapper();

    public SizeAndPackService(SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository, BuyQuantityMapper buyQuantityMapper,
                              SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository,
                              SizeAndPackObjectMapper sizeAndPackObjectMapper,
                              MerchCatPlanRepository merchCatPlanRepository, RestApiService restApiService,
                              SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository) {
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.buyQuantityMapper = buyQuantityMapper;
        this.spCustomerChoiceChannelFixtureRepository = spCustomerChoiceChannelFixtureRepository;
        this.sizeAndPackObjectMapper = sizeAndPackObjectMapper;
        this.merchCatPlanRepository = merchCatPlanRepository;
        this.restApiService = restApiService;
        this.spCustomerChoiceChannelFixtureSizeRepository = spCustomerChoiceChannelFixtureSizeRepository;
    }

    public BuyQtyResponse fetchFinelineBuyQnty(BuyQtyRequest buyQtyRequest) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        try {
            List<BuyQntyResponseDTO> buyQntyResponseDTOS = spFineLineChannelFixtureRepository
                    .getBuyQntyByPlanChannel(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()));
            Optional.of(buyQntyResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(buyQntyResponseDTO -> buyQuantityMapper
                            .mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponse, null));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline Buy Qunatities :", e);
            throw new CustomException("Failed to fetch Fineline Buy Qunatities, due to" + e);
        }
        log.info("Fetch Buy Qty Fineline response: {}", buyQtyRequest);
        return buyQtyResponse;
    }

    public BuyQtyResponse fetchCcBuyQnty(BuyQtyRequest buyQtyRequest, Integer finelineNbr) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        try {
            List<BuyQntyResponseDTO> buyQntyResponseDTOS = spCustomerChoiceChannelFixtureRepository
                    .getBuyQntyByPlanChannelFineline(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()), finelineNbr);
            Optional.of(buyQntyResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(buyQntyResponseDTO -> buyQuantityMapper
                            .mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponse, finelineNbr));
        } catch (Exception e) {
            log.error("Exception While fetching CC Buy Qunatities :", e);
            throw new CustomException("Failed to fetch CC Buy Qunatities, due to" + e);
        }
        log.info("Fetch Buy Qty CC response: {}", buyQtyResponse);
        return buyQtyResponse;
    }

    public BuyQtyResponse fetchSizeBuyQnty(BuyQtyRequest buyQtyRequest) {
        try {
            BuyQtyResponse buyQtyResponse = getBuyQtyResponseSizeProfile(buyQtyRequest);

            if (buyQtyResponse != null) {
                List<BuyQntyResponseDTO> buyQntyResponseDTOS = spCustomerChoiceChannelFixtureSizeRepository
                        .getSizeBuyQntyByPlanChannelCc(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()), buyQtyRequest.getCcId());

                Optional.of(buyQntyResponseDTOS)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(buyQntyResponseDTO -> buyQuantityMapper
                                .mapBuyQntySizeSp(buyQntyResponseDTO, fetchSizes(buyQtyResponse)));
                log.info("Fetch Buy Qty CC response: {}", buyQtyResponse);
            }
            return buyQtyResponse;
        } catch (Exception e) {
            log.error("Exception While fetching CC Buy Qunatities :", e);
            throw new CustomException("Failed to fetch CC Buy Qunatities, due to" + e);
        }
    }

    private List<SizeDto> fetchSizes(BuyQtyResponse buyQtyResponse) {
        return Optional.of(buyQtyResponse.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3Dto::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4Dto::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(FinelineDto::getStyles)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(StyleDto::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(CustomerChoiceDto::getClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(clustersDto -> clustersDto.getClusterId().equals(0))
                .findFirst()
                .map(ClustersDto::getSizes)
                .orElse(new ArrayList<>());
    }

    private BuyQtyResponse getBuyQtyResponseSizeProfile(BuyQtyRequest buyQtyRequest) throws SizeAndPackException {
        Map<String, String> headers = new HashMap<>();
        headers.put("WM_CONSUMER.ID", graphQLProperties.getSizeProfileConsumerId());
        headers.put("WM_SVC.NAME", graphQLProperties.getSizeProfileConsumerName());
        headers.put("WM_SVC.ENV", graphQLProperties.getSizeProfileConsumerEnv());

        Map<String, Object> data = new HashMap<>();
        data.put("sizeProfileRequest", buyQtyRequest);

        GraphQLResponse graphQLResponse = restApiService.getSizeProfiles(graphQLProperties.getSizeProfileUrl(), graphQLProperties.getSizeProfileQuery(),
                headers,data);

        return Optional.ofNullable(graphQLResponse)
                .stream()
                .map(GraphQLResponse::getData)
                .map(Payload::getGetCcSizeClus)
                .findFirst()
                .orElse(null);
    }


    @Transactional
    public SizeAndPackResponse saveSizeAndPackData(PlanSizeAndPackDTO planSizeAndPackDTO) {
        SizeAndPackResponse sizeAndPackResponse = new SizeAndPackResponse();
        try {
            log.info("Received the payload from strategy listener for CLP & Analytics: {}", objectMapper.writeValueAsString(planSizeAndPackDTO));
        } catch (JsonProcessingException exp) {
            sizeAndPackResponse.setStatus(FAILED_STATUS);
            log.error("Couldn't parse the payload sent to Strategy Listener. Error: {}", exp.toString());
        }

        try {
            for (Lvl1 lvl1 : planSizeAndPackDTO.getLvl1List()) {
                for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                    for (Lvl3 lvl3 : lvl2.getLvl3List()) {
                        merchCatPlanRepository.saveAll(sizeAndPackObjectMapper.setMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3));
                    }
                }
            }
            sizeAndPackResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception ex) {
            sizeAndPackResponse.setStatus(FAILED_STATUS);
            log.error("Failed to save the line plan events to size and pack database. Error: {}", ex.toString());
        }
        return sizeAndPackResponse;
  }


    @Transactional
    public SizeAndPackResponse updateSizeAndPackData(PlanSizeAndPackDTO planSizeAndPackDTO) {
        SizeAndPackResponse sizeAndPackResponse = new SizeAndPackResponse();
        try {
            log.info("Received the Updated payload from strategy listener for CLP & Analytics: {}", objectMapper.writeValueAsString(planSizeAndPackDTO));
        } catch (JsonProcessingException exp) {
            sizeAndPackResponse.setStatus(FAILED_STATUS);
            log.error("Couldn't parse the payload sent to Size and Pack. Error: {}", exp.toString());
        }

        try {
            for (Lvl1 lvl1 : planSizeAndPackDTO.getLvl1List()) {
                for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                    for (Lvl3 lvl3 : lvl2.getLvl3List()) {
                        merchCatPlanRepository.saveAll(sizeAndPackObjectMapper.updateMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, lvl2.getLvl3List(), merchCatPlanRepository));
                    }
                }
            }
            sizeAndPackResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception ex) {
            sizeAndPackResponse.setStatus(FAILED_STATUS);
            log.error("Failed to save the line plan events to size and pack database. Error: {}", ex.toString());
        }
        return sizeAndPackResponse;
    }
}
