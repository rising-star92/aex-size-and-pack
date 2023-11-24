package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.packoptimization.packDescription.PackDescCustChoiceDTO;
import com.walmart.aex.sp.entity.CustChoicePlan;
import com.walmart.aex.sp.entity.CustChoicePlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerChoiceRepository extends JpaRepository<CustChoicePlan, CustChoicePlanId> {

    @Query(value="select new com.walmart.aex.sp.dto.packoptimization.packDescription.PackDescCustChoiceDTO(" +
            "ccp.custChoicePlanId.ccId, " +
            "ccp.colorName, " +
            "COALESCE(fp.altFinelineName , fp.finelineDesc) AS altFinelineDesc) " +
            "from CustChoicePlan ccp " +
            "inner join " +
            "FinelinePlan fp " +
            "ON " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = fp.finelinePlanId.subCatPlanId.lvl4Nbr " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = fp.finelinePlanId.finelineNbr " +
            "WHERE ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = :planId and ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = :finelineNbr and fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = :channelId")
    List<PackDescCustChoiceDTO> getCustomerChoicesByFinelineAndPlanId(@Param("planId") Long planId, @Param("finelineNbr") Integer finelineNbr, @Param("channelId") Integer channelId);
}
