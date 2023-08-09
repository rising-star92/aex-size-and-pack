package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AnalyticsMlSendRepository extends JpaRepository<AnalyticsMlSend, BigInteger> {

    Optional<AnalyticsMlSend> findByPlanIdAndFinelineNbrAndRunStatusCode(@Param("planId") Long planId,
                                                               @Param("finelineNbr") Integer finelineNbr,
                                                               @Param("runStatusCode") Integer runStatusCode);
    @Query(value="select ams.analyticsMlChildSend from AnalyticsMlSend ams where ams.planId=:planId and ams.finelineNbr=:finelineNbr " +
            "and ams.analyticsSendId = (select max(ams.analyticsSendId) " +
            "from AnalyticsMlSend ams where ams.planId=:planId and ams.finelineNbr=:finelineNbr )")
    Optional<Set<AnalyticsMlChildSend>> findAnalyticsMlSendByPlanIdAndfinelineNbr(@Param("planId")  Long planId, @Param("finelineNbr")  Integer finelineNbr);
}
