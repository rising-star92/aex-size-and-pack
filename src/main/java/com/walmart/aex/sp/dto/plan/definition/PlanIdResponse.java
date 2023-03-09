package com.walmart.aex.sp.dto.plan.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanIdResponse {
    private Integer planId;
    private String planDesc;
}
