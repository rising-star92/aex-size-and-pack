package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.entity.FinelinePlan;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.FinelinePlanRepository;
import com.walmart.aex.sp.repository.common.BuyQuantityCommonRepository;
import com.walmart.aex.sp.repository.common.ReplenishmentCommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.walmart.aex.security.service.UserDetailsService.getAuthenticatedUserName;
import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Slf4j
@Service
public class CalculateBuyQuantityService {
    private final BuyQuantityCommonRepository buyQuantityCommonRepository;
    private final ReplenishmentCommonRepository replenishmentCommonRepository;
    private final CalculateFinelineBuyQuantity calculateFinelineBuyQuantity;
    private final FinelinePlanRepository finelinePlanRepository;
    private final CalculateFinelineBuyQuantityMapper calculateFinelineBuyQuantityMapper;
    private final BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService;

    public CalculateBuyQuantityService(BuyQuantityCommonRepository buyQuantityCommonRepository,
                                       ReplenishmentCommonRepository replenishmentCommonRepository,
                                       CalculateFinelineBuyQuantity calculateFinelineBuyQuantity,
                                       FinelinePlanRepository finelinePlanRepository, CalculateFinelineBuyQuantityMapper calculateFinelineBuyQuantityMapper, BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService) {
        this.buyQuantityCommonRepository = buyQuantityCommonRepository;
        this.replenishmentCommonRepository = replenishmentCommonRepository;
        this.calculateFinelineBuyQuantity = calculateFinelineBuyQuantity;
        this.finelinePlanRepository = finelinePlanRepository;
        this.calculateFinelineBuyQuantityMapper = calculateFinelineBuyQuantityMapper;
        this.buyQtyReplenishmentMapperService = buyQtyReplenishmentMapperService;
    }

