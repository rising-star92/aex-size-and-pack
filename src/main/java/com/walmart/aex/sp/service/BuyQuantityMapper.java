package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class BuyQuantityMapper {

    public void mapBuyQntyLvl2Sp(BuyQntyResponseDTO buyQntyResponseDTO, BuyQtyResponse response, Integer finelineNbr) {
        if (response.getPlanId() == null) {
            response.setPlanId(buyQntyResponseDTO.getPlanId());
        }
        if (response.getLvl0Nbr() == null)
            response.setLvl0Nbr(buyQntyResponseDTO.getLvl0Nbr());
        if (response.getLvl1Nbr() == null)
            response.setLvl1Nbr(buyQntyResponseDTO.getLvl1Nbr());
        if (response.getLvl2Nbr() == null)
            response.setLvl2Nbr(buyQntyResponseDTO.getLvl2Nbr());
        response.setLvl3List(mapBuyQntyLvl3Sp(buyQntyResponseDTO, response, finelineNbr));
    }

    private List<Lvl3Dto> mapBuyQntyLvl3Sp(BuyQntyResponseDTO buyQntyResponseDTO, BuyQtyResponse response, Integer finelineNbr) {
        List<Lvl3Dto> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> buyQntyResponseDTO.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapBuyQntyLvl4Sp(buyQntyResponseDTO, lvl3, finelineNbr)),
                        () -> setLvl3SP(buyQntyResponseDTO, lvl3List, finelineNbr));
        return lvl3List;
    }

    private void setLvl3SP(BuyQntyResponseDTO buyQntyResponseDTO, List<Lvl3Dto> lvl3List, Integer finelineNbr) {
        Lvl3Dto lvl3 = new Lvl3Dto();
        lvl3.setLvl3Nbr(buyQntyResponseDTO.getLvl3Nbr());
        if (finelineNbr == null) {
            lvl3.setLvl3Desc(buyQntyResponseDTO.getLvl3Desc());
        }
        lvl3List.add(lvl3);
        lvl3.setLvl4List(mapBuyQntyLvl4Sp(buyQntyResponseDTO, lvl3, finelineNbr));
    }

    private List<Lvl4Dto> mapBuyQntyLvl4Sp(BuyQntyResponseDTO buyQntyResponseDTO, Lvl3Dto lvl3, Integer finelineNbr) {
        List<Lvl4Dto> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> buyQntyResponseDTO.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapBuyQntyFlSp(buyQntyResponseDTO, lvl4, finelineNbr)),
                        () -> setLvl4SP(buyQntyResponseDTO, lvl4DtoList, finelineNbr));
        return lvl4DtoList;
    }

    private void setLvl4SP(BuyQntyResponseDTO buyQntyResponseDTO, List<Lvl4Dto> lvl4DtoList, Integer finelineNbr) {
        Lvl4Dto lvl4 = new Lvl4Dto();
        lvl4.setLvl4Nbr(buyQntyResponseDTO.getLvl4Nbr());
        if (finelineNbr == null) {
            lvl4.setLvl4Desc(buyQntyResponseDTO.getLvl4Desc());
        }
        lvl4DtoList.add(lvl4);
        lvl4.setFinelines(mapBuyQntyFlSp(buyQntyResponseDTO, lvl4, finelineNbr));
    }

    private List<FinelineDto> mapBuyQntyFlSp(BuyQntyResponseDTO buyQntyResponseDTO, Lvl4Dto lvl4, Integer finelineNbr) {
        List<FinelineDto> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

        finelineDtoList.stream()
                .filter(finelineDto -> buyQntyResponseDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineDto -> {
                            if (finelineNbr != null) {
                                finelineDto.setStyles(mapBuyQntyStyleSp(buyQntyResponseDTO, finelineDto, finelineNbr));
                            } else updateFineline(buyQntyResponseDTO, finelineDto);
                        },
                        () -> setFinelineSP(buyQntyResponseDTO, finelineDtoList, finelineNbr));
        return finelineDtoList;
    }

    private void updateFineline(BuyQntyResponseDTO buyQntyResponseDTO, FinelineDto finelineDto) {
        MetricsDto metricsDto = finelineDto.getMetrics();

        int buyQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getBuyQty())
                .orElse(0)
                : 0;

        metricsDto.setBuyQty(buyQty + metricsDto.getBuyQty());

        int isQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getInitialSetQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalInitialSetQty(isQty + metricsDto.getFinalInitialSetQty());

        int rplnQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getReplnQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalReplenishmentQty(rplnQty + metricsDto.getFinalReplenishmentQty());
        metricsDto.setFinalBuyQty(buyQty + metricsDto.getBuyQty());

        finelineDto.setMetrics(metricsDto);
    }

    private void setFinelineSP(BuyQntyResponseDTO buyQntyResponseDTO, List<FinelineDto> finelineDtoList, Integer finelineNbr) {
        FinelineDto fineline = new FinelineDto();
        fineline.setFinelineNbr(buyQntyResponseDTO.getFinelineNbr());
        if (finelineNbr == null) {
            fineline.setFinelineDesc(buyQntyResponseDTO.getFinelineDesc());
            MetricsDto metricsDto = new MetricsDto();
            metricsDto.setBuyQty(buyQntyResponseDTO.getBuyQty());
            metricsDto.setFinalInitialSetQty(buyQntyResponseDTO.getInitialSetQty());
            metricsDto.setFinalReplenishmentQty(buyQntyResponseDTO.getReplnQty());
            metricsDto.setFinalBuyQty(buyQntyResponseDTO.getBuyQty());
            fineline.setMetrics(metricsDto);
        } else {
            fineline.setStyles(mapBuyQntyStyleSp(buyQntyResponseDTO, fineline, finelineNbr));
        }
        finelineDtoList.add(fineline);
    }

    private List<StyleDto> mapBuyQntyStyleSp(BuyQntyResponseDTO buyQntyResponseDTO, FinelineDto fineline, Integer finelineNbr) {
        List<StyleDto> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

        styleDtoList.stream()
                .filter(styleDto -> buyQntyResponseDTO.getStyleNbr().equals(styleDto.getStyleNbr())).findFirst()
                .ifPresentOrElse(styleDto -> styleDto.setCustomerChoices(mapBuyQntyCcSp(buyQntyResponseDTO, styleDto)),
                        () -> setStyleSP(buyQntyResponseDTO, styleDtoList));
        return styleDtoList;
    }

    private void setStyleSP(BuyQntyResponseDTO buyQntyResponseDTO, List<StyleDto> styleDtoList) {
        StyleDto styleDto = new StyleDto();
        styleDto.setStyleNbr(buyQntyResponseDTO.getStyleNbr());

        MetricsDto metricsDto = new MetricsDto();
        metricsDto.setBuyQty(buyQntyResponseDTO.getStyleBuyQty());
        metricsDto.setFinalInitialSetQty(buyQntyResponseDTO.getStyleIsQty());
        metricsDto.setFinalReplenishmentQty(buyQntyResponseDTO.getStyleReplnQty());
        metricsDto.setFinalBuyQty(buyQntyResponseDTO.getStyleBuyQty());
        styleDto.setMetrics(metricsDto);
        styleDto.setCustomerChoices(mapBuyQntyCcSp(buyQntyResponseDTO, styleDto));
        styleDtoList.add(styleDto);
    }

    private List<CustomerChoiceDto> mapBuyQntyCcSp(BuyQntyResponseDTO buyQntyResponseDTO, StyleDto styleDto) {
        List<CustomerChoiceDto> customerChoiceDtoList = Optional.ofNullable(styleDto.getCustomerChoices()).orElse(new ArrayList<>());

        customerChoiceDtoList.stream()
                .filter(customerChoiceDto -> buyQntyResponseDTO.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> {
                            log.info("Size implementation");
                            updateCc(buyQntyResponseDTO, customerChoiceDto);
                        },
                        //customerChoiceDto -> customerChoiceDto.setClusters(mapBuyQntySizeSp(buyQntyResponseDTO, customerChoiceDto)),
                        () -> setCcSP(buyQntyResponseDTO, customerChoiceDtoList));

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
    }

    private void updateCc(BuyQntyResponseDTO buyQntyResponseDTO, CustomerChoiceDto customerChoiceDto) {
        MetricsDto metricsDto = customerChoiceDto.getMetrics();

        int buyQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getBuyQty())
                .orElse(0)
                : 0;

        metricsDto.setBuyQty(buyQty + metricsDto.getBuyQty());

        int isQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getInitialSetQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalInitialSetQty(isQty + metricsDto.getFinalInitialSetQty());

        int rplnQty = buyQntyResponseDTO.getBuyQty() != null
                ? Optional.ofNullable(buyQntyResponseDTO.getReplnQty())
                .orElse(0)
                : 0;

        metricsDto.setFinalReplenishmentQty(rplnQty + metricsDto.getFinalReplenishmentQty());
        metricsDto.setFinalBuyQty(buyQty + metricsDto.getBuyQty());
        customerChoiceDto.setMetrics(metricsDto);
    }

    private void setCcSP(BuyQntyResponseDTO buyQntyResponseDTO, List<CustomerChoiceDto> customerChoiceDtoList) {
        CustomerChoiceDto customerChoiceDto = new CustomerChoiceDto();
        customerChoiceDto.setCcId(buyQntyResponseDTO.getCcId());

        MetricsDto metricsDto = new MetricsDto();
        metricsDto.setBuyQty(buyQntyResponseDTO.getCcBuyQty());
        metricsDto.setFinalInitialSetQty(buyQntyResponseDTO.getCcIsQty());
        metricsDto.setFinalReplenishmentQty(buyQntyResponseDTO.getCcReplnQty());
        metricsDto.setFinalBuyQty(buyQntyResponseDTO.getCcBuyQty());
        customerChoiceDto.setMetrics(metricsDto);
        customerChoiceDtoList.add(customerChoiceDto);
    }

    public void mapBuyQntySizeSp(BuyQntyResponseDTO buyQntyResponseDTO, List<SizeDto> sizeDtos) {
        Optional.of(sizeDtos)
                .stream()
                .flatMap(Collection::stream)
                .filter(sizeDto -> sizeDto.getAhsSizeId().equals(buyQntyResponseDTO.getAhsSizeId()))
                .findFirst()
                .ifPresent(sizeDto -> {
                    MetricsDto metricsDto = sizeDto.getMetrics();
                    metricsDto.setBuyQty(buyQntyResponseDTO.getBuyQty());
                    metricsDto.setFinalInitialSetQty(buyQntyResponseDTO.getInitialSetQty());
                    metricsDto.setFinalReplenishmentQty(buyQntyResponseDTO.getReplnQty());
                    metricsDto.setFinalBuyQty(buyQntyResponseDTO.getBuyQty());
                });
    }
}
