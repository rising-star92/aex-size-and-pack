package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SPFactoryService {

    private final CcPackOptimizationRepository ccPackOptimizationRepository;


    public SPFactoryService(CcPackOptimizationRepository ccPackOptimizationRepository) {
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
    }

    public void setFactoriesForFinelines(BuyQtyRequest buyQtyRequest, BuyQtyResponse buyQtyResponse) {
        List<FactoryDTO> factoryDTOS = ccPackOptimizationRepository.getFactoriesByPlanId(buyQtyRequest.getPlanId(), null);
        buyQtyResponse.getLvl3List().forEach(lvl3Dto -> lvl3Dto.getLvl4List()
                .forEach(lvl4Dto -> lvl4Dto.getFinelines()
                        .forEach(finelineDto -> {
                                    if (finelineDto.getMetrics() == null) {
                                        finelineDto.setMetrics(new MetricsDto());
                                    }
                                    finelineDto.getMetrics().setFactories(findFactoriesFinelines(finelineDto, factoryDTOS));
                                }
                        )));
    }


    public void setFactoriesForCCs(BuyQtyRequest buyQtyRequest, BuyQtyResponse buyQtyResponse) {
        List<FactoryDTO> factoryDTOS = ccPackOptimizationRepository.getFactoriesByPlanId(buyQtyRequest.getPlanId(), buyQtyRequest.getFinelineNbr());
        buyQtyResponse.getLvl3List().forEach(lvl3Dto -> lvl3Dto.getLvl4List()
                .forEach(lvl4Dto -> lvl4Dto.getFinelines()
                        .forEach(finelineDto -> finelineDto.getStyles().forEach(styleDto -> {
                            if (styleDto.getMetrics() == null) {
                                styleDto.setMetrics(new MetricsDto());
                            }
                            styleDto.getMetrics().setFactories(setFactoriesForStyles(styleDto, factoryDTOS));
                            styleDto.getCustomerChoices().forEach(customerChoiceDto -> {
                                if (customerChoiceDto.getMetrics() == null) {
                                    customerChoiceDto.setMetrics(new MetricsDto());
                                }
                                customerChoiceDto.getMetrics().setFactories(findFactoriesCCs(customerChoiceDto, factoryDTOS));
                            });
                        }))));

    }

    private List<FactoryDTO> findFactoriesFinelines(FinelineDto finelineDto, List<FactoryDTO> factoryDTOS) {
        return factoryDTOS.stream()
                .filter(factoryDTO -> factoryDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr()))
                .collect(Collectors.toList());
    }

    private List<FactoryDTO> setFactoriesForStyles(StyleDto styleDto, List<FactoryDTO> factoryDTOS) {
        return factoryDTOS.stream()
                .filter(factoryDTO -> factoryDTO.getStyleNbr().equals(styleDto.getStyleNbr()))
                .collect(Collectors.toList());
    }

    private List<FactoryDTO> findFactoriesCCs(CustomerChoiceDto customerChoiceDto, List<FactoryDTO> factoryDTOS) {
        return factoryDTOS.stream()
                .filter(factoryDTO -> factoryDTO.getCcId().equals(customerChoiceDto.getCcId()))
                .collect(Collectors.toList());
    }
}
