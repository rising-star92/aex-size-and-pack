package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.AppMessageText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppMessageTextRepository extends JpaRepository<AppMessageText, Integer> {
}
