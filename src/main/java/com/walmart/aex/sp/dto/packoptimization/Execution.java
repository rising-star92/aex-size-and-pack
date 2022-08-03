package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class Execution {
    private Integer id;
    private Integer statusCode;
    private String statusDesc;
    private String validationMessage;
}
