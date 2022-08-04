package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.AnalyticsMlSend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface AnalyticsMlSendRepository extends JpaRepository<AnalyticsMlSend, BigInteger> {
}
