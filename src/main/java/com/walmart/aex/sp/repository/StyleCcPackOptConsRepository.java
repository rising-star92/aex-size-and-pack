package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.packoptimization.PackOptConstraintResponseDTO;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StyleCcPackOptConsRepository extends JpaRepository<CcPackOptimization, CcPackOptimizationID> {

    @Query(value = "SELECT \n" +
            " new com.walmart.aex.sp.dto.packoptimization.PackOptConstraintResponseDTO ( "+
            "merchCatPlan.merchCatPlanId.planId,\n" +
            "merchCatPlan.merchCatPlanId.channelId ,\n" +
            "merchCatPlan.merchCatPlanId.lvl0Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl1Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl2Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl3Nbr ,\n" +
            "subCatPlan.lvl4Nbr,\n" +
            "fp.finelinePlanId.finelineNbr,\n" +
            "fp.finelineDesc,\n" +
            "fp.altFinelineName,\n" +
            "sp.stylePlanId.styleNbr, " +
            "spk.vendorName as styleSupplierName , " +
            "spk.vendorNbr6 as styleVendorNumber6 , " +
            "spk.gsmSupplierId as styleGsmSupplierNumber , " +
            "spk.vendorNbr9 as styleVendorNumber9 , " +
            "spk.factoryId as styleFactoryIds , " +
            "spk.originCountryName as styleCountryOfOrigin , " +
            "spk.portOfOriginName as stylePortOfOrigin , " +
            "spk.singlePackInd as styleSinglePackIndicator , " +
            "spk.colorCombination as styleColorCombination , " +
            "spk.maxUnitsPerPack as styleMaxUnitsPerPack , " +
            "spk.maxNbrOfPacks as styleMaxPacks , " +
            "ccp.custChoicePlanId.ccId , " +
            "cpk.vendorName as ccSupplierName , " +
            "cpk.vendorNbr6 as ccVendorNumber6 , " +
            "cpk.gsmSupplierId as ccGsmSupplierNumber , " +
            "cpk.vendorNbr9 as ccVendorNumber9 , " +
            "cpk.factoryId as ccFactoryIds , " +
            "cpk.originCountryName as ccCountryOfOrigin , " +
            "cpk.portOfOriginName as ccPortOfOrigin , " +
            "cpk.singlePackInd as ccSinglePackIndicator , " +
            "cpk.colorCombination as ccColorCombination , " +
            "cpk.maxUnitsPerPack as ccMaxUnitsPerPack , " +
            "cpk.maxNbrOfPacks as ccMaxPacks , " +
            "merchCatPlan.lvl0Desc,\n" +
            "merchCatPlan.lvl1Desc,\n" +
            "merchCatPlan.lvl2Desc,\n" +
            "merchCatPlan.lvl3Desc,\n" +
            "subCatPlan.lvl4Desc ) " +
            "FROM MerchCatPlan  merchCatPlan \n" +
            "inner JOIN SubCatPlan subCatPlan ON merchCatPlan.merchCatPlanId.lvl3Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.lvl2Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl2Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.lvl1Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl1Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.lvl0Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl0Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.channelId = subCatPlan.merchCatPlan.merchCatPlanId.channelId \n" +
            "AND merchCatPlan.merchCatPlanId.planId = subCatPlan.merchCatPlan.merchCatPlanId.planId \n" +

            "inner JOIN  FinelinePlan  fp ON fp.lvl3Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr \n" +
            "AND subCatPlan.lvl4Nbr = fp.finelinePlanId.subCatPlanId.lvl4Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl2Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl1Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl0Nbr = fp.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.channelId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.channelId \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.planId = fp.finelinePlanId.subCatPlanId.merchCatPlanId.planId \n" +

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
            "StylePackOptimization spk " +
            "ON " +
            "sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = spk.stylePackoptimizationId.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = spk.stylePackoptimizationId.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = spk.stylePackoptimizationId.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = spk.stylePackoptimizationId.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = spk.stylePackoptimizationId.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = spk.stylePackoptimizationId.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 " +
            "AND sp.stylePlanId.finelinePlanId.finelineNbr = spk.stylePackoptimizationId.finelinePackOptimizationID.finelineNbr " +
            "AND sp.stylePlanId.styleNbr =spk.stylePackoptimizationId.styleNbr " +
            "AND sp.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = spk.channelText.channelId " +

            "left join " +
            "CcPackOptimization cpk " +
            "ON " +
            "ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.planId = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.lvl3Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.lvl4Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.finelineNbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr " +
            "AND ccp.custChoicePlanId.stylePlanId.styleNbr = cpk.ccPackOptimizationId.stylePackOptimizationID.styleNbr " +
            "AND ccp.custChoicePlanId.ccId = cpk.ccPackOptimizationId.customerChoice " +
            "AND ccp.custChoicePlanId.stylePlanId.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = cpk.channelText.channelId " +

            " WHERE  merchCatPlan.merchCatPlanId.channelId = :channelId and merchCatPlan.merchCatPlanId.planId = :planId and fp.finelinePlanId.finelineNbr = :finelineNbr")
    List<PackOptConstraintResponseDTO> findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(Long planId, Integer channelId, Integer finelineNbr);

}
