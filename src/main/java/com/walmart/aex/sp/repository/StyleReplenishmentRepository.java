package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.StyleReplPackId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StyleReplenishmentRepository extends JpaRepository<StyleReplPack, StyleReplPackId> {
}
