package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.buyquantity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class BuyQtyCommonUtil {
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
}
