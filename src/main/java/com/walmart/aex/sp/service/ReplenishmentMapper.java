package com.walmart.aex.sp.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import org.springframework.stereotype.Service;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReplenishmentMapper {

    public void mapReplenishmentLvl2Sp(ReplenishmentResponseDTO replenishmentResponseDTO, ReplenishmentResponse response, Integer finelineNbr, String ccId) {
        if (response.getPlanId() == null) {
            response.setPlanId(replenishmentResponseDTO.getPlanId());
        }
        if (response.getLvl0Nbr() == null) {
            response.setLvl0Nbr(replenishmentResponseDTO.getLvl0Nbr());
            response.setLvl0Desc(replenishmentResponseDTO.getLvl0Desc());
        }
        if (response.getLvl1Nbr() == null) {
            response.setLvl1Nbr(replenishmentResponseDTO.getLvl1Nbr());
            response.setLvl1Desc(replenishmentResponseDTO.getLvl1Desc());
        }
        if (response.getLvl2Nbr() == null) {
            response.setLvl2Nbr(replenishmentResponseDTO.getLvl2Nbr());
            response.setLvl2Desc(replenishmentResponseDTO.getLvl2Desc());
        }
        response.setLvl3List(mapReplenishmentLvl3Sp(replenishmentResponseDTO, response, finelineNbr, ccId));
    }

    private List<Lvl3Dto> mapReplenishmentLvl3Sp(ReplenishmentResponseDTO replenishmentResponseDTO, ReplenishmentResponse response, Integer finelineNbr, String ccId) {
        List<Lvl3Dto> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> replenishmentResponseDTO.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapReplenishmentLvl4Sp(replenishmentResponseDTO, lvl3, finelineNbr, ccId)),
                        () -> setLvl3SP(replenishmentResponseDTO, lvl3List, finelineNbr, ccId));
        return lvl3List;
    }

    private void setLvl3SP(ReplenishmentResponseDTO replenishmentResponseDTO, List<Lvl3Dto> lvl3List, Integer finelineNbr, String ccId) {
        Lvl3Dto lvl3 = new Lvl3Dto();
        lvl3.setLvl3Nbr(replenishmentResponseDTO.getLvl3Nbr());
        lvl3.setLvl3Desc(replenishmentResponseDTO.getLvl3Desc());
        if (finelineNbr == null && ccId == null){
            MetricsDto metricsDto = new MetricsDto();
            metricsDto.setVendorPack(replenishmentResponseDTO.getLvl3VenderPackCount());
            metricsDto.setWarehousePack(replenishmentResponseDTO.getLvl3WhsePackCount());
            metricsDto.setPackRatio(replenishmentResponseDTO.getLvl3vnpkWhpkRatio());
            metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getLvl3ReplQty());

            lvl3.setMetrics(metricsDto);
            lvl3.setLvl4List(mapReplenishmentLvl4Sp(replenishmentResponseDTO, lvl3, finelineNbr, ccId));
        }
        else {
            lvl3.setLvl4List(mapReplenishmentLvl4Sp(replenishmentResponseDTO, lvl3, finelineNbr, ccId));
        }
        lvl3List.add(lvl3);

    }

    private List<Lvl4Dto> mapReplenishmentLvl4Sp(ReplenishmentResponseDTO replenishmentResponseDTO, Lvl3Dto lvl3, Integer finelineNbr, String ccId) {
        List<Lvl4Dto> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> replenishmentResponseDTO.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapReplenishmentFl(replenishmentResponseDTO, lvl4, finelineNbr, ccId)),
                        () -> setLvl4SP(replenishmentResponseDTO, lvl4DtoList, finelineNbr, ccId));
        return lvl4DtoList;
    }

    private void setLvl4SP(ReplenishmentResponseDTO replenishmentResponseDTO, List<Lvl4Dto> lvl4DtoList, Integer finelineNbr, String ccId) {
        Lvl4Dto lvl4 = new Lvl4Dto();
        lvl4.setLvl4Nbr(replenishmentResponseDTO.getLvl4Nbr());
        lvl4.setLvl4Desc(replenishmentResponseDTO.getLvl4Desc());

        if (finelineNbr == null && ccId == null) {
            MetricsDto metricsDto = new MetricsDto();
            metricsDto.setVendorPack(replenishmentResponseDTO.getLvl4VenderPackCount());
            metricsDto.setWarehousePack(replenishmentResponseDTO.getLvl4WhsePackCount());
            metricsDto.setPackRatio(replenishmentResponseDTO.getLvl4vnpkWhpkRatio());
            metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getLvl4ReplQty());

            lvl4.setMetrics(metricsDto);
            lvl4.setFinelines(mapReplenishmentFl(replenishmentResponseDTO, lvl4, finelineNbr, ccId));
        }
        else {
            lvl4.setFinelines(mapReplenishmentFl(replenishmentResponseDTO, lvl4, finelineNbr, ccId));
        }
        lvl4DtoList.add(lvl4);

    }

    private List<FinelineDto> mapReplenishmentFl(ReplenishmentResponseDTO replenishmentResponseDTO, Lvl4Dto lvl4, Integer finelineNbr, String ccId) {
        List<FinelineDto> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

        finelineDtoList.stream()
                .filter(finelineDto -> replenishmentResponseDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineDto -> finelineDto.setStyles(mapReplenishmentStyles(replenishmentResponseDTO, finelineDto, finelineNbr, ccId)),
                        () -> setFinelineSP(replenishmentResponseDTO, finelineDtoList, finelineNbr, ccId));
        return finelineDtoList;
    }

    private void setFinelineSP(ReplenishmentResponseDTO replenishmentResponseDTO, List<FinelineDto> finelineDtoList, Integer finelineNbr, String ccId) {
        FinelineDto fineline = new FinelineDto();
        fineline.setFinelineNbr(replenishmentResponseDTO.getFinelineNbr());
        fineline.setFinelineDesc(replenishmentResponseDTO.getFinelineDesc());
        fineline.setFinelineAltDesc(replenishmentResponseDTO.getFinelineAltDesc());
        if (finelineNbr == null && ccId == null) {
            MetricsDto metricsDto = new MetricsDto();
            metricsDto.setFinalBuyQty(replenishmentResponseDTO.getFinelineFinalBuyUnits());
            metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getFinelineReplQty());
            metricsDto.setVendorPack(replenishmentResponseDTO.getFinelineVenderPackCount());
            metricsDto.setWarehousePack(replenishmentResponseDTO.getFinelineWhsePackCount());
            metricsDto.setPackRatio(replenishmentResponseDTO.getFinelineVnpkWhpkRatio());
            metricsDto.setReplenishmentPacks(replenishmentResponseDTO.getFinelineReplPack());
            fineline.setMetrics(metricsDto);
        } else {
            fineline.setStyles(mapReplenishmentStyles(replenishmentResponseDTO, fineline, finelineNbr, ccId));
        }
        finelineDtoList.add(fineline);
    }

    private List<StyleDto> mapReplenishmentStyles(ReplenishmentResponseDTO replenishmentResponseDTO, FinelineDto fineline, Integer finelineNbr, String ccId) {
        List<StyleDto> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

        styleDtoList.stream()
                .filter(styleDto -> replenishmentResponseDTO.getStyleNbr().equals(styleDto.getStyleNbr())).findFirst()
                .ifPresentOrElse(styleDto -> styleDto.setCustomerChoices(mapReplenishmentCc(replenishmentResponseDTO, styleDto, finelineNbr, ccId)),
                        () -> setStyleSP(replenishmentResponseDTO, styleDtoList, finelineNbr, ccId));
        return styleDtoList;
    }

    private void setStyleSP(ReplenishmentResponseDTO replenishmentResponseDTO, List<StyleDto> styleDtoList, Integer finelineNbr, String ccId) {

        StyleDto styleDto = new StyleDto();
        styleDto.setStyleNbr(replenishmentResponseDTO.getStyleNbr());
        if (ccId == null) {
            MetricsDto metricsDto = new MetricsDto();
            metricsDto.setFinalBuyQty(replenishmentResponseDTO.getStyleFinalBuyUnits());
            metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getStyleReplQty());
            metricsDto.setVendorPack(replenishmentResponseDTO.getStyleVenderPackCount());
            metricsDto.setWarehousePack(replenishmentResponseDTO.getStyleWhsePackCount());
            metricsDto.setPackRatio(replenishmentResponseDTO.getStyleVnpkWhpkRatio());
            metricsDto.setReplenishmentPacks(replenishmentResponseDTO.getStyleReplPack());
            styleDto.setCustomerChoices(mapReplenishmentCc(replenishmentResponseDTO, styleDto, finelineNbr, ccId));
            styleDto.setMetrics(metricsDto);

        } else {
            styleDto.setCustomerChoices(mapReplenishmentCc(replenishmentResponseDTO, styleDto, finelineNbr, ccId));
        }
        styleDtoList.add(styleDto);
    }

    private List<CustomerChoiceDto> mapReplenishmentCc(ReplenishmentResponseDTO replenishmentResponseDTO, StyleDto styleDto, Integer finelineNbr, String ccId) {
        List<CustomerChoiceDto> customerChoiceList = Optional.ofNullable(styleDto.getCustomerChoices()).orElse(new ArrayList<>());

        customerChoiceList.stream()
                .filter(customerChoiceDto -> replenishmentResponseDTO.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> customerChoiceDto.setMerchMethods(mapMerchMethod(replenishmentResponseDTO, customerChoiceDto)),
                        () -> setCcSP(replenishmentResponseDTO, customerChoiceList, finelineNbr, ccId));
        return customerChoiceList;

    }

    private void setCcSP(ReplenishmentResponseDTO replenishmentResponseDTO, List<CustomerChoiceDto> customerChoiceDtoList, Integer finelineNbr, String ccId) {

        CustomerChoiceDto customerChoiceDto = new CustomerChoiceDto();
        customerChoiceDto.setCcId(replenishmentResponseDTO.getCcId());
        if (ccId == null) {
            MetricsDto metricsDto = new MetricsDto();
            metricsDto.setFinalBuyQty(replenishmentResponseDTO.getCcFinalBuyUnits());
            metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getCcReplQty());
            metricsDto.setVendorPack(replenishmentResponseDTO.getCcVenderPackCount());
            metricsDto.setWarehousePack(replenishmentResponseDTO.getCcWhsePackCount());
            metricsDto.setPackRatio(replenishmentResponseDTO.getCcVnpkWhpkRatio());
            metricsDto.setReplenishmentPacks(replenishmentResponseDTO.getCcReplPack());
            customerChoiceDto.setMetrics(metricsDto);

        } else {
            customerChoiceDto.setMerchMethods(mapMerchMethod(replenishmentResponseDTO, customerChoiceDto));
        }
        customerChoiceDtoList.add(customerChoiceDto);
    }

    private List<MerchMethodsDto> mapMerchMethod(ReplenishmentResponseDTO replenishmentResponseDTO, CustomerChoiceDto customerChoiceDto) {
        List<MerchMethodsDto> merchMethodsDtoList = Optional.ofNullable(customerChoiceDto.getMerchMethods()).orElse(new ArrayList<>());

        merchMethodsDtoList.stream()
                .filter(merchMethodsDto -> replenishmentResponseDTO.getMerchMethod().equals(merchMethodsDto.getMerchMethod())).findFirst()
                .ifPresentOrElse(merchMethodsDto -> merchMethodsDto.setSizes(mapSize(replenishmentResponseDTO, merchMethodsDto)),
                        () -> setMerch(replenishmentResponseDTO, merchMethodsDtoList));
        return merchMethodsDtoList;

    }

    private void setMerch(ReplenishmentResponseDTO replenishmentResponseDTO, List<MerchMethodsDto> merchMethodsDtoList) {

        MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
        merchMethodsDto.setMerchMethod(replenishmentResponseDTO.getMerchMethod());
        MetricsDto metricsDto = new MetricsDto();
        metricsDto.setFinalBuyQty(replenishmentResponseDTO.getCcSpFinalBuyUnits());
        metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getCcSpReplQty());
        metricsDto.setVendorPack(replenishmentResponseDTO.getCcSpVenderPackCount());
        metricsDto.setWarehousePack(replenishmentResponseDTO.getCcSpWhsePackCount());
        metricsDto.setPackRatio(replenishmentResponseDTO.getCcSpVnpkWhpkRatio());
        metricsDto.setReplenishmentPacks(replenishmentResponseDTO.getCcSpReplPack());
        merchMethodsDto.setMetrics(metricsDto);
        merchMethodsDto.setSizes(mapSize(replenishmentResponseDTO, merchMethodsDto));
        merchMethodsDtoList.add(merchMethodsDto);

    }




    private List<SizeDto> mapSize(ReplenishmentResponseDTO replenishmentResponseDTO, MerchMethodsDto merchMethodsDto) {
        List<SizeDto> sizeDtoList = Optional.ofNullable(merchMethodsDto.getSizes()).orElse(new ArrayList<>());

        sizeDtoList.stream()
                .filter(sizeDto -> replenishmentResponseDTO.getAhsSizeId().equals(sizeDto.getAhsSizeId())).findFirst()
                .ifPresentOrElse(customerChoiceDto ->log.info("Size implementation"),
                        ()->setSizes(replenishmentResponseDTO, sizeDtoList));
        return sizeDtoList;
    }


    private void setSizes(ReplenishmentResponseDTO replenishmentResponseDTO, List<SizeDto> sizeDtoList) {

        SizeDto sizeDto= new SizeDto();
        sizeDto.setAhsSizeId(replenishmentResponseDTO.getAhsSizeId());
        sizeDto.setSizeDesc(replenishmentResponseDTO.getSizeDesc());
        MetricsDto metricsDto = new MetricsDto();
        metricsDto.setFinalBuyQty(replenishmentResponseDTO.getCcSpFinalBuyUnits());
        metricsDto.setFinalReplenishmentQty(replenishmentResponseDTO.getCcSpReplQty());
        metricsDto.setVendorPack(replenishmentResponseDTO.getCcSpVenderPackCount());
        metricsDto.setWarehousePack(replenishmentResponseDTO.getCcSpWhsePackCount());
        metricsDto.setPackRatio(replenishmentResponseDTO.getCcSpVnpkWhpkRatio());
        metricsDto.setReplenishmentPacks(replenishmentResponseDTO.getCcSpReplPack());
        sizeDto.setMetrics(metricsDto);
        sizeDtoList.add(sizeDto);

    }
}
