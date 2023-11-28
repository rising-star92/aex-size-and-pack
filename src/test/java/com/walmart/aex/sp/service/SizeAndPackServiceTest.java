package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.commitmentreport.*;
import com.walmart.aex.sp.dto.packoptimization.packDescription.PackDescCustChoiceDTO;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDeleteDTO;
import com.walmart.aex.sp.dto.planhierarchy.SizeAndPackResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.walmart.aex.sp.util.BuyQtyResponseInputs.convertChannelToStore;
import static com.walmart.aex.sp.util.SizeAndPackTest.getPlanSizeAndPackDeleteDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SizeAndPackServiceTest {

    @Mock
    private SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    @Mock
    private SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;

    private SizeAndPackService sizeAndPackService;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    @Mock
    private BuyQtyCommonUtil buyQtyCommonUtil;

    @Mock
    private StrategyFetchService strategyFetchService;

    @Mock
    private SizeAndPackDeleteService sizeAndPackDeleteService;

    @Mock
    private BigQueryInitialSetPlanService bigQueryInitialSetPlanService;

    @Mock
    private SizeAndPackObjectMapper sizeAndPackObjectMapper;

    @Mock
    private MerchCatPlanRepository merchCatPlanRepository;

    @Mock
    private SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;

    @Mock
    private SizeAndPackDeletePlanService sizeAndPackDeletePlanService;

    private InitialSetPlanMapper initialSetPlanMapper;

    @Mock
    private MerchPackOptimizationRepository merchPackOptimizationRepository;

    @Mock
    private PackOptUpdateDataMapper packOptUpdateDataMapper;

    @Mock
    private PackOptAddDataMapper packOptAddDataMapper;

    @Mock
    private BigQueryPackStoresService bigQueryPackStoresService;

    @Mock
    private SizeAndPackDeletePackOptMapper sizeAndPackDeletePackOptMapper;

    @Mock
    private CustomerChoiceRepository customerChoiceRepository;

    @Mock
    private BigQueryConnectionProperties bigQueryConnectionProperties;

    private static Integer fineline1Nbr = 151;
    private static String styleNbr = "151_2_23_001";
    private static String ccId = "151_2_23_001_001";

    @BeforeEach
    void setup() {
        initialSetPlanMapper = new InitialSetPlanMapper();
        sizeAndPackService = new SizeAndPackService(spFineLineChannelFixtureRepository, buyQuantityMapper, spCustomerChoiceChannelFixtureRepository, sizeAndPackObjectMapper,
                merchCatPlanRepository, strategyFetchService, spCustomerChoiceChannelFixtureSizeRepository, sizeAndPackDeleteService, sizeAndPackDeletePlanService,
                buyQtyCommonUtil, bigQueryInitialSetPlanService, initialSetPlanMapper, merchPackOptimizationRepository, packOptUpdateDataMapper, packOptAddDataMapper,
                bigQueryPackStoresService, sizeAndPackDeletePackOptMapper, customerChoiceRepository);
        ReflectionTestUtils.setField(sizeAndPackService, "bigQueryConnectionProperties", bigQueryConnectionProperties);
        lenient().when(bigQueryConnectionProperties.getPackDescriptionFeatureFlag()).thenReturn("true");
    }

    @Test
    void fetchFinelineBuyQntyTest() throws IOException, SizeAndPackException {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInput();
        convertChannelToStore(buyQntyResponseDTOS);
        Mockito.when(spFineLineChannelFixtureRepository.getBuyQntyByPlanChannel(471l, 1)).thenReturn(buyQntyResponseDTOS);
        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForStore();
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = sizeAndPackService.fetchFinelineBuyQnty(buyQtyRequest);
        assertEquals(471, buyQtyRequest.getPlanId());
    }

    @Test
    void fetchCcBuyQtyTest() throws IOException, SizeAndPackException {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        convertChannelToStore(buyQntyResponseDTOS);
        Mockito.when(spCustomerChoiceChannelFixtureRepository.getBuyQntyByPlanChannelFineline(471l, 1,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForStore();
        buyQtyRequest.setFinelineNbr(2855);
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest, 2855)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = sizeAndPackService.fetchCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471, buyQtyRequest.getPlanId());
    }

    @Test
    void deleteSizeAndPackDataFinelineTest() throws SizeAndPackException {
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(fineline1Nbr, null, null);
        doNothing().when(sizeAndPackDeleteService).deleteSizeAndPackDataAtFl(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        assertEquals("Success", sizeAndPackResponse.getStatus());
    }

    @Test
    void deleteSizeAndPackDataStyleTest() throws SizeAndPackException {
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(fineline1Nbr, styleNbr, null);
        doNothing().when(sizeAndPackDeleteService).deleteSizeAndPackDataAtStyleOrCC(Mockito.anyList(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        assertEquals("Success", sizeAndPackResponse.getStatus());
    }

    @Test
    void deleteSizeAndPackDataCCTest() throws SizeAndPackException {
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(fineline1Nbr, styleNbr, ccId);
        doNothing().when(sizeAndPackDeleteService).deleteSizeAndPackDataAtStyleOrCC(Mockito.anyList(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        assertEquals("Success", sizeAndPackResponse.getStatus());
    }

    @Test
    void deleteSizeAndPackDataFinelineTestWithNullStrongKey() throws SizeAndPackException {
        PlanSizeAndPackDeleteDTO request = getPlanSizeAndPackDeleteDTO(null, null, null);
        SizeAndPackResponse sizeAndPackResponse = sizeAndPackService.deleteSizeAndPackData(request);
        assertEquals("Failed", sizeAndPackResponse.getStatus());
    }

    @Test
    void getInitialSetAndBumpSetDetailsTest() {
        PackDescCustChoiceDTO packDescCustChoiceDTO = new PackDescCustChoiceDTO();
        packDescCustChoiceDTO.setCcId("34_3463_2_21_3_BKRINS");
        packDescCustChoiceDTO.setColorName("WHITE");
        packDescCustChoiceDTO.setAltFinelineDesc("3463 - FINELINE DESC");

        InitialSetPackRequest request = new InitialSetPackRequest();
        request.setPlanId(114);
        request.setFinelineNbr(3463);
        when(bigQueryInitialSetPlanService.getInitialAndBumpSetDetails(any(InitialSetPackRequest.class))).thenReturn(getRFAInitialSetBumpSetData());
        when(customerChoiceRepository.getCustomerChoicesByFinelineAndPlanId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(packDescCustChoiceDTO));
        InitialBumpSetResponse response = sizeAndPackService.getInitialAndBumpSetDetails(request);

        PackDetails packDetails = response.getIntialSetStyles().stream()
                .filter(style -> style.getStyleId().equalsIgnoreCase("34_3463_2_21_3"))
                .map(InitialSetStyle::getInitialSetPlan)
                .flatMap(Collection::stream)
                .filter(is -> is.getInStoreWeek().equalsIgnoreCase("202510"))
                .map(InitialSetPlan::getPackDetails)
                .flatMap(Collection::stream)
                .filter(pack -> pack.getPackId().equalsIgnoreCase("SP_bs114_3463_0_34_3463_2_21_3_BKRINS_HANGING_0"))
                .findFirst().orElse(null);

        assertEquals("34_3463_2_21_3", response.getIntialSetStyles().get(0).getStyleId());
        assertEquals(3, response.getIntialSetStyles().get(0).getInitialSetPlan().size());
        assertNotNull(packDetails);
        assertEquals("3463 - FINELINE DESC_WHITE_HANGING_BP1_01", packDetails.getPackDescription());
        assertEquals(1, packDetails.getBumpPackNbr());
        assertEquals("4d820910-5b63-4bda-97d8-7b1d79de89a8", packDetails.getUuId());
    }

    private List<RFAInitialSetBumpSetResponse> getRFAInitialSetBumpSetData() {
        RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse1 = new RFAInitialSetBumpSetResponse();
        rfaInitialSetBumpSetResponse1.setStyle_id("34_3463_2_21_3");
        rfaInitialSetBumpSetResponse1.setIn_store_week("202504");
        rfaInitialSetBumpSetResponse1.setCc("34_3463_2_21_3_BKRINS");
        rfaInitialSetBumpSetResponse1.setMerch_method("HANGING");
        rfaInitialSetBumpSetResponse1.setPack_id("SP_is114_3463_0_34_3463_2_21_3_BKRINS_HANGING_0");
        rfaInitialSetBumpSetResponse1.setSize("16W");
        rfaInitialSetBumpSetResponse1.setInitialpack_ratio(3);
        rfaInitialSetBumpSetResponse1.setIs_quantity(1488);
        rfaInitialSetBumpSetResponse1.setUuid("c40e4359-9c7e-4915-ad2d-98980dcf8f82");
        rfaInitialSetBumpSetResponse1.setProduct_fineline("114_3463");

        RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse2 = new RFAInitialSetBumpSetResponse();
        rfaInitialSetBumpSetResponse2.setStyle_id("34_3463_2_21_3");
        rfaInitialSetBumpSetResponse2.setIn_store_week("202510");
        rfaInitialSetBumpSetResponse2.setCc("34_3463_2_21_3_BKRINS");
        rfaInitialSetBumpSetResponse2.setMerch_method("HANGING");
        rfaInitialSetBumpSetResponse2.setPack_id("SP_bs114_3463_0_34_3463_2_21_3_BKRINS_HANGING_0");
        rfaInitialSetBumpSetResponse2.setSize("16W");
        rfaInitialSetBumpSetResponse2.setBumppack_ratio(2);
        rfaInitialSetBumpSetResponse2.setBs_quantity(982);
        rfaInitialSetBumpSetResponse2.setUuid("4d820910-5b63-4bda-97d8-7b1d79de89a8");
        rfaInitialSetBumpSetResponse2.setBumpPackNbr(1);

        RFAInitialSetBumpSetResponse rfaInitialSetBumpSetResponse3 = new RFAInitialSetBumpSetResponse();
        rfaInitialSetBumpSetResponse3.setStyle_id("34_3463_2_21_3");
        rfaInitialSetBumpSetResponse3.setIn_store_week("202515");
        rfaInitialSetBumpSetResponse3.setCc("34_3463_2_21_3_BKRINS");
        rfaInitialSetBumpSetResponse3.setMerch_method("HANGING");
        rfaInitialSetBumpSetResponse3.setPack_id("SP_bs114_3463-BP2_0_34_3463_2_21_3_BKRINS_HANGING_0");
        rfaInitialSetBumpSetResponse3.setSize("16W");
        rfaInitialSetBumpSetResponse3.setBumppack_ratio(2);
        rfaInitialSetBumpSetResponse3.setBs_quantity(878);
        rfaInitialSetBumpSetResponse3.setUuid("b591d985-bdd2-4487-9933-c1ea8f542edb");
        rfaInitialSetBumpSetResponse3.setBumpPackNbr(2);

        return List.of(rfaInitialSetBumpSetResponse1, rfaInitialSetBumpSetResponse2, rfaInitialSetBumpSetResponse3);
    }
}
