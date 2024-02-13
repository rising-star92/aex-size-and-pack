package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.SizeAndPackException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.DoubleStream;

@Service
@Slf4j
public class BuyQuantityMapper {

    private final StrategyFetchService strategyFetchService;
    private final ObjectMapper objectMapper;
    private final AppMessageTextService appMessageTextService;

    BuyQuantityMapper(StrategyFetchService strategyFetchService, ObjectMapper objectMapper, AppMessageTextService appMessageTextService){
        this.strategyFetchService = strategyFetchService;
        this.objectMapper = objectMapper;
        this.appMessageTextService = appMessageTextService;
    }

    public void mapBuyQntyLvl2Sp(BuyQntyMapperDTO buyQntyMapperDTO) {
        BuyQntyResponseDTO buyQntyResponseDTO = buyQntyMapperDTO.getBuyQntyResponseDTO();
        BuyQtyResponse response = buyQntyMapperDTO.getResponse();
        if (response.getPlanId() == null) {
            response.setPlanId(buyQntyResponseDTO.getPlanId());
        }
        if (response.getLvl0Nbr() == null)
            response.setLvl0Nbr(buyQntyResponseDTO.getLvl0Nbr());
        if (response.getLvl1Nbr() == null)
            response.setLvl1Nbr(buyQntyResponseDTO.getLvl1Nbr());
        if (response.getLvl2Nbr() == null)
            response.setLvl2Nbr(buyQntyResponseDTO.getLvl2Nbr());
        response.setLvl3List(mapBuyQntyLvl3Sp(buyQntyMapperDTO));
    }

