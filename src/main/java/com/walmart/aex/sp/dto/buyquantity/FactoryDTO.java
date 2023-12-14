package com.walmart.aex.sp.dto.buyquantity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FactoryDTO {
    private Integer finelineNbr;
    private String styleNbr;
    private String ccId;
    private String factoryId;
    private String factoryName;
}
