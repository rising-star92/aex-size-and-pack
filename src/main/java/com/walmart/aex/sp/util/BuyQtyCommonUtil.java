package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.enums.FlowStrategy;
import com.walmart.aex.sp.service.BuyQuantityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;
import static java.util.Objects.requireNonNull;

@Component
@Slf4j
public class BuyQtyCommonUtil {

    private final BuyQuantityMapper buyQuantityMapper;
    public static final String ERROR = "Error";
    public BuyQtyCommonUtil(BuyQuantityMapper buyQuantityMapper)
    {
        this.buyQuantityMapper = buyQuantityMapper;
    }

    public static List<SizeDto> fetchSizes(BuyQtyResponse buyQtyResponse) {
        return Optional.of(buyQtyResponse.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3Dto::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4Dto::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(FinelineDto::getStyles)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(StyleDto::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(CustomerChoiceDto::getClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(clustersDto -> clustersDto.getClusterID().equals(0))
                .findFirst()
                .map(ClustersDto::getSizes)
                .orElse(new ArrayList<>());
    }

    public BuyQtyResponse filterFinelinesWithSizes(List<BuyQntyResponseDTO> buyQntyResponseDTOS, BuyQtyResponse finelinesWithSizesFromStrategy) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        BuyQntyMapperDTO buyQntyMapperDTO = BuyQntyMapperDTO.builder()
                .response(buyQtyResponse).requestFinelineNbr(null)
                .build();
        buyQntyResponseDTOS.forEach(buyQntyResponseDTO -> getFinelines(buyQntyResponseDTO, finelinesWithSizesFromStrategy)
                .forEach(fineline -> {
                    buyQntyMapperDTO.setBuyQntyResponseDTO(buyQntyResponseDTO);
                    buyQntyMapperDTO.setHierarchyMetadata(HierarchyMetadata.builder().finelineMetadata(fineline.getMetadata()).build());
                    buyQuantityMapper.mapBuyQntyLvl2Sp(buyQntyMapperDTO);
                }));
        return buyQtyResponse;
    }

    public BuyQtyResponse filterStylesCcWithSizes(List<BuyQntyResponseDTO> buyQntyResponseDTOS, BuyQtyResponse stylesCcWithSizesFromStrategy, Integer finelineNbr) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        BuyQntyMapperDTO buyQntyMapperDTO = BuyQntyMapperDTO.builder()
                .response(buyQtyResponse).requestFinelineNbr(finelineNbr)
                .build();
        buyQntyResponseDTOS.forEach(buyQntyResponseDTO -> getFinelines(buyQntyResponseDTO, stylesCcWithSizesFromStrategy)
                .stream()
                .map(FinelineDto::getStyles)
                .flatMap(Collection::stream)
                .filter(styleDto -> styleDto.getStyleNbr().equals(buyQntyResponseDTO.getStyleNbr()))
                .forEach(styleDto -> styleDto.getCustomerChoices().stream()
                        .filter(customerChoiceDto -> customerChoiceDto.getCcId().equals(buyQntyResponseDTO.getCcId()))
                        .forEach(cc -> {
                            buyQntyMapperDTO.setBuyQntyResponseDTO(buyQntyResponseDTO);
                            buyQntyMapperDTO.setHierarchyMetadata(HierarchyMetadata.builder()
                                    .styleMetadata(styleDto.getMetadata())
                                    .ccMetadata(cc.getMetadata())
                                    .build());
                            buyQuantityMapper.mapBuyQntyLvl2Sp(buyQntyMapperDTO);
                        })
                ));

        return buyQtyResponse;
    }

    private List<FinelineDto> getFinelines(BuyQntyResponseDTO dbResponse, BuyQtyResponse stratResponse) {
        return Optional.of(stratResponse.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .filter(lvl3Dto -> lvl3Dto.getLvl3Nbr().equals(dbResponse.getLvl3Nbr()))
                .map(Lvl3Dto::getLvl4List)
                .flatMap(Collection::stream)
                .filter(lvl4Dto -> lvl4Dto.getLvl4Nbr().equals(dbResponse.getLvl4Nbr()))
                .map(Lvl4Dto::getFinelines)
                .flatMap(Collection::stream)
                .filter(finelineDto -> finelineDto.getFinelineNbr().equals(dbResponse.getFinelineNbr()))
                .collect(Collectors.toList());
    }

    public static Double getSizePct(SizeDto sizeDto) {
        final Double ZERO = 0.0;
        return sizeDto.getMetrics() != null
                ? Optional.ofNullable(sizeDto.getMetrics().getAdjSizeProfilePct())
                .orElse(Optional.ofNullable(sizeDto.getMetrics().getSizeProfilePct()).orElse(ZERO))
                : ZERO;
    }

    public static List<Replenishment> getReplenishments(List<MerchMethodsDto> merchMethodsDtos, BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        List<Replenishment> replenishments = Optional.ofNullable(bqfpResponse.getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(styleDto.getStyleNbr()))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(customerChoiceDto.getCcId()))
                .findFirst()
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> merchMethodsDtos.stream().map(MerchMethodsDto::getFixtureTypeRollupId).collect(Collectors.toList()).contains(fixture.getFixtureTypeRollupId()))
                .map(Fixture::getReplenishments)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        Map<Integer, Replenishment> replnMap = new LinkedHashMap<>();
        replenishments.forEach(rep -> {
            if (!replnMap.containsKey(rep.getReplnWeek())) {
                replnMap.put(rep.getReplnWeek(), new Replenishment(rep.getReplnWeek(), rep.getReplnWeekDesc(), 0L, 0L, 0L, 0L, 0L));
            }
            Replenishment replnObjectMap = replnMap.get(rep.getReplnWeek());
            //We only need DC Inbound Units from here for downstream calculation.  Will use DcInboundAdjUnits if present or else DcInboundUnits

            Long units = Optional.ofNullable(rep.getDcInboundAdjUnits())
                  .orElse(Optional.ofNullable(rep.getDcInboundUnits()).orElse(0L));
            replnObjectMap.setDcInboundUnits(replnObjectMap.getDcInboundUnits() + units);
        });
        return sortReplenishments(new ArrayList<>(replnMap.values()));
    }

