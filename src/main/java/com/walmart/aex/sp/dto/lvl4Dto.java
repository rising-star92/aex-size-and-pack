package com.walmart.aex.sp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Lvl4Dto {
    private Integer lvl4Nbr;
    private String lvl4Desc;
    private List<FinelineDto> finelines;
}
