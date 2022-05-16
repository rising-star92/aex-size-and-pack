package com.walmart.aex.sp.dto;


import lombok.Data;

import java.util.List;

@Data
public class Strategy {
    private List<WeatherCluster> weatherClusters;
    private List<Fixture> fixture;
    private LinePlanStrategy linePlan;
}
