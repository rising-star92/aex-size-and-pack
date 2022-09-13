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
            "new com.walmart.aex.sp.dto.mapper.FineLineMapperDto ( "+
            "merchCatPlan.merchCatPlanId.planId,\n" +
            "merchCatPlan.merchCatPlanId.channelId ,\n" +
            "merchCatPlan.merchCatPlanId.lvl0Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl1Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl2Nbr ,\n" +
            "merchCatPlan.merchCatPlanId.lvl3Nbr ,\n" +
            "subCatPlan.lvl4Nbr,\n" +
            "fineplan.finelinePlanId.finelineNbr,\n" +
            "fineplan.finelineDesc,\n" +
            "fineplan.altFinelineName,\n" +
            "merchCatPlan.lvl0Desc,\n" +
            "merchCatPlan.lvl1Desc,\n" +
            "merchCatPlan.lvl2Desc,\n" +
            "merchCatPlan.lvl3Desc,\n" +
            "subCatPlan.lvl4Desc,\n" +
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

            "inner JOIN  FinelinePlan  fineplan ON fineplan.lvl3Nbr = subCatPlan.merchCatPlan.merchCatPlanId.lvl3Nbr \n" +
            "AND subCatPlan.lvl4Nbr = fineplan.finelinePlanId.subCatPlanId.lvl4Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl2Nbr = fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl1Nbr = fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.lvl0Nbr = fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.channelId = fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.channelId \n" +
            "AND subCatPlan.merchCatPlan.merchCatPlanId.planId = fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.planId \n" +

            "inner JOIN SpFineLineChannelFixture spFlChFix ON fineplan.finelinePlanId.finelineNbr = spFlChFix.spFineLineChannelFixtureId.fineLineNbr \n" +
            "AND fineplan.finelinePlanId.subCatPlanId.lvl4Nbr = spFlChFix.spFineLineChannelFixtureId.lvl4Nbr \n" +
            "AND fineplan.lvl3Nbr = spFlChFix.spFineLineChannelFixtureId.lvl3Nbr \n" +
            "AND fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl2Nbr = spFlChFix.spFineLineChannelFixtureId.lvl2Nbr \n" +
            "AND fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl1Nbr = spFlChFix.spFineLineChannelFixtureId.lvl1Nbr \n" +
            "AND fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.lvl0Nbr = spFlChFix.spFineLineChannelFixtureId.lvl0Nbr \n" +
            "AND fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.planId = spFlChFix.spFineLineChannelFixtureId.planId \n" +
            "AND (spFlChFix.bumpPackQty + spFlChFix.initialSetQty > 0 OR spFlChFix.buyQty > 0) \n" +

            "left JOIN AnalyticsMlSend analytic " +
            " ON analytic.planId = fineplan.finelinePlanId.subCatPlanId.merchCatPlanId.planId \n" +
            " AND analytic.finelineNbr = fineplan.finelinePlanId.finelineNbr \n" +
            " left join RunStatusText r ON r.runStatusCode = analytic.runStatusCode.runStatusCode " +
            " WHERE  merchCatPlan.merchCatPlanId.channelId = ?2 and merchCatPlan.merchCatPlanId.planId =?1")
    List<FineLineMapperDto> findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(Long planId, Integer channelId);




}