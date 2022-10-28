package com.walmart.aex.sp.dto.mapper;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FineLineMapperDto {

    private Long planId;
    private Integer channelId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer fineLineNbr;
    private String fineLineDesc;
    private String altfineLineDesc;
    private String lvl0Desc;
    private String lvl1Desc;
    private String lvl2Desc;
    private String lvl3Desc;
    private String lvl4Desc;
    private String merchSupplierName;
    private Integer merchMaxUnitsPerPack;
    private Integer merchMaxNbrOfPacks;
    private String merchFactoryId;
    private String merchOriginCountryName;
    private String merchPortOfOriginName;
    private Integer merchSinglePackInd;
    private String merchColorCombination;
    private String subCatSupplierName;
    private Integer subCatMaxUnitsPerPack;
    private Integer subCatMaxNbrOfPacks;
    private String subCatFactoryId;
    private String subCatOriginCountryName;
    private String subCatPortOfOriginName;
    private Integer subCatSinglePackInd;
    private String subCatColorCombination;
    private String fineLineSupplierName;
    private Integer fineLineMaxUnitsPerPack;
    private Integer fineLineMaxNbrOfPacks;
    private String fineLineFactoryId;
    private String fineLineOriginCountryName;
    private String fineLinePortOfOriginName;
    private Integer fineLineSinglePackInd;
    private String fineLineColorCombination;
    private Date startTs;
    private Date endTs;
    private Integer runStatusCode;
    private String runStatusDesc;
    private String firstName;
    private String lastName;
    private String returnMessage;
}
