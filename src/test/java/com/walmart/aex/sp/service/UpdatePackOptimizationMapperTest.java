package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.UpdatePackOptConstraintRequestDTO;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsResponse;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.SinglePackIndicator;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.repository.common.PackOptimizationCommonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.walmart.aex.sp.util.SizeAndPackConstants.DEFAULT_FACTORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdatePackOptimizationMapperTest {
    @InjectMocks
    UpdatePackOptimizationMapper pkOptConstMapper;
    @Mock
    PackOptimizationCommonRepository packOptimizationCommonRepository;
    @Mock
    MerchPackOptimizationRepository merchPackOptimizationRepository;
    @Mock
    SubCatgPackOptimizationRepository subCatgPackOptimizationRepository;
    @Mock
    CcPackOptimizationRepository ccPackOptimizationRepository;
    @Mock
    StylePackOptimizationRepository stylePackOptimizationRepository;
    @Mock
    FinelinePackOptConsRepository finelinePackOptConsRepository;

    @Test
    public void testUpdateCategoryPackOptCons() {
        UpdatePackOptConstraintRequestDTO request =  new UpdatePackOptConstraintRequestDTO();
        request.setPlanId(12L);
        request.setChannel("store");
        request.setLvl3Nbr(12228);
        request.setOriginCountryCode("US");

        ChannelText channelText = new ChannelText();
        channelText.setChannelId(1);
        channelText.setChannelDesc("store");

        List<MerchantPackOptimization> merchantPackOptimizationList = new ArrayList<>();
        MerchantPackOptimization merchantPackOptimization = new  MerchantPackOptimization();
        MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID();
        merchantPackOptimizationID.setPlanId(12L);
        merchantPackOptimizationID.setRepTLvl0(50000);
        merchantPackOptimizationID.setRepTLvl1(34);
        merchantPackOptimizationID.setRepTLvl2(6419);
        merchantPackOptimizationID.setRepTLvl3(12228);
        merchantPackOptimization.setMerchantPackOptimizationID(merchantPackOptimizationID);
        merchantPackOptimization.setVendorName("NIKE");
        merchantPackOptimization.setOriginCountryName("United States of America");
        merchantPackOptimization.setOriginCountryCode("USA");
        merchantPackOptimization.setFactoryName("Nike Manufacture");

        Set<SubCatgPackOptimization> subCatgPkOptPkConsList = new HashSet<>();
        SubCatgPackOptimization subCatgPackOptimization = new SubCatgPackOptimization();
        SubCatgPackOptimizationID subCatgPackOptimizationID =new SubCatgPackOptimizationID();
        subCatgPackOptimizationID.setMerchantPackOptimizationID(merchantPackOptimizationID);
        subCatgPackOptimizationID.setRepTLvl4(31507);
        subCatgPackOptimization.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
        subCatgPackOptimization.setChannelText(channelText);

        Set<FineLinePackOptimization> fineLinePackOptimizationList = new HashSet<>();
        FineLinePackOptimization fineLinePackOptimization = new FineLinePackOptimization();
        FineLinePackOptimizationID fineLinePackOptimizationID = new FineLinePackOptimizationID();
        fineLinePackOptimizationID.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
        fineLinePackOptimizationID.setFinelineNbr(2702);
        fineLinePackOptimization.setFinelinePackOptId(fineLinePackOptimizationID);
        fineLinePackOptimization.setChannelText(channelText);

        Set<StylePackOptimization> stylePackOptimizationList = new HashSet<>();
        StylePackOptimization stylePackOptimization = new StylePackOptimization();
        StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
        stylePackOptimizationID.setFinelinePackOptimizationID(fineLinePackOptimizationID);
        stylePackOptimizationID.setStyleNbr("34_2839_2_22_6");
        stylePackOptimization.setStylePackoptimizationId(stylePackOptimizationID);
        stylePackOptimization.setChannelText(channelText);

        Set<CcPackOptimization> ccPackOptimizationList = new HashSet<>();
        CcPackOptimization ccPackOptimization = new CcPackOptimization();
        CcPackOptimizationID CcPackOptimizationId = new CcPackOptimizationID();
        CcPackOptimizationId.setStylePackOptimizationID(stylePackOptimizationID);
        CcPackOptimizationId.setCustomerChoice("34_2839_2_22_6_VIVID WHITE");
        ccPackOptimization.setCcPackOptimizationId(CcPackOptimizationId);
        ccPackOptimization.setChannelText(channelText);
        ccPackOptimizationList.add(ccPackOptimization);

        stylePackOptimization.setCcPackOptimization(ccPackOptimizationList);
        stylePackOptimizationList.add(stylePackOptimization);

        fineLinePackOptimization.setStylePackOptimization(stylePackOptimizationList);
        fineLinePackOptimizationList.add(fineLinePackOptimization);

        subCatgPackOptimization.setFinelinepackOptimization(fineLinePackOptimizationList);
        subCatgPkOptPkConsList.add(subCatgPackOptimization);

        merchantPackOptimization.setSubCatgPackOptimization(subCatgPkOptPkConsList);
        merchantPackOptimizationList.add(merchantPackOptimization);

        FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
        factoryDetails.setCountry("UNITED STATES OF AMERICA");
        factoryDetails.setCountryCode("US");
        factoryDetails.setFactoryName("PKG--LA BC");
        when(packOptimizationCommonRepository.getMerchPackOptimizationRepository()).thenReturn(merchPackOptimizationRepository);
        when(packOptimizationCommonRepository.getSubCatgPackOptimizationRepository()).thenReturn(subCatgPackOptimizationRepository);
        when(packOptimizationCommonRepository.getFinelinePackOptConsRepository()).thenReturn(finelinePackOptConsRepository);
        when(packOptimizationCommonRepository.getStylePackOptimizationRepository()).thenReturn(stylePackOptimizationRepository);
        when(packOptimizationCommonRepository.getCcPackOptimizationRepository()).thenReturn(ccPackOptimizationRepository);
        pkOptConstMapper.updateCategoryPackOptCons(request,merchantPackOptimizationList,factoryDetails);
        assertEquals("US",merchantPackOptimizationList.get(0).getOriginCountryCode());
        for (SubCatgPackOptimization subcatgOptCons :merchantPackOptimizationList.get(0).getSubCatgPackOptimization()){
            assertEquals("US",subcatgOptCons.getOriginCountryCode());
            for (FineLinePackOptimization flPackOptimization :subcatgOptCons.getFinelinepackOptimization()){
                assertEquals("US",flPackOptimization.getOriginCountryCode());
                for (StylePackOptimization stPackOptimization :flPackOptimization.getStylePackOptimization()){
                    assertEquals("US",stPackOptimization.getOriginCountryCode());
                    for (CcPackOptimization ccPkOptimization :stPackOptimization.getCcPackOptimization()){
                        assertEquals("US",ccPkOptimization.getOriginCountryCode());
                    }
                }
            }
        }
    }
    @Test
    public void testUpdateCategoryPackOptConsWhenFactoryIdIsZero() {
        UpdatePackOptConstraintRequestDTO request =  new UpdatePackOptConstraintRequestDTO();
        request.setPlanId(12L);
        request.setChannel("store");
        request.setLvl3Nbr(12228);
        request.setFactoryId("0");

        ChannelText channelText = new ChannelText();
        channelText.setChannelId(1);
        channelText.setChannelDesc("store");

        List<MerchantPackOptimization> merchantPackOptimizationList = new ArrayList<>();
        MerchantPackOptimization merchantPackOptimization = new  MerchantPackOptimization();
        MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID();
        merchantPackOptimizationID.setPlanId(12L);
        merchantPackOptimizationID.setRepTLvl0(50000);
        merchantPackOptimizationID.setRepTLvl1(34);
        merchantPackOptimizationID.setRepTLvl2(6419);
        merchantPackOptimizationID.setRepTLvl3(12228);
        merchantPackOptimization.setMerchantPackOptimizationID(merchantPackOptimizationID);
        merchantPackOptimization.setVendorName("NIKE");
        merchantPackOptimization.setOriginCountryName("United States of America");
        merchantPackOptimization.setOriginCountryCode("USA");
        merchantPackOptimization.setFactoryName("Nike Manufacture");

        Set<SubCatgPackOptimization> subCatgPkOptPkConsList = new HashSet<>();
        SubCatgPackOptimization subCatgPackOptimization = new SubCatgPackOptimization();
        SubCatgPackOptimizationID subCatgPackOptimizationID =new SubCatgPackOptimizationID();
        subCatgPackOptimizationID.setMerchantPackOptimizationID(merchantPackOptimizationID);
        subCatgPackOptimizationID.setRepTLvl4(31507);
        subCatgPackOptimization.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
        subCatgPackOptimization.setChannelText(channelText);
        subCatgPackOptimization.setOriginCountryCode("US");
        subCatgPackOptimization.setOriginCountryName("UNITED STATES");
        subCatgPackOptimization.setFactoryName("Nike Manufacturing");

        Set<FineLinePackOptimization> fineLinePackOptimizationList = new HashSet<>();
        FineLinePackOptimization fineLinePackOptimization = new FineLinePackOptimization();
        FineLinePackOptimizationID fineLinePackOptimizationID = new FineLinePackOptimizationID();
        fineLinePackOptimizationID.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
        fineLinePackOptimizationID.setFinelineNbr(2702);
        fineLinePackOptimization.setFinelinePackOptId(fineLinePackOptimizationID);
        fineLinePackOptimization.setChannelText(channelText);
        fineLinePackOptimization.setOriginCountryCode("US");
        fineLinePackOptimization.setOriginCountryName("UNITED STATES");
        fineLinePackOptimization.setFactoryName("Nike Manufacturing");

        Set<StylePackOptimization> stylePackOptimizationList = new HashSet<>();
        StylePackOptimization stylePackOptimization = new StylePackOptimization();
        StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
        stylePackOptimizationID.setFinelinePackOptimizationID(fineLinePackOptimizationID);
        stylePackOptimizationID.setStyleNbr("34_2839_2_22_6");
        stylePackOptimization.setStylePackoptimizationId(stylePackOptimizationID);
        stylePackOptimization.setChannelText(channelText);
        stylePackOptimization.setOriginCountryCode("US");
        stylePackOptimization.setOriginCountryName("UNITED STATES");
        stylePackOptimization.setFactoryName("Nike Manufacturing");

        Set<CcPackOptimization> ccPackOptimizationList = new HashSet<>();
        CcPackOptimization ccPackOptimization = new CcPackOptimization();
        CcPackOptimizationID CcPackOptimizationId = new CcPackOptimizationID();
        CcPackOptimizationId.setStylePackOptimizationID(stylePackOptimizationID);
        CcPackOptimizationId.setCustomerChoice("34_2839_2_22_6_VIVID WHITE");
        ccPackOptimization.setCcPackOptimizationId(CcPackOptimizationId);
        ccPackOptimization.setChannelText(channelText);
        ccPackOptimization.setOriginCountryCode("US");
        ccPackOptimization.setOriginCountryName("UNITED STATES");
        ccPackOptimization.setFactoryName("Nike Manufacturing");
        ccPackOptimizationList.add(ccPackOptimization);


        stylePackOptimization.setCcPackOptimization(ccPackOptimizationList);
        stylePackOptimizationList.add(stylePackOptimization);

        fineLinePackOptimization.setStylePackOptimization(stylePackOptimizationList);
        fineLinePackOptimizationList.add(fineLinePackOptimization);

        subCatgPackOptimization.setFinelinepackOptimization(fineLinePackOptimizationList);
        subCatgPkOptPkConsList.add(subCatgPackOptimization);

        merchantPackOptimization.setSubCatgPackOptimization(subCatgPkOptPkConsList);
        merchantPackOptimizationList.add(merchantPackOptimization);

        FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
        factoryDetails.setFactoryName(DEFAULT_FACTORY);
        when(packOptimizationCommonRepository.getMerchPackOptimizationRepository()).thenReturn(merchPackOptimizationRepository);
        when(packOptimizationCommonRepository.getSubCatgPackOptimizationRepository()).thenReturn(subCatgPackOptimizationRepository);
        when(packOptimizationCommonRepository.getFinelinePackOptConsRepository()).thenReturn(finelinePackOptConsRepository);
        when(packOptimizationCommonRepository.getStylePackOptimizationRepository()).thenReturn(stylePackOptimizationRepository);
        when(packOptimizationCommonRepository.getCcPackOptimizationRepository()).thenReturn(ccPackOptimizationRepository);
        pkOptConstMapper.updateCategoryPackOptCons(request,merchantPackOptimizationList,factoryDetails);
        assertEquals("DEFAULT",merchantPackOptimizationList.get(0).getFactoryName());
        assertNull(merchantPackOptimizationList.get(0).getOriginCountryCode());
        assertNull(merchantPackOptimizationList.get(0).getOriginCountryName());
        for (SubCatgPackOptimization subcatgOptCons :merchantPackOptimizationList.get(0).getSubCatgPackOptimization()){
            assertEquals("DEFAULT",subcatgOptCons.getFactoryName());
            assertNull(subcatgOptCons.getOriginCountryCode());
            assertNull(subcatgOptCons.getOriginCountryName());
            for (FineLinePackOptimization flPackOptimization :subcatgOptCons.getFinelinepackOptimization()){
                assertEquals("DEFAULT",flPackOptimization.getFactoryName());
                assertNull(flPackOptimization.getOriginCountryCode());
                assertNull(flPackOptimization.getOriginCountryName());
                for (StylePackOptimization stPackOptimization :flPackOptimization.getStylePackOptimization()){
                    assertEquals("DEFAULT",stPackOptimization.getFactoryName());
                    assertNull(stPackOptimization.getOriginCountryCode());
                    assertNull(stPackOptimization.getOriginCountryName());
                    for (CcPackOptimization ccPkOptimization :stPackOptimization.getCcPackOptimization()){
                        assertEquals("DEFAULT",ccPkOptimization.getFactoryName());
                        assertNull(ccPkOptimization.getOriginCountryCode());
                        assertNull(ccPkOptimization.getOriginCountryName());
                    }
                }
            }
        }
    }
    @Test
    public void testSignalIndicator(){
        assertEquals(SinglePackIndicator.PARTIAL.getId(),pkOptConstMapper.getSinglePackIndicatorFlag(List.of(1,0,0,0,1)));
        assertEquals(SinglePackIndicator.SELECTED.getId(),pkOptConstMapper.getSinglePackIndicatorFlag(List.of(1,1,1,1)));
        assertEquals(SinglePackIndicator.UNSELECTED.getId(),pkOptConstMapper.getSinglePackIndicatorFlag(List.of(0,0,0,0)));
    }

}
