package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.mapper.FineLineMapper;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.ColorCombinationRequest;
import com.walmart.aex.sp.dto.packoptimization.ColorCombinationStyle;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.PackOptConstraintRequest;
import com.walmart.aex.sp.dto.packoptimization.PackOptConstraintResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.UpdatePackOptConstraintRequestDTO;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import com.walmart.aex.sp.repository.FineLinePackOptimizationRepository;
import com.walmart.aex.sp.repository.FinelinePackOptRepository;
import com.walmart.aex.sp.repository.MerchPackOptimizationRepository;
import com.walmart.aex.sp.repository.StyleCcPackOptConsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.COLOR_COMBINATION_EXIST_MSG;
import static com.walmart.aex.sp.util.SizeAndPackConstants.COLOR_COMBINATION_MISSING_MSG;
import static com.walmart.aex.sp.util.SizeAndPackConstants.FAILED_STATUS;
import static com.walmart.aex.sp.util.SizeAndPackConstants.SUCCESS_STATUS;

@Service
@Slf4j
public class PackOptimizationService {

    private final FineLinePackOptimizationRepository finelinePackOptimizationRepository;
    private final PackOptimizationMapper packOptimizationMapper;
    private final FinelinePackOptRepository packOptfineplanRepo;
    private final AnalyticsMlSendRepository analyticsMlSendRepository;
    private final MerchPackOptimizationRepository merchPackOptimizationRepository;
    private final UpdatePackOptimizationMapper updatePackOptimizationMapper;
    private final StyleCcPackOptConsRepository styleCcPackOptConsRepository;
    private final PackOptConstraintMapper packOptConstraintMapper;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private static final String DEFAULT_COLOR_COMBINATION_ID = "0";
    private static final int COLOR_COMBINATION_INCREMENT_VALUE = 1;

    public PackOptimizationService(FineLinePackOptimizationRepository finelinePackOptimizationRepository,
                                   FinelinePackOptRepository packOptfineplanRepo,
                                   AnalyticsMlSendRepository analyticsMlSendRepository,
                                   PackOptimizationMapper packOptimizationMapper,
                                   StyleCcPackOptConsRepository styleCcPackOptConsRepository,
                                   PackOptConstraintMapper packOptConstraintMapper,
                                   MerchPackOptimizationRepository merchPackOptimizationRepository,
                                   UpdatePackOptimizationMapper updatePackOptimizationMapper,
                                   CcPackOptimizationRepository ccPackOptimizationRepository) {
        this.finelinePackOptimizationRepository = finelinePackOptimizationRepository;
        this.packOptfineplanRepo = packOptfineplanRepo;
        this.packOptimizationMapper = packOptimizationMapper;
        this.analyticsMlSendRepository = analyticsMlSendRepository;
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
        this.updatePackOptimizationMapper = updatePackOptimizationMapper;
        this.styleCcPackOptConsRepository = styleCcPackOptConsRepository;
        this.packOptConstraintMapper = packOptConstraintMapper;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
    }

