package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
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

    public CalculateBuyQuantityService (CalculateFinelineBuyQuantity calculateFinelineBuyQuantity,
                                        SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository) {
        this.calculateFinelineBuyQuantity = calculateFinelineBuyQuantity;
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
    }

    @Transactional
    public void calculateBuyQuantity(CalculateBuyQtyRequest calculateBuyQtyRequest) {
        try {
            if (!CollectionUtils.isEmpty(calculateBuyQtyRequest.getLvl3List())) {
                calculateBuyQtyRequest.getLvl3List().forEach(lvl3Dto -> {
                    if (!CollectionUtils.isEmpty(lvl3Dto.getLvl4List())) {
                        lvl3Dto.getLvl4List().forEach(lvl4Dto -> {
                            if (!CollectionUtils.isEmpty(lvl4Dto.getFinelines())) {
                                calculateFinelinesParallel(calculateBuyQtyRequest, lvl3Dto, lvl4Dto);
                            } else
                                log.info("No Finelines available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
                        });
                    } else
                        log.info("No Sub Categories available to calculate buy quantity for request: {}", calculateBuyQtyRequest);
                });
            } else log.info("No Categories available to calculate buy quantity for request: {}", calculateBuyQtyRequest); }
        catch (Exception e) {
            log.error("Failed to Calculate Buy Quantity. Error: ", e);
        }
    }

    private void calculateFinelinesParallel(CalculateBuyQtyRequest calculateBuyQtyRequest, Lvl3Dto lvl3Dto, Lvl4Dto lvl4Dto) {
        List<SpFineLineChannelFixture> spFineLineChannelFixtures1 = spFineLineChannelFixtureRepository.findSpFineLineChannelFixtureBySpFineLineChannelFixtureId_planIdAndSpFineLineChannelFixtureId_channelId(calculateBuyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel())).orElse(new ArrayList<>());
        List<CompletableFuture<List<SpFineLineChannelFixture>>> completableFutures = lvl4Dto.getFinelines().stream().map(finelineDto -> CompletableFuture.supplyAsync(() -> {
                    List<SpFineLineChannelFixture> spFineLineChannelFixtures2 = Optional.of(spFineLineChannelFixtures1)
                            .stream()
                            .flatMap(Collection::stream)
                            .filter(spFineLineChannelFixture -> spFineLineChannelFixture.getSpFineLineChannelFixtureId().getFineLineNbr().equals(finelineDto.getFinelineNbr()))
                            .collect(Collectors.toList());
                    try {
                        return calculateFinelineBuyQuantity.calculateFinelineBuyQty(calculateBuyQtyRequest, lvl3Dto, lvl4Dto, finelineDto, spFineLineChannelFixtures2);
                    } catch (Exception e) {
                        log.error("Failed to get Size profiles: ", e);
                        throw new CustomException("Failed to calculate buy quantity: "+ e);
                    }
                })
        ).collect(Collectors.toList());
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[lvl4Dto.getFinelines().size()]));
        List<List<SpFineLineChannelFixture>> spFineLineChannelFixtures = completableFutures
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
        if (completableFutures.size() == lvl4Dto.getFinelines().size()) {
            Set<SpFineLineChannelFixture> spFineLineChannelFixtures2 = spFineLineChannelFixtures.stream()
                    .flatMap(Collection::stream).collect(Collectors.toSet());
            spFineLineChannelFixtureRepository.saveAll(spFineLineChannelFixtures2);
        } else throw new CustomException("Not All finelines complted successfully");
    }
}
