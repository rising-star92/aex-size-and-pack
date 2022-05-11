package com.walmart.aex.sp.repository;


import com.walmart.aex.sp.entity.CustChoice;
import com.walmart.aex.sp.entity.CustChoiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerChoiceRepository extends JpaRepository<CustChoice, CustChoiceId> {
}
