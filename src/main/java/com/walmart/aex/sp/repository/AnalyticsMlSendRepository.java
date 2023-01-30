package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.AnalyticsMlSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface AnalyticsMlSendRepository extends JpaRepository<AnalyticsMlSend, BigInteger> {

    Optional<AnalyticsMlSend> findByPlanIdAndFinelineNbrAndRunStatusCode(@Param("planId") Long planId,
                                                               @Param("finelineNbr") Integer finelineNbr,
                                                               @Param("runStatusCode") Integer runStatusCode);


}
