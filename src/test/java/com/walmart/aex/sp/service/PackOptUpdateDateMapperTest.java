package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.planhierarchy.Lvl1;
import com.walmart.aex.sp.dto.planhierarchy.Lvl2;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import com.walmart.aex.sp.entity.SubCatgPackOptimization;
import com.walmart.aex.sp.repository.MerchPackOptimizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class PackOptUpdateDateMapperTest {

    @InjectMocks
    private PackOptUpdateDataMapper packOptUpdateDataMapper;

    @Mock
    private SizeAndPackObjectMapper sizeAndPackObjectMapper;

    @Mock
    private PackOptAddDataMapper packOptAddDataMapper;

    @Mock
    private MerchPackOptimizationRepository merchPackOptimizationRepository;


    @Test
    void testUpdateLinePlanEventsToPackOpt() {
        SizeAndPackObjectMapperTest sizeAndPackObjectMapperTest=new SizeAndPackObjectMapperTest();
        PlanSizeAndPackDTO planSizeAndPackDTO = sizeAndPackObjectMapperTest.getPlanSizeAndPackDTOObj();
        Lvl1 lvl1 = planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        MerchantPackOptimization merchantPackOptimization = new MerchantPackOptimization();
        MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID(planSizeAndPackDTO.getPlanId(),planSizeAndPackDTO.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(),1);
        merchantPackOptimization.setMerchantPackOptimizationID(merchantPackOptimizationID);
        merchantPackOptimization.setMaxNbrOfPacks(50);
        Set<SubCatgPackOptimization> subCatgPackOptimizationSet =  packOptAddDataMapper.setSubCatPackOpt(merchantPackOptimization, lvl3.getLvl4List());
        merchantPackOptimization.setSubCatgPackOptimization(subCatgPackOptimizationSet);
        MerchantPackOptimization merchantPackOptimization2 = new MerchantPackOptimization();
        MerchantPackOptimizationID merchantPackOptimizationID2 = new MerchantPackOptimizationID(planSizeAndPackDTO.getPlanId(),planSizeAndPackDTO.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(),2);
        merchantPackOptimization2.setMerchantPackOptimizationID(merchantPackOptimizationID2);
        Set<SubCatgPackOptimization> subCatgPackOptimizationSet2 =  packOptAddDataMapper.setSubCatPackOpt(merchantPackOptimization,  lvl3.getLvl4List());
        merchantPackOptimization2.setSubCatgPackOptimization(subCatgPackOptimizationSet2);
        List<MerchantPackOptimization> merchCatPackOptResult = new ArrayList<>();
        merchCatPackOptResult.add(merchantPackOptimization);
        merchCatPackOptResult.add(merchantPackOptimization2);
        List<Integer> count = new ArrayList<>();
        count.add(1);
        Mockito.when(sizeAndPackObjectMapper.getChannelListFromChannelId(1)).thenReturn(count);

        Set<MerchantPackOptimization> merchantPackOptimizationSet1 = merchCatPackOptResult.stream().filter(x->x.getMerchantPackOptimizationID().getChannelId().equals(1)).collect(Collectors.toSet());
        Mockito.when(merchPackOptimizationRepository.findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl0AndMerchantPackOptimizationID_repTLvl1AndMerchantPackOptimizationID_repTLvl2AndMerchantPackOptimizationID_repTLvl3(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(merchCatPackOptResult);
        Mockito.when(packOptAddDataMapper.setMerchCatPackOpt(planSizeAndPackDTO,lvl1,lvl2,lvl3,merchPackOptimizationRepository)).thenReturn(merchantPackOptimizationSet1);

        Set<MerchantPackOptimization> merchantPackOptimizationSet = packOptUpdateDataMapper.updateMerchCatPackOpt(planSizeAndPackDTO, lvl1, lvl2, lvl3, merchPackOptimizationRepository);
        assertTrue(!merchantPackOptimizationSet.isEmpty());
        List<MerchantPackOptimization> merchantPackOptimizationList = merchantPackOptimizationSet.stream().collect(Collectors.toList());
        assertEquals(100,merchantPackOptimizationList.get(0).getMerchantPackOptimizationID().getPlanId());
        assertEquals(221,merchantPackOptimizationList.get(0).getMerchantPackOptimizationID().getRepTLvl3());
        assertEquals(50,merchantPackOptimizationList.get(0).getMaxNbrOfPacks());
    }

}
