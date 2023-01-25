package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestContextDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.mapper.FineLineMapper;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsResponse;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.CommonGCPUtil;
import com.walmart.aex.sp.util.PackOptimizationUtil;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

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
    private final SourcingFactoryService sourcingFactoryService;

    @Autowired
    private SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    @Autowired
    private IntegrationHubService integrationHubService;

    @ManagedConfiguration
    private IntegrationHubServiceProperties integrationHubServiceProperties;

    @Autowired
    private PackOptimizationUtil packOptimizationUtil;
    private final CommonGCPUtil commonGCPUtil;
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
                                   CcPackOptimizationRepository ccPackOptimizationRepository,
                                   SourcingFactoryService sourcingFactoryService, CommonGCPUtil commonGCPUtil) {
        this.finelinePackOptimizationRepository = finelinePackOptimizationRepository;
        this.packOptfineplanRepo = packOptfineplanRepo;
        this.packOptimizationMapper = packOptimizationMapper;
        this.analyticsMlSendRepository = analyticsMlSendRepository;
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
        this.updatePackOptimizationMapper = updatePackOptimizationMapper;
        this.styleCcPackOptConsRepository = styleCcPackOptConsRepository;
        this.packOptConstraintMapper = packOptConstraintMapper;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
        this.sourcingFactoryService = sourcingFactoryService;
        this.commonGCPUtil = commonGCPUtil;
    }

    public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelId) {
        try {
            List<FineLineMapperDto> finePlanPackOptimizationList = packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelId);
            if(CollectionUtils.isEmpty(finePlanPackOptimizationList)) {
                return new PackOptimizationResponse();
            }
            return packOptConstraintMapper.packOptDetails(finePlanPackOptimizationList);
        } catch (Exception e) {
            log.error("Error Occurred while fetching Pack Opt", e);
            throw e;
        }

    }

    public FineLinePackOptimizationResponse getPackOptFinelineDetails(Long planId, Integer finelineNbr, Integer bumpPackNbr) {
        FineLinePackOptimizationResponse finelinePackOptimizationResponse = new FineLinePackOptimizationResponse();
        try {
            List<FineLinePackOptimizationResponseDTO> finelinePackOptimizationResponseDTOS = finelinePackOptimizationRepository.getPackOptByFineline(planId, finelineNbr);
            if(CollectionUtils.isEmpty(finelinePackOptimizationResponseDTOS)) {
                return finelinePackOptimizationResponse;
            }
            Optional.of(finelinePackOptimizationResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(finelinePackOptimizationResponseDTO -> packOptimizationMapper.
                            mapPackOptimizationFineline(finelinePackOptimizationResponseDTO, finelinePackOptimizationResponse, planId, bumpPackNbr));
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
                FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
                if(request.getFactoryId()!=null && request.getFactoryName()==null){
                    factoryDetails = sourcingFactoryService.getFactoryDetails(request.getFactoryId());
                }else if(request.getFactoryId()!=null && request.getFactoryName()!=null){
                    factoryDetails.setFactoryName(request.getFactoryName().trim());
                }
                updatePackOptimizationMapper.updateCategoryPackOptCons(request, merchantPackOptimizationList,factoryDetails);
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
            for (PackOptConstraintResponseDTO packOptConstraintResponseDTO1 : packOptConstraintResponseDTO) {
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
        if (CollectionUtils.isEmpty(colorCombinationIds)) return DEFAULT_COLOR_COMBINATION_ID;
        int nextColorCombinationId = colorCombinationIds.stream()
                .filter(StringUtils::isNumeric)
                .mapToInt(Integer::valueOf)
                .max().orElse(Integer.parseInt(DEFAULT_COLOR_COMBINATION_ID)) + COLOR_COMBINATION_INCREMENT_VALUE;
        return String.valueOf(nextColorCombinationId);
    }

    public void deleteMultiBumpPackDataSet(Long planId, Integer finelineNbr, String env) {
        try {
            String storagePathInput = env + "/input/" + planId;
            String storagePathOutput = env + "/output/" + planId;
            String multiBumpSetInputFolderPrefix = storagePathInput + '/' + finelineNbr + MULTI_BUMP_PACK_SUFFIX;
            String multiBumpSetOutputFolderPrefix = storagePathOutput + '/' + finelineNbr + MULTI_BUMP_PACK_SUFFIX;
            boolean isDeletedAllInputFolders = commonGCPUtil.delete(storagePathInput, multiBumpSetInputFolderPrefix);
            boolean isDeletedAllOutputFolders = commonGCPUtil.delete(storagePathOutput, multiBumpSetOutputFolderPrefix);

            if (isDeletedAllInputFolders && isDeletedAllOutputFolders) {
                log.info("Bump Pack dataset cleanup for planId {} and finelineNbr {} completed successfully !", planId, finelineNbr);
            } else {
                log.info("Bump Pack dataset cleanup for planId {} and finelineNbr {} not completed successfully !", planId, finelineNbr);
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting GCP bump pack dataset for planId {} and finelineNbr {}. Exception: ", planId, finelineNbr, e);
        }

    }

    public RunPackOptResponse callIntegrationHubForPackOptByFineline(RunPackOptRequest request) {
        RunPackOptResponse runPackOptResponse = null;
        try {
            List<String> finelineIsBsList = getFinelineIsBsList(request);
            finelineIsBsList.forEach(finelineNbr -> {
                IntegrationHubRequestDTO integrationHubRequestDTO = getIntegrationHubRequest(request.getPlanId(), finelineNbr);
                if (!finelineNbr.contains(MULTI_BUMP_PACK_SUFFIX)) {
                    deleteMultiBumpPackDataSet(request.getPlanId(), Integer.valueOf(finelineNbr), integrationHubServiceProperties.getEnv());
                }
                IntegrationHubResponseDTO integrationHubResponseDTO = integrationHubService.callIntegrationHubForPackOpt(integrationHubRequestDTO);
                if (null != integrationHubResponseDTO) {
                    Set<AnalyticsMlSend> analyticsMlSendSet = packOptimizationUtil.createAnalyticsMlSendEntry(request, integrationHubRequestDTO, integrationHubResponseDTO.getWf_running_id(), integrationHubResponseDTO.getStarted_time());
                    analyticsMlSendRepository.saveAll(analyticsMlSendSet);
                    log.info("Done creating the entries in analytics_ml_send for plan_id : {}, finelineNbr: {}", request.getPlanId(), finelineNbr);
                } else {
                    throw new CustomException("Unable to reach Integration Hub service for plan_id :" + request.getPlanId() + "finelineNbr: " + finelineNbr);
                }
            });
            //todo - for now, sending the Execution id as 1 in the response
            BigInteger bigInteger = BigInteger.ONE;
            runPackOptResponse = new RunPackOptResponse(new Execution(bigInteger, HttpStatus.OK.value(), SUCCESS_STATUS, null));
            return runPackOptResponse;
        } catch (Exception ex) {
            log.error("Error connecting with Integration Hub service for request: {} ", request,ex);
            return null;
        }
    }

    private List<String> getFinelineIsBsList(RunPackOptRequest request) {
        InputRequest inputRequest = request.getInputRequest();
        List<Integer> finelinesList = new ArrayList<>();
        List<String> finelineIsBsList = new ArrayList<>();
        if (inputRequest != null) {
            for (Lvl3Dto lvl3 : inputRequest.getLvl3List()) {
                for (Lvl4Dto lv4 : lvl3.getLvl4List()) {
                    for (FinelineDto finelines : lv4.getFinelines()) {
                        finelinesList.add(finelines.getFinelineNbr());
                    }
                }
            }
            setFinelineIsBSList(request, finelinesList, finelineIsBsList);
        }
        return finelineIsBsList;
    }

    private void setFinelineIsBSList(RunPackOptRequest request, List<Integer> finelinesList, List<String> finelineIsBsList) {
        List<BuyQntyResponseDTO> bumpPackCntByFinelines = spFineLineChannelFixtureRepository.getBumpPackCntByFinelines(request.getPlanId(), finelinesList);
        bumpPackCntByFinelines.forEach(bumpPackCntByFineline -> {
            if (bumpPackCntByFineline.getBumpPackCnt() > 1) {
                int bumpPackCntFlag = 1;
                while (bumpPackCntFlag <= bumpPackCntByFineline.getBumpPackCnt()) {
                    if (bumpPackCntFlag > 1) {
                        finelineIsBsList.add(bumpPackCntByFineline.getFinelineNbr().toString() + MULTI_BUMP_PACK_SUFFIX + bumpPackCntFlag);
                        bumpPackCntFlag++;
                    }
                    else {
                        finelineIsBsList.add(bumpPackCntByFineline.getFinelineNbr().toString());
                        bumpPackCntFlag++;
                    }
                }
            } else finelineIsBsList.add(bumpPackCntByFineline.getFinelineNbr().toString());
        });

    }

    private IntegrationHubRequestDTO getIntegrationHubRequest(Long planId, String finelineNbr) {
        List<String> finelineIsBsList = new ArrayList<>();
        finelineIsBsList.add(finelineNbr);
        IntegrationHubRequestDTO integrationHubRequestDTO = new IntegrationHubRequestDTO();
        IntegrationHubRequestContextDTO integrationHubRequestContextDTO = new IntegrationHubRequestContextDTO();
        String sizeAndPackSvcUrl = integrationHubServiceProperties.getSizeAndPackUrl();
        if (finelineNbr.contains(MULTI_BUMP_PACK_SUFFIX)) {
            integrationHubRequestContextDTO.setGetPackOptFinelineDetails(sizeAndPackSvcUrl + PACKOPT_FINELINE_DETAILS_SUFFIX + BUMPPACK_DETAILS_SUFFIX);
        } else {
            integrationHubRequestContextDTO.setGetPackOptFinelineDetails(sizeAndPackSvcUrl + PACKOPT_FINELINE_DETAILS_SUFFIX);
        }
        integrationHubRequestContextDTO.setUpdatePackOptFinelineStatus(sizeAndPackSvcUrl + PACKOPT_FINELINE_STATUS_SUFFIX);
        integrationHubRequestContextDTO.setPlanId(planId);
        integrationHubRequestContextDTO.setFinelineNbrs(finelineIsBsList);
        integrationHubRequestContextDTO.setEnv(integrationHubServiceProperties.getEnv());
        integrationHubRequestDTO.setContext(integrationHubRequestContextDTO);
        return integrationHubRequestDTO;
    }
}