    @Transactional
    public List<StatusResponse> calculateBuyQuantity(CalculateBuyQtyRequest calculateBuyQtyRequest) {
        List<CalculateBuyQtyParallelRequest> calculateBuyQtyParallelRequests = new ArrayList<>();
        calculateBuyQtyRequest.setUserId(getAuthenticatedUserName());
        calculateBuyQtyRequest.setCreateTs(new Date());
        log.info("Received calculateBuyQtyRequest payload to calculate buy quantity: {} ",calculateBuyQtyRequest);
        try {
            List<FinelinePlan> finelinePlanList = getFinelinePlans(calculateBuyQtyRequest);
            if (!CollectionUtils.isEmpty(calculateBuyQtyRequest.getLvl3List())) {
                calculateBuyQtyRequest.getLvl3List().forEach(lvl3Dto -> {
                    if (!CollectionUtils.isEmpty(lvl3Dto.getLvl4List())) {
                        lvl3Dto.getLvl4List().forEach(lvl4Dto -> {
                            if (!CollectionUtils.isEmpty(lvl4Dto.getFinelines())) {
                                lvl4Dto.getFinelines().forEach(finelineDto ->
                                    calculateBuyQtyParallelRequests.add(createCalculateBuyQtyParallelRequest(finelinePlanList, lvl3Dto, lvl4Dto, finelineDto, calculateBuyQtyRequest))
                                );
                            } else
                                log.info("No Finelines available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
                        });
                    } else
                        log.info("No Sub Categories available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
                });
            } else
                log.info("No Categories available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
            if (!CollectionUtils.isEmpty(calculateBuyQtyParallelRequests)) {
                return calculateFinelinesParallel(calculateBuyQtyRequest, calculateBuyQtyParallelRequests);
            } else {
                log.info("No Fineline to process");
                return List.of(new StatusResponse(ERROR_STATUS, null));
            }
        } catch (Exception e) {
            log.error("Failed to Calculate Buy Quantity. Error: ", e);
            throw new CustomException("Failed to Calculate Buy Quantity: " + e.getMessage());
        }
    }

    private List<FinelinePlan> getFinelinePlans(CalculateBuyQtyRequest calculateBuyQtyRequest) {
        List<Integer> finelines = Optional.of(calculateBuyQtyRequest.getLvl3List()).stream()
                .flatMap(Collection::stream)
                .map(Lvl3Dto::getLvl4List).flatMap(Collection::stream)
                .map(Lvl4Dto::getFinelines).flatMap(Collection::stream)
                .mapToInt(FinelineDto::getFinelineNbr).boxed().collect(Collectors.toList());
        Optional<List<FinelinePlan>> finelinePlanList = finelinePlanRepository.findAllByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrIn(calculateBuyQtyRequest.getPlanId(), finelines);
        return finelinePlanList.orElse(null);
    }

    private static CalculateBuyQtyParallelRequest createCalculateBuyQtyParallelRequest(List<FinelinePlan> finelinePlanList, Lvl3Dto lvl3Dto, Lvl4Dto lvl4Dto, FinelineDto finelineDto, CalculateBuyQtyRequest calculateBuyQtyRequest) {
        CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest = new CalculateBuyQtyParallelRequest();
        calculateBuyQtyParallelRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        calculateBuyQtyParallelRequest.setUserId(calculateBuyQtyRequest.getUserId());
        calculateBuyQtyParallelRequest.setCreateTs(calculateBuyQtyRequest.getCreateTs());
        calculateBuyQtyParallelRequest.setChannel(calculateBuyQtyRequest.getChannel());
        calculateBuyQtyParallelRequest.setLvl3Nbr(lvl3Dto.getLvl3Nbr());
        calculateBuyQtyParallelRequest.setLvl4Nbr(lvl4Dto.getLvl4Nbr());
        calculateBuyQtyParallelRequest.setFinelineNbr(finelineDto.getFinelineNbr());
        Optional<FinelinePlan> optionalFinelinePlan = finelinePlanList.stream()
                .filter(fineline -> fineline.getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getLvl3Nbr().equals(lvl3Dto.getLvl3Nbr()) &&
                        fineline.getFinelinePlanId().getSubCatPlanId().getLvl4Nbr().equals(lvl4Dto.getLvl4Nbr()) &&
                        fineline.getFinelinePlanId().getFinelineNbr().equals(finelineDto.getFinelineNbr())).findFirst();
        if (optionalFinelinePlan.isPresent()) {
            FinelinePlan finelinePlan = optionalFinelinePlan.get();
            calculateBuyQtyParallelRequest.setLvl0Nbr(finelinePlan.getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getLvl0Nbr());
            calculateBuyQtyParallelRequest.setLvl1Nbr(finelinePlan.getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getLvl1Nbr());
            calculateBuyQtyParallelRequest.setLvl2Nbr(finelinePlan.getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getLvl2Nbr());
        }
        return calculateBuyQtyParallelRequest;
    }

    private List<StatusResponse> calculateFinelinesParallel(CalculateBuyQtyRequest calculateBuyQtyRequest, List<CalculateBuyQtyParallelRequest> calculateBuyQtyParallelRequests) {
        Set<Integer> failedFinelines = new HashSet<>();

        Set<Integer> requestFinelines = calculateBuyQtyParallelRequests.stream()
              .map(CalculateBuyQtyParallelRequest::getFinelineNbr)
              .collect(Collectors.toSet());

        deleteExistingReplnValues(calculateBuyQtyRequest, requestFinelines);
        deleteExistingBuyQuantityValues(calculateBuyQtyRequest, requestFinelines);

        List<SpFineLineChannelFixture> spFineLineChannelFixtures1 = buyQuantityCommonRepository.getSpFineLineChannelFixtureRepository().findSpFineLineChannelFixtureBySpFineLineChannelFixtureId_planIdAndSpFineLineChannelFixtureId_channelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel())).orElse(new ArrayList<>());

        List<MerchCatgReplPack> merchCatgReplPacks = replenishmentCommonRepository.getMerchCatgReplPackRepository().findMerchCatgReplPackByMerchCatgReplPackId_planIdAndMerchCatgReplPackId_channelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel())).orElse(new ArrayList<>());
        log.info("Started Executing Calculate Buy Quantity Parallel Requests : {}", calculateBuyQtyParallelRequests);
        List<CompletableFuture<CalculateBuyQtyResponse>> completableFutures = calculateBuyQtyParallelRequests.stream().map(calculateBuyQtyParallelRequest -> CompletableFuture.supplyAsync(() -> {
                    List<SpFineLineChannelFixture> spFineLineChannelFixtures2 = Optional.of(spFineLineChannelFixtures1)
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(spFineLineChannelFixture -> spFineLineChannelFixture.getSpFineLineChannelFixtureId().getFineLineNbr().equals(calculateBuyQtyParallelRequest.getFinelineNbr()))
                            .collect(Collectors.toList());

                    List<MerchCatgReplPack> merchCatgReplPacks1 = Optional.of(merchCatgReplPacks)
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(merchCatgReplPack -> merchCatgReplPack.getMerchCatgReplPackId().getRepTLvl3().equals(calculateBuyQtyParallelRequest.getLvl3Nbr())
                            )
                            .collect(Collectors.toList());

                    CalculateBuyQtyResponse calculateBuyQtyResponse = new CalculateBuyQtyResponse();

                    calculateBuyQtyResponse.setSpFineLineChannelFixtures(spFineLineChannelFixtures2);
                    calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks1);

                    try {
                        if (null != calculateBuyQtyRequest.getSeasonCode() && null != calculateBuyQtyRequest.getFiscalYear()) {
                            calculateBuyQtyResponse = calculateFinelineBuyQuantity.calculateFinelineBuyQtyV2(calculateBuyQtyRequest, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
                        } else {
                            calculateBuyQtyResponse =  calculateFinelineBuyQuantity.calculateFinelineBuyQty(calculateBuyQtyRequest, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
                        }

                        return calculateBuyQtyResponse ;

                    } catch (Exception e) {
                        log.error("Failed to get Size profiles: ", e);
                        throw new CustomException("Failed to calculate buy quantity: " + e);
                    }
                })
        ).collect(Collectors.toList());

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[calculateBuyQtyParallelRequests.size()]));
        List<CalculateBuyQtyResponse> responses = completableFutures
                .stream()
                .filter(completableFuture1 -> !completableFuture1.isCompletedExceptionally())
                .map(completableFuture1 -> {
                	 try {
                         return completableFuture1.get();
                     } catch (InterruptedException e) {
                    log.error("InterruptedException occurred while calculating buy quantity- ", e);
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    log.error("ExecutionException occurred while calculating buy quantity - ", e);
                    throw new CustomException("Failed to Execute calculate buy quantity");
                }
                	 return null;
                }).collect(Collectors.toList());

        if (completableFutures.size() != calculateBuyQtyParallelRequests.size()) {
            throw new CustomException("Not All finelines complted successfully");
        }

        //Save Replenishment
        Set<MerchCatgReplPack> allMerchCatReplns = responses
              .stream()
              .map(CalculateBuyQtyResponse::getMerchCatgReplPacks)
              .flatMap(Collection::stream)
              .collect(Collectors.toSet());

        Set<SpFineLineChannelFixture> allSPFinelineChannelFixtures = responses
              .stream()
              .map(CalculateBuyQtyResponse::getSpFineLineChannelFixtures)
              .flatMap(Collection::stream)
              .collect(Collectors.toSet());
        //update the final units based on validation errors
        for (SpFineLineChannelFixture spFinelineChannelFixture : allSPFinelineChannelFixtures) {
            calculateFinelineBuyQuantityMapper.resetToZeroSpFinelineFixtures(spFinelineChannelFixture, failedFinelines, calculateBuyQtyRequest);
        }
        for (MerchCatgReplPack merchCatgReplPack : allMerchCatReplns) {
            buyQtyReplenishmentMapperService.resetToZeroMerchCatgReplPack(merchCatgReplPack, requestFinelines, failedFinelines, calculateBuyQtyRequest);
        }
        buyQuantityCommonRepository.getSpFineLineChannelFixtureRepository().saveAll(allSPFinelineChannelFixtures);
        replenishmentCommonRepository.getMerchCatgReplPackRepository().saveAll(allMerchCatReplns);

        return createStatusResponse(failedFinelines, requestFinelines);
    }

    private List<StatusResponse> createStatusResponse(Set<Integer> failedFinelines, Set<Integer> allFinelines) {
        List<StatusResponse> statusResponses = new ArrayList<>();
        allFinelines.removeIf(failedFinelines::contains);
        if (!allFinelines.isEmpty()) {
            String successFinelines = allFinelines.stream().map(Objects::toString).collect(Collectors.joining(", "));
            statusResponses.add(new StatusResponse(SUCCESS_STATUS, successFinelines));
        }
        if (!failedFinelines.isEmpty()) {
            String failedFinelinesStr = failedFinelines.stream().map(Objects::toString).collect(Collectors.joining(", "));
            statusResponses.add(new StatusResponse(ERROR_STATUS, failedFinelinesStr));
        }
        return statusResponses;
    }

    private void deleteExistingReplnValues(CalculateBuyQtyRequest calculateBuyQtyRequest, Set<Integer> replFinelinesToDelete) {
        replenishmentCommonRepository.getCcSpReplnPkConsRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getCcMmReplnPkConsRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getCcReplnPkConsRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getStyleReplenishmentRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getFineLineReplenishmentRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
    }

    private void deleteExistingBuyQuantityValues(CalculateBuyQtyRequest calculateBuyQtyRequest, Set<Integer> replFinelinesToDelete) {
        buyQuantityCommonRepository.getSpCustomerChoiceChannelFixtureSizeRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        buyQuantityCommonRepository.getSpCustomerChoiceChannelFixtureRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        buyQuantityCommonRepository.getSpStyleChannelFixtureRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        buyQuantityCommonRepository.getSpFineLineChannelFixtureRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
    }
}
