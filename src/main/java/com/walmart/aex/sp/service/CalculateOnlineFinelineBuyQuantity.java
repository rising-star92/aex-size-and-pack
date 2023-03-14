package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.CustomerChoice;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.bqfp.Style;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPackId;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.walmart.aex.sp.util.SizeAndPackConstants.VP_DEFAULT;


@Service
@Slf4j
public class CalculateOnlineFinelineBuyQuantity {

    private final ObjectMapper objectMapper;
    private final BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService;

    private final ReplenishmentsOptimizationService replenishmentsOptimizationServices;

    public CalculateOnlineFinelineBuyQuantity(ObjectMapper objectMapper,
                                              BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService, ReplenishmentsOptimizationService replenishmentsOptimizationServices) {
        this.objectMapper = objectMapper;
        this.buyQtyReplenishmentMapperService = buyQtyReplenishmentMapperService;
        this.replenishmentsOptimizationServices = replenishmentsOptimizationServices;
    }


    public CalculateBuyQtyResponse calculateOnlineBuyQty(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest,
                                                         FinelineDto finelineDto,
                                                         BQFPResponse bqfpResponse,
                                                         CalculateBuyQtyResponse calculateBuyQtyResponse) {
        //Online
        List<MerchMethodsDto> merchMethodsDtos = new ArrayList<>();
        if (ChannelType.ONLINE.getDescription().equalsIgnoreCase(calculateBuyQtyParallelRequest.getChannel())) {
            MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
            merchMethodsDto.setFixtureTypeRollupId(FixtureTypeRollup.ONLINE_FIXTURE.getCode());
            merchMethodsDto.setMerchMethod("ONLINE_MERCH_METHOD");
            merchMethodsDto.setMerchMethodCode(0);
            merchMethodsDtos.add(merchMethodsDto);
        }

        merchMethodsDtos.forEach(merchMethodsDto -> {
            if (merchMethodsDto.getMerchMethodCode() != null) {
                if (!CollectionUtils.isEmpty(finelineDto.getStyles())) {
                    getOnlineStyles(finelineDto.getStyles(), merchMethodsDto, bqfpResponse, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
                } else log.info("Styles Online Size Profiles are empty to calculate buy Qty: {}", finelineDto);
            }
        });
        return calculateBuyQtyResponse;
    }

    private void getOnlineStyles(List<StyleDto> styles, MerchMethodsDto merchMethodsDto, BQFPResponse bqfpResponse,
                                 CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) {
        styles.forEach(styleDto -> {
            log.info("Checking if Online Styles are existing: {}", styleDto);
            if (!CollectionUtils.isEmpty(styleDto.getCustomerChoices())) {
                getOnlineCustomerChoices(styleDto, merchMethodsDto, bqfpResponse, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
            }
        });
    }

    private void getOnlineCustomerChoices(StyleDto styleDto, MerchMethodsDto merchMethodsDto, BQFPResponse bqfpResponse,
                                          CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) {
        styleDto.getCustomerChoices().forEach(customerChoiceDto -> {
            log.info("Checking if Online Cc are existing: {}", customerChoiceDto);
            //TODO: Delete replenishment if replenishment is deleted after set
            if (!CollectionUtils.isEmpty(customerChoiceDto.getClusters())) {
                getOnlineCcClusters(styleDto, customerChoiceDto, merchMethodsDto, bqfpResponse, calculateBuyQtyResponse, calculateBuyQtyParallelRequest);
            }
        });
    }

    private void getOnlineCcClusters(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, MerchMethodsDto merchMethodsDto,
                                     BQFPResponse bqfpResponse, CalculateBuyQtyResponse calculateBuyQtyResponse, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId = new HashMap<>();
        //Replenishment
        List<Replenishment> replenishments = getReplenishments(bqfpResponse, styleDto, customerChoiceDto);
        log.info("Get All Replenishments if exists for customerchoice: {} and merch method: {}", customerChoiceDto.getCcId(), merchMethodsDto.getFixtureTypeRollupId());
        if (!CollectionUtils.isEmpty(replenishments)) {
            //Set Replenishment for Size Map
            Optional.ofNullable(customerChoiceDto.getClusters())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(clustersDto1 -> clustersDto1.getClusterID().equals(0))
                    .findFirst().ifPresent(clustersDto -> setReplenishmentSizes(clustersDto, replenishments, storeBuyQtyBySizeId));
        }

        Set<CcSpMmReplPack> ccSpMmReplPacks = new HashSet<>();
        for (Map.Entry<SizeDto, BuyQtyObj> entry : storeBuyQtyBySizeId.entrySet()) {
            //Replenishment
            if (!CollectionUtils.isEmpty(replenishments)) {

                long totalReplenishment = 0L;
                if ((!CollectionUtils.isEmpty(entry.getValue().getReplenishments()))) {
                    totalReplenishment = entry.getValue().getReplenishments()
                            .stream()
                            .filter(Objects::nonNull)
                            .mapToLong(replenishment -> Optional.ofNullable(replenishment.getAdjReplnUnits()).orElse(0L))
                            .sum();
                }

                setCcMmSpReplenishment(ccSpMmReplPacks, entry, (int) totalReplenishment, (int) totalReplenishment);
            }
        }
        if (!CollectionUtils.isEmpty(ccSpMmReplPacks)) {
            //Replenishment
            List<MerchCatgReplPack> merchCatgReplPacks = buyQtyReplenishmentMapperService.setAllReplenishments(styleDto, merchMethodsDto, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks);
            calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
        }
    }

    private void setCcMmSpReplenishment(Set<CcSpMmReplPack> ccSpMmReplPacks, Map.Entry<SizeDto, BuyQtyObj> entry, int totalReplenishment, int totalBuyQty) {
        CcSpMmReplPackId ccSpMmReplPackId = new CcSpMmReplPackId();
        ccSpMmReplPackId.setAhsSizeId(entry.getKey().getAhsSizeId());

        CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
        ccSpMmReplPack.setSizeDesc(entry.getKey().getSizeDesc());

        ccSpMmReplPack.setCcSpReplPackId(ccSpMmReplPackId);

        ccSpMmReplPack.setFinalBuyUnits(totalBuyQty);
        ccSpMmReplPack.setReplUnits(totalReplenishment);
        try {
            ccSpMmReplPack.setReplenObj(objectMapper.writeValueAsString(entry.getValue().getReplenishments()));
        } catch (Exception e) {
            log.error("Failed to create replenishment Obj for size: {}", entry.getKey(), e);
            throw new CustomException("Failed to create replenishment Obj for size " + e);
        }

        ccSpMmReplPacks.add(ccSpMmReplPack);
    }


    //TODO: Move to common Replenishment Utils
    private List<Replenishment> getReplenishments(BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        return Optional.ofNullable(bqfpResponse.getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(styleDto.getStyleNbr()))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(customerChoiceDto.getCcId()))
                .findFirst()
                .map(CustomerChoice::getReplenishments)
                .orElse(new ArrayList<>());
    }

    private void setReplenishmentSizes(ClustersDto clustersDto, List<Replenishment> replenishments, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId) {
        clustersDto.getSizes().forEach(sizeDto -> {
            BuyQtyObj buyQtyObj;
            if (storeBuyQtyBySizeId.containsKey(sizeDto)) {
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            } else {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            }

            List<Replenishment> replObj = new ArrayList<>();

            replenishments.forEach(replenishment -> {
                Replenishment replenishment1 = new Replenishment();
                replenishment1.setReplnWeek(replenishment.getReplnWeek());
                replenishment1.setReplnWeekDesc(replenishment.getReplnWeekDesc());
                replenishment1.setAdjReplnUnits(Math.round ((getReplenishmentUnits(replenishment) * getAvgSizePct(sizeDto)) / 100));
                replObj.add(replenishment1);
            });
            buyQtyObj.setReplenishments(replenishmentsOptimizationServices.getUpdatedReplenishmentsPack(replObj,VP_DEFAULT, 2));

        });
    }

    private Double getAvgSizePct(SizeDto sizeDto) {
        final Double ZERO = 0.0;
        return sizeDto.getMetrics() != null
                ? Optional.ofNullable(sizeDto.getMetrics().getAdjAvgSizeProfilePct())
                .orElse(Optional.ofNullable(sizeDto.getMetrics().getAvgSizeProfilePct()).orElse(ZERO))
                : ZERO;
    }

    private Long getReplenishmentUnits(Replenishment replenishment) {
        if (replenishment.getDcInboundAdjUnits() != null && replenishment.getDcInboundAdjUnits() != 0) {
            return replenishment.getDcInboundAdjUnits();
        } else if (replenishment.getDcInboundUnits() != null) {
            return replenishment.getDcInboundUnits();
        } else return 0L;
    }
}
