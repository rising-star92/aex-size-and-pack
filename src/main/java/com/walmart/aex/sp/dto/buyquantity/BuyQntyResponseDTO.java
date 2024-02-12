package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuyQntyResponseDTO {
    private Long planId;
    private Integer channelId;
    private Integer lvl0Nbr;
    private String lvl0Desc;
    private Integer lvl1Nbr;
    private String lvl1Desc;
    private Integer lvl2Nbr;
    private String lvl2Desc;
    private Integer lvl3Nbr;
    private String lvl3Desc;
    private Integer lvl4Nbr;
    private String lvl4Desc;
    private Integer finelineNbr;
    private String finelineDesc;
    private String finelineMessageObj;
    private String styleNbr;
    private String ccId;
    private Integer ahsSizeId;
    private String sizeDesc;
    private String sizeMessageObj;
    private Integer flowStrategyCode;
    private Integer merchMethodCode;
    private String merchMethodShortDesc;
    private Integer bumpPackQty;
    private Integer initialSetQty;
    private Integer buyQty;
    private Integer replnQty;
    private Integer adjReplnQty;
    private Integer styleFlowStrategy;
    private Integer styleMerchCode;
    private String styleMerchDesc;
    private Integer styleBumpQty;
    private Integer styleIsQty;
    private Integer styleBuyQty;
    private Integer styleReplnQty;
    private Integer styleAdjReplnQty;
    private String styleMessageObj;
    private Integer ccFlowStrategy;
    private Integer ccMerchCode;
    private String ccMerchDesc;
    private Integer ccBumpQty;
    private Integer ccIsQty;
    private Integer ccBuyQty;
    private Integer ccReplnQty;
    private Integer ccAdjReplnQty;
    private String altFineLineDesc;
    private String colorFamilyDesc;
    private String colorName;
    private Integer bumpPackCnt;
    private String altStyleDesc;
    private String altCcDesc;
    private String ccMessageObj;

    public BuyQntyResponseDTO(Long planId, Integer channelId, Integer lvl0Nbr, String lvl0Desc, Integer lvl1Nbr, String lvl1Desc, Integer lvl2Nbr,
                              String lvl2Desc, Integer lvl3Nbr, String lvl3Desc, Integer lvl4Nbr, String lvl4Desc, Integer finelineNbr,
                              String finelineDesc, Integer flowStrategyCode, Integer merchMethodCode, String merchMethodShortDesc, Integer bumpPackQty,
                              Integer initialSetQty, Integer buyQty, Integer replnQty, Integer adjReplnQty, String altFineLineDesc,String finelineMessageObj) {
        this.planId=planId;
        this.channelId=channelId;
        this.lvl0Nbr=lvl0Nbr;
        this.lvl0Desc=lvl0Desc;
        this.lvl1Nbr=lvl1Nbr;
        this.lvl1Desc=lvl1Desc;
        this.lvl2Nbr=lvl2Nbr;
        this.lvl2Desc=lvl2Desc;
        this.lvl3Nbr=lvl3Nbr;
        this.lvl3Desc=lvl3Desc;
        this.lvl4Nbr=lvl4Nbr;
        this.lvl4Desc=lvl4Desc;
        this.finelineNbr=finelineNbr;
        this.finelineDesc=finelineDesc;
        this.flowStrategyCode=flowStrategyCode;
        this.merchMethodCode=merchMethodCode;
        this.merchMethodShortDesc=merchMethodShortDesc;
        this.bumpPackQty=bumpPackQty;
        this.initialSetQty=initialSetQty;
        this.buyQty=buyQty;
        this.replnQty=replnQty;
        this.adjReplnQty=adjReplnQty;
        this.altFineLineDesc=altFineLineDesc;
        this.finelineMessageObj=finelineMessageObj;
    }

    public BuyQntyResponseDTO(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr,
                              String styleNbr, String ccId,Integer styleFlowStrategy, Integer styleMerchCode, String styleMerchDesc, Integer styleBumpQty,
                              Integer styleIsQty, Integer styleBuyQty, Integer styleReplnQty, Integer styleAdjReplnQty, String styleMessageObj,
                              Integer ccFlowStrategy, Integer ccMerchCode, String ccMerchDesc, Integer ccBumpQty,
                              Integer ccIsQty, Integer ccBuyQty, Integer ccReplnQty, Integer ccAdjReplnQty, Integer channelId, String colorFamilyDesc, String colorName, String altStyleDesc, String altCcDesc, String ccMessageObj) {
        this.planId=planId;
        this.lvl0Nbr=lvl0Nbr;
        this.lvl1Nbr=lvl1Nbr;
        this.lvl2Nbr=lvl2Nbr;
        this.lvl3Nbr=lvl3Nbr;
        this.lvl4Nbr=lvl4Nbr;
        this.finelineNbr=finelineNbr;
        this.styleNbr=styleNbr;
        this.ccId=ccId;
        this.styleFlowStrategy=styleFlowStrategy;
        this.styleMerchCode=styleMerchCode;
        this.styleMerchDesc=styleMerchDesc;
        this.styleBumpQty=styleBumpQty;
        this.styleIsQty=styleIsQty;
        this.styleBuyQty=styleBuyQty;
        this.styleReplnQty=styleReplnQty;
        this.styleAdjReplnQty=styleAdjReplnQty;
        this.styleMessageObj=styleMessageObj;
        this.ccFlowStrategy=ccFlowStrategy;
        this.ccMerchCode=ccMerchCode;
        this.ccMerchDesc=ccMerchDesc;
        this.ccBumpQty=ccBumpQty;
        this.ccIsQty=ccIsQty;
        this.ccBuyQty=ccBuyQty;
        this.ccReplnQty=ccReplnQty;
        this.ccAdjReplnQty=ccAdjReplnQty;
        this.channelId = channelId;
        this.colorFamilyDesc= colorFamilyDesc;
        this.colorName=colorName;
        this.altStyleDesc=altStyleDesc;
        this.altCcDesc=altCcDesc;
        this.ccMessageObj=ccMessageObj;
    }

    public BuyQntyResponseDTO(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                              Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr, String ccId, Integer ahsSizeId, String sizeDesc,
                              Integer flowStrategyCode, Integer merchMethodCode, String merchMethodShortDesc, Integer bumpPackQty,
                              Integer initialSetQty, Integer buyQty, Integer replnQty, Integer adjReplnQty, String sizeMessageObj) {
        this.planId=planId;
        this.lvl0Nbr=lvl0Nbr;
        this.lvl1Nbr=lvl1Nbr;
        this.lvl2Nbr=lvl2Nbr;
        this.lvl3Nbr=lvl3Nbr;
        this.lvl4Nbr=lvl4Nbr;
        this.finelineNbr=finelineNbr;
        this.styleNbr=styleNbr;
        this.ccId=ccId;
        this.ahsSizeId=ahsSizeId;
        this.sizeDesc=sizeDesc;
        this.flowStrategyCode=flowStrategyCode;
        this.merchMethodCode=merchMethodCode;
        this.merchMethodShortDesc=merchMethodShortDesc;
        this.bumpPackQty=bumpPackQty;
        this.initialSetQty=initialSetQty;
        this.buyQty=buyQty;
        this.replnQty=replnQty;
        this.adjReplnQty=adjReplnQty;
        this.sizeMessageObj=sizeMessageObj;
    }

    public BuyQntyResponseDTO(Long planId, Integer channelId, Integer lvl0Nbr, String lvl0Desc, Integer lvl1Nbr, String lvl1Desc, Integer lvl2Nbr,
                              String lvl2Desc, Integer lvl3Nbr, String lvl3Desc, Integer lvl4Nbr, String lvl4Desc, Integer finelineNbr,
                              String finelineDesc, Integer buyQty, Integer replnQty, Integer adjReplnQty, String altFineLineDesc, String finelineMessageObj) {
        this.planId=planId;
        this.channelId=channelId;
        this.lvl0Nbr=lvl0Nbr;
        this.lvl0Desc=lvl0Desc;
        this.lvl1Nbr=lvl1Nbr;
        this.lvl1Desc=lvl1Desc;
        this.lvl2Nbr=lvl2Nbr;
        this.lvl2Desc=lvl2Desc;
        this.lvl3Nbr=lvl3Nbr;
        this.lvl3Desc=lvl3Desc;
        this.lvl4Nbr=lvl4Nbr;
        this.lvl4Desc=lvl4Desc;
        this.finelineNbr=finelineNbr;
        this.finelineDesc=finelineDesc;
        this.buyQty=buyQty;
        this.replnQty=replnQty;
        this.adjReplnQty=adjReplnQty;
        this.altFineLineDesc=altFineLineDesc;
        this.finelineMessageObj=finelineMessageObj;
    }

    public BuyQntyResponseDTO(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr,
                              String styleNbr, String ccId,Integer styleBuyQty, Integer styleReplnQty, Integer styleAdjReplnQty, String styleMessageObj,
                              Integer ccBuyQty, Integer ccReplnQty, Integer ccAdjReplnQty, Integer channelId, String colorFamilyDesc, String colorName, String altStyleDesc, String altCcDesc, String ccMessageObj) {
        this.planId=planId;
        this.lvl0Nbr=lvl0Nbr;
        this.lvl1Nbr=lvl1Nbr;
        this.lvl2Nbr=lvl2Nbr;
        this.lvl3Nbr=lvl3Nbr;
        this.lvl4Nbr=lvl4Nbr;
        this.finelineNbr=finelineNbr;
        this.styleNbr=styleNbr;
        this.ccId=ccId;
        this.styleBuyQty=styleBuyQty;
        this.styleReplnQty=styleReplnQty;
        this.styleAdjReplnQty=styleAdjReplnQty;
        this.styleMessageObj=styleMessageObj;
        this.ccBuyQty=ccBuyQty;
        this.ccReplnQty=ccReplnQty;
        this.ccAdjReplnQty=ccAdjReplnQty;
        this.channelId = channelId;
        this.colorFamilyDesc = colorFamilyDesc;
        this.colorName=colorName;
        this.altStyleDesc=altStyleDesc;
        this.altCcDesc=altCcDesc;
        this.ccMessageObj=ccMessageObj;
    }

    public BuyQntyResponseDTO(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                              Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr, String ccId, Integer ahsSizeId, String sizeDesc,
                              Integer buyQty, Integer replnQty, Integer adjReplnQty, String sizeMessageObj) {
        this.planId=planId;
        this.lvl0Nbr=lvl0Nbr;
        this.lvl1Nbr=lvl1Nbr;
        this.lvl2Nbr=lvl2Nbr;
        this.lvl3Nbr=lvl3Nbr;
        this.lvl4Nbr=lvl4Nbr;
        this.finelineNbr=finelineNbr;
        this.styleNbr=styleNbr;
        this.ccId=ccId;
        this.ahsSizeId=ahsSizeId;
        this.sizeDesc=sizeDesc;
        this.buyQty=buyQty;
        this.replnQty=replnQty;
        this.adjReplnQty=adjReplnQty;
        this.sizeMessageObj=sizeMessageObj;
    }

    public BuyQntyResponseDTO(Integer finelineNbr, Integer bumpPackCnt) {
        this.finelineNbr=finelineNbr;
        this.bumpPackCnt=bumpPackCnt;
    }
}
