package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class UpdatePackOptConstraintRequestDTO {
    private Long planId;
    private String planDesc;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private String styleNbr;
    private String ccId;
    private Integer vendorNbr6;
    private Integer vendorNbr9;
    private String vendorName;
    private String factoryId;
    private String factoryName;
    private Integer singlePackInd;
    private Integer portOfOriginId;
    private String portOfOriginName;
    private Integer maxUnitsPerPack;
    private Integer maxNbrOfPacks;
    private String colorCombination;
    private String channel;
}
