package com.walmart.aex.sp.dto.packoptimization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Supplier {
    @EqualsAndHashCode.Include
    private Integer supplierId;
    private Integer vendorNumber6;
    private Integer supplier8Number;
    private Integer gsmSupplierNumber;
    @EqualsAndHashCode.Include
    private String supplierName;
    private String supplierType;
    private Integer supplierNumber;
    private Integer vendorNumber9;

    public Supplier(Integer vendorNumber6, Integer gsmSupplierNumber, String supplierName, String supplierType, Integer vendorNumber9) {
        this.supplierId = vendorNumber6;
        this.vendorNumber6 = vendorNumber6;
        this.supplier8Number = gsmSupplierNumber;
        this.gsmSupplierNumber = gsmSupplierNumber;
        this.supplierName = supplierName;
        this.supplierType = supplierType;
        this.supplierNumber = vendorNumber9;
        this.vendorNumber9 = vendorNumber9;
    }
}
