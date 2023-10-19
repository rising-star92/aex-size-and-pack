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
import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.CommonGCPUtil;
import com.walmart.aex.sp.util.CommonUtil;
import com.walmart.aex.sp.util.PackOptimizationUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.PackOptimizationUtil.createAnalyticsMlSendEntry;
import static com.walmart.aex.sp.util.PackOptimizationUtil.setAnalyticsChildDataToAnalyticsMlSend;
import static com.walmart.aex.sp.util.SizeAndPackConstants.BUMPPACK_DETAILS_SUFFIX;
import static com.walmart.aex.sp.util.SizeAndPackConstants.COLOR_COMBINATION_EXIST_MSG;
import static com.walmart.aex.sp.util.SizeAndPackConstants.COLOR_COMBINATION_MISSING_MSG;
import static com.walmart.aex.sp.util.SizeAndPackConstants.FAILED_STATUS;
import static com.walmart.aex.sp.util.SizeAndPackConstants.MULTI_BUMP_PACK_SUFFIX;
import static com.walmart.aex.sp.util.SizeAndPackConstants.PACKOPT_FINELINE_DETAILS_SUFFIX;
import static com.walmart.aex.sp.util.SizeAndPackConstants.PACKOPT_FINELINE_STATUS_SUFFIX;
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
    private final SourcingFactoryService sourcingFactoryService;
    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    private final IntegrationHubService integrationHubService;

    private final PackOptimizationUtil packOptimizationUtil;

    @ManagedConfiguration
    private IntegrationHubServiceProperties integrationHubServiceProperties;
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
                                   SourcingFactoryService sourcingFactoryService, SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository, IntegrationHubService integrationHubService, PackOptimizationUtil packOptimizationUtil, CommonGCPUtil commonGCPUtil) {
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
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.integrationHubService = integrationHubService;
        this.packOptimizationUtil = packOptimizationUtil;
        this.commonGCPUtil = commonGCPUtil;
    }

    public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelId) {
        try {
            List<FineLineMapperDto> finePlanPackOptimizationList = packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelId);
            if (CollectionUtils.isEmpty(finePlanPackOptimizationList)) {
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
            Integer totalCCsAcrossAllSets = ccPackOptimizationRepository.getTotalCCsAcrossAllSetsByPlanIdFineline(planId,finelineNbr);
            if (CollectionUtils.isEmpty(finelinePackOptimizationResponseDTOS)) {
                return finelinePackOptimizationResponse;
            }
            Optional.of(finelinePackOptimizationResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(finelinePackOptimizationResponseDTO -> packOptimizationMapper.
                            mapPackOptimizationFineline(finelinePackOptimizationResponseDTO, finelinePackOptimizationResponse, planId, bumpPackNbr,totalCCsAcrossAllSets));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline pack Optimization :", e);
            throw new CustomException("Failed to fetch Fineline Pack Optimization , due to" + e);
        }
        log.info("Fetch Pack Optimization Fineline response: {}", finelinePackOptimizationResponse);
        return finelinePackOptimizationResponse;
    }

    @Transactional
    public void updatePackOptServiceStatus(Long planId, String finelineNbr, Integer status) {
        List<Integer> fineLineAndBumpCount = CommonUtil.getNumbersFromString(finelineNbr);
        Integer fineLineNumber = !fineLineAndBumpCount.isEmpty() ? fineLineAndBumpCount.get(0) : null;
        Integer bumpNbr = fineLineAndBumpCount.size() > 1 ? fineLineAndBumpCount.get(1) : 1;
        if (fineLineNumber != null) {
            try {
                log.info("Pack Optimization completed for planId:{} and fineLineNbr:{} and bumpPack:{} with RunStatusCode:{}",
                        planId,finelineNbr,bumpNbr,status);
                Optional<AnalyticsMlSend> analyticsMlSend = analyticsMlSendRepository.findByPlanIdAndFinelineNbrAndRunStatusCode(planId, fineLineNumber, RunStatusCodeType.SENT_TO_ANALYTICS.getId()
                );
                if (analyticsMlSend.isPresent()) {
                    Set<AnalyticsMlChildSend> analyticsMlChildSendList = analyticsMlSend.get().getAnalyticsMlChildSend();
                    analyticsMlSend.get().setEndTs(new Date());
                    for (AnalyticsMlChildSend analyticsMlChildSend : analyticsMlChildSendList) {
                        if (Objects.equals(analyticsMlChildSend.getBumpPackNbr(), bumpNbr)) {
                            analyticsMlChildSend.setRunStatusCode(status);
                            analyticsMlChildSend.setEndTs(new Date());
                            updateParentRunStatusCode(analyticsMlSend.get());
                            break;
                        }
                    }
                    analyticsMlSendRepository.save(analyticsMlSend.get());
                }
            } catch (Exception ex) {
                log.info("Failed to update status for planId: {} and fineLineNbr:{} with RunStatusCode:{}",
                        planId, finelineNbr, RunStatusCodeType.SENT_TO_ANALYTICS.getId());
            }
        }
    }

    private void updateParentRunStatusCode(AnalyticsMlSend analyticsMlSend) {
        Set<AnalyticsMlChildSend> analyticsMlChildSendList = analyticsMlSend.getAnalyticsMlChildSend();
        Set<Integer> runStatusCodeSentAndAnalyticsFailedSet = analyticsMlChildSendList
                .stream()
                .filter(val -> Objects.equals(RunStatusCodeType.SENT_TO_ANALYTICS.getId(), val.getRunStatusCode()) ||
                        RunStatusCodeType.ANALYTICS_ERRORS_LIST.contains(val.getRunStatusCode()))
                .map(AnalyticsMlChildSend::getRunStatusCode)
                .collect(Collectors.toSet());

        if(runStatusCodeSentAndAnalyticsFailedSet.isEmpty()) {
            analyticsMlSend.setRunStatusCode(RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId());
        } else {
            if (RunStatusCodeType.ANALYTICS_ERRORS_LIST.stream().anyMatch(runStatusCodeSentAndAnalyticsFailedSet::contains)) {
                analyticsMlSend.setRunStatusCode(RunStatusCodeType.ERROR.getId());
            }
            if (runStatusCodeSentAndAnalyticsFailedSet.contains(RunStatusCodeType.SENT_TO_ANALYTICS.getId())) {
                analyticsMlSend.setRunStatusCode(RunStatusCodeType.SENT_TO_ANALYTICS.getId());
            }
        }

    }

    public StatusResponse updatePackOptConstraints(UpdatePackOptConstraintRequestDTO request) {
        StatusResponse response = new StatusResponse();
        if (isRequestValid(request)) {
            Integer channelId = ChannelType.getChannelIdFromName(request.getChannel());
            log.debug("Check if a MerchCatPackOptimization for planID : {} already exists or not", request.getPlanId().toString());
            List<MerchantPackOptimization> merchantPackOptimizationList = merchPackOptimizationRepository.findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl3AndChannelText_channelId(request.getPlanId(), request.getLvl3Nbr(), channelId);
            if (!CollectionUtils.isEmpty(merchantPackOptimizationList)) {
                FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
                if (request.getFactoryId() != null && request.getFactoryName() == null) {
                    factoryDetails = sourcingFactoryService.getFactoryDetails(request.getFactoryId());
                } else if (request.getFactoryId() != null && request.getFactoryName() != null) {
                    factoryDetails.setFactoryName(request.getFactoryName().trim());
                }
                updatePackOptimizationMapper.updateCategoryPackOptCons(request, merchantPackOptimizationList, factoryDetails);
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
            log.warn("Invalid Request: Lvl3Nbr cannot be NULL in the request {}", request);
            return false;
        } else if (request.getLvl4Nbr() != null && request.getLvl3Nbr() == null) {
            log.warn("Invalid Request: Lvl3Nbr cannot be NULL when lvl4Nbr is not NULL in the request {}", request);
            return false;
        } else if (request.getLvl4Nbr() == null && request.getFinelineNbr() != null) {
            log.warn("Invalid Request: Lvl4Nbr cannot be NULL when finelineNbr is not NULL in the request {}", request);
            return false;
        } else if (request.getFinelineNbr() == null && request.getStyleNbr() != null) {
            log.warn("Invalid Request: finelineNbr cannot be NULL when styleNbr is not NULL in the request {}", request);
            return false;
        } else if (request.getStyleNbr() == null && request.getCcId() != null) {
            log.warn("Invalid Request: styleNbr cannot be NULL when ccId is not NULL in the request {}", request);
            return false;
        }
        return true;
    }

    public PackOptimizationResponse getPackOptConstraintDetails(PackOptConstraintRequest request) {
        PackOptimizationResponse packOptimizationResponse;
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
        Map<String, IntegrationHubResponseDTO> fineLineWithIntegrationHubResponseDTOMap = new HashMap<>();
        Map<String, IntegrationHubRequestDTO> fineLineWithIntegrationHubRequestDTOMap = new HashMap<>();
        try {
            List<String> finelineIsBsList = getFinelineIsBsList(request);
            finelineIsBsList.forEach(finelineNbr -> {
                IntegrationHubRequestDTO integrationHubRequestDTO = getIntegrationHubRequest(request.getPlanId(), finelineNbr);
                if (!finelineNbr.contains(MULTI_BUMP_PACK_SUFFIX)) {
                    deleteMultiBumpPackDataSet(request.getPlanId(), Integer.valueOf(finelineNbr), integrationHubServiceProperties.getEnv());
                }
                IntegrationHubResponseDTO integrationHubResponseDTO = integrationHubService.callIntegrationHubForPackOpt(integrationHubRequestDTO);
                if (null != integrationHubResponseDTO) {
                    fineLineWithIntegrationHubResponseDTOMap.put(finelineNbr, integrationHubResponseDTO);
                    fineLineWithIntegrationHubRequestDTOMap.put(finelineNbr, integrationHubRequestDTO);
                    log.info("Successfully processed request to IntegrationHub. PlanId:{} & fineLineNbr: {}", request.getPlanId(), finelineNbr);
                } else {
                    throw new CustomException("Unable to process the request to IntegrationHub. PlanId:" + request.getPlanId() + " & fineLineNbr: " + finelineNbr);
                }
            });
            saveAnalyticDataInDB(request, fineLineWithIntegrationHubResponseDTOMap, fineLineWithIntegrationHubRequestDTOMap);

            //todo - for now, sending the Execution id as 1 in the response
            BigInteger bigInteger = BigInteger.ONE;
            runPackOptResponse = new RunPackOptResponse(new Execution(bigInteger, HttpStatus.OK.value(), SUCCESS_STATUS, null));
            return runPackOptResponse;
        } catch (Exception ex) {
            log.error("Error connecting with Integration Hub service for request: {} ", request, ex);
            return null;
        }
    }

    private void saveAnalyticDataInDB(RunPackOptRequest request, Map<String, IntegrationHubResponseDTO> fineLineWithIntegrationHubResponseDTOMap, Map<String, IntegrationHubRequestDTO> fineLineWithIntegrationHubRequestDTOMap) {
        Set<AnalyticsMlSend> analyticsMlSendSet = createAnalyticsMlSendEntry(request, fineLineWithIntegrationHubResponseDTOMap);
        if (!CollectionUtils.isEmpty(analyticsMlSendSet)) {
            analyticsMlSendSet = updateAnalyticsMlSendWithAnalyticsChildData(analyticsMlSendSet,
                    fineLineWithIntegrationHubResponseDTOMap, fineLineWithIntegrationHubRequestDTOMap);
            analyticsMlSendRepository.saveAll(analyticsMlSendSet);
        }
    }

    private Set<AnalyticsMlSend> updateAnalyticsMlSendWithAnalyticsChildData(Set<AnalyticsMlSend> analyticsMlSendSet,
                                                                             Map<String, IntegrationHubResponseDTO> flWithIHResMap,
                                                                             Map<String, IntegrationHubRequestDTO> flWithIHReqMap) {
        Set<AnalyticsMlSend> res = new HashSet<>();
        Long planId = analyticsMlSendSet.iterator().next().getPlanId();
        List<Integer> fineLines = analyticsMlSendSet.stream().map(AnalyticsMlSend::getFinelineNbr).collect(Collectors.toList());
        Map<Integer, Integer> fineLineWithBumpCntMap = getBumpPackByFineLineMap(planId, fineLines);
        for (AnalyticsMlSend analyticsMlSend : analyticsMlSendSet) {
            Set<AnalyticsMlChildSend> analyticsMlChildSendSet = setAnalyticsChildDataToAnalyticsMlSend(flWithIHResMap, fineLineWithBumpCntMap, analyticsMlSend, flWithIHReqMap);
            analyticsMlSend.setAnalyticsMlChildSend(analyticsMlChildSendSet);
            res.add(analyticsMlSend);
        }
        return res;
    }

    private Map<Integer, Integer> getBumpPackByFineLineMap(Long planId, List<Integer> fineLines) {
        Map<Integer, Integer> fineLineWithBumpCntMap = new HashMap<>();
        List<BuyQntyResponseDTO> bumpPackCntByFinelines = spFineLineChannelFixtureRepository.getBumpPackCntByFinelines(planId, fineLines);
        bumpPackCntByFinelines.forEach(buyQntyResponseDTO -> fineLineWithBumpCntMap.put(buyQntyResponseDTO.getFinelineNbr(), buyQntyResponseDTO.getBumpPackCnt()));
        return fineLineWithBumpCntMap;
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
                    } else {
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

    public List<PackOptFinelinesByStatusResponse> getPackOptFinelinesByStatus(List<Integer> status) {
        List<PackOptFinelinesByStatusResponse> finelinesByStatus = new ArrayList<>();
        List<AnalyticsMlSend> analyticsMlSend = analyticsMlSendRepository.getAllFinelinesByStatus(status);
        if (!CollectionUtils.isEmpty(analyticsMlSend)) {
            analyticsMlSend.forEach(result -> {
                PackOptFinelinesByStatusResponse finelineByStatusResponse = new PackOptFinelinesByStatusResponse();
                finelineByStatusResponse.setPlanId(result.getPlanId());
                finelineByStatusResponse.setFinelineNbr(result.getFinelineNbr());
                finelineByStatusResponse.setRunStatusCode(result.getRunStatusCode());
                finelineByStatusResponse.setRunStatusDesc(result.getRunStatusText().getRunStatusDesc());
                finelineByStatusResponse.setStartTs(result.getStartTs());
                finelineByStatusResponse.setEndTs(result.getEndTs());
                finelinesByStatus.add(finelineByStatusResponse);
            });
        } else {
            log.info("No Pack Optimization finelines found with status code : {}", status);
        }
        return finelinesByStatus;
    }
}