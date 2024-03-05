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

    @Query(value = "select sum((a.mm_cnt * a.bump_pack_cnt) + (a.mm_cnt * a.initial_set_cnt)) from \n" +
            "(select \n" +
            "sccf.customer_choice, \n" +
            "count(sccf.merch_method_code) mm_cnt, \n" +
            "sccf.bump_pack_cnt,\n" +
            "1 as initial_set_cnt\n" +
            "from [dbo].[sp_cc_chan_fixtr] sccf  \n" +
            "where sccf.plan_id =:planId and  \n" +
            "sccf.fineline_nbr =:finelineNbr and  \n" +
            "sccf.channel_id=1 and \n" +
            "(sccf.bump_pack_qty + sccf.initial_set_qty > 0)\n" +
            "group by sccf.customer_choice , sccf.bump_pack_cnt ) a", nativeQuery = true)
    Integer getTotalCCsAcrossAllSetsByPlanIdFineline(@Param("planId") Long planId, @Param("finelineNbr")Integer finelineNbr);

    @Query(value = "select new com.walmart.aex.sp.dto.buyquantity.FactoryDTO(ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr," +
            "ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.styleNbr," +
            "ccPackOpt.ccPackOptimizationId.customerChoice," +
            "ccPackOpt.overrideFactoryId," +
            "ccPackOpt.overrideFactoryName) from CcPackOptimization ccPackOpt " +
            " where ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId =?1 " +
            " and (?2 is null OR  ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr = ?2 ) " +
            " and ccPackOpt.overrideFactoryId is not null")
    List<FactoryDTO> getFactoriesByPlanId(Long planId, Integer finelineNbr);
}
