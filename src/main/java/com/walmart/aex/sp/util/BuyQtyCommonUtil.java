package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.service.BuyQuantityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BuyQtyCommonUtil {

    private final BuyQuantityMapper buyQuantityMapper;
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
        buyQntyResponseDTOS.forEach(buyQntyResponseDTO -> getFinelines(buyQntyResponseDTO, finelinesWithSizesFromStrategy)
                .forEach(finelineNbr -> buyQuantityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponse, null)));
        return buyQtyResponse;
    }

    public BuyQtyResponse filterStylesCcWithSizes(List<BuyQntyResponseDTO> buyQntyResponseDTOS, BuyQtyResponse stylesCcWithSizesFromStrategy, Integer finelineNbr) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();

        buyQntyResponseDTOS.forEach(buyQntyResponseDTO -> getFinelines(buyQntyResponseDTO, stylesCcWithSizesFromStrategy)
                .stream()
                .map(FinelineDto::getStyles)
                .flatMap(Collection::stream)
                .filter(styleDto -> styleDto.getStyleNbr().equals(buyQntyResponseDTO.getStyleNbr()))
                .map(StyleDto::getCustomerChoices)
                .flatMap(Collection::stream)
                .filter(customerChoiceDto -> customerChoiceDto.getCcId().equals(buyQntyResponseDTO.getCcId()))
                .forEach(ccId -> buyQuantityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponse, finelineNbr)));

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
}
