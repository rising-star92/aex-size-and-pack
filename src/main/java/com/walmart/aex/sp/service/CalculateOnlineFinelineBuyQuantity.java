package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.dto.replenishment.cons.ReplenishmentCons;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.walmart.aex.sp.util.SizeAndPackConstants.VP_DEFAULT;


@Service
@Slf4j
public class CalculateOnlineFinelineBuyQuantity {
    private final BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService;
    private final ReplenishmentsOptimizationService replenishmentsOptimizationServices;
    private final ReplenishmentService replenishmentService;
    private final BqfpValidationsService bqfpValidationsService;

    public CalculateOnlineFinelineBuyQuantity(BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService,
                                              ReplenishmentsOptimizationService replenishmentsOptimizationServices,
                                              ReplenishmentService replenishmentService,
                                              BqfpValidationsService bqfpOnlineValidationServiceImpl) {
        this.buyQtyReplenishmentMapperService = buyQtyReplenishmentMapperService;
        this.replenishmentsOptimizationServices = replenishmentsOptimizationServices;
        this.replenishmentService = replenishmentService;
        this.bqfpValidationsService = bqfpOnlineValidationServiceImpl;
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
        ReplenishmentCons replenishmentCons = replenishmentService.fetchHierarchyReplnCons(calculateBuyQtyParallelRequest, merchMethodsDto);
        styles.forEach(styleDto -> {
            log.info("Checking if Online Styles are existing: {}", styleDto);
            if (!CollectionUtils.isEmpty(styleDto.getCustomerChoices())) {
                if (BuyQtyCommonUtil.isStyleHasBQFP(bqfpResponse, styleDto.getStyleNbr())) {
                    replenishmentService.setStyleReplenishmentCons(replenishmentCons, styleDto);
                }
                getOnlineCustomerChoices(styleDto, merchMethodsDto, bqfpResponse, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, replenishmentCons);
            }
        });
    }

    private void getOnlineCustomerChoices(StyleDto styleDto, MerchMethodsDto merchMethodsDto, BQFPResponse bqfpResponse,
                                          CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse, ReplenishmentCons replenishmentCons) {
        styleDto.getCustomerChoices().forEach(customerChoiceDto -> {
            log.info("Checking if Online Cc are existing: {}", customerChoiceDto);
            //TODO: Delete replenishment if replenishment is deleted after set
            ValidationResult ccValidationResult = bqfpValidationsService.missingBuyQuantity(List.of(merchMethodsDto), bqfpResponse, styleDto, customerChoiceDto);
            if (!CollectionUtils.isEmpty(customerChoiceDto.getClusters())) {
                getOnlineCcClusters(styleDto, customerChoiceDto, merchMethodsDto, bqfpResponse, calculateBuyQtyResponse, calculateBuyQtyParallelRequest, replenishmentCons, ccValidationResult);
            }
        });
    }

    private void getOnlineCcClusters(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, MerchMethodsDto merchMethodsDto,
                                     BQFPResponse bqfpResponse, CalculateBuyQtyResponse calculateBuyQtyResponse,
                                     CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, ReplenishmentCons replenishmentCons,
                                     ValidationResult ccValidationResult) {
        Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId = new HashMap<>();
        //Replenishment
        List<Replenishment> replenishments = BuyQtyCommonUtil.getReplenishments(bqfpResponse, styleDto, customerChoiceDto);
        log.info("Get All Replenishments if exists for customerchoice: {} and merch method: {}", customerChoiceDto.getCcId(), merchMethodsDto.getFixtureTypeRollupId());
        if (!CollectionUtils.isEmpty(replenishments)) {
            // Query the Replenishment constraint if Replenishment unit exist
            if(hasDcInboundAndAdjUnits(replenishments)){
                replenishmentService.setCcsReplenishmentCons(replenishmentCons, calculateBuyQtyParallelRequest, merchMethodsDto, styleDto, customerChoiceDto);
            }
            //Set Replenishment for Size Map
            Optional.ofNullable(customerChoiceDto.getClusters())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(clustersDto1 -> clustersDto1.getClusterID().equals(0))
                    .findFirst().ifPresent(clustersDto -> setReplenishmentSizes(clustersDto, replenishments, storeBuyQtyBySizeId, bqfpResponse.getLvl1Nbr(), bqfpResponse.getPlanId(), replenishmentCons.getCcSpMmReplPackConsMap() ));
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
                if (totalReplenishment > 0) {
                    ccSpMmReplPacks.add(buyQtyReplenishmentMapperService.setCcMmSpReplenishment(entry, (int) totalReplenishment, (int) totalReplenishment));
                }
            }
        }
        if (!CollectionUtils.isEmpty(ccSpMmReplPacks)) {
            //Replenishment
            List<MerchCatgReplPack> merchCatgReplPacks = buyQtyReplenishmentMapperService.setAllReplenishments(styleDto, merchMethodsDto, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks, replenishmentCons, ccValidationResult);
            calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
        }
    }

    private void setReplenishmentSizes(ClustersDto clustersDto, List<Replenishment> replenishments, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId, Integer lvl1Nbr, Long planId, Map<Integer, CcSpMmReplPack> cCSpMmReplPackSizeMap) {
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
                replenishment1.setAdjReplnUnits(Math.round((getReplenishmentUnits(replenishment) * getAvgSizePct(sizeDto)) / 100));
                replObj.add(replenishment1);
            });
            Integer vendorPackQty = VP_DEFAULT;
            if (!CollectionUtils.isEmpty(cCSpMmReplPackSizeMap) && cCSpMmReplPackSizeMap.containsKey(sizeDto.getAhsSizeId())) {
                vendorPackQty = cCSpMmReplPackSizeMap.get(sizeDto.getAhsSizeId()).getVendorPackCnt();
            }
            buyQtyObj.setReplenishments(replenishmentsOptimizationServices.getUpdatedReplenishmentsPack(replObj, vendorPackQty, SizeAndPackConstants.ONLINE_CHANNEL_ID, lvl1Nbr, planId));
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

    private boolean hasDcInboundAndAdjUnits(List<Replenishment> replenishments) {
        return Optional.ofNullable(replenishments)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(replenishment -> ((replenishment.getDcInboundAdjUnits() != null && replenishment.getDcInboundAdjUnits() > 0) || replenishment.getDcInboundUnits() != null && replenishment.getDcInboundUnits() > 0));
    }
}
