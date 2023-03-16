package com.walmart.aex.sp.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.BumpSet;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BuyQtyCommonUtilTest {

    @InjectMocks
    private BuyQtyCommonUtil buyQtyCommonUtil;

    @Mock
    private BuyQtyResponseInputs buyQtyInputs;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void filterFinelinesWithSizesTest() throws IOException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInput();
        BuyQtyResponse buyQtyResponseFromStrategy = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyResponse = buyQtyCommonUtil.filterFinelinesWithSizes(buyQntyResponseDTOS,buyQtyResponseFromStrategy);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(0),new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(1),new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(2),new BuyQtyResponse(),null);

    }

    @Test
    void filterStylesCcWithSizesTest() throws IOException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        BuyQtyResponse buyQtyResponseFromStrategy = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyResponse = buyQtyCommonUtil.filterStylesCcWithSizes(buyQntyResponseDTOS,buyQtyResponseFromStrategy,2855);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(0),new BuyQtyResponse(),2855);
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(1),new BuyQtyResponse(),2855);
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTOS.get(2),new BuyQtyResponse(),2855);

    }

    @Test
    void getBumpSetTest() throws IOException {
        try (MockedStatic<BuyQtyCommonUtil> mockedStatic = Mockito.mockStatic(BuyQtyCommonUtil.class, invocationOnMock -> {
            Method method = invocationOnMock.getMethod();
            if ("getBumpSet".equals(method.getName())) {
                return invocationOnMock.callRealMethod();
            } else {
                return invocationOnMock.getMock();
            }
        })) {
            BQFPResponse bqfpResponse = BuyQtyResponseInputs.bQFPResponseFromJson("/bqfpServiceResponse");
            BumpSet bumpSet = BuyQtyCommonUtil.getBumpSet(bqfpResponse, "73_3483-BP2", "34_3483_4_19_8", "34_3483_4_19_8_BLCOVE", "WALLS", 1);

            assertNotNull(bumpSet);
            assertEquals(2, bumpSet.getBumpPackNbr());
            assertEquals("FYE2023WK50", bumpSet.getWeekDesc().trim());
            assertEquals(12250, bumpSet.getWmYearWeek());
        }

    }

    @Test
    void getBumpSetNullTest() throws IOException {
        try (MockedStatic<BuyQtyCommonUtil> mockedStatic = Mockito.mockStatic(BuyQtyCommonUtil.class, invocationOnMock -> {
            Method method = invocationOnMock.getMethod();
            if ("getBumpSet".equals(method.getName())) {
                return invocationOnMock.callRealMethod();
            } else {
                return invocationOnMock.getMock();
            }
        })) {
            BQFPResponse bqfpResponse = BuyQtyResponseInputs.bQFPResponseFromJson("/bqfpServiceResponse");
            BumpSet bumpSet = BuyQtyCommonUtil.getBumpSet(bqfpResponse, "73_3483-BP2", "34_3483_0_15_11", "34_3483_0_15_11_BLKSOT", "WALLS", 1);

            assertNotNull(bumpSet);
            assertEquals(new BumpSet(), bumpSet);
        }
    }

    @Test
    void getInStoreWeekTest() {
        try (MockedStatic<BuyQtyCommonUtil> mockedStatic = Mockito.mockStatic(BuyQtyCommonUtil.class, invocationOnMock -> {
            Method method = invocationOnMock.getMethod();
            if ("getInStoreWeek".equals(method.getName()) || "formatWeekDesc".equals(method.getName())) {
                return invocationOnMock.callRealMethod();
            } else {
                return invocationOnMock.getMock();
            }
        })) {
            BumpSet bumpSet = new BumpSet();
            bumpSet.setBumpPackNbr(2);
            bumpSet.setWeekDesc("FYE2023WK50    ");
            bumpSet.setWmYearWeek(12250);

            String inStoreWeek = BuyQtyCommonUtil.getInStoreWeek(bumpSet);

            assertNotNull(inStoreWeek);
            assertEquals("202350", inStoreWeek);
        }
    }

}
