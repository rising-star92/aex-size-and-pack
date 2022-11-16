package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.commitmentreport.InitialBumpSetResponse;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetPackRequest;
import com.walmart.aex.sp.dto.commitmentreport.Metrics;
import com.walmart.aex.sp.dto.commitmentreport.RFAInitialSetBumpSetResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.isVolume.FinelineVolume;
import com.walmart.aex.sp.dto.isVolume.InitialSetVolumeRequest;
import com.walmart.aex.sp.dto.isVolume.InitialSetVolumeResponse;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class SizeAndPackService {

    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";

    public static final String ERROR_MSG_LP = "Failed to save the line plan events to size and pack database. Error: {}";
    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
    private final SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
    private final SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;

    private final MerchCatPlanRepository merchCatPlanRepository;
    private final BuyQuantityMapper buyQuantityMapper;

    private final SizeAndPackObjectMapper sizeAndPackObjectMapper;

    private final StrategyFetchService strategyFetchService;

    private final SizeAndPackDeleteService sizeAndPackDeleteService;

    private final SizeAndPackDeletePlanService sizeAndPackDeletePlanService;

    private final BuyQtyCommonUtil buyQtyCommonUtil;
    
    private final BigQueryInitialSetPlanService bigQueryInitialSetPlanService;
    
    private final InitialSetPlanMapper initialSetPlanMapper;

    @ManagedConfiguration
	BigQueryConnectionProperties bigQueryConnectionProperties;

    @ManagedConfiguration
    GraphQLProperties graphQLProperties;

    final ObjectMapper objectMapper = new ObjectMapper();

    public SizeAndPackService(SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository, BuyQuantityMapper buyQuantityMapper,
                              SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository,
                              SizeAndPackObjectMapper sizeAndPackObjectMapper,
                              MerchCatPlanRepository merchCatPlanRepository, StrategyFetchService strategyFetchService,
                              SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository,
                              SizeAndPackDeleteService sizeAndPackDeleteService, SizeAndPackDeletePlanService sizeAndPackDeletePlanService
            , BuyQtyCommonUtil buyQtyCommonUtil, BigQueryInitialSetPlanService bigQueryInitialSetPlanService,InitialSetPlanMapper initialSetPlanMapper) {
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.buyQuantityMapper = buyQuantityMapper;
        this.spCustomerChoiceChannelFixtureRepository = spCustomerChoiceChannelFixtureRepository;
        this.sizeAndPackObjectMapper = sizeAndPackObjectMapper;
        this.merchCatPlanRepository = merchCatPlanRepository;
        this.strategyFetchService = strategyFetchService;
        this.spCustomerChoiceChannelFixtureSizeRepository = spCustomerChoiceChannelFixtureSizeRepository;

        this.sizeAndPackDeleteService = sizeAndPackDeleteService;
        this.sizeAndPackDeletePlanService = sizeAndPackDeletePlanService;
        this.buyQtyCommonUtil = buyQtyCommonUtil;
        this.bigQueryInitialSetPlanService = bigQueryInitialSetPlanService;
        this.initialSetPlanMapper = initialSetPlanMapper;
    }

    public BuyQtyResponse fetchFinelineBuyQnty(BuyQtyRequest buyQtyRequest) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        try {
            List<BuyQntyResponseDTO> buyQntyResponseDTOS;
            if (buyQtyRequest.getChannel() != null) {
                BuyQtyResponse finelinesWithSizesFromStrategy = strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest);
                if (finelinesWithSizesFromStrategy != null) {
                    buyQntyResponseDTOS = spFineLineChannelFixtureRepository
                            .getBuyQntyByPlanChannel(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()));
                    buyQtyResponse = buyQtyCommonUtil.filterFinelinesWithSizes(buyQntyResponseDTOS, finelinesWithSizesFromStrategy);
                }
            } else {
                BuyQtyResponse buyQtyResponseAllChannels = new BuyQtyResponse();
                buyQntyResponseDTOS = spFineLineChannelFixtureRepository
                        .getBuyQntyByPlanChannel(buyQtyRequest.getPlanId(), null);
                Optional.of(buyQntyResponseDTOS)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(buyQntyResponseDTO -> buyQuantityMapper
                                .mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponseAllChannels, null));
                return buyQtyResponseAllChannels;
            }
            return buyQtyResponse;

        } catch (Exception e) {
            log.error("Exception While fetching Fineline Buy Qunatities with Sizes :", e);
            throw new CustomException("Failed to fetch Fineline Buy Qunatities with Sizes, due to" + e);
        }
    }

    public BuyQtyResponse fetchCcBuyQnty(BuyQtyRequest buyQtyRequest, Integer finelineNbr) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        try {
            List<BuyQntyResponseDTO> buyQntyResponseDTOS;
            if (buyQtyRequest.getChannel() != null) {
                BuyQtyResponse stylesCcWithSizesFromStrategy = strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest, finelineNbr);
                if (stylesCcWithSizesFromStrategy != null) {
                    buyQntyResponseDTOS = spCustomerChoiceChannelFixtureRepository
                            .getBuyQntyByPlanChannelFineline(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()), finelineNbr);
                    buyQtyResponse = buyQtyCommonUtil.filterStylesCcWithSizes(buyQntyResponseDTOS, stylesCcWithSizesFromStrategy, finelineNbr);
                }
            } else {
                BuyQtyResponse buyQtyResponseAllChannels = new BuyQtyResponse();
                buyQntyResponseDTOS = spCustomerChoiceChannelFixtureRepository
                        .getBuyQntyByPlanChannelFineline(buyQtyRequest.getPlanId(), null, finelineNbr);
                Optional.of(buyQntyResponseDTOS)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(buyQntyResponseDTO -> buyQuantityMapper
                                .mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponseAllChannels, finelineNbr));
                return buyQtyResponseAllChannels;
            }
            return buyQtyResponse;
        } catch (Exception e) {
            log.error("Exception While fetching CC Buy Qunatities with Sizes:", e);
            throw new CustomException("Failed to fetch CC Buy Qunatities with Sizes, due to" + e);
        }
    }

    public BuyQtyResponse fetchSizeBuyQnty(BuyQtyRequest buyQtyRequest) {
        try {
            BuyQtyResponse buyQtyResponse = strategyFetchService.getBuyQtyResponseSizeProfile(buyQtyRequest);

            if (buyQtyResponse != null) {
                List<BuyQntyResponseDTO> buyQntyResponseDTOS = spCustomerChoiceChannelFixtureSizeRepository
                        .getSizeBuyQntyByPlanChannelCc(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()), buyQtyRequest.getCcId());

                List<SizeDto> sizeDtos = fetchSizes(buyQtyResponse);
                Optional.of(sizeDtos)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(sizeDto -> buyQuantityMapper
                                .mapBuyQntySizeSp(buyQntyResponseDTOS, sizeDto));
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
                .filter(clustersDto -> clustersDto.getClusterID().equals(0))
                .findFirst()
                .map(ClustersDto::getSizes)
                .orElse(new ArrayList<>());
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
                        merchCatPlanRepository.saveAll(sizeAndPackObjectMapper.setMerchCatPlan(planSizeAndPackDTO, lvl1, lvl2, lvl3, merchCatPlanRepository));
                    }
                }
            }
            sizeAndPackResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception ex) {
            sizeAndPackResponse.setStatus(FAILED_STATUS);
            log.error(ERROR_MSG_LP, ex.toString());
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
            log.error(ERROR_MSG_LP, ex.toString());
        }
        return sizeAndPackResponse;
    }

    @Transactional
    public SizeAndPackResponse deleteSizeAndPackData(PlanSizeAndPackDeleteDTO request) {
        SizeAndPackResponse sizeAndPackResponse = new SizeAndPackResponse();
        try {
            StrongKey strongKey = Optional.ofNullable(request.getStrongKey()).orElse(null);
            if (strongKey != null) {
                if (strongKey.getFineline().getFinelineNbr() != null && CollectionUtils.isEmpty(strongKey.getFineline().getStyles())) {
                    sizeAndPackDeleteService.deleteSizeAndPackDataAtFl(strongKey.getPlanId(), strongKey.getLvl3Nbr(), strongKey.getLvl4Nbr(),
                            strongKey.getFineline().getFinelineNbr());
                } else if (!CollectionUtils.isEmpty(strongKey.getFineline().getStyles())) {
                    sizeAndPackDeleteService.deleteSizeAndPackDataAtStyleOrCC(strongKey.getFineline().getStyles(), strongKey.getPlanId(),
                            strongKey.getLvl3Nbr(), strongKey.getLvl4Nbr(), strongKey.getFineline().getFinelineNbr());
                }
                if (request.getSizeAndPackPayloadDTO() != null) {
                    updateSizeAndPackPlanData(request.getSizeAndPackPayloadDTO(), strongKey.getFineline());
                }
                sizeAndPackResponse.setStatus(SUCCESS_STATUS);
            } else {
                log.error("StrongKey not provided, please validate");
                throw new CustomException("StrongKey not provided, please validate");
            }
        } catch (Exception ex) {
            sizeAndPackResponse.setStatus(FAILED_STATUS);
            log.error(ERROR_MSG_LP, ex.toString());
        }
        return sizeAndPackResponse;
    }

    private void updateSizeAndPackPlanData(PlanSizeAndPackDTO sizeAndPackPayloadDTO, Fineline fineline) {
        try {
            List<Lvl1> lvl1s = sizeAndPackPayloadDTO.getLvl1List();
            for (Lvl1 lvl1 : lvl1s) {
                for (Lvl2 lvl2 : lvl1.getLvl2List()) {
                    for (Lvl3 lvl3 : lvl2.getLvl3List()) {
                        merchCatPlanRepository.saveAll(sizeAndPackDeletePlanService.updateMerchCatPlan(sizeAndPackPayloadDTO, lvl1, lvl2, lvl3, fineline, merchCatPlanRepository));
                    }
                }
            }
        } catch (Exception ex) {
            log.error(ERROR_MSG_LP, ex.toString());
        }
    }
    
    public InitialBumpSetResponse getInitialAndBumpSetDetails(InitialSetPackRequest request) {
    	InitialBumpSetResponse response = new InitialBumpSetResponse();
		List<RFAInitialSetBumpSetResponse> rfaInitialSetBumpSetResponses = new ArrayList<>();
		try {
			if (request.getPlanId() != null && request.getFinelineNbr() != null) {
				rfaInitialSetBumpSetResponses = bigQueryInitialSetPlanService.getInitialAndBumpSetDetails(request);
			}
			Optional.of(rfaInitialSetBumpSetResponses).stream().flatMap(Collection::stream).forEach(
					intialSetResponseOne -> initialSetPlanMapper.mapInitialSetPlan(intialSetResponseOne, response, request.getFinelineNbr()));


		} catch (Exception e) {
			log.error("Exception While fetching Initial Set Pack Quantities :", e);
		}

		return response;
	}


    public List<InitialSetVolumeResponse> getInitialAndBumpSetDetailsByVolumeCluster(InitialSetVolumeRequest request) {
        List<InitialSetVolumeResponse> response = new ArrayList<>();
        try {
            for (FinelineVolume fineline : request.getFinelines()) {
                response.addAll(bigQueryInitialSetPlanService.getInitialAndBumpSetDetailsByVolumeCluster(request.getPlanId(),fineline));
            }
        } catch (Exception e) {
            log.error("Exception While fetching Initial Set Cluster volume ", e);
        }
        return response;
    }
}
