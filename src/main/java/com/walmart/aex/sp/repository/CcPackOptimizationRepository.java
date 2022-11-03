package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CcPackOptimizationRepository extends JpaRepository<CcPackOptimization, CcPackOptimizationID> {
    @Query(value = "SELECT ccPackOpt from CcPackOptimization AS ccPackOpt where \n" +
    "ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId =?1 \n" +
    "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 =?2 \n" +
    "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 =?3 \n" +
    "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 =?4 \n" +
    "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 =?5 \n" +
    "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 =?6 \n" +
    "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr =?7 \n" +
    "AND ccPackOpt.ccPackOptimizationId.stylePackOptimizationID.styleNbr IN (?8) \n" +
    "AND ccPackOpt.ccPackOptimizationId.customerChoice IN (?9)")
    List<CcPackOptimization> findCCPackOptimizationList(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, List<String> styles, List<String> customerChoices);
}
