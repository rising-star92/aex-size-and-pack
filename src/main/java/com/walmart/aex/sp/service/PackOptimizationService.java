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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            finePlanPackOptimizationList = getUniqueFineLineMapperDTO(finePlanPackOptimizationList);
            return packOptDetails(finePlanPackOptimizationList, planId, channelId);
        } catch (Exception e) {
            log.error("Error Occurred while fetching Pack Opt", e);
            throw e;
        }

    }

    private List<FineLineMapperDto> getUniqueFineLineMapperDTO(List<FineLineMapperDto> finePlanPackOptimizationList) {
        Map<Integer, Map<String,FineLineMapperDto>> res = new HashMap<>();
        List<FineLineMapperDto> finalFineLineMapperDtoList = new ArrayList<>();
        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            res.putIfAbsent(fineLineMapperDto.getFineLineNbr(), new HashMap<>());
            Map<String, FineLineMapperDto> currentCCIDMap = res.get(fineLineMapperDto.getFineLineNbr());
            currentCCIDMap.put(fineLineMapperDto.getCcId()+""+fineLineMapperDto.getFineLineNbr(), fineLineMapperDto);
            res.put(fineLineMapperDto.getFineLineNbr(), currentCCIDMap);
        });
        res.forEach((k,v) -> v.keySet().stream().map(v::get).forEach(finalFineLineMapperDtoList::add));
        return finalFineLineMapperDtoList;
    }

    private PackOptimizationResponse packOptDetails(
            List<FineLineMapperDto> fineLineMapperDtos,
            Long planId,
            Integer channelId) {

        PackOptimizationResponse packOptResp = new PackOptimizationResponse();
        packOptResp.setPlanId(planId);
        packOptResp.setChannel(ChannelType.getChannelNameFromId(channelId));
        fineLineMapperDtos.forEach(fineLineMapperDto ->
                mapPackOptLvl2(fineLineMapperDto, packOptResp)
        );

        return packOptResp;
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
            Optional.of(fineLineMapperDtoList)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(fineLineMapperDto -> mapPackOptLvl2(fineLineMapperDto, packOptimizationResponse));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline PackOpt :", e);
            throw new CustomException("Failed to fetch Fineline PackOpt, due to" + e);
        }
        log.info("Fetch PackOpt Fineline response: {}", packOptimizationResponse);
        return packOptimizationResponse;

    }

    private void mapPackOptLvl2(FineLineMapperDto fineLineMapperDto, PackOptimizationResponse response) {
        if (response.getPlanId() == null) {
            response.setPlanId(fineLineMapperDto.getPlanId());
        }
        if (response.getLvl0Nbr() == null) {
            response.setLvl0Nbr(fineLineMapperDto.getLvl0Nbr());
            response.setLvl0Desc(fineLineMapperDto.getLvl0Desc());
        }
        if (response.getLvl1Nbr() == null) {
            response.setLvl1Nbr(fineLineMapperDto.getLvl1Nbr());
            response.setLvl1Desc(fineLineMapperDto.getLvl1Desc());
        }
        if (response.getLvl2Nbr() == null) {
            response.setLvl2Nbr(fineLineMapperDto.getLvl2Nbr());
            response.setLvl2Desc(fineLineMapperDto.getLvl2Desc());
        }
        if (response.getChannel() == null) {
            response.setChannel(ChannelType.getChannelNameFromId(fineLineMapperDto.getChannelId()));
        }
        response.setLvl3List(packOptConstraintMapper.mapPackOptLvl3(fineLineMapperDto, response));
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
//        TODO: add logic for generating colorCombination
        String colorCombination = "4-22-1";
        try {
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
}