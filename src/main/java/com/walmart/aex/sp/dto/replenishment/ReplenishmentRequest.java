package com.walmart.aex.sp.dto.replenishment;


import lombok.Data;

@Data
public class ReplenishmentRequest {

    private Long planId;
    private String channel;
    private String planDesc;
    private Integer finelineNbr;
    private String styleNbr;
    private String ccId;

}
