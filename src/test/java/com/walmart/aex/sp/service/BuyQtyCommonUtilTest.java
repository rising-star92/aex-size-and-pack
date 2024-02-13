package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BuyQtyCommonUtilTest {

    @InjectMocks
    private BuyQtyCommonUtil buyQtyCommonUtil;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    @Test
    void filterFinelinesWithSizesTest() throws IOException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInput();
        BuyQtyResponse buyQtyResponseFromStrategy = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        buyQtyCommonUtil.filterFinelinesWithSizes(buyQntyResponseDTOS,buyQtyResponseFromStrategy);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(BuyQntyMapperDTO.builder()
                .buyQntyResponseDTO(buyQntyResponseDTOS.get(0))
                .response(new BuyQtyResponse())
                .metadata(null)
                .requestFinelineNbr(null)
                .build());
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(BuyQntyMapperDTO.builder()
                .buyQntyResponseDTO(buyQntyResponseDTOS.get(1))
                .response(new BuyQtyResponse())
                .metadata(null)
                .requestFinelineNbr(null)
                .build());
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(BuyQntyMapperDTO.builder()
                .buyQntyResponseDTO(buyQntyResponseDTOS.get(2))
                .response(new BuyQtyResponse())
                .metadata(null)
                .requestFinelineNbr(null)
                .build());
    }

    @Test
    void filterStylesCcWithSizesTest() throws IOException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        BuyQtyResponse buyQtyResponseFromStrategy = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        buyQtyCommonUtil.filterStylesCcWithSizes(buyQntyResponseDTOS,buyQtyResponseFromStrategy,2855);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(BuyQntyMapperDTO.builder()
                .buyQntyResponseDTO(buyQntyResponseDTOS.get(0))
                .response(new BuyQtyResponse())
                .metadata(null)
                .requestFinelineNbr(2855)
                .build());
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(BuyQntyMapperDTO.builder()
                .buyQntyResponseDTO(buyQntyResponseDTOS.get(1))
                .response(new BuyQtyResponse())
                .metadata(null)
                .requestFinelineNbr(2855)
                .build());
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(BuyQntyMapperDTO.builder()
                .buyQntyResponseDTO(buyQntyResponseDTOS.get(2))
                .response(new BuyQtyResponse())
                .metadata(null)
                .requestFinelineNbr(2855)
                .build());

    }

    @Test
    void getBumpSetTest() throws IOException {
        try (MockedStatic<BuyQtyCommonUtil> mockedStatic = Mockito.mockStatic(BuyQtyCommonUtil.class, invocationOnMock -> {
            Method method = invocationOnMock.getMethod();
            if ("getBumpSet".equals(method.getName()) || "getBumpPackNbr".equals(method.getName())) {
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
            if ("getBumpSet".equals(method.getName()) || "getBumpPackNbr".equals(method.getName())) {
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

    @Test
    void getReplenishmentTest() {
        try (MockedStatic<BuyQtyCommonUtil> mockedStatic = Mockito.mockStatic(BuyQtyCommonUtil.class, invocationOnMock -> {
            Method method = invocationOnMock.getMethod();
            if ("getReplenishments".equals(method.getName()) || "sortReplenishments".equals(method.getName())) {
                return invocationOnMock.callRealMethod();
            } else {
                return invocationOnMock.getMock();
            }
        })) {
            StyleDto styleDto = new StyleDto();
            styleDto.setStyleNbr("34_3483_0_15_11");

            CustomerChoiceDto customerChoiceDto = new CustomerChoiceDto();
            customerChoiceDto.setCcId("34_3483_0_15_11_BLKSOT");

            MerchMethodsDto merchMethodsDto = new MerchMethodsDto();
            merchMethodsDto.setFixtureTypeRollupId(1);
            BQFPResponse bqfpResponse = BuyQtyResponseInputs.bQFPResponseFromJson("/bqfpServiceResponse");
            List<Replenishment> originalReplenishment = Optional.ofNullable(bqfpResponse.getStyles())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(style -> style.getStyleId().equalsIgnoreCase(styleDto.getStyleNbr()))
                    .findFirst()
                    .map(Style::getCustomerChoices)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(customerChoiceDto.getCcId()))
                    .findFirst()
                    .map(CustomerChoice::getFixtures)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(fixture -> merchMethodsDto.getFixtureTypeRollupId().equals(fixture.getFixtureTypeRollupId()))
                    .map(Fixture::getReplenishments)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            List<Replenishment> result = BuyQtyCommonUtil.getReplenishments(List.of(merchMethodsDto), bqfpResponse, styleDto, customerChoiceDto);

            assertNotNull(result);

            for (int i = 0; i < originalReplenishment.size(); i++) {
                assertEquals(originalReplenishment.get(i).getReplnWeek(), result.get(i).getReplnWeek());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
