package com.walmart.aex.sp.dto.packoptimization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Supplier {
    @EqualsAndHashCode.Include
    private Integer vendorNumber6;
    private Integer gsmSupplierNumber;
    @EqualsAndHashCode.Include
    private String supplierName;
    private String supplierType;
    private Integer vendorNumber9;
}
