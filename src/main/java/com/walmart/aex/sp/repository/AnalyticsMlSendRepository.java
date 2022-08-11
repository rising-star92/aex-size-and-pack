package com.walmart.aex.sp.repository;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;

@Repository
public interface AnalyticsMlSendRepository extends JpaRepository<AnalyticsMlSend, BigInteger> {
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update analytics_ml_send set run_status_code = :status where plan_id = :planId and fineline_nbr=:finelineNbr and run_status_code in (0,3)", nativeQuery = true)
    void updateStatus(@Param("planId") Long planId, @Param("finelineNbr") Integer finelineNbr, @Param("status") Integer status);
}