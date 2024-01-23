package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpStyleChannelFixture;
import com.walmart.aex.sp.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CalculateFinelineBuyQuantityMapper {

    private final ObjectMapper objectMapper;

    public CalculateFinelineBuyQuantityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setFinelineChanFixtures(SpFineLineChannelFixture spFineLineChannelFixture, Set<SpStyleChannelFixture> spStyleChannelFixtures) {
        spFineLineChannelFixture.setInitialSetQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getInitialSetQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setBumpPackQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getBumpPackQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setBuyQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getBuyQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setReplnQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getReplnQty()).orElse(0))
                .sum()
        );
        setFinelineChanFixtureValidation(spFineLineChannelFixture, spStyleChannelFixtures);
    }

    private void setFinelineChanFixtureValidation(SpFineLineChannelFixture spFineLineChannelFixture, Set<SpStyleChannelFixture> spStyleChannelFixtures) {
        Set<Integer> styleValidationCodes = new HashSet<>();
        ValidationResult finelineValidationResult = new ValidationResult(new ArrayList<>());
        spStyleChannelFixtures.forEach(style -> {
            ValidationResult styleValidationResult = getValidationResult(style.getMessageObj());
            if (!styleValidationResult.getCodes().isEmpty())
                styleValidationCodes.addAll(styleValidationResult.getCodes());
        });
        if (!styleValidationCodes.isEmpty()) {
            finelineValidationResult.getCodes().addAll(styleValidationCodes);
        }
        spFineLineChannelFixture.setMessageObj(setMessage(finelineValidationResult));
    }

    public void setStyleChanFixtures(SpStyleChannelFixture spStyleChannelFixture, Set<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixtures) {
        spStyleChannelFixture.setInitialSetQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getInitialSetQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setBumpPackQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getBumpPackQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setBuyQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getBuyQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setReplnQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getReplnQty()).orElse(0))
                .sum()
        );
        setStyleChanFixtureValidation(spStyleChannelFixture, spCustomerChoiceChannelFixtures);
    }

    private void setStyleChanFixtureValidation(SpStyleChannelFixture spStyleChannelFixture, Set<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixtures) {
        Set<Integer> ccValidationCodes = new HashSet<>();
        ValidationResult styleValidationResult = new ValidationResult(new ArrayList<>());
        spCustomerChoiceChannelFixtures.forEach(cc -> {
            ValidationResult ccValidationResult = getValidationResult(cc.getMessageObj());
            if (!ccValidationResult.getCodes().isEmpty())
                ccValidationCodes.addAll(ccValidationResult.getCodes());
        });
        if (!ccValidationCodes.isEmpty()) {
            styleValidationResult.getCodes().addAll(ccValidationCodes);
        }

        spStyleChannelFixture.setMessageObj(setMessage(styleValidationResult));
    }

    public void setCcChanFixtures(SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture, Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes, ValidationResult ccValidationResult) {
        spCustomerChoiceChannelFixture.setInitialSetQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getInitialSetQty()).orElse(0))
                .sum()
        );
        spCustomerChoiceChannelFixture.setBumpPackQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getBumpPackQty()).orElse(0))
                .sum()
        );
        spCustomerChoiceChannelFixture.setBuyQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getBuyQty()).orElse(0))
                .sum()
        );
        spCustomerChoiceChannelFixture.setReplnQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getReplnQty()).orElse(0))
                .sum()
        );
        setCcChanFixtureValidation(spCustomerChoiceChannelFixture, spCustomerChoiceChannelFixtureSizes, ccValidationResult);

    }

    private void setCcChanFixtureValidation(SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture, Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes, ValidationResult ccValidationResult) {
        Set<Integer> sizesValidationCodes = new HashSet<>();
        spCustomerChoiceChannelFixtureSizes.forEach(size -> {
            ValidationResult validationResult = getValidationResult(size.getMessageObj());
            sizesValidationCodes.addAll(validationResult.getCodes());
        });
        if (!sizesValidationCodes.isEmpty()) {
            ccValidationResult.getCodes().addAll(sizesValidationCodes);
        }
        spCustomerChoiceChannelFixture.setMessageObj(setMessage(ccValidationResult));
    }

    private ValidationResult getValidationResult(String messageObj) {
        try {
            return StringUtils.isNotEmpty(messageObj) ? objectMapper.readValue(messageObj, ValidationResult.class) : new ValidationResult(new ArrayList<>());
        } catch (Exception e) {
            throw new CustomException("Exception occurred while deserializing validation messages");
        }
    }

    public String setMessage(ValidationResult validationResult) {
        try {
            return objectMapper.writeValueAsString(validationResult);
        } catch (Exception ex) {
            throw new CustomException("Exception occurred while creating validation messages");
        }

    }
}
