package com.walmart.aex.sp.dto.buyquantity;

import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import lombok.Data;

import java.util.List;

@Data
public class CalculateBuyQtyResponse {
    private List<SpFineLineChannelFixture> spFineLineChannelFixtures;
    private List<MerchCatgReplPack> merchCatgReplPacks;
}