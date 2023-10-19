package com.walmart.aex.sp.dto.packoptimization;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackOptFinelinesByStatusResponse {
    private Long planId;
    private Integer finelineNbr;
    private LocalDateTime startTs;
    private LocalDateTime endTs;
    private Integer runStatusCode;
    private String runStatusDesc;
}
