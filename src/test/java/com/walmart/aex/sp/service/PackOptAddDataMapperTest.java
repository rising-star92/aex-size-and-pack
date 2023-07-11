package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsResponse;
import com.walmart.aex.sp.dto.planhierarchy.Lvl1;
import com.walmart.aex.sp.dto.planhierarchy.Lvl2;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import com.walmart.aex.sp.repository.MerchPackOptimizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.DEFAULT_SINGLE_PACK_INDICATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class PackOptAddDataMapperTest {

    @InjectMocks
    PackOptAddDataMapper packOptAddDataMapper;

    @Spy
    ObjectMapper objectMapper;

    @Mock
    MerchantPackOptimization merchantPackOptimization;

    @Mock
    SizeAndPackObjectMapper sizeAndPackObjectMapper;

    @Mock
    MerchPackOptimizationRepository merchPackOptimizationRepository;

    @Mock
    MerchCatPlanRepository merchCatPlanRepository;
    @Mock
    SourcingFactoryService sourcingFactoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sizeAndPackObjectMapper = new SizeAndPackObjectMapper(merchCatPlanRepository);
        packOptAddDataMapper = new PackOptAddDataMapper(sizeAndPackObjectMapper, merchPackOptimizationRepository, sourcingFactoryService);
        merchantPackOptimization = new MerchantPackOptimization();
    }

    @Test
    void setMerchCatPackOptSupplierNameChangeTest() throws JsonProcessingException {
        PlanSizeAndPackDTO planSizeAndPackDTO = getRequestPayload(true);
        Lvl1 lvl1= planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        List<Integer> channelList = new ArrayList<>();
        channelList.add(1);

        FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
        factoryDetails.setFactoryName("WALMART");
        when(sourcingFactoryService.getFactoryDetails(Mockito.anyString())).thenReturn(factoryDetails);
        Mockito.when(merchPackOptimizationRepository.findById(Mockito.any(MerchantPackOptimizationID.class))).thenReturn(java.util.Optional.ofNullable(getMerchantPackOptimization()));
        Set<MerchantPackOptimization> merchantPackOptimizationSet = packOptAddDataMapper.setMerchCatPackOpt(planSizeAndPackDTO, lvl1, lvl2, lvl3);
        List<CcPackOptimization> ccPackOptimizations = getCcPackOptimization(merchantPackOptimizationSet);
        CcPackOptimization ccPackOptimization = ccPackOptimizations.get(0);
        assertNull(ccPackOptimization.getColorCombination());
        assertEquals("WALMART",ccPackOptimization.getFactoryName());
        assertNull(ccPackOptimization.getPortOfOriginName());
    }

    @Test
    void setMerchCatPackOptCountryNameChangeTest() throws JsonProcessingException {
        PlanSizeAndPackDTO planSizeAndPackDTO = getRequestPayload(false);
        Lvl1 lvl1= planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        List<Integer> channelList = new ArrayList<>();
        channelList.add(1);
        FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
        factoryDetails.setFactoryName("WALMART");
        when(sourcingFactoryService.getFactoryDetails(Mockito.anyString())).thenReturn(factoryDetails);
        Mockito.when(merchPackOptimizationRepository.findById(Mockito.any(MerchantPackOptimizationID.class))).thenReturn(java.util.Optional.ofNullable(getMerchantPackOptimization()));
        Set<MerchantPackOptimization> merchantPackOptimizationSet = packOptAddDataMapper.setMerchCatPackOpt(planSizeAndPackDTO, lvl1, lvl2, lvl3);
        List<CcPackOptimization> ccPackOptimizations = getCcPackOptimization(merchantPackOptimizationSet);
        CcPackOptimization ccPackOptimization = ccPackOptimizations.get(0);
        assertNull(ccPackOptimization.getColorCombination());
    }

    @Test
    void setMerchCatPackOptSupplierNameChangeWhenChannelIsOnlineTest() throws JsonProcessingException {
        PlanSizeAndPackDTO planSizeAndPackDTO = getRequestPayload(true);
        Lvl1 lvl1= planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        lvl3.getLvl4List().get(0).getFinelines().get(0).setChannel("Online");
        FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
        factoryDetails.setFactoryName("WALMART");
        when(sourcingFactoryService.getFactoryDetails(Mockito.anyString())).thenReturn(factoryDetails);
        Mockito.when(merchPackOptimizationRepository.findById(Mockito.any(MerchantPackOptimizationID.class))).thenReturn(java.util.Optional.ofNullable(getMerchantPackOptimization()));
        Set<MerchantPackOptimization> merchantPackOptimizationSet = packOptAddDataMapper.setMerchCatPackOpt(planSizeAndPackDTO, lvl1, lvl2, lvl3);
        List<StylePackOptimization> stylePackOptimizationList = getStylePackOptimization(merchantPackOptimizationSet);
        List<FineLinePackOptimization> fineLinePackOptimizationList = getFineLinePackOptimization(merchantPackOptimizationSet);
        List<CcPackOptimization> ccPackOptimizations = getCcPackOptimization(merchantPackOptimizationSet);
        assertEquals(0,merchantPackOptimizationSet.size());
        assertEquals(0,fineLinePackOptimizationList.size());
        assertEquals(0,stylePackOptimizationList.size());
        assertEquals(0,ccPackOptimizations.size());
    }
  
  @Test
      void test_setMerchCatPackOptShouldSetDefaultSinglePackIndicator() throws JsonProcessingException {
        PlanSizeAndPackDTO planSizeAndPackDTO = getRequestPayload(false);
        Lvl1 lvl1= planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        List<Integer> channelList = new ArrayList<>();
        channelList.add(1);
        FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
        factoryDetails.setFactoryName("WALMART");
        when(sourcingFactoryService.getFactoryDetails(Mockito.anyString())).thenReturn(factoryDetails);
        Mockito.when(merchPackOptimizationRepository.findById(Mockito.any(MerchantPackOptimizationID.class))).thenReturn(java.util.Optional.ofNullable(getMerchantPackOptimization()));
        Set<MerchantPackOptimization> merchantPackOptimizationSet = packOptAddDataMapper.setMerchCatPackOpt(planSizeAndPackDTO, lvl1, lvl2, lvl3);
        List<CcPackOptimization> ccPackOptimizations = getCcPackOptimization(merchantPackOptimizationSet);
        List<StylePackOptimization> stylePackOptimizationList = getStylePackOptimization(merchantPackOptimizationSet);
        List<FineLinePackOptimization> fineLinePackOptimizationList = getFineLinePackOptimization(merchantPackOptimizationSet);
        List<SubCatgPackOptimization> subCatgPackOptimizationList = merchantPackOptimizationSet.stream().map(MerchantPackOptimization::getSubCatgPackOptimization).flatMap(Collection::stream).collect(Collectors.toList());

        assertEquals(DEFAULT_SINGLE_PACK_INDICATOR, ccPackOptimizations.iterator().next().getSinglePackInd());
        assertEquals(DEFAULT_SINGLE_PACK_INDICATOR, stylePackOptimizationList.iterator().next().getSinglePackInd());
        assertEquals(DEFAULT_SINGLE_PACK_INDICATOR, fineLinePackOptimizationList.iterator().next().getSinglePackInd());
        assertEquals(DEFAULT_SINGLE_PACK_INDICATOR, subCatgPackOptimizationList.iterator().next().getSinglePackInd());
        assertEquals(DEFAULT_SINGLE_PACK_INDICATOR, merchantPackOptimizationSet.iterator().next().getSinglePackInd());
    }

    private List<FineLinePackOptimization> getFineLinePackOptimization(Set<MerchantPackOptimization> merchantPackOptimizationSet) {
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .flatMap(Collection::stream)
                .map(SubCatgPackOptimization::getFinelinepackOptimization)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<StylePackOptimization> getStylePackOptimization(Set<MerchantPackOptimization> merchantPackOptimizationSet) {
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .flatMap(Collection::stream)
                .map(SubCatgPackOptimization::getFinelinepackOptimization)
                .flatMap(Collection::stream)
                .map(FineLinePackOptimization::getStylePackOptimization)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<CcPackOptimization> getCcPackOptimization(Set<MerchantPackOptimization> merchantPackOptimizationSet) {
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .flatMap(Collection::stream)
                .map(SubCatgPackOptimization::getFinelinepackOptimization)
                .flatMap(Collection::stream)
                .map(FineLinePackOptimization::getStylePackOptimization)
                .flatMap(Collection::stream)
                .map(StylePackOptimization::getCcPackOptimization)
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private PlanSizeAndPackDTO getRequestPayload(boolean isSupplierChangePayload) throws JsonProcessingException {
        String json = "{\"planId\":12,\"planDesc\":\"S3 - FYE 2024\",\"lvl0Nbr\":50000,\"lvl0Name\":\"Apparel\",\"lvl1List\":[{\"lvl1Nbr\":34,\"lvl1Name\":\"D34 - Womens Apparel\",\"lvl2List\":[{\"lvl2Nbr\":6419,\"lvl2Name\":\"Plus Womens\",\"lvl3List\":[{\"lvl0Nbr\":null,\"lvl1Nbr\":null,\"lvl2Nbr\":null,\"lvl3Nbr\":12231,\"lvl3Name\":\"Dresses And Rompers Plus Womens\",\"constraints\":null,\"lvl4List\":[{\"lvl4Nbr\":31516,\"lvl4Name\":\"Rompers And Jumpsuits Plus Womens\",\"constraints\":null,\"finelines\":[{\"finelineNbr\":5160,\"finelineName\":\"5160 - TS SS SWEATSHIRT PANT SET\",\"altFinelineName\":null,\"channel\":\"Store\",\"packOptimizationStatus\":null,\"constraints\":null,\"styles\":[{\"styleNbr\":\"34_5160_2_22_5\",\"altStyleDesc\":null,\"channel\":\"Store\",\"constraints\":null,\"customerChoices\":[{\"ccId\":\"34_5160_2_22_5_ORCHID BLOOM\",\"altCcDesc\":null,\"colorName\":\"ALEUTIAN\",\"colorFamily\":\"Blue\",\"channel\":\"Store\",\"constraints\":{\"supplierConstraints\":null,\"ccLevelConstraints\":null,\"colorCombinationConstraints\":{\"suppliers\":[{\"supplierId\":737873341,\"vendorNumber6\":null,\"supplier8Number\":28021452,\"gsmSupplierNumber\":null,\"supplierName\":\"737873341 - LEVI STRAUSS &amp; CO\",\"supplierType\":\"Domestic Buy\",\"supplierNumber\":737873,\"vendorNumber9\":null}],\"factoryId\":\"311112\",\"portOfOrigin\":null,\"singlePackIndicator\":null,\"colorCombination\":null},\"finelineLevelConstraints\":{\"maxPacks\":null,\"maxUnitsPerPack\":null}}}]}],\"optimizationDetails\":null}]}]}]}]}]}";
        if(isSupplierChangePayload) {
            json = "{\"planId\":12,\"planDesc\":\"S3 - FYE 2024\",\"lvl0Nbr\":50000,\"lvl0Name\":\"Apparel\",\"lvl1List\":[{\"lvl1Nbr\":34,\"lvl1Name\":\"D34 - Womens Apparel\",\"lvl2List\":[{\"lvl2Nbr\":6419,\"lvl2Name\":\"Plus Womens\",\"lvl3List\":[{\"lvl0Nbr\":null,\"lvl1Nbr\":null,\"lvl2Nbr\":null,\"lvl3Nbr\":12231,\"lvl3Name\":\"Dresses And Rompers Plus Womens\",\"constraints\":null,\"lvl4List\":[{\"lvl4Nbr\":31516,\"lvl4Name\":\"Rompers And Jumpsuits Plus Womens\",\"constraints\":null,\"finelines\":[{\"finelineNbr\":5160,\"finelineName\":\"5160 - TS SS SWEATSHIRT PANT SET\",\"altFinelineName\":null,\"channel\":\"Store\",\"packOptimizationStatus\":null,\"constraints\":null,\"styles\":[{\"styleNbr\":\"34_5160_2_22_5\",\"altStyleDesc\":null,\"channel\":\"Store\",\"constraints\":null,\"customerChoices\":[{\"ccId\":\"34_5160_2_22_5_ORCHID BLOOM\",\"altCcDesc\":null,\"colorName\":\"ALEUTIAN\",\"colorFamily\":\"Blue\",\"channel\":\"Store\",\"constraints\":{\"supplierConstraints\":null,\"ccLevelConstraints\":null,\"colorCombinationConstraints\":{\"suppliers\":[{\"supplierId\":2178340,\"vendorNumber6\":null,\"supplier8Number\":20005669,\"gsmSupplierNumber\":null,\"supplierName\":\"2178340 - RICHA &amp; CO\",\"supplierType\":\"Direct Import\",\"supplierNumber\":2178,\"vendorNumber9\":null}],\"factoryId\":\"311112\",\"portOfOrigin\":null,\"singlePackIndicator\":null,\"colorCombination\":null},\"finelineLevelConstraints\":{\"maxPacks\":null,\"maxUnitsPerPack\":null}}}]}],\"optimizationDetails\":null}]}]}]}]}]}";
        }
        return objectMapper.readValue(json, PlanSizeAndPackDTO.class);
    }

    private MerchantPackOptimization getMerchantPackOptimization(){
        MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID();
        merchantPackOptimizationID.setPlanId(12L);
        merchantPackOptimizationID.setChannelId(1);
        merchantPackOptimizationID.setRepTLvl0(50000);
        merchantPackOptimizationID.setRepTLvl1(34);
        merchantPackOptimizationID.setRepTLvl2(6419);
        merchantPackOptimizationID.setRepTLvl3(12231);

        SubCatgPackOptimizationID subCatgPackOptimizationID = new SubCatgPackOptimizationID();
        subCatgPackOptimizationID.setRepTLvl4(31516);
        subCatgPackOptimizationID.setMerchantPackOptimizationID(merchantPackOptimizationID);

        FineLinePackOptimizationID fineLinePackOptimizationID = new FineLinePackOptimizationID();
        fineLinePackOptimizationID.setFinelineNbr(5160);
        fineLinePackOptimizationID.setSubCatgPackOptimizationID(subCatgPackOptimizationID);

        StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
        stylePackOptimizationID.setStyleNbr("34_5160_2_22_5");
        stylePackOptimizationID.setFinelinePackOptimizationID(fineLinePackOptimizationID);

        CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID();
        ccPackOptimizationID.setCustomerChoice("34_5160_2_22_5_ORCHID BLOOM");
        ccPackOptimizationID.setStylePackOptimizationID(stylePackOptimizationID);
        ccPackOptimizationID.setStylePackOptimizationID(stylePackOptimizationID);

        /**CC object **/
        CcPackOptimization cc = new CcPackOptimization();
        cc.setCcPackOptimizationId(ccPackOptimizationID);
        cc.setColorCombination("0");
        cc.setFactoryName("Factory");
        cc.setPortOfOriginName("India");
        cc.setFactoryId("121131");
        cc.setVendorName("2178340 - RICHA &amp; CO");
        cc.setVendorNbr6(2178340);
        cc.setChannelText(getChannelText("Store",1));
        Set<CcPackOptimization> ccPackOptimizationSet = new HashSet<>();
        ccPackOptimizationSet.add(cc);

        /**Style Object**/
        StylePackOptimization stylePackOpt = new StylePackOptimization();
        stylePackOpt.setStylePackoptimizationId(stylePackOptimizationID);
        stylePackOpt.setCcPackOptimization(ccPackOptimizationSet);
        stylePackOpt.setChannelText(getChannelText("Store",1));
        Set<StylePackOptimization> stylePackOptimizationSet = new HashSet<>();
        stylePackOptimizationSet.add(stylePackOpt);

        /*** Fineline Object **/
        FineLinePackOptimization fineLinePackOptimization = new FineLinePackOptimization();
        fineLinePackOptimization.setFinelinePackOptId(fineLinePackOptimizationID);
        fineLinePackOptimization.setStylePackOptimization(stylePackOptimizationSet);
        fineLinePackOptimization.setChannelText(getChannelText("Store",1));
        Set<FineLinePackOptimization> fineLinePackOptimizationSet = new HashSet<>();
        fineLinePackOptimizationSet.add(fineLinePackOptimization);

        /*** Subcategory Object **/
        SubCatgPackOptimization subCatgPackOptimization = new SubCatgPackOptimization();
        subCatgPackOptimization.setChannelText(getChannelText("Store",1));
        subCatgPackOptimization.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
        subCatgPackOptimization.setFinelinepackOptimization(fineLinePackOptimizationSet);
        Set<SubCatgPackOptimization> subCatgPackOptimizationSet = new HashSet<>();
        subCatgPackOptimizationSet.add(subCatgPackOptimization);

        /*** Category Object **/
        MerchantPackOptimization merchantPackOptimization = new MerchantPackOptimization();
        merchantPackOptimization.setMerchantPackOptimizationID(merchantPackOptimizationID);
        merchantPackOptimization.setSubCatgPackOptimization(subCatgPackOptimizationSet);
        return merchantPackOptimization;

    }

    private ChannelText getChannelText(String channelName , Integer channelId) {
        ChannelText channelText = new ChannelText();
        channelText.setChannelDesc(channelName);
        channelText.setChannelId(channelId);
        return channelText;
    }
}
