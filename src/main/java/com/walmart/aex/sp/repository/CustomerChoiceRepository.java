package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.CustChoicePlan;
import com.walmart.aex.sp.entity.CustChoicePlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CustomerChoiceRepository extends JpaRepository<CustChoicePlan, CustChoicePlanId> {

    @Query(value = "select * from dbo.cc_plan where plan_id = :planId and channel_id = :channelId \n" +
            "and fineline_nbr=:fineline and customer_choice in (:ccs) ", nativeQuery = true)
    Set<CustChoicePlan> getCustomerChoicesByPlanIdFinelineNbrAndCc(@Param("planId")Long planId, @Param("channelId") Integer channelId, @Param("fineline") Integer fineline, @Param("ccs") Set<String> ccs);
}
