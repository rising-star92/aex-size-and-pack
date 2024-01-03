package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BQFactoryMapper {

    public void setFactoriesForFinelines(List<FactoryDTO> factoryDTOS, BuyQtyResponse buyQtyResponse) {
        if (buyQtyResponse.getLvl3List() != null) {
            buyQtyResponse.getLvl3List().stream()
                    .filter(lvl3Dto -> lvl3Dto.getLvl4List() != null)
                    .forEach(lvl3Dto -> lvl3Dto.getLvl4List().stream()
                    .filter(lvl4Dto -> lvl4Dto.getFinelines() != null)
                    .forEach(lvl4Dto -> lvl4Dto.getFinelines()
                            .forEach(finelineDto -> {
                                        if (finelineDto.getMetrics() == null) {
                                            finelineDto.setMetrics(new MetricsDto());
                                        }
                                        finelineDto.getMetrics().setFactories(findFactoriesForFineline(finelineDto, factoryDTOS));
                                    }
                            )));
        }
    }


    public void setFactoriesForCCs(List<FactoryDTO> factoryDTOS, BuyQtyResponse buyQtyResponse) {
        if (buyQtyResponse.getLvl3List() != null) {
            buyQtyResponse.getLvl3List().stream()
                    .filter(lvl3Dto -> lvl3Dto.getLvl4List() != null)
                    .forEach(lvl3Dto -> lvl3Dto.getLvl4List().stream()
                    .filter(lvl4Dto -> lvl4Dto.getFinelines() != null)
                    .forEach(lvl4Dto -> lvl4Dto.getFinelines().stream()
                            .filter(finelineDto -> finelineDto.getStyles() != null)
                            .forEach(finelineDto -> finelineDto.getStyles().forEach(styleDto -> {
                                if (styleDto.getMetrics() == null) {
                                    styleDto.setMetrics(new MetricsDto());
                                }
                                styleDto.getMetrics().setFactories(setFactoriesForStyle(styleDto, factoryDTOS));
                                if(styleDto.getCustomerChoices() != null) {
                                    styleDto.getCustomerChoices().forEach(customerChoiceDto -> {
                                        if (customerChoiceDto.getMetrics() == null) {
                                            customerChoiceDto.setMetrics(new MetricsDto());
                                        }
                                        customerChoiceDto.getMetrics().setFactories(findFactoriesForCC(customerChoiceDto, factoryDTOS));
                                    });
                                }
                            }))));
        }
    }

    private List<FactoryDTO> findFactoriesForFineline(FinelineDto finelineDto, List<FactoryDTO> factoryDTOS) {
        return factoryDTOS.stream()
                .filter(factoryDTO -> factoryDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr()))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<FactoryDTO> setFactoriesForStyle(StyleDto styleDto, List<FactoryDTO> factoryDTOS) {
        return factoryDTOS.stream()
                .filter(factoryDTO -> factoryDTO.getStyleNbr().equals(styleDto.getStyleNbr()))
                .distinct()
                .collect(Collectors.toList());
    }

    private List<FactoryDTO> findFactoriesForCC(CustomerChoiceDto customerChoiceDto, List<FactoryDTO> factoryDTOS) {
        return factoryDTOS.stream()
                .filter(factoryDTO -> factoryDTO.getCcId().equals(customerChoiceDto.getCcId()))
                .distinct()
                .collect(Collectors.toList());
    }


}
