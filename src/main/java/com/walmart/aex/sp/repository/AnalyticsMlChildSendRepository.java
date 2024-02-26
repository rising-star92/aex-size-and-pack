package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AnalyticsMlChildSendRepository extends JpaRepository<AnalyticsMlChildSend, BigInteger> {

}