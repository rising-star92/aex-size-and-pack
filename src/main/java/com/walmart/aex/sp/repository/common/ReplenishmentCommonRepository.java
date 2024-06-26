package com.walmart.aex.sp.repository.common;

import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class ReplenishmentCommonRepository {
   private final MerchCatgReplPackRepository merchCatgReplPackRepository;
   private final FineLineReplenishmentRepository fineLineReplenishmentRepository;
   private final StyleReplnPkConsRepository styleReplenishmentRepository;
   private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
   private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
   private final CcReplnPkConsRepository ccReplnPkConsRepository;
}
