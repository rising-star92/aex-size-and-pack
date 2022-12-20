package com.walmart.aex.sp.repository.common;


import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpStyleChannelFixtureRepository;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class BuyQuantityCommonRepository {
   private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
   private final SpStyleChannelFixtureRepository spStyleChannelFixtureRepository;
   private final SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
   private final SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;
}
