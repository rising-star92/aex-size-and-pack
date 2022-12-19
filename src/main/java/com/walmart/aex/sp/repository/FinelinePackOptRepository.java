package com.walmart.aex.sp.repository;


import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.entity.FinelinePlan;
import com.walmart.aex.sp.entity.FinelinePlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FinelinePackOptRepository
        extends JpaRepository<FinelinePlan, FinelinePlanId> {
    @Query(value = "SELECT \n" +
            "distinct new com.walmart.aex.sp.dto.mapper.FineLineMapperDto ( "+
            "merchCatPlan.merchCatPlanId.planId,\n" +
            "merchCatPlan.merchCatPlanId.channelId ,\n" +
            "merchCatPlan.merchCatPlanId.lvl0Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl1Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl2Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl3Nbr ,\n" +
            "subCatPlan.lvl4Nbr,\n" +
            "finePlan.finelinePlanId.finelineNbr,\n" +
            "finePlan.finelineDesc,\n" +
            "finePlan.altFinelineName,\n" +
            "merchCatPlan.lvl0Desc,\n" +
            "merchCatPlan.lvl1Desc,\n" +
            "merchCatPlan.lvl2Desc,\n" +
            "merchCatPlan.lvl3Desc,\n" +
            "subCatPlan.lvl4Desc,\n" +
            "merchPackOpt.vendorName as merchSupplierName,\n" +
            "merchPackOpt.vendorNbr6 as merchVendorNumber6,\n" +
            "merchPackOpt.gsmSupplierId as merchGsmSupplierNumber,\n" +
            "merchPackOpt.vendorNbr9 as merchVendorNumber9,\n" +
            "merchPackOpt.maxUnitsPerPack,\n" +
            "merchPackOpt.maxNbrOfPacks,\n" +
            "merchPackOpt.factoryId,\n" +
            "merchPackOpt.originCountryName,\n" +
            "merchPackOpt.portOfOriginName,\n" +
            "merchPackOpt.singlePackInd,\n" +
            "merchPackOpt.colorCombination,\n" +
            "subCatPackOpt.vendorName as subCatSupplierName,\n" +
            "subCatPackOpt.vendorNbr6 as subCatVendorNumber6,\n" +
            "subCatPackOpt.gsmSupplierId as subCatGsmSupplierNumber,\n" +
            "subCatPackOpt.vendorNbr9 as subCatVendorNumber9,\n" +
            "subCatPackOpt.maxUnitsPerPack,\n" +
            "subCatPackOpt.maxNbrOfPacks,\n" +
            "subCatPackOpt.factoryId,\n" +
            "subCatPackOpt.originCountryName,\n" +
            "subCatPackOpt.portOfOriginName,\n" +
            "subCatPackOpt.singlePackInd,\n" +
            "subCatPackOpt.colorCombination,\n" +
            "fineLinePackOpt.vendorName as fineLineSupplierName,\n" +
            "fineLinePackOpt.vendorNbr6 as fineLineVendorNumber6,\n" +
            "fineLinePackOpt.gsmSupplierId as fineLineGsmSupplierNumber,\n" +
            "fineLinePackOpt.vendorNbr9 as fineLineVendorNumber9,\n" +
            "fineLinePackOpt.maxUnitsPerPack,\n" +
            "fineLinePackOpt.maxNbrOfPacks,\n" +
            "fineLinePackOpt.factoryId,\n" +
            "fineLinePackOpt.originCountryName,\n" +
            "fineLinePackOpt.portOfOriginName,\n" +
            "fineLinePackOpt.singlePackInd,\n" +
            "fineLinePackOpt.colorCombination,\n" +
            "cpk.ccPackOptimizationId.customerChoice, \n" +
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
            "cpk.factoryName as ccFactoryName , " +
            "analytic.startTs,\n" +
            "analytic.endTs,\n" +
            "r.runStatusCode,\n" +
            "r.runStatusDesc,\n" +
            "analytic.firstName, \n" +
            "analytic.lastName,\n" +
            "analytic.returnMessage ) \n"+
            "FROM MerchCatPlan  merchCatPlan \n" +
            "inner JOIN SubCatPlan subCatPlan ON merchCatPlan.merchCatPlanId.lvl3Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.lvl2Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl2Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.lvl1Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl1Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.lvl0Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl0Nbr \n" +
            "AND merchCatPlan.merchCatPlanId.channelId = subCatPlan.merchCatPlan.merchCatPlanId.channelId \n" +
            "AND merchCatPlan.merchCatPlanId.planId = subCatPlan.merchCatPlan.merchCatPlanId.planId \n" +

            "inner JOIN  FinelinePlan  finePlan ON finePlan.lvl3Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr \n" +
            "AND subCatPlan.lvl4Nbr = finePlan.finelinePlanId.subCatPlanId.lvl4Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl2Nbr = finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl1Nbr = finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl0Nbr = finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.channelId = finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.channelId \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.planId = finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.planId \n" +

            "inner JOIN SpFineLineChannelFixture spFlChFix ON finePlan.finelinePlanId.finelineNbr = spFlChFix.spFineLineChannelFixtureId.fineLineNbr \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.lvl4Nbr = spFlChFix.spFineLineChannelFixtureId.lvl4Nbr \n" +
            "AND finePlan.lvl3Nbr = spFlChFix.spFineLineChannelFixtureId.lvl3Nbr \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = spFlChFix.spFineLineChannelFixtureId.lvl2Nbr \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = spFlChFix.spFineLineChannelFixtureId.lvl1Nbr \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = spFlChFix.spFineLineChannelFixtureId.lvl0Nbr \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.planId = spFlChFix.spFineLineChannelFixtureId.planId \n" +
            "AND (spFlChFix.bumpPackQty + spFlChFix.initialSetQty > 0 OR spFlChFix.buyQty > 0) \n" +

            "left JOIN MerchantPackOptimization merchPackOpt ON merchCatPlan.merchCatPlanId.lvl3Nbr = merchPackOpt.merchantPackOptimizationID.repTLvl3 \n" +
            "AND merchCatPlan.merchCatPlanId.lvl2Nbr = merchPackOpt.merchantPackOptimizationID.repTLvl2 \n" +
            "AND merchCatPlan.merchCatPlanId.lvl1Nbr = merchPackOpt.merchantPackOptimizationID.repTLvl1 \n" +
            "AND merchCatPlan.merchCatPlanId.lvl0Nbr = merchPackOpt.merchantPackOptimizationID.repTLvl0 \n" +
            "AND merchCatPlan.merchCatPlanId.channelId = merchPackOpt.channelText.channelId \n" +
            "AND merchCatPlan.merchCatPlanId.planId = merchPackOpt.merchantPackOptimizationID.planId \n" +

            "left JOIN SubCatgPackOptimization subCatPackOpt ON subCatPlan.lvl4Nbr = subCatPackOpt.subCatgPackOptimizationID.repTLvl4 \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr = subCatPackOpt.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl2Nbr = subCatPackOpt.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl1Nbr = subCatPackOpt.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl0Nbr = subCatPackOpt.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.channelId = subCatPackOpt.channelText.channelId \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.planId = subCatPackOpt.subCatgPackOptimizationID.merchantPackOptimizationID.planId\n" +

            "left JOIN  FineLinePackOptimization  fineLinePackOpt ON finePlan.finelinePlanId.finelineNbr = fineLinePackOpt.finelinePackOptId.finelineNbr \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.lvl4Nbr =  fineLinePackOpt.finelinePackOptId.subCatgPackOptimizationID.repTLvl4\n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr = fineLinePackOpt.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = fineLinePackOpt.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = fineLinePackOpt.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = fineLinePackOpt.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = fineLinePackOpt.channelText.channelId \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.planId = fineLinePackOpt.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.planId \n" +

            "left join CcPackOptimization cpk ON finePlan.finelinePlanId.finelineNbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.planId = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3\n " +
            "AND finePlan.finelinePlanId.subCatPlanId.lvl4Nbr = cpk.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 \n" +
            "AND finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.channelId = cpk.channelText.channelId " +

            "left JOIN AnalyticsMlSend analytic " +
            " ON analytic.planId = finePlan.finelinePlanId.subCatPlanId.merchCatPlanId.planId \n" +
            " AND analytic.finelineNbr = finePlan.finelinePlanId.finelineNbr \n" +
            " left join RunStatusText r ON r.runStatusCode = analytic.runStatusCode.runStatusCode \n" +
            " WHERE  merchCatPlan.merchCatPlanId.channelId = ?2 and merchCatPlan.merchCatPlanId.planId =?1")
    List<FineLineMapperDto> findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(Long planId, Integer channelId);

}
