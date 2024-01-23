package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessageText;
import com.walmart.aex.sp.enums.FlowStrategy;
import com.walmart.aex.sp.service.BQFPValidationsService;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BQFPValidationsServiceImpl implements BQFPValidationsService {

    @Override
    public List<Integer> missingBuyQuantity(List<MerchMethodsDto> merchMethodsDtos, BQFPResponse bqfpResponse,
                                       StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        List<Integer> validationCodes = new ArrayList<>();
        merchMethodsDtos.forEach(merchMethodsDto -> {

            Fixture bqfpFixture = getBQFPFixture(bqfpResponse, styleDto, customerChoiceDto, merchMethodsDto);
            List<Cluster> clusters = getClusters(bqfpFixture);

            if (CollectionUtils.isEmpty(clusters)) {
                //Missing IS Data for fixture
                validationCodes.add(AppMessageText.BQFP_MISSING_IS_DATA.getId());
            } else {
                List<Integer> flowStrategies = getFlowStrategiesFromCluster(clusters);
                //Warning: Missing IS Quantities
                addMissingISQuantitiesMsg(validationCodes, clusters);
                //ERROR: Missing Replenishment Quantities
                addMissingReplenishmentMsg(validationCodes, bqfpFixture, flowStrategies);
                //ERROR: Missing BS Quantities
                addMissingBSQuantitiesNsg(validationCodes, clusters);
            }
        });
        return validationCodes;
    }

    private void addMissingBSQuantitiesNsg(List<Integer> validationCodes, List<Cluster> clusters) {
        clusters.stream().filter(cluster -> cluster.getFlowStrategy().equals(FlowStrategy.BUMP_SET.getId())).forEach(cluster -> {
            //ERROR: Missing Bumpset Quantities with Flow Strategy Initialset + Bumpset
            if (cluster.getBumpList()
                    .stream()
                    .filter(Objects::nonNull).mapToLong(bumpset -> Optional.ofNullable(bumpset.getUnits()).orElse((long) 0)).sum() <= 0) {
                //Missing Bumpset quantities
                validationCodes.add(AppMessageText.BQFP_MISSING_BUMPSET_QUANTITIES.getId());
            }
            //ERROR: Missing Bumpset Weeks with Flow Strategy Initialset + Bumpset and Bumpset Qty > 0
            if (cluster.getBumpList()
                    .stream()
                    .filter(Objects::nonNull).anyMatch(bumpSet -> bumpSet.getUnits() > 0 && bumpSet.getWeekDesc() == null)) {
                //Missing Bumpset Weeks
                validationCodes.add(AppMessageText.BQFP_MISSING_BUMPSET_WEEKS.getId());
            }
        });
    }

    private void addMissingReplenishmentMsg(List<Integer> validationCodes, Fixture bqfpFixture, List<Integer> flowStrategies) {
        //ERROR: Missing Replenishment Quantities with Flow Strategy Initialset + Replenishment
        if (flowStrategies.contains(FlowStrategy.REPLENISHMENT_SET.getId()) && (bqfpFixture != null && (CollectionUtils.isEmpty(bqfpFixture.getReplenishments())
                || bqfpFixture.getReplenishments().stream().filter(Objects::nonNull)
                .mapToLong(replenishment -> Optional.ofNullable(replenishment.getDcInboundUnits()).orElse((long) 0)).sum() <= 0))) {
            //Missing Replenishment quantities
            validationCodes.add(AppMessageText.BQFP_MISSING_REPLENISHMENT_QUANTITIES.getId());
        }
    }

    private void addMissingISQuantitiesMsg(List<Integer> validationCodes, List<Cluster> clusters) {
        long bqfpISQty = clusters.stream()
                .filter(Objects::nonNull)
                .mapToLong(cluster -> Optional.ofNullable(cluster.getInitialSet().getTotalInitialSetUnits()).orElse((long) 0))
                .sum();

        if (bqfpISQty <= 0) {
            //Missing IS quantities for fixture
            validationCodes.add(AppMessageText.BQFP_MISSING_IS_QUANTITIES.getId());
        }
    }

    private List<Cluster> getClusters(Fixture bqfpFixture) {
        return Optional.ofNullable(bqfpFixture)
                .map(Fixture::getClusters)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Fixture getBQFPFixture(BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto, MerchMethodsDto merchMethodsDto) {
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
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> fixture.getFixtureTypeRollupId().equals(merchMethodsDto.getFixtureTypeRollupId()))
                .findFirst().orElse(null);
    }

    private List<Integer> getFlowStrategiesFromCluster(List<Cluster> clusters) {
        return Optional.of(clusters)
                .stream()
                .flatMap(Collection::stream)
                .map(Cluster::getFlowStrategy)
                .collect(Collectors.toList());
    }
}
