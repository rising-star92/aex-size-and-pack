package com.walmart.aex.sp.dto.integrationhub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WFAttributes {
    private List<Integer> context_finelineNbrs;
    private String context_getPackOptFinelineDetails;
    private Long context_planId;
    private String context_updatePackOptFinelineStatus;

}
