package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPackId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchCatgReplPackRepository extends JpaRepository<MerchCatgReplPack, MerchCatgReplPackId> {
    Optional<List<MerchCatgReplPack>> findMerchCatgReplPackByMerchCatgReplPackId_planIdAndMerchCatgReplPackId_channelId(Long planId, Integer channelIdFromName);
}
