package com.walmart.aex.sp.dto.packoptimization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorCombinationConstraints {
    private Supplier supplier;
    private String factoryId;
    private String countryOfOrigin;
    private String portOfOrigin;
    private Integer singlePackIndicator;
    private String colorCombination;
}
