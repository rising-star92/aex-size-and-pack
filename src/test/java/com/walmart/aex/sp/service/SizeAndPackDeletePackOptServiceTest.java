package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.walmart.aex.sp.util.BuyQtyResponseInputs.readJsonFileAsString;

@ExtendWith(MockitoExtension.class)
public class SizeAndPackDeletePackOptServiceTest {
    @InjectMocks
    private SizeAndPackDeletePackOptService sizeAndPackDeletePackOptService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testUpdateMerchantPackOptForDeleteingGivenCC() throws IOException {
        PlanSizeAndPackDeleteDTO planSizeAndPackDeleteDTO = spDeletePayloadFromJson("plan12fineline198");
        List<MerchantPackOptimization> merchantPackOptList = new ArrayList<>();
        MerchantPackOptimization MerchantPackOptimization = new MerchantPackOptimization();
//        (List<MerchantPackOptimization> merchantPackOpts, Lvl3 lvl3, Fineline strongKeyFineline)
        sizeAndPackDeletePackOptService.updateMerchantPackOpt(new ArrayList<>(),planSizeAndPackDeleteDTO.getSizeAndPackPayloadDTO().getLvl1List().get(0).getLvl2List().get(0).getLvl3List().get(0) ,planSizeAndPackDeleteDTO.getStrongKey().getFineline());

    }

    public static String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/sizeAndPackDeletePayloads/" + fileName + ".json")));
    }
    private static PlanSizeAndPackDeleteDTO spDeletePayloadFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), PlanSizeAndPackDeleteDTO.class);
    }




}
