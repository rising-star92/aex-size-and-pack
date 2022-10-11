package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CalculateBuyQuantityService {

    private final CalculateFinelineBuyQuantity calculateFinelineBuyQuantity;
    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
    private final MerchCatgReplPackRepository merchCatgReplPackRepository;

    public CalculateBuyQuantityService(CalculateFinelineBuyQuantity calculateFinelineBuyQuantity,
                                       SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository,
                                       MerchCatgReplPackRepository merchCatgReplPackRepository) {
        this.calculateFinelineBuyQuantity = calculateFinelineBuyQuantity;
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.merchCatgReplPackRepository = merchCatgReplPackRepository;
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
        List<SpFineLineChannelFixture> spFineLineChannelFixtures1 = spFineLineChannelFixtureRepository.findSpFineLineChannelFixtureBySpFineLineChannelFixtureId_planIdAndSpFineLineChannelFixtureId_channelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel())).orElse(new ArrayList<>());
        List<MerchCatgReplPack> merchCatgReplPacks = merchCatgReplPackRepository.findMerchCatgReplPackByMerchCatgReplPackId_planIdAndMerchCatgReplPackId_channelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel())).orElse(new ArrayList<>());

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

                        Set<SpFineLineChannelFixture> spFineLineChannelFixturesSet = calculateBuyQtyResponse.getSpFineLineChannelFixtures().stream().collect(Collectors.toSet());
                        spFineLineChannelFixtureRepository.saveAll(spFineLineChannelFixturesSet);

                        Set<MerchCatgReplPack> merchCatgReplPacksSet =  calculateBuyQtyResponse.getMerchCatgReplPacks().stream().collect(Collectors.toSet());
                        //delete orphan repln catg and sub catg
                        deleteReplnOrphanRecords(merchCatgReplPacksSet);
                        merchCatgReplPackRepository.saveAll(merchCatgReplPacksSet);
                        return calculateBuyQtyResponse ;

                    } catch (Exception e) {
                        log.error("Failed to get Size profiles: ", e);
                        throw new CustomException("Failed to calculate buy quantity: " + e);
                    }
                })
        ).collect(Collectors.toList());
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[calculateBuyQtyParallelRequests.size()]));
        completableFutures
                .stream()
                .filter(completableFuture1 -> !completableFuture1.isCompletedExceptionally())
                .map(completableFuture1 -> {
                    try {
                        return completableFuture1.get();
                    } catch (Exception e) {
                        throw new CustomException("Failed to Execute calculate buy quantity");
                    }
                })
                .collect(Collectors.toList());
        if (completableFutures.size() != calculateBuyQtyParallelRequests.size()) {
            throw new CustomException("Not All finelines complted successfully");
        }
    }

    private void deleteReplnOrphanRecords(Set<MerchCatgReplPack> merchCatgReplPacks) {
        if (!CollectionUtils.isEmpty(merchCatgReplPacks)) {
            log.info("Deleting Replenishment Orphan records");
            merchCatgReplPacks.forEach(merchCatgReplPack -> {
                if (!CollectionUtils.isEmpty(merchCatgReplPack.getSubReplPack())) {
                    merchCatgReplPack.getSubReplPack().removeIf(subCatgReplPack -> CollectionUtils.isEmpty(subCatgReplPack.getFinelineReplPack()));
                }
            });
            merchCatgReplPacks.removeIf(merchCatgReplPack -> CollectionUtils.isEmpty(merchCatgReplPack.getSubReplPack()));
        }
    }
}
