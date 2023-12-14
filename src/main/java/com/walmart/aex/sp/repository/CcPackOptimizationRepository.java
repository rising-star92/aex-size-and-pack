package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.buyquantity.FactoryDTO;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CcPackOptimizationRepository extends JpaRepository<CcPackOptimization, CcPackOptimizationID> {
    @Query(value = "SELECT ccPackOpt from CcPackOptimization AS ccPackOpt where \n" +
            "ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId =?1 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 =?2 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 =?3 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 =?4 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 =?5 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.channelId = 1 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 =?6 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr =?7 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.styleNbr IN (?8) \n" +
            "AND ccPackOpt.ccPackOptimizationId.customerChoice IN (?9)")
    List<CcPackOptimization> findCCPackOptimizationList(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, List<String> styles, List<String> customerChoices);

    @Query(value = "SELECT ccPackOpt from CcPackOptimization AS ccPackOpt where \n" +
            "ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId =?1 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 =?2 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 =?3 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 =?4 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 =?5 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.channelId = 1 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 =?6 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr =?7 \n" +
            "AND ccPackOpt.colorCombination IN (?8)")
    List<CcPackOptimization> findCCPackOptimizationByColorCombinationList(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, List<String> colorCombinationIds);

    @Query(value = "SELECT ccPackOpt.colorCombination from CcPackOptimization AS ccPackOpt where \n" +
            "ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId =?1 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 =?2 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 =?3 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 =?4 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 =?5 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.channelId = 1 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 =?6 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr =?7 \n" +
            "AND ccPackOpt.colorCombination IS NOT NULL")
    Set<String> findCCPackOptimizationColorCombinationList(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr);

    @Query(value = "SELECT ccPackOpt from CcPackOptimization AS ccPackOpt where \n" +
            "ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId =?1 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 =?2 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 =?3 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 =?4 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 =?5 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 =?6 \n" +
            "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr =?7 ")
    List<CcPackOptimization> findCCPackOptimizationByFineLineNbr(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr);

    @Query(value = "select sum(t.bump_pack_cnt + 1) from (" +
            "select distinct(sccf.customer_choice) , sccf.bump_pack_cnt  " +
            "from [dbo].[sp_cc_chan_fixtr] sccf " +
            "left join [dbo].[fineline_pkopt_cons] fp ON " +
            "sccf.plan_id = fp.plan_id " +
            "AND sccf.rpt_lvl_0_nbr = fp.rpt_lvl_0_nbr " +
            "AND sccf.rpt_lvl_1_nbr = fp.rpt_lvl_1_nbr " +
            "AND sccf.rpt_lvl_2_nbr  = fp.rpt_lvl_2_nbr " +
            "AND sccf.rpt_lvl_3_nbr = fp.rpt_lvl_3_nbr " +
            "AND sccf.rpt_lvl_4_nbr = fp.rpt_lvl_4_nbr " +
            "AND sccf.fineline_nbr = fp.fineline_nbr " +
            "AND  sccf.channel_id = fp.channel_id " +
            "inner join  [dbo].[cc_pkopt_cons] ccp  ON " +
            "sccf.plan_id = ccp.plan_id " +
            "AND sccf.rpt_lvl_0_nbr = ccp.rpt_lvl_0_nbr " +
            "AND sccf.rpt_lvl_1_nbr = ccp.rpt_lvl_1_nbr " +
            "AND sccf.rpt_lvl_2_nbr = ccp.rpt_lvl_2_nbr " +
            "AND sccf.rpt_lvl_3_nbr = ccp.rpt_lvl_3_nbr " +
            "AND sccf.rpt_lvl_4_nbr = ccp.rpt_lvl_4_nbr " +
            "AND sccf.fineline_nbr = ccp.fineline_nbr " +
            "AND sccf.style_nbr= ccp.style_nbr " +
            "AND sccf.customer_choice = ccp.customer_choice " +
            "AND sccf.channel_id = ccp.channel_id " +
            "where sccf.plan_id =:planId and " +
            "sccf.fineline_nbr =:finelineNbr and " +
            "sccf.channel_id=1 ) as t", nativeQuery = true)
    Integer getTotalCCsAcrossAllSetsByPlanIdFineline(@Param("planId") Long planId, @Param("finelineNbr")Integer finelineNbr);

    @Query(value = "select new com.walmart.aex.sp.dto.buyquantity.FactoryDTO(ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr," +
            "ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.styleNbr," +
            "ccPackOpt.ccPackOptimizationId.customerChoice," +
            "ccPackOpt.overrideFactoryId," +
            "ccPackOpt.overrideFactoryName) from CcPackOptimization ccPackOpt " +
            " where ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId =?1 " +
            " and (?2 is null OR  ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr = ?2 ) " +
            " and ccPackOpt.overrideFactoryId is not null")
    List<FactoryDTO> getFactoriesByPlanId(@Param("planId") Long planId, @Param("finelineNbr") Integer finelineNbr);
}
