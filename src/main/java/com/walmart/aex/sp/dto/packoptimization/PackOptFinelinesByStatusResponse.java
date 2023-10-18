package com.walmart.aex.sp.dto.packoptimization;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackOptFinelinesByStatusResponse {
    private Long planId;
    private Integer finelineNbr;
    private Date startTs;
    private Date endTs;
    private Integer runStatusCode;
}
