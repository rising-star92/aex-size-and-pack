package com.walmart.aex.sp.dto;


import com.walmart.aex.sp.enums.ExcludeOffshoreMkt;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WeatherCluster {
    private ClusterType type;
    private Boolean isEligible;
    private String location;
    private List<Trait> trait;
    private Integer storeCount;
    private Integer sellingWeeks;
    private FiscalWeek inStoreDate;
    private FiscalWeek markDownDate;
    private List<ExcludeOffshoreMkt> excludeOffshore;
    private BigDecimal lySales;
    private Integer lyUnits;
    private Integer onHandQty;
    private BigDecimal salesToStockRatio;
    private BigDecimal forecastedSales;
    private Integer forecastedUnits;
    private Integer ranking;
    private Integer algoClusterRanking;

}
