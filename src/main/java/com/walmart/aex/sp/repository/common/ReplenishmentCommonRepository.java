package com.walmart.aex.sp.repository.common;

import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpStyleChannelFixtureRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class ReplenishmentCommonRepository {
   private final MerchCatgReplPackRepository merchCatgReplPackRepository;
   private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
   private final StyleReplnPkConsRepository styleReplenishmentRepository;
   private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
   private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
   private final CcReplnPkConsRepository ccReplnPkConsRepository;

   private final SpStyleChannelFixtureRepository spStyleChannelFixtureRepository;
   private final SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
   private final SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;
}
