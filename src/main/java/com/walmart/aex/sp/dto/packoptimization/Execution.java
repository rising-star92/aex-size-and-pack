package com.walmart.aex.sp.dto.packoptimization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Execution {
    private BigInteger id;
    private Integer statusCode;
    private String statusDesc;
    private String validationMessage;
}
