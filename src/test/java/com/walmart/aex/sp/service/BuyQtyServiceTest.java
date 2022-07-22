package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.BuyQtyRequest;
import com.walmart.aex.sp.dto.FetchFineLineResponse;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.FineLineBuyRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class BuyQtyServiceTest {

    @InjectMocks
    private FineLineBuyService fineLineBuyService;

    @Mock
    private FineLineBuyRepo fineLineBuyRepo;

    @Mock
    FetchFineLineResponse fetchFineLineResponse;

    @Mock
    List<SpFineLineChannelFixture> spFineLineChannelFixtureList;

    @Test
    public void getFineLineResponse() {
        Long planId = 362L;
        Integer channelId = 1;
        ChannelText channeltext = new ChannelText();
        channeltext.setChannelId(1);
        channeltext.setChannelDesc("Store");

        SpFineLineChannelFixture spFineLineChannelFixture = new SpFineLineChannelFixture();

        spFineLineChannelFixtureList = new ArrayList<>();

        SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId();
        spFineLineChannelFixtureId.setPlanId(planId);
        spFineLineChannelFixtureId.setFixtureTypeRollUpId(null);
        spFineLineChannelFixtureId.setChannelId(channelId);
        spFineLineChannelFixtureId.setFineLineNbr(345);
        spFineLineChannelFixtureId.setRepTLvl0(45);
        spFineLineChannelFixtureId.setRepTLvl1(8);
        spFineLineChannelFixtureId.setRepTLvl2(98);
        spFineLineChannelFixtureId.setRepTLvl3(567);
        spFineLineChannelFixtureId.setRepTLvl4(561);

        spFineLineChannelFixture.setSpFineLineChannelFixtureId(spFineLineChannelFixtureId);
        spFineLineChannelFixture.setSpStyleChannelFixtures(null);
        spFineLineChannelFixture.setBuyQty(678);
        spFineLineChannelFixture.setAdjReplnQty(89);
        spFineLineChannelFixture.setInitialSetQty(57);
        spFineLineChannelFixture.setReplnQty(97);
        spFineLineChannelFixture.setFpStrategyText(null);
        spFineLineChannelFixture.setFixtureTypeRollUp(null);
        spFineLineChannelFixture.setMerchMethodShortDesc("ujhygt");
        spFineLineChannelFixture.setBumpPackQty(567);
        spFineLineChannelFixture.setStoreObj(null);
        spFineLineChannelFixture.setMerchMethodCode(null);

        spFineLineChannelFixtureList.add(spFineLineChannelFixture);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(planId);
        buyQtyRequest.setPlanDesc(null);
        buyQtyRequest.setChannel("store");
        buyQtyRequest.setRepTLvl3(null);


        Mockito.when(fineLineBuyRepo.findBySpFineLineChannelFixtureIdPlanIdAndSpFineLineChannelFixtureIdChannelId(planId, channelId)).thenReturn(spFineLineChannelFixtureList);
        fetchFineLineResponse = fineLineBuyService.getFineLineResponse(buyQtyRequest);


        assertNotNull(fetchFineLineResponse);
        assertEquals(fetchFineLineResponse.getPlanId(), 362L);


    }
}
