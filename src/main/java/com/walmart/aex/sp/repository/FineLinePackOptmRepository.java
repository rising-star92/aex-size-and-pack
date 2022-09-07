package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.fineLinePackOptimization;
import com.walmart.aex.sp.entity.fineLinePackOptimizationID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineLinePackOptmRepository extends JpaRepository<fineLinePackOptimization, fineLinePackOptimizationID> {
    void deleteFineLinePackOptimizationByfinelinePackOptId_SubCatgPackOptimizationID_MerchantPackOptimizationID_planIdAndFinelinePackOptId_SubCatgPackOptimizationID_MerchantPackOptimizationID_repTLvl3AndFinelinePackOptId_SubCatgPackOptimizationID_repTLvl4AndFinelinePackOptId_finelineNbr(
            Long planId, Integer repTLvl3, Integer repTLvl4, Integer finelineNbr);

}
