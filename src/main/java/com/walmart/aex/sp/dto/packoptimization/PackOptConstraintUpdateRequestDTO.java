package com.walmart.aex.sp.dto.packoptimization;

import com.walmart.aex.sp.entity.ChannelText;
import lombok.Data;

@Data
public class PackOptConstraintUpdateRequestDTO {
    private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private String styleNbr;
    private String ccId;
    // merch Catg , subcatg ,fineline, style,cc
    private Integer vendorNbr6;
    private Integer vendorNbr9;
    private String vendorName;
    private String originCountryCode;
    private String originCountryName;
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
