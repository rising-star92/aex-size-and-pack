package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.repository.common.ReplenishmentCommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CalculateBuyQuantityService {

    private final CalculateFinelineBuyQuantity calculateFinelineBuyQuantity;
    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    private final ReplenishmentCommonRepository replenishmentCommonRepository;

    public CalculateBuyQuantityService(CalculateFinelineBuyQuantity calculateFinelineBuyQuantity,
                                       SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository,
                                       ReplenishmentCommonRepository replenishmentCommonRepository) {
        this.calculateFinelineBuyQuantity = calculateFinelineBuyQuantity;
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.replenishmentCommonRepository = replenishmentCommonRepository;
    }

    @Transactional
    public void calculateBuyQuantity(CalculateBuyQtyRequest calculateBuyQtyRequest) {
        List<CalculateBuyQtyParallelRequest> calculateBuyQtyParallelRequests = new ArrayList<>();
        try {
            if (!CollectionUtils.isEmpty(calculateBuyQtyRequest.getLvl3List())) {
                calculateBuyQtyRequest.getLvl3List().forEach(lvl3Dto -> {
                    if (!CollectionUtils.isEmpty(lvl3Dto.getLvl4List())) {
                        lvl3Dto.getLvl4List().forEach(lvl4Dto -> {
                            if (!CollectionUtils.isEmpty(lvl4Dto.getFinelines())) {
                                lvl4Dto.getFinelines().forEach(finelineDto -> {
                                    CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest = new CalculateBuyQtyParallelRequest();
                                    calculateBuyQtyParallelRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
                                    calculateBuyQtyParallelRequest.setLvl0Nbr(calculateBuyQtyRequest.getLvl0Nbr());
                                    calculateBuyQtyParallelRequest.setLvl1Nbr(calculateBuyQtyRequest.getLvl1Nbr());
                                    calculateBuyQtyParallelRequest.setLvl2Nbr(calculateBuyQtyRequest.getLvl2Nbr());
                                    calculateBuyQtyParallelRequest.setChannel(calculateBuyQtyRequest.getChannel());
                                    calculateBuyQtyParallelRequest.setLvl3Nbr(lvl3Dto.getLvl3Nbr());
                                    calculateBuyQtyParallelRequest.setLvl4Nbr(lvl4Dto.getLvl4Nbr());
                                    calculateBuyQtyParallelRequest.setFinelineNbr(finelineDto.getFinelineNbr());
                                    calculateBuyQtyParallelRequests.add(calculateBuyQtyParallelRequest);
                                });
                            } else
                                log.info("No Finelines available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
                        });
                    } else
                        log.info("No Sub Categories available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
                });
            } else
                log.info("No Categories available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
            if (!CollectionUtils.isEmpty(calculateBuyQtyParallelRequests)) {
                calculateFinelinesParallel(calculateBuyQtyRequest, calculateBuyQtyParallelRequests);
            } else log.info("No Fineline to process");
        } catch (Exception e) {
            log.error("Failed to Calculate Buy Quantity. Error: ", e);
        }
    }

    private void calculateFinelinesParallel(CalculateBuyQtyRequest calculateBuyQtyRequest, List<CalculateBuyQtyParallelRequest> calculateBuyQtyParallelRequests) {

        Set<Integer> finelinesToDelete = calculateBuyQtyParallelRequests.stream()
              .map(CalculateBuyQtyParallelRequest::getFinelineNbr)
              .collect(Collectors.toSet());

        deleteExistingReplnValues(calculateBuyQtyRequest, finelinesToDelete);
        deleteExistingBuyQuantityValues(calculateBuyQtyRequest, finelinesToDelete);

        List<SpFineLineChannelFixture> spFineLineChannelFixtures1 = spFineLineChannelFixtureRepository.findSpFineLineChannelFixtureBySpFineLineChannelFixtureId_planIdAndSpFineLineChannelFixtureId_channelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel())).orElse(new ArrayList<>());

        List<MerchCatgReplPack> merchCatgReplPacks = replenishmentCommonRepository.getMerchCatgReplPackRepository().findMerchCatgReplPackByMerchCatgReplPackId_planIdAndMerchCatgReplPackId_channelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel())).orElse(new ArrayList<>());

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

                        calculateBuyQtyResponse =  calculateFinelineBuyQuantity.calculateFinelineBuyQty(calculateBuyQtyRequest, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);

                        Set<SpFineLineChannelFixture> spFineLineChannelFixturesSet = new HashSet<>(calculateBuyQtyResponse.getSpFineLineChannelFixtures());
                        Set<MerchCatgReplPack> merchCatgReplPacksSet = new HashSet<>(calculateBuyQtyResponse.getMerchCatgReplPacks());
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

        spFineLineChannelFixtureRepository.saveAll(allSPFinelineChannelFixtures);
        replenishmentCommonRepository.getMerchCatgReplPackRepository().saveAll(allMerchCatReplns);
    }

    private void deleteExistingReplnValues(CalculateBuyQtyRequest calculateBuyQtyRequest, Set<Integer> replFinelinesToDelete) {
        replenishmentCommonRepository.getCcSpReplnPkConsRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getCcMmReplnPkConsRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getCcReplnPkConsRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getStyleReplenishmentRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
    }

    private void deleteExistingBuyQuantityValues(CalculateBuyQtyRequest calculateBuyQtyRequest, Set<Integer> replFinelinesToDelete) {
        replenishmentCommonRepository.getSpCustomerChoiceChannelFixtureSizeRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getSpCustomerChoiceChannelFixtureRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getSpStyleChannelFixtureRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
        replenishmentCommonRepository.getSpFineLineChannelFixtureRepository().deleteByPlanIdFinelineIdChannelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()), replFinelinesToDelete);
    }
}
