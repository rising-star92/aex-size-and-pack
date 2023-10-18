package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.packoptimization.PackOptFinelinesByStatusResponse;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsMlSendRepository extends JpaRepository<AnalyticsMlSend, BigInteger> {

    Optional<AnalyticsMlSend> findByPlanIdAndFinelineNbrAndRunStatusCode(@Param("planId") Long planId,
                                                               @Param("finelineNbr") Integer finelineNbr,
                                                               @Param("runStatusCode") Integer runStatusCode);

    @Query(value = "select * from dbo.analytics_ml_send t where t.run_status_code = :runStatus and \n" +
            "t.start_ts = ( SELECT MAX(start_ts) FROM analytics_ml_send WHERE fineline_nbr = t.fineline_nbr\n" +
            ") ", nativeQuery = true)
    List<AnalyticsMlSend> getAllFinelinesByStatus(@Param("runStatus") Integer channelId);
}
