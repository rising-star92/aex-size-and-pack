package com.walmart.aex.sp.repository;

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

    @Query(value="select sum(sccf.bumpPackCnt + 1)" +
            "from SpCustomerChoiceChannelFixture sccf " +
            "left join " + "CcPackOptimization ccp " + "ON " +
            "sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.styleNbr= ccp.ccPackOptimizationId.stylePackOptimizationID.styleNbr " +
            "AND sccf.spCustomerChoiceChannelFixtureId.customerChoice = ccp.ccPackOptimizationId.customerChoice " +
            "AND sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = ccp.channelText.channelId " +

            "where sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId = :planId and " +
            "sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr =:finelineNbr and " +
            "sccf.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId=1")
    Integer getTotalCCsAcrossAllSetsByPlanIdFineline(@Param("planId") Long planId, @Param("finelineNbr")Integer finelineNbr);

}
