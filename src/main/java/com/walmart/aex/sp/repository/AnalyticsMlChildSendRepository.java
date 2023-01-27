package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Repository
public interface AnalyticsMlChildSendRepository extends JpaRepository<AnalyticsMlChildSend, BigInteger> {

}