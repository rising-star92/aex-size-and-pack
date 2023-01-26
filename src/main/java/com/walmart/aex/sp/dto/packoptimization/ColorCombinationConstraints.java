package com.walmart.aex.sp.dto.packoptimization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorCombinationConstraints {
    private List<Supplier> suppliers;
    private String factoryId;
    private String portOfOrigin;
    private Integer singlePackIndicator;
    private String colorCombination;
}
