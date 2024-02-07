package com.walmart.aex.sp.service.impl;

import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.enums.AppMessageText;
import com.walmart.aex.sp.service.BqfpValidationsService;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class BqfpOnlineValidationServiceImpl implements BqfpValidationsService {
    @Override
    public ValidationResult missingBuyQuantity(List<MerchMethodsDto> merchMethodsDtos, BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        Set<Integer> validationCodes = new HashSet<>();
        merchMethodsDtos.forEach(merchMethodsDto -> {

            List<Replenishment> replenishments = BuyQtyCommonUtil.getReplenishments(bqfpResponse, styleDto, customerChoiceDto);

            if (!replenishments.isEmpty() && replenishments.stream().filter(Objects::nonNull)
                    .anyMatch(replenishment -> Optional.ofNullable(replenishment.getDcInboundUnits()).orElse(0L) < 0 ||
                            Optional.ofNullable(replenishment.getDcInboundAdjUnits()).orElse(0L) < 0 )) {
                // Repln units include negative values
                validationCodes.add(AppMessageText.BQFP_REPLN_NEGATIVE_UNITS.getId());
            } else if (replenishments.isEmpty() || replenishments.stream().filter(Objects::nonNull)
                    .mapToLong(replenishment -> Optional.ofNullable(replenishment.getDcInboundUnits()).orElse((long) 0) +
                            Optional.ofNullable(replenishment.getDcInboundAdjUnits()).orElse((long) 0) ).sum() <= 0) {
                //Missing Replenishment units
                validationCodes.add(AppMessageText.BQFP_MISSING_REPLENISHMENT_QUANTITIES.getId());
            }
        });
        return ValidationResult.builder().codes(validationCodes).build();
    }
}
