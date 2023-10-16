package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


@ExtendWith(MockitoExtension.class)
public class SizeAndPackDeletePackOptMapperTest {
    @InjectMocks
    private SizeAndPackDeletePackOptMapper sizeAndPackDeletePackOptMapper;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testUpdateMerchantPackOptForDeleteingGivenStyle() throws IOException {
        PlanSizeAndPackDeleteDTO planSizeAndPackDeleteDTO = spDeletePayloadFromJson("deleteGivenStyleInputPayload");
        List<MerchantPackOptimization> merchantPackOptList = getMerchantPackOptimizations();
        Set<MerchantPackOptimization>  updatedMerchantPackOptSet = sizeAndPackDeletePackOptMapper.updateMerchantPackOpt(merchantPackOptList,planSizeAndPackDeleteDTO.getSizeAndPackPayloadDTO().getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0) ,planSizeAndPackDeleteDTO.getStrongKey().getFineline());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.size());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.stream().map(MerchantPackOptimization::getSubCatgPackOptimization).count());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().size());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().iterator().next().getStylePackOptimization().size());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().iterator().next().getStylePackOptimization().iterator().next().getCcPackOptimization().size());
        Assertions.assertEquals("34_198_1_13_2", updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().iterator().next().getStylePackOptimization().stream().findFirst().get().getStylePackoptimizationId().getStyleNbr());
        Assertions.assertEquals("34_198_1_13_2_001", updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().iterator().next().getStylePackOptimization().iterator().next().getCcPackOptimization().stream().findFirst().get().getCcPackOptimizationId().getCustomerChoice());
    }

    @Test
    void testUpdateMerchantPackOptForDeleteingGivenCC() throws IOException {
        PlanSizeAndPackDeleteDTO planSizeAndPackDeleteDTO = spDeletePayloadFromJson("deleteGivenCCInputPayload");
        List<MerchantPackOptimization> merchantPackOptList = getMerchantPackOptimizations();
        Set<MerchantPackOptimization>  updatedMerchantPackOptSet = sizeAndPackDeletePackOptMapper.updateMerchantPackOpt(merchantPackOptList,planSizeAndPackDeleteDTO.getSizeAndPackPayloadDTO().getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0) ,planSizeAndPackDeleteDTO.getStrongKey().getFineline());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.size());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.stream().map(MerchantPackOptimization::getSubCatgPackOptimization).count());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().size());
        Assertions.assertEquals(2, updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().iterator().next().getStylePackOptimization().size());
        Assertions.assertEquals(1, updatedMerchantPackOptSet.iterator().next().getSubCatgPackOptimization().iterator().next().getFinelinepackOptimization().iterator().next().getStylePackOptimization().iterator().next().getCcPackOptimization().size());

    }

    @Test
    void testUpdateMerchantPackOptForDeleteingGivenFineline() throws IOException {
        PlanSizeAndPackDeleteDTO planSizeAndPackDeleteDTO = spDeletePayloadFromJson("deleteGivenFinelineInputPayload");
        List<MerchantPackOptimization> merchantPackOptList = getMerchantPackOptimizations();
        Set<MerchantPackOptimization>  updatedMerchantPackOptSet = sizeAndPackDeletePackOptMapper.updateMerchantPackOpt(merchantPackOptList,planSizeAndPackDeleteDTO.getSizeAndPackPayloadDTO().getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0) ,planSizeAndPackDeleteDTO.getStrongKey().getFineline());
        Assertions.assertEquals(0, updatedMerchantPackOptSet.size());
    }

    private List<MerchantPackOptimization> getMerchantPackOptimizations() {
        List<MerchantPackOptimization> merchantPackOptList = new ArrayList<>();
        MerchantPackOptimization merchantPackOptimization = new MerchantPackOptimization();
        MerchantPackOptimizationID merchantPackOptimizationID = getMerchantPackOptimizationID();
        merchantPackOptimization.setMerchantPackOptimizationID(merchantPackOptimizationID);

        Set<SubCatgPackOptimization> subCatgPackOptimizationSet = new HashSet<>();
        SubCatgPackOptimization subCatgPackOptimization = new SubCatgPackOptimization();
        SubCatgPackOptimizationID subCatgPackOptimizationID = getSubCatgPackOptimizationID(merchantPackOptimizationID);
        subCatgPackOptimization.setSubCatgPackOptimizationID(subCatgPackOptimizationID);

        Set<FineLinePackOptimization> fineLinePackOptimizationSet = new HashSet<>();
        FineLinePackOptimization fineLinePackOptimization = new FineLinePackOptimization();
        FineLinePackOptimizationID fineLinePackOptimizationID = getFineLinePackOptimizationID(subCatgPackOptimizationID,198);
        fineLinePackOptimization.setFinelinePackOptId(fineLinePackOptimizationID);

        Set<StylePackOptimization> stylePackOptimizationSet = new HashSet<>();
        StylePackOptimization stylePackOptimization1 = new StylePackOptimization();
        StylePackOptimization stylePackOptimization2 = new StylePackOptimization();
        StylePackOptimizationID stylePackOptimizationID1 = getStylePackOptimizationID(fineLinePackOptimizationID,"34_198_1_13_1");
        StylePackOptimizationID stylePackOptimizationID2 = getStylePackOptimizationID(fineLinePackOptimizationID,"34_198_1_13_2");
        stylePackOptimization1.setStylePackoptimizationId(stylePackOptimizationID1);
        stylePackOptimization2.setStylePackoptimizationId(stylePackOptimizationID2);

        Set<CcPackOptimization> ccPackOptimizationSet1 = new HashSet<>();
        Set<CcPackOptimization> ccPackOptimizationSet2 = new HashSet<>();
        CcPackOptimization ccPackOptimization1 = getCcPackOptimization(stylePackOptimizationID1, "34_198_1_13_1_001");
        CcPackOptimization ccPackOptimization2 = getCcPackOptimization(stylePackOptimizationID1, "34_198_1_13_1_002");
        CcPackOptimization ccPackOptimization3 = getCcPackOptimization(stylePackOptimizationID2, "34_198_1_13_2_001");
        ccPackOptimizationSet1.add(ccPackOptimization1);
        ccPackOptimizationSet1.add(ccPackOptimization2);
        ccPackOptimizationSet2.add(ccPackOptimization3);

        stylePackOptimization1.setCcPackOptimization(ccPackOptimizationSet1);
        stylePackOptimizationSet.add(stylePackOptimization1);
        stylePackOptimization2.setCcPackOptimization(ccPackOptimizationSet2);
        stylePackOptimizationSet.add(stylePackOptimization2);

        fineLinePackOptimization.setStylePackOptimization(stylePackOptimizationSet);
        fineLinePackOptimizationSet.add(fineLinePackOptimization);

        subCatgPackOptimization.setFinelinepackOptimization(fineLinePackOptimizationSet);
        subCatgPackOptimizationSet.add(subCatgPackOptimization);

        merchantPackOptimization.setSinglePackInd(1);
        merchantPackOptimization.setChannelText(new ChannelText(1,"Store"));
        merchantPackOptimization.setSubCatgPackOptimization(subCatgPackOptimizationSet);
        merchantPackOptList.add(merchantPackOptimization);
        return merchantPackOptList;
    }

    private CcPackOptimization getCcPackOptimization(StylePackOptimizationID stylePackOptimizationID, String customerChoice) {
        CcPackOptimization ccPackOptimization = new CcPackOptimization();
        CcPackOptimizationID ccPackOptimizationID = getCcPackOptimizationID(stylePackOptimizationID, customerChoice);
        ccPackOptimization.setCcPackOptimizationId(ccPackOptimizationID);
        ccPackOptimization.setChannelText(new ChannelText(1,"Store"));
        return ccPackOptimization;
    }

    private CcPackOptimizationID getCcPackOptimizationID(StylePackOptimizationID stylePackOptimizationID, String customerChoice) {
        CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID();
        ccPackOptimizationID.setStylePackOptimizationID(stylePackOptimizationID);
        ccPackOptimizationID.setCustomerChoice(customerChoice);
        return ccPackOptimizationID;
    }

    private StylePackOptimizationID getStylePackOptimizationID(FineLinePackOptimizationID fineLinePackOptimizationID, String styleNbr) {
        StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
        stylePackOptimizationID.setFinelinePackOptimizationID(fineLinePackOptimizationID);
        stylePackOptimizationID.setStyleNbr(styleNbr);
        return stylePackOptimizationID;
    }

    private FineLinePackOptimizationID getFineLinePackOptimizationID(SubCatgPackOptimizationID subCatgPackOptimizationID, Integer flNbr) {
        FineLinePackOptimizationID fineLinePackOptimizationID = new FineLinePackOptimizationID();
        fineLinePackOptimizationID.setFinelineNbr(flNbr);
        fineLinePackOptimizationID.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
        return fineLinePackOptimizationID;
    }

    private SubCatgPackOptimizationID getSubCatgPackOptimizationID(MerchantPackOptimizationID merchantPackOptimizationID) {
        SubCatgPackOptimizationID subCatgPackOptimizationID = new SubCatgPackOptimizationID();
        subCatgPackOptimizationID.setRepTLvl4(1056384);
        subCatgPackOptimizationID.setMerchantPackOptimizationID(merchantPackOptimizationID);
        return subCatgPackOptimizationID;
    }

    private MerchantPackOptimizationID getMerchantPackOptimizationID() {
        MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID();
        merchantPackOptimizationID.setPlanId(12l);
        merchantPackOptimizationID.setRepTLvl0(50000);
        merchantPackOptimizationID.setRepTLvl1(34);
        merchantPackOptimizationID.setRepTLvl2(1056308);
        merchantPackOptimizationID.setRepTLvl3(1056309);
        merchantPackOptimizationID.setChannelId(1);
        return merchantPackOptimizationID;
    }

    public static String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/sizeAndPackDeletePayloads/" + fileName + ".json")));
    }
    private static PlanSizeAndPackDeleteDTO spDeletePayloadFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), PlanSizeAndPackDeleteDTO.class);
    }




}