    public static List<Replenishment> getReplenishments(BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
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

    public static StoreQuantity createStoreQuantity(RFASizePackData rfaSizePackData, double perStoreQty, List<Integer> storeListWithOldQty, double totalUnits, Cluster volumeCluster) {
        StoreQuantity storeQuantity = new StoreQuantity();
        storeQuantity.setTotalUnits(totalUnits);
        storeQuantity.setIsUnits(perStoreQty);
        storeQuantity.setVolumeCluster(rfaSizePackData.getVolume_group_cluster_id());
        storeQuantity.setSizeCluster(rfaSizePackData.getSize_cluster_id());
        storeQuantity.setStoreList(storeListWithOldQty);
        storeQuantity.setRfaSizePackData(rfaSizePackData);
        storeQuantity.setCluster(volumeCluster);
        storeQuantity.setBumpSets(new ArrayList<>());
        if (volumeCluster.getFlowStrategy() != null)
            storeQuantity.setFlowStrategyCode(volumeCluster.getFlowStrategy());
        return storeQuantity;
    }

    public static BumpSet getBumpSet(BQFPResponse bqfpResponse, String productFineline, String styleNbr, String ccId, String fixtureType, Integer clusterId) {
        Integer bumpPackNumber = getBumpPackNbr(productFineline);
        return Optional.ofNullable(bqfpResponse).stream().
                flatMap( styles -> styles.getStyles().stream())
                .filter((StringUtils.isNotEmpty(styleNbr)) ? style -> styleNbr.contains(style.getStyleId()) : style -> true)
                .flatMap( ccs -> ccs.getCustomerChoices().stream())
                .filter((StringUtils.isNotEmpty(ccId)) ? cc -> ccId.contains(cc.getCcId()) : cc -> true)
                .flatMap(fixtures -> fixtures.getFixtures().stream())
                .filter((StringUtils.isNotEmpty(fixtureType)) ? fixture -> fixtureType.contains(fixture.getFixtureType()) : fixture -> true)
                .flatMap(clusters -> clusters.getClusters().stream())
                .filter((null != clusterId) ? cluster -> clusterId.equals(cluster.getAnalyticsClusterId()) : cluster -> true)
                .flatMap(bump -> bump.getBumpList().stream())
                .filter(bump -> null != bump && bump.getBumpPackNbr().equals(bumpPackNumber)  && StringUtils.isNotEmpty(bump.getWeekDesc()))
                .findFirst().orElse(new BumpSet());
    }

    public static Integer getBumpPackNbr(String productFineline) {
        return StringUtils.isNotEmpty(productFineline) && productFineline.contains(BUMP_PACK) ?
                Integer.parseInt(productFineline.replaceFirst(BUMP_PACK_PATTERN, "")) : 1;
    }

    public static String getInStoreWeek(BumpSet bp) {
        if (null != bp && StringUtils.isNotEmpty(bp.getWeekDesc())) {
            return formatWeekDesc(bp.getWeekDesc());
        }
        return null;
    }

    private static String formatWeekDesc(String input) {
        requireNonNull(input, "input is required and missing.");

        StringBuilder weekDesc = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c >= '0' && c <= '9') {
                weekDesc.append(c);
            }
        }
        return weekDesc.toString();
    }

    public static boolean isStyleHasBQFP(BQFPResponse bqfpResponse, String styleId) {
        return Optional.ofNullable(bqfpResponse.getStyles())
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(style -> (style != null && style.getStyleId().equalsIgnoreCase(styleId)));
    }

    public static List<Replenishment> sortReplenishments(List<Replenishment> replenishments) {
        return replenishments.stream().sorted(Comparator.comparing(Replenishment::getReplnWeek)).collect(Collectors.toList());
    }

    public static Cluster getVolumeCluster(String styleNbr, String ccId, BQFPResponse bqfpResponse, RFASizePackData rfaSizePackData) {
        return Optional.ofNullable(bqfpResponse.getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(styleNbr))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(ccId))
                .findFirst()
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> fixture.getFixtureTypeRollupId().equals(FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type())))
                .findFirst()
                .map(Fixture::getClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(cluster -> cluster.getAnalyticsClusterId().equals(rfaSizePackData.getVolume_group_cluster_id()))
                .findFirst()
                .orElse(null);
    }

    public static boolean isReplenishmentEligible(Integer flowStrategy) {
        return (null != flowStrategy && flowStrategy.equals(FlowStrategy.REPLENISHMENT_SET.getId()));
    }

    public static boolean isFlCalBuyQtyFailed(List<AppMessageTextResponse> appMessageTexts) {
        return appMessageTexts.stream().map(AppMessageTextResponse::getTypeDesc).anyMatch(v -> v.contains(ERROR));
    }

    public static Set<Integer> getValidationCodesFromRequest(CalculateBuyQtyRequest calculateBuyQtyRequest, Integer finelineNbr) {
        return Optional.ofNullable(calculateBuyQtyRequest.getLvl3List()).stream()
                .flatMap(Collection::stream)
                .map(Lvl3Dto::getLvl4List)
                .flatMap(Collection::stream)
                .map(Lvl4Dto::getFinelines)
                .flatMap(Collection::stream)
                .filter(fineline -> fineline.getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .map(FinelineDto::getMetadata)
                .map(m -> new HashSet<>(m.getValidationCodes()))
                .orElse(new HashSet<>());
    }

}
