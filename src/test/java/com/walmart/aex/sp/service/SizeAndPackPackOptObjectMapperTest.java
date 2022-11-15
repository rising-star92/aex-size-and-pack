package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.MerchPackOptimizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SizeAndPackPackOptObjectMapperTest {

    @InjectMocks
    PackOptAddDataMapper packOptAddDataMapper;

    @Mock
    SizeAndPackObjectMapper sizeAndPackObjectMapper;

    @Mock
    MerchPackOptimizationRepository merchPackOptimizationRepository;


    private SizeAndPackObjectMapperTest sizeAndPackObjectMapperTest;

    @Test
    void testAddLinePlanEventsToPackOpt() {
        sizeAndPackObjectMapperTest=new SizeAndPackObjectMapperTest();
        PlanSizeAndPackDTO planSizeAndPackDTO = sizeAndPackObjectMapperTest.getPlanSizeAndPackDTOObj();
        Lvl1 lvl1 = planSizeAndPackDTO.getLvl1List().get(0);
        Lvl2 lvl2 = lvl1.getLvl2List().get(0);
        Lvl3 lvl3 = lvl2.getLvl3List().get(0);
        Mockito.when(sizeAndPackObjectMapper.getChannelListFromChannelId(1)).thenCallRealMethod();
        Set<MerchantPackOptimization> merchantPackOptimizationSet = packOptAddDataMapper.setMerchCatPackOpt(planSizeAndPackDTO, lvl1, lvl2, lvl3,merchPackOptimizationRepository);
        assertTrue(!merchantPackOptimizationSet.isEmpty());
        List<MerchantPackOptimization> merchantPackOptimizationList = merchantPackOptimizationSet.stream().collect(Collectors.toList());
        assertEquals(100,merchantPackOptimizationList.get(0).getMerchantPackOptimizationID().getPlanId());
        assertEquals(221,merchantPackOptimizationList.get(0).getMerchantPackOptimizationID().getRepTLvl3());
        assertEquals(50,merchantPackOptimizationList.get(0).getMaxNbrOfPacks());
    }

}
