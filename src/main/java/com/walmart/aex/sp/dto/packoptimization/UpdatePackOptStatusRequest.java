package com.walmart.aex.sp.dto.packoptimization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatePackOptStatusRequest {
    private Integer statusCode;
    private String statusDesc;
    private String statusLongDesc;
}
