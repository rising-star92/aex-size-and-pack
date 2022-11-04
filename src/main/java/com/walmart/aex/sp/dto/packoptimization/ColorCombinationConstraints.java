package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class ColorCombinationConstraints {

    private String supplierName;

    private String factoryId;

    private String countryOfOrigin;

    private String portOfOrigin;

    private String colorCombination;

    private Integer singlePackInd;

}