    private List<Lvl3Dto> mapBuyQntyLvl3Sp(BuyQntyMapperDTO buyQntyMapperDTO) {
        List<Lvl3Dto> lvl3List = Optional.ofNullable(buyQntyMapperDTO.getResponse().getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> buyQntyMapperDTO.getBuyQntyResponseDTO().getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapBuyQntyLvl4Sp(buyQntyMapperDTO, lvl3)),
                        () -> setLvl3SP(buyQntyMapperDTO, lvl3List));
        return lvl3List;
    }

    private void setLvl3SP(BuyQntyMapperDTO buyQntyMapperDTO, List<Lvl3Dto> lvl3List) {
        Lvl3Dto lvl3 = new Lvl3Dto();
        lvl3.setLvl3Nbr(buyQntyMapperDTO.getBuyQntyResponseDTO().getLvl3Nbr());
        if (Objects.isNull(buyQntyMapperDTO.getRequestFinelineNbr())) {
            lvl3.setLvl3Desc(buyQntyMapperDTO.getBuyQntyResponseDTO().getLvl3Desc());
        }
        lvl3List.add(lvl3);
        lvl3.setLvl4List(mapBuyQntyLvl4Sp(buyQntyMapperDTO, lvl3));
    }
    private Long ifNullThenZero(Integer i) {
        return Objects.nonNull(i) ? i.longValue() : 0;
    }
    

    MetricsDto fineLineMetricsAggregateQtys(List<FinelineDto> finelineDtoList) {
        MetricsDto metricsDto = new MetricsDto();
        int totalFinalBuyQty = 0;
        int totalReplQty = 0;
        int buyQty = 0;
        int totalBumpPackQty = 0;
        int iniQty = 0;
		int replenishmentPacks = 0;
        for (FinelineDto finelineDto : finelineDtoList) {
            MetricsDto finelineMetrics = finelineDto.getMetrics();
            if(Objects.nonNull(finelineDto.getMetrics())) {
                buyQty += ifNullThenZero( finelineMetrics.getBuyQty());
                totalFinalBuyQty += ifNullThenZero( finelineMetrics.getFinalBuyQty());
                totalReplQty += ifNullThenZero(finelineMetrics.getFinalReplenishmentQty());
                totalBumpPackQty += ifNullThenZero(finelineMetrics.getBumpPackQty());
                iniQty += ifNullThenZero(finelineMetrics.getFinalInitialSetQty());
				replenishmentPacks += ifNullThenZero(finelineMetrics.getReplenishmentPacks());
            }
        }
        metricsDto.setBuyQty(buyQty);
        metricsDto.setFinalBuyQty(totalFinalBuyQty);
        metricsDto.setFinalReplenishmentQty(totalReplQty);
        metricsDto.setBumpPackQty(totalBumpPackQty);
        metricsDto.setFinalInitialSetQty(iniQty);
		metricsDto.setReplenishmentPacks(replenishmentPacks);
        return metricsDto;
    }

    MetricsDto lvl4MetricsAggregateQtys(List<Lvl4Dto> lvl4DtoList) {
        MetricsDto metricsDto = new MetricsDto();
        int totalFinalBuyQty = 0;
        int buyQty = 0;
        int totalReplQty = 0;
        int totalBumpPackQty = 0;
        int iniQty = 0;
        int replenishmentPacs = 0;

        for (Lvl4Dto lvl4Dto : lvl4DtoList) {
            MetricsDto lvl4DtoMetrics= lvl4Dto.getMetrics();
            if (Objects.nonNull(lvl4DtoMetrics)) {
                buyQty += ifNullThenZero(lvl4DtoMetrics.getBuyQty());
                totalFinalBuyQty += ifNullThenZero( lvl4DtoMetrics.getFinalBuyQty());
                totalReplQty += ifNullThenZero(lvl4DtoMetrics.getFinalReplenishmentQty());
                totalBumpPackQty += ifNullThenZero(lvl4DtoMetrics.getBumpPackQty());
                iniQty += ifNullThenZero(lvl4DtoMetrics.getFinalInitialSetQty());
                replenishmentPacs += ifNullThenZero(lvl4DtoMetrics.getReplenishmentPacks());
            }
        }

        metricsDto.setBuyQty(buyQty);
        metricsDto.setFinalBuyQty(totalFinalBuyQty);
        metricsDto.setFinalReplenishmentQty(totalReplQty);
        metricsDto.setBumpPackQty(totalBumpPackQty);
        metricsDto.setFinalInitialSetQty(iniQty);
		metricsDto.setReplenishmentPacks(replenishmentPacs);
        return metricsDto;
    }


    private List<Lvl4Dto> mapBuyQntyLvl4Sp(BuyQntyMapperDTO buyQntyMapperDTO, Lvl3Dto lvl3) {
        List<Lvl4Dto> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> buyQntyMapperDTO.getBuyQntyResponseDTO().getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapBuyQntyFlSp(buyQntyMapperDTO, lvl4)),
                        () -> setLvl4SP(buyQntyMapperDTO, lvl4DtoList));
        lvl3.setMetrics(lvl4MetricsAggregateQtys(lvl4DtoList));
        return lvl4DtoList;
    }

    private void setLvl4SP(BuyQntyMapperDTO buyQntyMapperDTO, List<Lvl4Dto> lvl4DtoList) {
        Lvl4Dto lvl4 = new Lvl4Dto();
        lvl4.setLvl4Nbr(buyQntyMapperDTO.getBuyQntyResponseDTO().getLvl4Nbr());
        if (Objects.isNull(buyQntyMapperDTO.getRequestFinelineNbr())) {
            lvl4.setLvl4Desc(buyQntyMapperDTO.getBuyQntyResponseDTO().getLvl4Desc());
        }
        lvl4DtoList.add(lvl4);
        lvl4.setFinelines(mapBuyQntyFlSp(buyQntyMapperDTO, lvl4));
    }

    private List<FinelineDto> mapBuyQntyFlSp(BuyQntyMapperDTO buyQntyMapperDTO, Lvl4Dto lvl4) {
        List<FinelineDto> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

        finelineDtoList.stream()
                .filter(finelineDto -> buyQntyMapperDTO.getBuyQntyResponseDTO().getFinelineNbr().equals(finelineDto.getFinelineNbr()) && finelineDto.getChannelId() != null &&
                        buyQntyMapperDTO.getBuyQntyResponseDTO().getChannelId().equals(finelineDto.getChannelId())).findFirst()
                .ifPresentOrElse(finelineDto -> {
                            if (Objects.nonNull(buyQntyMapperDTO.getRequestFinelineNbr())) {
                                finelineDto.setStyles(mapBuyQntyStyleSp(buyQntyMapperDTO, finelineDto));
                            } else {
                                updateFineline(buyQntyMapperDTO.getBuyQntyResponseDTO(), finelineDto);
                            }
                        },
                        () -> setFinelineSP(buyQntyMapperDTO, finelineDtoList));
        lvl4.setMetrics(fineLineMetricsAggregateQtys(finelineDtoList));
        return finelineDtoList;
    }

    private void updateFineline(BuyQntyResponseDTO buyQntyResponseDTO, FinelineDto finelineDto) {
        MetricsDto metricsDto = finelineDto.getMetrics();
        int buyQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getBuyQty())
                .orElse(0)
                : 0;

        int isQty = buyQntyResponseDTO.getInitialSetQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getInitialSetQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalInitialSetQty(isQty + metricsDto.getFinalInitialSetQty());
        int rplnQty = buyQntyResponseDTO.getReplnQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getReplnQty())
                .orElse(0)
                : 0;

        int bumpQty = buyQntyResponseDTO.getBumpPackQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getBumpPackQty())
                .orElse(0)
                : 0;

        metricsDto.setBumpPackQty(bumpQty + metricsDto.getBumpPackQty());
        metricsDto.setFinalReplenishmentQty(rplnQty + metricsDto.getFinalReplenishmentQty());
        metricsDto.setFinalBuyQty(buyQty + metricsDto.getBuyQty());
        metricsDto.setBuyQty(buyQty + metricsDto.getBuyQty());
        finelineDto.setMetrics(metricsDto);
    }

    private void setFinelineSP(BuyQntyMapperDTO buyQntyMapperDTO, List<FinelineDto> finelineDtoList) {
        BuyQntyResponseDTO buyQntyResponseDTO = buyQntyMapperDTO.getBuyQntyResponseDTO();
        FinelineDto fineline = new FinelineDto();
        fineline.setFinelineNbr(buyQntyResponseDTO.getFinelineNbr());
        fineline.setFinelineAltDesc(buyQntyResponseDTO.getAltFineLineDesc());
        fineline.setChannelId(buyQntyResponseDTO.getChannelId());
        if (Objects.isNull(buyQntyMapperDTO.getRequestFinelineNbr())) {
            fineline.setFinelineDesc(buyQntyResponseDTO.getFinelineDesc());
            fineline.setMetrics(getFlMetricsDto(buyQntyResponseDTO));
            fineline.setMetadata(getMetadataDto(buyQntyResponseDTO.getFinelineMessageObj(), buyQntyMapperDTO.getMetadata()));
        } else {
            fineline.setStyles(mapBuyQntyStyleSp(buyQntyMapperDTO, fineline));
        }
        finelineDtoList.add(fineline);
    }

    /***
     * This method will build Metadata object for fineline/style/cc/size level
     * @param messageObj;
     * @return Metadata
     */
    protected Metadata getMetadataDto(String messageObj, Metadata metadata) {
        if (Objects.isNull(metadata)) {
            metadata = Metadata.builder().build();
        }
        List<ValidationMessage> validations = new ArrayList<>();
        ValidationResult validationResult = getValidationResult(messageObj, metadata);
        if (!validationResult.getCodes().isEmpty()) {
            List<AppMessageTextResponse> matchingAppMessageTexts = appMessageTextService.getAppMessagesByIds(validationResult.getCodes());
            if (!matchingAppMessageTexts.isEmpty()) {
                for (AppMessageTextResponse appMessageTextObj : matchingAppMessageTexts) {
                    ValidationMessage validationMessage = getValidationObjByType(appMessageTextObj.getTypeDesc(), validations);
                    if (Objects.nonNull(validationMessage) && !appMessageTextObj.getLongDesc().isBlank()) {
                        validationMessage.getMessages().add(appMessageTextObj.getLongDesc());
                    } else {
                        List<String> messages = new ArrayList<>();
                        messages.add(appMessageTextObj.getLongDesc());
                        ValidationMessage newValidationMessage = ValidationMessage.builder()
                                .type(appMessageTextObj.getTypeDesc())
                                .messages(messages)
                                .build();
                        validations.add(newValidationMessage);
                    }
                }
            } else {
                log.info("No matching Buy Quantity Validation Message found!");
            }
        }
        metadata.setValidations(validations);
        return metadata ;
    }

    private ValidationResult getValidationResult(String messageObj, Metadata metadata) {
        ValidationResult validationResult = ValidationResult.builder().codes(new HashSet<>()).build();
        try {
            if (StringUtils.isNotEmpty(messageObj)) {
                validationResult = objectMapper.readValue(messageObj, ValidationResult.class);
            }
            if (Objects.nonNull(metadata.getValidationCodes()) && !metadata.getValidationCodes().isEmpty()) {
                validationResult.getCodes().addAll(metadata.getValidationCodes());
            }
        } catch (JsonProcessingException e) {
            log.error("Exception while parsing message object for validation codes : {}", e.getMessage());
        }
        return validationResult;
    }

    /***
     * Below method will fetch the ValidationMessage object for given type , so that we can append new message to its message list
     * @return ValidationMessage
     */
    private ValidationMessage getValidationObjByType(String type, List<ValidationMessage> validations) {
        return validations.stream().filter( validationObj -> validationObj.getType().equals(type)).findAny().orElse(null);
    }

    private MetricsDto getFlMetricsDto(BuyQntyResponseDTO buyQntyResponseDTO) {
        MetricsDto metricsDto = new MetricsDto();
        metricsDto.setBumpPackQty(Objects.nonNull(buyQntyResponseDTO.getBumpPackQty()) ? buyQntyResponseDTO.getBumpPackQty(): 0);
        int buyQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getBuyQty())
                .orElse(0)
                : 0;

        metricsDto.setBuyQty(buyQty);
        int isQty = buyQntyResponseDTO.getInitialSetQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getInitialSetQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalInitialSetQty(isQty);
        int rplnQty = buyQntyResponseDTO.getReplnQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getReplnQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalReplenishmentQty(rplnQty);

        int bumpQty = buyQntyResponseDTO.getBumpPackQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getBumpPackQty())
                .orElse(0)
                : 0;

        metricsDto.setBumpPackQty(bumpQty);

        metricsDto.setFinalBuyQty(buyQty);
        return metricsDto;
    }

    private List<StyleDto> mapBuyQntyStyleSp(BuyQntyMapperDTO buyQntyMapperDTO, FinelineDto fineline) {
        List<StyleDto> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

        styleDtoList.stream()
                .filter(styleDto -> buyQntyMapperDTO.getBuyQntyResponseDTO().getStyleNbr().equals(styleDto.getStyleNbr()) && styleDto.getChannelId() != null &&
                        buyQntyMapperDTO.getBuyQntyResponseDTO().getChannelId().equals(styleDto.getChannelId())).findFirst()
                .ifPresentOrElse(styleDto -> styleDto.setCustomerChoices(mapBuyQntyCcSp(buyQntyMapperDTO, styleDto)),
                        () -> setStyleSP(buyQntyMapperDTO, styleDtoList));
        return styleDtoList;
    }

    private void setStyleSP(BuyQntyMapperDTO buyQntyMapperDTO, List<StyleDto> styleDtoList) {
        BuyQntyResponseDTO buyQntyResponseDTO = buyQntyMapperDTO.getBuyQntyResponseDTO();
        StyleDto styleDto = new StyleDto();
        styleDto.setStyleNbr(buyQntyResponseDTO.getStyleNbr());
        styleDto.setAltStyleDesc(buyQntyResponseDTO.getAltStyleDesc());
        styleDto.setChannelId(buyQntyResponseDTO.getChannelId());
        MetricsDto metricsDto = new MetricsDto();

        int buyQty = buyQntyResponseDTO.getStyleBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getStyleBuyQty())
                .orElse(0)
                : 0;

        metricsDto.setBuyQty(buyQty);
        metricsDto.setBumpPackQty(Objects.nonNull(buyQntyResponseDTO.getBumpPackQty()) ? buyQntyResponseDTO.getBumpPackQty(): 0);
        int isQty = buyQntyResponseDTO.getStyleIsQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getStyleIsQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalInitialSetQty(isQty);
        int rplnQty = buyQntyResponseDTO.getStyleReplnQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getStyleReplnQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalReplenishmentQty(rplnQty);

        int bumpQty = buyQntyResponseDTO.getStyleBumpQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getStyleBumpQty())
                .orElse(0)
                : 0;

        metricsDto.setBumpPackQty(bumpQty);

        metricsDto.setFinalBuyQty(buyQty);
        styleDto.setMetrics(metricsDto);
        styleDto.setMetadata(getMetadataDto(buyQntyResponseDTO.getStyleMessageObj(), null));
        styleDto.setCustomerChoices(mapBuyQntyCcSp(buyQntyMapperDTO, styleDto));
        styleDtoList.add(styleDto);
    }

    private List<CustomerChoiceDto> mapBuyQntyCcSp(BuyQntyMapperDTO buyQntyMapperDTO, StyleDto styleDto) {
        List<CustomerChoiceDto> customerChoiceDtoList = Optional.ofNullable(styleDto.getCustomerChoices()).orElse(new ArrayList<>());

        customerChoiceDtoList.stream()
                .filter(customerChoiceDto -> buyQntyMapperDTO.getBuyQntyResponseDTO().getCcId().equals(customerChoiceDto.getCcId()) && customerChoiceDto.getChannelId() != null &&
                        buyQntyMapperDTO.getBuyQntyResponseDTO().getChannelId().equals(customerChoiceDto.getChannelId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> {
                            log.info("Size implementation");
                            updateCc(buyQntyMapperDTO.getBuyQntyResponseDTO(), customerChoiceDto);
                        },
                        //customerChoiceDto -> customerChoiceDto.setClusters(mapBuyQntySizeSp(buyQntyResponseDTO, customerChoiceDto)),
                        () -> setCcSP(buyQntyMapperDTO, customerChoiceDtoList));

        if (!CollectionUtils.isEmpty(customerChoiceDtoList)) {
            updateStyle(styleDto, customerChoiceDtoList);
        }

        return customerChoiceDtoList;
    }

    private void updateStyle(StyleDto styleDto, List<CustomerChoiceDto> customerChoiceDtoList) {
        MetricsDto metricsDto = Optional.ofNullable(styleDto.getMetrics()).orElse(new MetricsDto());

        metricsDto.setBuyQty(customerChoiceDtoList.stream()
                .filter(Objects::nonNull)
                .map(CustomerChoiceDto::getMetrics)
                .mapToInt(metricsDto1 -> Optional.ofNullable(metricsDto1.getBuyQty()).orElse(0))
                .sum());

        metricsDto.setFinalBuyQty(customerChoiceDtoList.stream()
                .filter(Objects::nonNull)
                .map(CustomerChoiceDto::getMetrics)
                .mapToInt(metricsDto1 -> Optional.ofNullable(metricsDto1.getFinalBuyQty()).orElse(0))
                .sum());

        metricsDto.setFinalInitialSetQty(customerChoiceDtoList.stream()
                .filter(Objects::nonNull)
                .map(CustomerChoiceDto::getMetrics)
                .mapToInt(metricsDto1 -> Optional.ofNullable(metricsDto1.getFinalInitialSetQty()).orElse(0))
                .sum());
        metricsDto.setFinalReplenishmentQty(customerChoiceDtoList.stream()
                .filter(Objects::nonNull)
                .map(CustomerChoiceDto::getMetrics)
                .mapToInt(metricsDto1 -> Optional.ofNullable(metricsDto1.getFinalReplenishmentQty()).orElse(0))
                .sum());
        metricsDto.setBumpPackQty(customerChoiceDtoList.stream()
                .filter(Objects::nonNull)
                .map(CustomerChoiceDto::getMetrics)
                .mapToInt(metricsDto1 -> Optional.ofNullable(metricsDto1.getBumpPackQty()).orElse(0))
                .sum());

        styleDto.setMetrics(metricsDto);
    }

    private void updateCc(BuyQntyResponseDTO buyQntyResponseDTO, CustomerChoiceDto customerChoiceDto) {
        MetricsDto metricsDto = customerChoiceDto.getMetrics();

        int buyQty = buyQntyResponseDTO.getCcBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcBuyQty())
                .orElse(0)
                : 0;


        int isQty = buyQntyResponseDTO.getCcIsQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcIsQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalInitialSetQty(isQty + metricsDto.getFinalInitialSetQty());

        int rplnQty = buyQntyResponseDTO.getCcReplnQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcReplnQty())
                .orElse(0)
                : 0;

        int bumpQty = buyQntyResponseDTO.getCcBumpQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcBumpQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalReplenishmentQty(rplnQty + metricsDto.getFinalReplenishmentQty());
        metricsDto.setFinalBuyQty(buyQty + metricsDto.getBuyQty());
        metricsDto.setBumpPackQty(bumpQty + metricsDto.getBumpPackQty());
        metricsDto.setBuyQty(buyQty + metricsDto.getBuyQty());
        customerChoiceDto.setColorFamilyDesc(buyQntyResponseDTO.getColorFamilyDesc());
        customerChoiceDto.setColorName(buyQntyResponseDTO.getColorName());
        customerChoiceDto.setMetrics(metricsDto);
    }

    private void setCcSP(BuyQntyMapperDTO buyQntyMapperDTO, List<CustomerChoiceDto> customerChoiceDtoList) {
        BuyQntyResponseDTO buyQntyResponseDTO = buyQntyMapperDTO.getBuyQntyResponseDTO();
        CustomerChoiceDto customerChoiceDto = new CustomerChoiceDto();
        customerChoiceDto.setCcId(buyQntyResponseDTO.getCcId());
        customerChoiceDto.setAltCcDesc(buyQntyResponseDTO.getAltCcDesc());
        customerChoiceDto.setChannelId(buyQntyResponseDTO.getChannelId());
        customerChoiceDto.setColorName(buyQntyResponseDTO.getColorName());
        customerChoiceDto.setColorFamilyDesc(buyQntyResponseDTO.getColorFamilyDesc());
        BuyQtyResponse sizeLevelData = null;
        try {
            BuyQtyRequest newBuyReq = new BuyQtyRequest();
            newBuyReq.setPlanId(buyQntyResponseDTO.getPlanId());
            newBuyReq.setChannel(ChannelType.getChannelNameFromId(buyQntyResponseDTO.getChannelId()));
            newBuyReq.setLvl3Nbr(buyQntyResponseDTO.getLvl3Nbr());
            newBuyReq.setLvl4Nbr(buyQntyResponseDTO.getLvl4Nbr());
            newBuyReq.setFinelineNbr(buyQntyResponseDTO.getFinelineNbr());
            newBuyReq.setStyleNbr(buyQntyResponseDTO.getStyleNbr());
            newBuyReq.setCcId(buyQntyResponseDTO.getCcId());

            sizeLevelData = strategyFetchService.getBuyQtyResponseSizeProfile(newBuyReq);
        } catch (SizeAndPackException e) {
            log.error("Error occured while fetching values from Strategy.",e);
        }

        MetricsDto metricsDto = new MetricsDto();
        if(sizeLevelData != null &&  sizeLevelData.getLvl3List() != null){
            double avgSizeProfilePctSum = sizeLevelData.getLvl3List().stream()
                    .flatMapToDouble(lvl3Dto -> lvl3Dto.getLvl4List().stream()
                            .flatMapToDouble(lvl4Dto -> lvl4Dto.getFinelines().stream()
                                    .flatMapToDouble(finelineDto -> finelineDto.getStyles().stream()
                                            .flatMapToDouble(styleDto -> styleDto.getCustomerChoices().stream()
                                                    .flatMapToDouble(cc->cc.getClusters().get(0).getSizes().stream()
                                                            .flatMapToDouble(sizeDto -> DoubleStream.of(sizeDto.getMetrics().getAvgSizeProfilePct()))))))).sum();
            double adjSizeProfilePctSum =  sizeLevelData.getLvl3List().stream()
                    .flatMapToDouble(lvl3Dto -> lvl3Dto.getLvl4List().stream()
                            .flatMapToDouble(lvl4Dto -> lvl4Dto.getFinelines().stream()
                                    .flatMapToDouble(finelineDto -> finelineDto.getStyles().stream()
                                            .flatMapToDouble(styleDto -> styleDto.getCustomerChoices().stream()
                                                    .flatMapToDouble(cc->cc.getClusters().get(0).getSizes().stream()
                                                            .flatMapToDouble(sizeDto -> DoubleStream.of(sizeDto.getMetrics().getAdjAvgSizeProfilePct()))))))).sum();

            metricsDto.setAvgSizeProfilePct((double) Math.round(avgSizeProfilePctSum));
            metricsDto.setBumpPackQty(Objects.nonNull(buyQntyResponseDTO.getBumpPackQty()) ? buyQntyResponseDTO.getBumpPackQty(): 0);
            metricsDto.setAdjAvgSizeProfilePct((double) Math.round(adjSizeProfilePctSum));

        }

        int buyQty = buyQntyResponseDTO.getCcBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcBuyQty())
                .orElse(0)
                : 0;

        metricsDto.setBuyQty(buyQty);
        int isQty = buyQntyResponseDTO.getCcIsQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcIsQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalInitialSetQty(isQty);
        int rplnQty = buyQntyResponseDTO.getCcReplnQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcReplnQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalReplenishmentQty(rplnQty);
        metricsDto.setFinalBuyQty(buyQty);

        int bumpQty = buyQntyResponseDTO.getCcBumpQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getCcBumpQty())
                .orElse(0)
                : 0;

        metricsDto.setBumpPackQty(bumpQty);
        customerChoiceDto.setMetrics(metricsDto);
        customerChoiceDto.setMetadata(getMetadataDto(buyQntyResponseDTO.getCcMessageObj(), buyQntyMapperDTO.getMetadata()));
        customerChoiceDtoList.add(customerChoiceDto);
    }

    public void mapBuyQntySizeSp(List<BuyQntyResponseDTO> buyQntyResponseDTOs, SizeDto sizeDto) {
        MetricsDto metricsDto = sizeDto.getMetrics();
        Optional.of(buyQntyResponseDTOs)
                .stream()
                .flatMap(Collection::stream)
                .filter(buyQntyResponseDTO -> sizeDto.getAhsSizeId().equals(buyQntyResponseDTO.getAhsSizeId()))
                        .forEach(buyQntyResponseDTO -> {
                    metricsDto.setBuyQty(Optional.ofNullable(metricsDto.getBuyQty()).orElse(0) + Optional.ofNullable(buyQntyResponseDTO.getBuyQty()).orElse(0));
                    metricsDto.setBumpPackQty(Optional.ofNullable(metricsDto.getBumpPackQty()).orElse(0) + Optional.ofNullable(buyQntyResponseDTO.getBumpPackQty()).orElse(0));
                    metricsDto.setFinalInitialSetQty(Optional.ofNullable(metricsDto.getFinalInitialSetQty()).orElse(0) +Optional.ofNullable(buyQntyResponseDTO.getInitialSetQty()).orElse(0));
                    metricsDto.setFinalReplenishmentQty(Optional.ofNullable(metricsDto.getFinalReplenishmentQty()).orElse(0) + Optional.ofNullable(buyQntyResponseDTO.getReplnQty()).orElse(0));
                    metricsDto.setFinalBuyQty(Optional.ofNullable(metricsDto.getFinalBuyQty()).orElse(0) + Optional.ofNullable(buyQntyResponseDTO.getBuyQty()).orElse(0));
                    sizeDto.setMetadata(getMetadataDto(buyQntyResponseDTO.getSizeMessageObj(), null));
                        });
    }
}
