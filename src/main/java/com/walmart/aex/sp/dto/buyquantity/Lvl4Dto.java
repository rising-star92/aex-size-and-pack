package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class Lvl4Dto {
    private Integer lvl4Nbr;
    private String lvl4Desc;
    private MetricsDto metrics;
    private List<FinelineDto> finelines;
}
