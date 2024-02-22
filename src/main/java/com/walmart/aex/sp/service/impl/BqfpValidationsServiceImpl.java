package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessage;
import com.walmart.aex.sp.enums.FlowStrategy;
import com.walmart.aex.sp.service.BqfpValidationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BqfpValidationsServiceImpl implements BqfpValidationsService {

    @Override
    public ValidationResult missingBuyQuantity(List<MerchMethodsDto> merchMethodsDtos, BQFPResponse bqfpResponse,
                                               StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        Set<Integer> validationCodes = new HashSet<>();
        merchMethodsDtos.forEach(merchMethodsDto -> {

            Fixture bqfpFixture = getBQFPFixture(bqfpResponse, styleDto, customerChoiceDto, merchMethodsDto);
            List<Cluster> clusters = null!=bqfpFixture ? bqfpFixture.getClusters():Collections.emptyList();

            List<Integer> flowStrategies = getFlowStrategiesFromCluster(clusters);
            //ERROR: Missing IS Quantities
            validateISUnits(validationCodes, clusters);
            //ERROR: Missing Replenishment Quantities
            validateReplenishmentUnits(validationCodes, bqfpFixture, flowStrategies);
            //ERROR: Missing BS Quantities
            validateBSUnits(validationCodes, clusters);
        });
        return ValidationResult.builder().codes(validationCodes).build();
    }

    private void validateBSUnits(Set<Integer> validationCodes, List<Cluster> clusters) {
        clusters.stream().filter(cluster -> cluster.getFlowStrategy().equals(FlowStrategy.BUMP_SET.getId())).forEach(cluster -> {
            //ERROR: Missing Bumpset Quantities with Flow Strategy Initialset + Bumpset
            if (cluster.getBumpList()
                    .stream()
                    .filter(Objects::nonNull)
                    .anyMatch(bumpset -> Optional.ofNullable(bumpset.getUnits()).orElse(0L) < 0)) {
                // BS units include negative values
                validationCodes.add(AppMessage.BQFP_BS_NEGATIVE_UNITS.getId());
            } else if (cluster.getBumpList()
                        .stream()
                        .filter(Objects::nonNull).mapToLong(bumpset -> Optional.ofNullable(bumpset.getUnits()).orElse(0L)).sum() == 0) {
                //Missing Bumpset quantities
                validationCodes.add(AppMessage.BQFP_MISSING_BS_UNITS.getId());
            }
                //ERROR: Missing Bumpset Weeks with Flow Strategy Initialset + Bumpset and Bumpset Qty > 0
            if (cluster.getBumpList()
                    .stream()
                    .filter(Objects::nonNull).anyMatch(bumpSet -> bumpSet.getUnits() > 0 && bumpSet.getWeekDesc() == null)) {
                //Missing Bumpset Weeks
                validationCodes.add(AppMessage.BQFP_MISSING_BS_WEEKS.getId());
            }
        });
    }

    private void validateReplenishmentUnits(Set<Integer> validationCodes, Fixture bqfpFixture, List<Integer> flowStrategies) {
        //ERROR: Missing Replenishment Quantities with Flow Strategy Initialset + Replenishment
        if (flowStrategies.contains(FlowStrategy.REPLENISHMENT_SET.getId())) {
            if (bqfpFixture != null && bqfpFixture.getReplenishments().stream().filter(Objects::nonNull)
                    .anyMatch(replenishment -> Optional.ofNullable(replenishment.getDcInboundUnits()).orElse(0L) < 0 ||
                            Optional.ofNullable(replenishment.getDcInboundAdjUnits()).orElse(0L) < 0 )) {
                // Repln units include negative values
                validationCodes.add(AppMessage.BQFP_REPLN_NEGATIVE_UNITS.getId());
            } else if ((bqfpFixture != null && (CollectionUtils.isEmpty(bqfpFixture.getReplenishments())
                    || bqfpFixture.getReplenishments().stream().filter(Objects::nonNull)
                    .mapToLong(replenishment -> Optional.ofNullable(replenishment.getDcInboundUnits()).orElse(0L) +
                            Optional.ofNullable(replenishment.getDcInboundAdjUnits()).orElse(0L) ).sum() == 0))) {
                //Missing Replenishment quantities
                validationCodes.add(AppMessage.BQFP_MISSING_REPLN_UNITS.getId());
            }
        }
    }

    private void validateISUnits(Set<Integer> validationCodes, List<Cluster> clusters) {
        if (clusters.stream()
                .anyMatch(cluster -> Objects.isNull(cluster.getInitialSet()) ||
                        Objects.isNull(cluster.getInitialSet().getInitialSetUnitsPerFix()) ||
                        Objects.isNull(cluster.getInitialSet().getTotalInitialSetUnits()))) {
            // when IS is null, then it's missing
            validationCodes.add(AppMessage.BQFP_MISSING_IS_DATA.getId());
        } else if (clusters.stream()
                .filter(Objects::nonNull)
                .anyMatch(cluster -> Optional.ofNullable(cluster.getInitialSet().getTotalInitialSetUnits()).orElse(0L) < 0)) {
            // IS units include negative values
            validationCodes.add(AppMessage.BQFP_IS_NEGATIVE_UNITS.getId());
        }
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
