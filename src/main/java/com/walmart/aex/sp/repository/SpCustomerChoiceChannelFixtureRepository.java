package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpCustomerChoiceChannelFixtureRepository extends JpaRepository<SpCustomerChoiceChannelFixture, SpCustomerChoiceChannelFixtureId> {

    @Query(value="select new com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO(msp.merchCatPlanId.planId, " +
            "msp.merchCatPlanId.lvl0Nbr, " +
            "msp.merchCatPlanId.lvl1Nbr, " +
            "msp.merchCatPlanId.lvl2Nbr, " +
            "msp.merchCatPlanId.lvl3Nbr, " +
            "ssp.subCatPlanId.lvl4Nbr, " +
            "fp.finelinePlanId.finelineNbr, " +
            "sp.stylePlanId.styleNbr, " +
            "ccp.custChoicePlanId.ccId, " +
            "sscf.flowStrategyCode as styleFlowStrategy, " +
            "sscf.merchMethodCode as styleMerchCode, " +
            "sscf.merchMethodShortDesc as styleMerchDesc, " +
            "sscf.bumpPackQty as styleBumpQty, " +
            "sscf.initialSetQty as styleIsQty, " +
            "sscf.buyQty as styleBuyQty, " +
            "sscf.replnQty as styleReplnQty, " +
            "sscf.adjReplnQty as styleAdjReplnQty, " +
            "sccf.flowStrategyCode as ccFlowStrategy, " +
            "sccf.merchMethodCode as ccMerchCode, " +
            "sccf.merchMethodShortDesc as ccMerchDesc, " +
            "sccf.bumpPackQty as ccBumpQty, " +
            "sccf.initialSetQty as ccIsQty, " +
            "sccf.buyQty as ccBuyQty, " +
            "sccf.replnQty as ccReplnQty, " +
            "sccf.adjReplnQty as ccAdjReplnQty " +
            ") " +
            "from MerchCatPlan msp " +
            "inner join " +
            "SubCatPlan ssp " +
            "ON " +
            "msp.merchCatPlanId.planId = ssp.subCatPlanId.merchCatPlanId.planId " +
            "AND msp.merchCatPlanId.lvl0Nbr = ssp.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND msp.merchCatPlanId.lvl1Nbr = ssp.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND msp.merchCatPlanId.lvl2Nbr = ssp.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND msp.merchCatPlanId.lvl3Nbr = ssp.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND msp.merchCatPlanId.channelId = ssp.subCatPlanId.merchCatPlanId.channelId " +
            "inner join " +
            "FinelinePlan fp " +
            "ON " +
            "ssp.subCatPlanId.merchCatPlanId.planId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl0Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl1Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl2Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.lvl3Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND ssp.subCatPlanId.lvl4Nbr = fp.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND ssp.subCatPlanId.merchCatPlanId.channelId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "inner join " +
            "StylePlan sp " +
            "ON " +
            "fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND fp.finelinePlanId.subCatPlanId.lvl4Nbr = sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND fp.finelinePlanId.finelineNbr = sp.stylePlanId.finelinePlanId.finelineNbr " +
            "AND fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "inner join " +
            "CustChoicePlan ccp " +
            "ON " +
            "sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND sp.stylePlanId.finelinePlanId.finelineNbr = ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr " +
            "AND sp.stylePlanId.styleNbr = ccp.custChoicePlanId.stylePlanId.styleNbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "left join " +
            "SpStyleChannelFixture sscf " +
            "ON " +
            "sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr " +
            "AND sp.stylePlanId.finelinePlanId.finelineNbr = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr " +
            "AND sp.stylePlanId.styleNbr = sscf.spStyleChannelFixtureId.styleNbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId " +
            "left join " +
            "SpCustomerChoiceChannelFixture sccf " +
            "ON " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.styleNbr " +
            "AND ccp.custChoicePlanId.ccId = sccf.spCustomerChoiceChannelFixtureId.customerChoice " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId " +
            "where sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId) and " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId) and msp.merchCatPlanId.planId = :planId and " +
            "fp.finelinePlanId.finelineNbr = :finelineNbr and " +
            "(sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId is NULL or sscf.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = :channelId) and " +
            "(sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId is NULL or sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = :channelId) ")
    List<BuyQntyResponseDTO> getBuyQntyByPlanChannelFineline(@Param("planId") Long planId, @Param("channelId") Integer channelId,
                                                             @Param("finelineNbr") Integer finelineNbr);
}
