package com.walmart.aex.sp.dto.packoptimization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PackOptConstraintResponseDTO {
    private Long planId;
    private Integer channelId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private String finelineDesc;
    private String altfinelineDesc;
    private String styleNbr;
    private String styleSupplierName;
    private Integer styleSupplierNumber6;
    private Integer styleSupplierNumber8;
    private Integer styleSupplierNumber9;
    private String styleFactoryIds;
    private String styleCountryOfOrigin;
    private String stylePortOfOrigin;
    private Integer styleSinglePackIndicator;
    private String styleColorCombination;
    private Integer styleMaxUnitsPerPack;
    private Integer styleMaxPacks;
    private String ccId;
    private String ccSupplierName;
    private Integer ccSupplierNumber6;
    private Integer ccSupplierNumber8;
    private Integer ccSupplierNumber9;
    private String ccFactoryIds;
    private String ccCountryOfOrigin;
    private String ccPortOfOrigin;
    private Integer ccSinglePackIndicator;
    private String ccColorCombination;
    private Integer ccMaxUnitsPerPack;
    private Integer ccMaxPacks;
    private String lvl0Desc;
    private String lvl1Desc;
    private String lvl2Desc;
    private String lvl3Desc;
    private String lvl4Desc;
}
