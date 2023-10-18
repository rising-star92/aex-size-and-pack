package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.RunStatusText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunStatusTextRepository extends JpaRepository<RunStatusText, Integer> {
}
