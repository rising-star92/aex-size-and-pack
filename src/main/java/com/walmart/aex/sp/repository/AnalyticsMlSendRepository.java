package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.AnalyticsMlSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
@Repository
public interface AnalyticsMlSendRepository extends JpaRepository<AnalyticsMlSend, BigInteger> {
}
