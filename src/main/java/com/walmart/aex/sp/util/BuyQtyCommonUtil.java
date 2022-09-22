package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.service.BuyQuantityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public BuyQtyResponse filterFinelinesWithSizes(List<BuyQntyResponseDTO> buyQntyResponseDTOS,BuyQtyResponse finelinesWithSizesFromStrategy)
    {
        BuyQtyResponse buyQtyResponse=new BuyQtyResponse();
        buyQntyResponseDTOS.forEach(buyQntyResponseDTO -> {
            Optional.of(finelinesWithSizesFromStrategy.getLvl3List())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(lvl3Dto -> lvl3Dto.getLvl3Nbr().equals(buyQntyResponseDTO.getLvl3Nbr()))
                    .map(Lvl3Dto::getLvl4List)
                    .flatMap(Collection::stream)
                    .filter(lvl4Dto -> lvl4Dto.getLvl4Nbr().equals(buyQntyResponseDTO.getLvl4Nbr()))
                    .map(Lvl4Dto::getFinelines)
                    .flatMap(Collection::stream)
                    .filter(finelineDto -> finelineDto.getFinelineNbr().equals(buyQntyResponseDTO.getFinelineNbr()))
                    .forEach(finelineNbr->{
                        buyQuantityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,buyQtyResponse,null);
                    });});
        return buyQtyResponse;
    }

    public BuyQtyResponse filterStylesCcWithSizes(List<BuyQntyResponseDTO> buyQntyResponseDTOS,BuyQtyResponse stylesCcWithSizesFromStrategy,Integer finelineNbr)
    {
        BuyQtyResponse buyQtyResponse=new BuyQtyResponse();

        buyQntyResponseDTOS.forEach(buyQntyResponseDTO -> {
            Optional.of(stylesCcWithSizesFromStrategy.getLvl3List())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(lvl3Dto -> lvl3Dto.getLvl3Nbr().equals(buyQntyResponseDTO.getLvl3Nbr()))
                    .map(Lvl3Dto::getLvl4List)
                    .flatMap(Collection::stream)
                    .filter(lvl4Dto -> lvl4Dto.getLvl4Nbr().equals(buyQntyResponseDTO.getLvl4Nbr()))
                    .map(Lvl4Dto::getFinelines)
                    .flatMap(Collection::stream)
                    .filter(finelineDto -> finelineDto.getFinelineNbr().equals(buyQntyResponseDTO.getFinelineNbr()))
                    .map(FinelineDto::getStyles)
                    .flatMap(Collection::stream)
                    .filter(styleDto -> styleDto.getStyleNbr().equals(buyQntyResponseDTO.getStyleNbr()))
                    .map(StyleDto::getCustomerChoices)
                    .flatMap(Collection::stream)
                    .filter(customerChoiceDto -> customerChoiceDto.getCcId().equals(buyQntyResponseDTO.getCcId()))
                    .forEach(ccId->{
                        buyQuantityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,buyQtyResponse,finelineNbr);
                    });});

        return buyQtyResponse;
    }
}