    public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelId) {
        try {
            List<FineLineMapperDto> finePlanPackOptimizationList = packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelId);
            return packOptConstraintMapper.packOptDetails(finePlanPackOptimizationList);
        } catch (Exception e) {
            log.error("Error Occurred while fetching Pack Opt", e);
            throw e;
        }

    }

    public FineLinePackOptimizationResponse getPackOptFinelineDetails(Long planId, Integer finelineNbr) {
        FineLinePackOptimizationResponse finelinePackOptimizationResponse = new FineLinePackOptimizationResponse();

        try {
            List<FineLinePackOptimizationResponseDTO> finelinePackOptimizationResponseDTOS = finelinePackOptimizationRepository.getPackOptByFineline(planId, finelineNbr);
            Optional.of(finelinePackOptimizationResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(finelinePackOptimizationResponseDTO -> packOptimizationMapper.
                            mapPackOptimizationFineline(finelinePackOptimizationResponseDTO, finelinePackOptimizationResponse, planId));


        } catch (Exception e) {
            log.error("Exception While fetching Fineline pack Optimization :", e);
            throw new CustomException("Failed to fetch Fineline Pack Optimization , due to" + e);
        }
        log.info("Fetch Pack Optimization Fineline response: {}", finelinePackOptimizationResponse);
        return finelinePackOptimizationResponse;
    }

    public void updatePackOptServiceStatus(Long planId, Integer finelineNbr, Integer status) {
        analyticsMlSendRepository.updateStatus(planId, finelineNbr, status);
    }

    public StatusResponse updatePackOptConstraints(UpdatePackOptConstraintRequestDTO request) {
        StatusResponse response = new StatusResponse();
        if (isRequestValid(request)) {
            Integer channelId = ChannelType.getChannelIdFromName(request.getChannel());
            log.debug("Check if a MerchCatPackOptimization for planID : {} already exists or not", request.getPlanId().toString());
            List<MerchantPackOptimization> merchantPackOptimizationList = merchPackOptimizationRepository.findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl3AndChannelText_channelId(request.getPlanId(), request.getLvl3Nbr(), channelId);
            if (!CollectionUtils.isEmpty(merchantPackOptimizationList)) {
                updatePackOptimizationMapper.updateCategoryPackOptCons(request, merchantPackOptimizationList);
                response.setStatus(SUCCESS_STATUS);
            } else {
                log.warn("MerchCatPackOptimization for planID : {} doesn't exists and therefore cannot update", request.getPlanId().toString());
                response.setStatus(FAILED_STATUS);
            }
        } else {
            response.setStatus(FAILED_STATUS);
        }
        return response;
    }

    private boolean isRequestValid(UpdatePackOptConstraintRequestDTO request) {
        if (request.getLvl3Nbr() == null) {
            log.warn("Invalid Request: Lvl3Nbr cannot be NULL in the request {}", request.toString());
            return false;
        } else if (request.getLvl4Nbr() != null && request.getLvl3Nbr() == null) {
            log.warn("Invalid Request: Lvl3Nbr cannot be NULL when lvl4Nbr is not NULL in the request {}", request.toString());
            return false;
        } else if (request.getLvl4Nbr() == null && request.getFinelineNbr() != null) {
            log.warn("Invalid Request: Lvl4Nbr cannot be NULL when finelineNbr is not NULL in the request {}", request.toString());
            return false;
        } else if (request.getFinelineNbr() == null && request.getStyleNbr() != null) {
            log.warn("Invalid Request: finelineNbr cannot be NULL when styleNbr is not NULL in the request {}", request.toString());
            return false;
        } else if (request.getStyleNbr() == null && request.getCcId() != null) {
            log.warn("Invalid Request: styleNbr cannot be NULL when ccId is not NULL in the request {}", request.toString());
            return false;
        }
        return true;
    }

    public PackOptimizationResponse getPackOptConstraintDetails(PackOptConstraintRequest request) {
        PackOptimizationResponse packOptimizationResponse = new PackOptimizationResponse();
        try {
            List<PackOptConstraintResponseDTO> packOptConstraintResponseDTO = styleCcPackOptConsRepository
                    .findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(request.getPlanId(), ChannelType.getChannelIdFromName(request.getChannel()), request.getFinelineNbr());
            List<FineLineMapperDto> fineLineMapperDtoList = new ArrayList<>();
            for (PackOptConstraintResponseDTO packOptConstraintResponseDTO1: packOptConstraintResponseDTO) {
                FineLineMapperDto fineLineMapperDto = FineLineMapper.fineLineMapper.packOptConstraintResponseDTOToFineLineMapperDto(packOptConstraintResponseDTO1);
                fineLineMapperDtoList.add(fineLineMapperDto);
            }
            packOptimizationResponse = packOptConstraintMapper.packOptDetails(fineLineMapperDtoList);
        } catch (Exception e) {
            log.error("Exception While fetching Fineline PackOpt :", e);
            throw new CustomException("Failed to fetch Fineline PackOpt, due to" + e);
        }
        log.info("Fetch PackOpt Fineline response: {}", packOptimizationResponse);
        return packOptimizationResponse;

    }

    public StatusResponse deleteColorCombination(ColorCombinationRequest request) {
        StatusResponse response = new StatusResponse(SUCCESS_STATUS, SUCCESS_STATUS);
        try {
            if (request.getColorCombinationIds() == null || request.getColorCombinationIds().isEmpty()) {
                response.setMessage(COLOR_COMBINATION_MISSING_MSG);
                response.setStatus(FAILED_STATUS);
                return response;
            }
            List<CcPackOptimization> ccPackOptimizationList = ccPackOptimizationRepository.findCCPackOptimizationByColorCombinationList(request.getPlanId(),
                    request.getLvl0Nbr(), request.getLvl1Nbr(), request.getLvl2Nbr(), request.getLvl3Nbr(), request.getLvl4Nbr(),
                    request.getFinelineNbr(), request.getColorCombinationIds());
            if (!ccPackOptimizationList.isEmpty()) {
                for (CcPackOptimization ccPackOptimization : ccPackOptimizationList) {
                    ccPackOptimization.setColorCombination(null);
                }
                ccPackOptimizationRepository.saveAll(ccPackOptimizationList);
            }
        } catch (Exception e) {
            log.error("Exception While fetching CC PackOpt :", e);
            response.setMessage(e.getMessage());
            response.setStatus(FAILED_STATUS);
        }
        return response;
    }

    public StatusResponse addColorCombination(ColorCombinationRequest request) {
        StatusResponse response = new StatusResponse(SUCCESS_STATUS, SUCCESS_STATUS);
        try {
            Set<String> colorCombinationIds = ccPackOptimizationRepository.findCCPackOptimizationColorCombinationList(request.getPlanId(),
                    request.getLvl0Nbr(), request.getLvl1Nbr(), request.getLvl2Nbr(), request.getLvl3Nbr(), request.getLvl4Nbr(),
                    request.getFinelineNbr());
            String colorCombination = getColorCombinationId(colorCombinationIds);
            List<String> styles = request.getStyles().stream().map(ColorCombinationStyle::getStyleNbr).collect(Collectors.toList());
            List<String> customerChoices = request.getStyles().stream()
                    .map(ColorCombinationStyle::getCcIds)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            List<CcPackOptimization> ccPackOptimizationList = ccPackOptimizationRepository.findCCPackOptimizationList(request.getPlanId(),
                    request.getLvl0Nbr(), request.getLvl1Nbr(), request.getLvl2Nbr(), request.getLvl3Nbr(), request.getLvl4Nbr(),
                    request.getFinelineNbr(), styles, customerChoices);
            if (!ccPackOptimizationList.isEmpty()) {
                if (ccPackOptimizationList.stream().anyMatch(ccPackOptimization -> StringUtils.isNotEmpty(ccPackOptimization.getColorCombination()))) {
                    log.info("There are records which already have ColorCombinationId assigned. Cannot proceed further.");
                    response.setMessage(COLOR_COMBINATION_EXIST_MSG);
                    response.setStatus(FAILED_STATUS);
                    return response;
                }
                for (CcPackOptimization ccPackOptimization : ccPackOptimizationList) {
                    ccPackOptimization.setColorCombination(colorCombination);
                }
                ccPackOptimizationRepository.saveAll(ccPackOptimizationList);
            }
        } catch (Exception e) {
            log.error("Exception While fetching CC PackOpt :", e);
            response.setMessage(e.getMessage());
            response.setStatus(FAILED_STATUS);
        }
        return response;
    }

    private String getColorCombinationId(Set<String> colorCombinationIds) {
        int nextColorCombinationId =  colorCombinationIds.stream()
                .filter(id -> !StringUtils.isEmpty(id) && StringUtils.isNumeric(id))
                .mapToInt(Integer::valueOf)
                .max().orElse(Integer.parseInt(DEFAULT_COLOR_COMBINATION_ID)) + COLOR_COMBINATION_INCREMENT_VALUE;
        return String.valueOf(nextColorCombinationId);
    }
}