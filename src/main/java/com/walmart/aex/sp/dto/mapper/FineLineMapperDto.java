package com.walmart.aex.sp.dto.mapper;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    private Integer merchVendorNumber6;
    private Integer merchGsmSupplierNumber;
    private Integer merchVendorNumber9;
    private Integer merchMaxUnitsPerPack;
    private Integer merchMaxNbrOfPacks;
    private String merchFactoryId;
    private String merchOriginCountryName;
    private String merchPortOfOriginName;
    private Integer merchSinglePackInd;
    private String merchColorCombination;
    private String subCatSupplierName;
    private Integer subCatVendorNumber6;
    private Integer subCatGsmSupplierNumber;
    private Integer subCatVendorNumber9;
    private Integer subCatMaxUnitsPerPack;
    private Integer subCatMaxNbrOfPacks;
    private String subCatFactoryId;
    private String subCatOriginCountryName;
    private String subCatPortOfOriginName;
    private Integer subCatSinglePackInd;
    private String subCatColorCombination;
    private String fineLineSupplierName;
    private Integer fineLineVendorNumber6;
    private Integer fineLineGsmSupplierNumber;
    private Integer fineLineVendorNumber9;
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
    private String styleNbr;
    private String styleSupplierName;
    private Integer styleVendorNumber6;
    private Integer styleGsmSupplierNumber;
    private Integer styleVendorNumber9;
    private String styleFactoryIds;
    private String styleCountryOfOrigin;
    private String stylePortOfOrigin;
    private Integer styleSinglePackIndicator;
    private String styleColorCombination;
    private Integer styleMaxUnitsPerPack;
    private Integer styleMaxPacks;
    private String ccId;
    private String ccSupplierName;
    private Integer ccVendorNumber6;
    private Integer ccGsmSupplierNumber;
    private Integer ccVendorNumber9;
    private String ccFactoryIds;
    private String ccCountryOfOrigin;
    private String ccPortOfOrigin;
    private Integer ccSinglePackIndicator;
    private String ccColorCombination;
    private Integer ccMaxUnitsPerPack;
    private Integer ccMaxPacks;

    public FineLineMapperDto(
            Long planId, Integer channelId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr,
            Integer lvl4Nbr, Integer fineLineNbr, String fineLineDesc, String altfineLineDesc, String lvl0Desc,
            String lvl1Desc, String lvl2Desc, String lvl3Desc, String lvl4Desc, String merchSupplierName,
            Integer merchVendorNumber6, Integer merchGsmSupplierNumber, Integer merchVendorNumber9,
            Integer merchMaxUnitsPerPack, Integer merchMaxNbrOfPacks, String merchFactoryId,
            String merchOriginCountryName, String merchPortOfOriginName, Integer merchSinglePackInd,
            String merchColorCombination, String subCatSupplierName, Integer subCatVendorNumber6,
            Integer subCatGsmSupplierNumber, Integer subCatVendorNumber9, Integer subCatMaxUnitsPerPack,
            Integer subCatMaxNbrOfPacks, String subCatFactoryId, String subCatOriginCountryName,
            String subCatPortOfOriginName, Integer subCatSinglePackInd, String subCatColorCombination,
            String fineLineSupplierName, Integer fineLineVendorNumber6, Integer fineLineGsmSupplierNumber,
            Integer fineLineVendorNumber9, Integer fineLineMaxUnitsPerPack, Integer fineLineMaxNbrOfPacks,
            String fineLineFactoryId, String fineLineOriginCountryName, String fineLinePortOfOriginName,
            Integer fineLineSinglePackInd, String fineLineColorCombination, String ccId, String ccSupplierName,
            Integer ccVendorNumber6, Integer ccGsmSupplierNumber, Integer ccVendorNumber9, String ccFactoryIds,
            String ccCountryOfOrigin, String ccPortOfOrigin, Integer ccSinglePackIndicator, String ccColorCombination,
            Integer ccMaxUnitsPerPack, Integer ccMaxPacks, Date startTs, Date endTs, Integer runStatusCode,
            String runStatusDesc, String firstName, String lastName, String returnMessage) {
        this.planId = planId;
        this.channelId = channelId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl4Nbr = lvl4Nbr;
        this.fineLineNbr = fineLineNbr;
        this.fineLineDesc = fineLineDesc;
        this.altfineLineDesc = altfineLineDesc;
        this.lvl0Desc = lvl0Desc;
        this.lvl1Desc = lvl1Desc;
        this.lvl2Desc = lvl2Desc;
        this.lvl3Desc = lvl3Desc;
        this.lvl4Desc = lvl4Desc;
        this.merchSupplierName = merchSupplierName;
        this.merchVendorNumber6 = merchVendorNumber6;
        this.merchGsmSupplierNumber = merchGsmSupplierNumber;
        this.merchVendorNumber9 = merchVendorNumber9;
        this.merchMaxUnitsPerPack = merchMaxUnitsPerPack;
        this.merchMaxNbrOfPacks = merchMaxNbrOfPacks;
        this.merchFactoryId = merchFactoryId;
        this.merchOriginCountryName = merchOriginCountryName;
        this.merchPortOfOriginName = merchPortOfOriginName;
        this.merchSinglePackInd = merchSinglePackInd;
        this.merchColorCombination = merchColorCombination;
        this.subCatSupplierName = subCatSupplierName;
        this.subCatVendorNumber6 = subCatVendorNumber6;
        this.subCatGsmSupplierNumber = subCatGsmSupplierNumber;
        this.subCatVendorNumber9 = subCatVendorNumber9;
        this.subCatMaxUnitsPerPack = subCatMaxUnitsPerPack;
        this.subCatMaxNbrOfPacks = subCatMaxNbrOfPacks;
        this.subCatFactoryId = subCatFactoryId;
        this.subCatOriginCountryName = subCatOriginCountryName;
        this.subCatPortOfOriginName = subCatPortOfOriginName;
        this.subCatSinglePackInd = subCatSinglePackInd;
        this.subCatColorCombination = subCatColorCombination;
        this.fineLineSupplierName = fineLineSupplierName;
        this.fineLineVendorNumber6 = fineLineVendorNumber6;
        this.fineLineGsmSupplierNumber = fineLineGsmSupplierNumber;
        this.fineLineVendorNumber9 = fineLineVendorNumber9;
        this.fineLineMaxUnitsPerPack = fineLineMaxUnitsPerPack;
        this.fineLineMaxNbrOfPacks = fineLineMaxNbrOfPacks;
        this.fineLineFactoryId = fineLineFactoryId;
        this.fineLineOriginCountryName = fineLineOriginCountryName;
        this.fineLinePortOfOriginName = fineLinePortOfOriginName;
        this.fineLineSinglePackInd = fineLineSinglePackInd;
        this.fineLineColorCombination = fineLineColorCombination;
        this.startTs = startTs;
        this.endTs = endTs;
        this.runStatusCode = runStatusCode;
        this.runStatusDesc = runStatusDesc;
        this.firstName = firstName;
        this.lastName = lastName;
        this.returnMessage = returnMessage;
        this.ccId = ccId;
        this.ccSupplierName = ccSupplierName;
        this.ccGsmSupplierNumber = ccGsmSupplierNumber;
        this.ccVendorNumber6 = ccVendorNumber6;
        this.ccVendorNumber9 = ccVendorNumber9;
        this.ccFactoryIds = ccFactoryIds;
        this.ccCountryOfOrigin = ccCountryOfOrigin;
        this.ccPortOfOrigin = ccPortOfOrigin;
        this.ccSinglePackIndicator = ccSinglePackIndicator;
        this.ccColorCombination = ccColorCombination;
        this.ccMaxUnitsPerPack = ccMaxUnitsPerPack;
        this.ccMaxPacks = ccMaxPacks;
    }
}
