package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpStyleChannelFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CalculateFinelineBuyQuantityMapperTest {

    @InjectMocks
    CalculateFinelineBuyQuantityMapper calFlBuyQunatityMapper;

    @Mock
    AppMessageTextService appMessageTextService;

    @Test
    void updateSpFinelineFixturesTest() {
        SpFineLineChannelFixture spFineLineChannelFixture = getSpFlChannelFixtures();
        Mockito.when(appMessageTextService.getAppMessagesByIds(any())).thenReturn(getAppMessageTexts());
        calFlBuyQunatityMapper.updateSpFinelineFixtures(spFineLineChannelFixture);
        Assertions.assertEquals(0, spFineLineChannelFixture.getInitialSetQty().intValue());
        Assertions.assertEquals(0, spFineLineChannelFixture.getBumpPackQty().intValue());
        Assertions.assertEquals(0, spFineLineChannelFixture.getBuyQty().intValue());
        Assertions.assertEquals(0, spFineLineChannelFixture.getReplnQty().intValue());
        spFineLineChannelFixture.getSpStyleChannelFixtures().forEach(style-> {
            Assertions.assertEquals(0, style.getInitialSetQty().intValue());
            Assertions.assertEquals(0, style.getBumpPackQty().intValue());
            Assertions.assertEquals(0, style.getBuyQty().intValue());
            Assertions.assertEquals(0, style.getReplnQty().intValue());
            style.getSpCustomerChoiceChannelFixture().forEach(cc->{
                Assertions.assertEquals(0, cc.getInitialSetQty().intValue());
                Assertions.assertEquals(0, cc.getBumpPackQty().intValue());
                Assertions.assertEquals(0, cc.getBuyQty().intValue());
                Assertions.assertEquals(0, cc.getReplnQty().intValue());
                cc.getSpCustomerChoiceChannelFixtureSize().forEach(size->{
                    Assertions.assertEquals(0, size.getInitialSetQty().intValue());
                    Assertions.assertEquals(0, size.getBumpPackQty().intValue());
                    Assertions.assertEquals(0, size.getBuyQty().intValue());
                    Assertions.assertEquals(0, size.getReplnQty().intValue());
                });
            });
        });

    }

    private List<AppMessageTextResponse> getAppMessageTexts() {
        List<AppMessageTextResponse> appMessageTextResponseList = new ArrayList<>();
        AppMessageTextResponse appMessageTextResponse = AppMessageTextResponse.builder().id(160).typeDesc("Error").desc("BQFP_MESSAGE").longDesc("One or more CC have issues with BQFP dataset").build();
        appMessageTextResponseList.add(appMessageTextResponse);
        return appMessageTextResponseList;
    }

    private SpFineLineChannelFixture getSpFlChannelFixtures() {
        SpFineLineChannelFixture spFineLineChannelFixture = new SpFineLineChannelFixture();
        spFineLineChannelFixture.setSpStyleChannelFixtures(getSpStyleChanFixture());
        spFineLineChannelFixture.setInitialSetQty(1000);
        spFineLineChannelFixture.setBumpPackQty(1000);
        spFineLineChannelFixture.setBuyQty(1000);
        spFineLineChannelFixture.setReplnQty(1000);
        return spFineLineChannelFixture;
    }

    private Set<SpStyleChannelFixture> getSpStyleChanFixture() {
        Set<SpStyleChannelFixture> spStyleChannelFixtures = new HashSet<>();
        SpStyleChannelFixture spStyleChannelFixture = new SpStyleChannelFixture();
        spStyleChannelFixture.setSpCustomerChoiceChannelFixture(getSpCcChanFixtures());
        spStyleChannelFixture.setInitialSetQty(1000);
        spStyleChannelFixture.setBumpPackQty(1000);
        spStyleChannelFixture.setBuyQty(1000);
        spStyleChannelFixture.setReplnQty(1000);
        spStyleChannelFixtures.add(spStyleChannelFixture);
        return spStyleChannelFixtures;
    }

    private Set<SpCustomerChoiceChannelFixture> getSpCcChanFixtures() {
        Set<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixtures = new HashSet<>();
        SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture = new SpCustomerChoiceChannelFixture();
        spCustomerChoiceChannelFixture.setSpCustomerChoiceChannelFixtureSize(getSpCcChanFixtureSizes());
        spCustomerChoiceChannelFixture.setInitialSetQty(1000);
        spCustomerChoiceChannelFixture.setBumpPackQty(1000);
        spCustomerChoiceChannelFixture.setBuyQty(1000);
        spCustomerChoiceChannelFixture.setReplnQty(1000);
        spCustomerChoiceChannelFixtures.add(spCustomerChoiceChannelFixture);
        return spCustomerChoiceChannelFixtures;
    }

    private Set<SpCustomerChoiceChannelFixtureSize> getSpCcChanFixtureSizes() {
        Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes = new HashSet<>();
        SpCustomerChoiceChannelFixtureSize spCustomerChoiceChannelFixtureSize = new SpCustomerChoiceChannelFixtureSize();
        spCustomerChoiceChannelFixtureSize.setInitialSetQty(1000);
        spCustomerChoiceChannelFixtureSize.setBumpPackQty(1000);
        spCustomerChoiceChannelFixtureSize.setBuyQty(1000);
        spCustomerChoiceChannelFixtureSize.setReplnQty(1000);
        spCustomerChoiceChannelFixtureSizes.add(spCustomerChoiceChannelFixtureSize);
        return spCustomerChoiceChannelFixtureSizes;
    }
}
