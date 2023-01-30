package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSizeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

public interface SpCustomerChoiceChannelFixtureSizeRepository extends JpaRepository<SpCustomerChoiceChannelFixtureSize, SpCustomerChoiceChannelFixtureSizeId> {
    @Query(value="select new com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO(msp.merchCatPlanId.planId, " +
            "msp.merchCatPlanId.lvl0Nbr, " +
            "msp.merchCatPlanId.lvl1Nbr, " +
            "msp.merchCatPlanId.lvl2Nbr, " +
            "msp.merchCatPlanId.lvl3Nbr, " +
            "ssp.subCatPlanId.lvl4Nbr, " +
            "fp.finelinePlanId.finelineNbr, " +
            "sp.stylePlanId.styleNbr, " +
            "ccp.custChoicePlanId.ccId, " +
            "sccfs.spCustomerChoiceChannelFixtureSizeId.ahsSizeId, " +
            "sccfs.ahsSizeDesc, " +
            "sccfs.flowStrategyCode, " +
            "sccfs.merchMethodCode, " +
            "sccfs.merchMethodShortDesc, " +
            "sccfs.bumpPackQty, " +
            "sccfs.initialSetQty, " +
            "sccfs.buyQty, " +
            "sccfs.replnQty, " +
            "sccfs.adjReplnQty " +
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
            "SpCustomerChoiceChannelFixtureSize sccfs " +
            "ON " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.styleNbr " +
            "AND ccp.custChoicePlanId.ccId = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.customerChoice " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId " +
            "where ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId in (:channelId,3) and msp.merchCatPlanId.planId = :planId and " +
            "ccp.custChoicePlanId.ccId = :ccId and " +
            "(sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId is NULL or sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = :channelId) ")
    List<BuyQntyResponseDTO> getSizeBuyQntyByPlanChannelCc(@Param("planId") Long planId, @Param("channelId") Integer channelId,
                                                             @Param("ccId") String ccId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from dbo.sp_cc_chan_fixtr_size where plan_id = :planId and channel_id = :channelId and fineline_nbr in (:finelineNbrs)", nativeQuery = true)
    void deleteByPlanIdFinelineIdChannelId(@Param("planId") Long planId, @Param("channelId") Integer channelId, @Param("finelineNbrs") Set<Integer> finelineNbrs);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "select * from dbo.sp_cc_chan_fixtr_size where plan_id = :planId and channel_id = 1 and fineline_nbr=:fineline", nativeQuery = true)
    List<SpCustomerChoiceChannelFixtureSize> getSpCcChanFixtrDataByPlanFineline(@Param("planId")Long planId, @Param("fineline") Integer fineline);
}
