package com.walmart.aex.sp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.FinelineReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.SubCatgReplPack;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.SizeLevelReplenishmentRepository;
import com.walmart.aex.sp.repository.SizeListReplenishmentRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceReplenishmentRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplenishmentServiceTest {

    @Mock
    private FineLineReplenishmentRepository fineLineReplenishmentRepository;

    @Mock
    private SpCustomerChoiceReplenishmentRepository spCustomerChoiceReplenishmentRepository;

    @Mock
    private SizeListReplenishmentRepository sizeListReplenishmentRepository;

    @Mock
    private CatgReplnPkConsRepository catgReplnPkConsRepository;

    @Mock
    private SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

    @Mock
    private FinelineReplnPkConsRepository finelineReplnPkConsRepository;

    @Mock
    private StyleReplnPkConsRepository styleReplnConsRepository;

    @Mock
    private CcReplnPkConsRepository ccReplnConsRepository;

    @Mock
    private CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    @Spy
    private ReplenishmentMapper replenishmentMapper;

    @Mock
    private UpdateReplnConfigMapper updateReplnConfigMapper;

    @Mock
    private ReplenishmentService replenishmentService;

    @Mock
    private BuyQtyCommonUtil buyQtyCommonUtil;

    @InjectMocks
    private ReplenishmentService replenishmentService1;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    @Mock
    private StrategyFetchService strategyFetchService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private BuyQtyResponseInputs buyQtyInputs;

    @Mock
    private CcMmReplnPkConsRepository ccMmReplnPkConsRepository;

    List<ReplenishmentResponseDTO> replenishmentResponseDTOS = new ArrayList<>();
    @Mock
    private ReplenishmentRequest replenishmentRequest;

    @Mock
    private SizeLevelReplenishmentRepository sizeLevelReplenishmentRepository;
    @Mock
    private SizeLevelReplenishmentMapper sizeLevelReplenishmentMapper;

    @Captor
    ArgumentCaptor<List<BuyQntyResponseDTO>> buyQntyResponseDTOCaptor;

    @Captor
    ArgumentCaptor<SizeDto> sizeDtoArgumentCaptor;

    @Captor
    ArgumentCaptor<List<MerchCatgReplPack>> merchCatgReplPackCaptor;

    @Captor
    ArgumentCaptor<List<SubCatgReplPack>> listArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> vnpkArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> whpkArgumentCaptor;

    @Captor
    ArgumentCaptor<ReplenishmentResponseDTO> replenishmentResponseDTOArgumentCaptor;

    @Captor
    ArgumentCaptor<ReplenishmentResponse> replenishmentResponseArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> finelineNbrCaptor;

    @Captor
    ArgumentCaptor<List<StyleReplPack>> styleReplListCaptor;

    @Captor
    ArgumentCaptor<List<FinelineReplPack>> fineLineListcaptor;

    @Captor
    ArgumentCaptor<List<CcReplPack>> ccReplPackLisrcaptor;

    @Captor
    ArgumentCaptor<List<CcMmReplPack>> ccMmReplPackLisrcaptor;

    @Captor
    ArgumentCaptor<List<CcSpMmReplPack>> ccSpMmReplPackList;

    @BeforeEach
    void init() {
        replenishmentResponseDTOS.clear();
    }

    @Test
    void updateVnpkWhpkForCatgReplnConsTest() {
        UpdateVnPkWhPkReplnRequest request = new UpdateVnPkWhPkReplnRequest();
        request.setPlanId(1L);
        request.setChannel("Store");
        request.setLvl3Nbr(3);
        request.setVnpk(1);
        request.setWhpk(1);
        replenishmentService.updateVnpkWhpkForCatgReplnCons(request);
        Mockito.verify(replenishmentService, Mockito.times(1)).updateVnpkWhpkForCatgReplnCons(request);

    }

    @Test
    void fetchFinelineBuyQtyTest() throws IOException, SizeAndPackException {

        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInput();
        when(fineLineReplenishmentRepository.getBuyQntyByPlanChannelOnline(471l, 2)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForOnline();
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyFinalResponse = new BuyQtyResponse();

        when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineFinelineBuyQnty(buyQtyRequest);
        assertEquals(471, buyQtyRequest.getPlanId());
    }

    @Test
    void fetchCcBuyQtyTest() throws IOException, SizeAndPackException {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        when(spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(471l, 2,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForOnline();
        buyQtyRequest.setFinelineNbr(2855);
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest, 2855)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471, buyQtyRequest.getPlanId());

    }

    @Test
    void fetchSizeBuyQtyTest() throws IOException, SizeAndPackException {

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(88L, 50000, 34, 6420,
                12238, 31526, 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", 3174, "L",
                1125, 1125, 1125);
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        when(sizeListReplenishmentRepository.getSizeBuyQntyByPlanChannelOnlineCc(88L, 2,
                "34_5471_3_24_001_CHINO TAN")).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(88L);
        buyQtyRequest.setChannel("Online");
        buyQtyRequest.setCcId("34_5471_3_24_001_CHINO TAN");

        BuyQtyResponse buyQtyResponse = BuyQtyResponseInputs.buyQtyResponseFromJson("/sizeProfileResponse");

        when(strategyFetchService.getBuyQtyResponseSizeProfile(buyQtyRequest)).thenReturn(buyQtyResponse);

        BuyQtyResponse buyQtyResponse1 = replenishmentService1.fetchOnlineSizeBuyQnty(buyQtyRequest);

        Mockito.verify(buyQuantityMapper, Mockito.times(5)).mapBuyQntySizeSp(Mockito.any(), Mockito.any());

    }

    @Test
    void testUpdateVnpkWhpkForCatgReplnCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<MerchCatgReplPack> catgReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("online");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(3);
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        when(catgReplnPkConsRepository.getCatgReplnConsData(12l, 2, 3)).thenReturn(catgReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForCatgReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCatgReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(updateReplnConfigMapper).updateVnpkWhpkForCatgReplnConsMapper(merchCatgReplPackCaptor.capture(), vnpkArgumentCaptor.capture(), whpkArgumentCaptor.capture());
        List<MerchCatgReplPack> merchCatgReplPacks = merchCatgReplPackCaptor.getValue();
        Integer vnpk = vnpkArgumentCaptor.getValue();
        Integer whpk = whpkArgumentCaptor.getValue();
    }

    @Test
    void testUpdateVnpkWhpkForSubCatgReplnCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<SubCatgReplPack> subCatgReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        when(subCatgReplnPkConsRepository.getSubCatgReplnConsData(12l, 1, 12231, 31516)).thenReturn(subCatgReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForSubCatgReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForSubCatgReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(updateReplnConfigMapper).updateVnpkWhpkForSubCatgReplnConsMapper(listArgumentCaptor.capture(), vnpkArgumentCaptor.capture(), whpkArgumentCaptor.capture());
        List<SubCatgReplPack> subCatgReplPackList = listArgumentCaptor.getValue();
        Integer vnpk = vnpkArgumentCaptor.getValue();
        Integer whpk = whpkArgumentCaptor.getValue();
        assertNotNull(subCatgReplPackList);
        assertNotNull(vnpk);
        assertNotNull(whpk);
        assertEquals(vnpk, 2);
        assertEquals(whpk, 1);
    }


    @Test
    void testUpdateVnpkWhpkForFinelineReplnCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<FinelineReplPack> finelineReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setFineline(1021);
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        when(finelineReplnPkConsRepository.getFinelineReplnConsData(12l, 1, 12231, 31516, 1021)).thenReturn(finelineReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForFinelineReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForFinelineReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(updateReplnConfigMapper).updateVnpkWhpkForFinelineReplnConsMapper(fineLineListcaptor.capture(), vnpkArgumentCaptor.capture(), whpkArgumentCaptor.capture());
        List<FinelineReplPack> finelineReplPacks = fineLineListcaptor.getValue();
        assertEquals(finelineReplPacks.size(), 0);
        Integer vnpk = vnpkArgumentCaptor.getValue();
        Integer whpk = whpkArgumentCaptor.getValue();
        assertNotNull(finelineReplPacks);
        assertNotNull(vnpk);
        assertNotNull(whpk);
    }


    @Test
    void testUpdateVnpkWhpkForStyleReplnCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<StyleReplPack> styleReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setFineline(1021);
        updateVnPkWhPkReplnRequest.setStyle("34_1021_2_21_2");
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        when(styleReplnConsRepository.getStyleReplnConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2")).thenReturn(styleReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForStyleReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForStyleReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(updateReplnConfigMapper).updateVnpkWhpkForStyleReplnConsMapper(styleReplListCaptor.capture(), vnpkArgumentCaptor.capture(), whpkArgumentCaptor.capture());
        List<StyleReplPack> styleReplPacks = styleReplListCaptor.getValue();
        Integer vnpk = vnpkArgumentCaptor.getValue();
        Integer whpk = whpkArgumentCaptor.getValue();
        assertNotNull(styleReplPacks);
        assertNotNull(vnpk);
        assertNotNull(whpk);
    }


    @Test
    void testUpdateVnpkWhpkForCcReplnPkCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<CcReplPack> ccReplnPkConsList1 = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setFineline(1021);
        updateVnPkWhPkReplnRequest.setStyle("34_1021_2_21_2");
        updateVnPkWhPkReplnRequest.setCustomerChoice("34_1021_2_21_2_AURA ORANGE STENCIL");
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        when(ccReplnConsRepository.getCcReplnConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(ccReplnPkConsList1);
        replenishmentService1.updateVnpkWhpkForCcReplnPkCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcReplnPkConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(updateReplnConfigMapper).updateVnpkWhpkForCcReplnPkConsMapper(ccReplPackLisrcaptor.capture(), vnpkArgumentCaptor.capture(), whpkArgumentCaptor.capture());
        List<CcReplPack> ccReplPacks = ccReplPackLisrcaptor.getValue();
        Integer vnpk = vnpkArgumentCaptor.getValue();
        Integer whpk = whpkArgumentCaptor.getValue();
        assertNotNull(ccReplPacks);
        assertNotNull(vnpk);
        assertNotNull(whpk);
    }


    @Test
    void testUpdateVnPkWhPkCcMerchMethodReplnCon() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<CcMmReplPack> ccMmReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setFineline(1021);
        updateVnPkWhPkReplnRequest.setStyle("34_1021_2_21_2");
        updateVnPkWhPkReplnRequest.setCustomerChoice("34_1021_2_21_2_AURA ORANGE STENCIL");
        updateVnPkWhPkReplnRequest.setMerchMethodDesc("folded");
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        when(ccMmReplnPkConsRepository.getCcMmReplnPkConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL", "folded")).thenReturn(ccMmReplnPkConsList);
        replenishmentService1.updateVnPkWhPkCcMerchMethodReplnCon(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcMmReplnPkConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(updateReplnConfigMapper).updateVnpkWhpkForCcMmReplnPkConsMapper(ccMmReplPackLisrcaptor.capture(), vnpkArgumentCaptor.capture(), whpkArgumentCaptor.capture());
        List<CcMmReplPack> ccMmReplPacks = ccMmReplPackLisrcaptor.getValue();
        Integer vnpk = vnpkArgumentCaptor.getValue();
        Integer whpk = whpkArgumentCaptor.getValue();
        assertNotNull(ccMmReplPacks);
        assertNotNull(vnpk);
        assertNotNull(whpk);
    }


    @Test
    void testUpdateVnPkWhPkCcSpSizeReplnCon() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<CcSpMmReplPack> ccSpReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setFineline(1021);
        updateVnPkWhPkReplnRequest.setStyle("34_1021_2_21_2");
        updateVnPkWhPkReplnRequest.setCustomerChoice("34_1021_2_21_2_AURA ORANGE STENCIL");
        updateVnPkWhPkReplnRequest.setMerchMethodDesc("folded");
        updateVnPkWhPkReplnRequest.setAhsSizeId(246);
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        when(ccSpReplnPkConsRepository.getCcSpMmReplnPkConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL", "folded", 246)).thenReturn(ccSpReplnPkConsList);
        replenishmentService1.updateVnPkWhPkCcSpSizeReplnCon(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcSpMmReplnPkConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(updateReplnConfigMapper).updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpMmReplPackList.capture(), vnpkArgumentCaptor.capture(), whpkArgumentCaptor.capture());
        List<CcSpMmReplPack> ccMmReplPacks = ccSpMmReplPackList.getValue();
        Integer vnpk = vnpkArgumentCaptor.getValue();
        Integer whpk = whpkArgumentCaptor.getValue();
        assertNotNull(ccMmReplPacks);
        assertNotNull(vnpk);
        assertNotNull(whpk);
    }

    @Test
    void testfetchSizeListReplenishment() {
        replenishmentRequest = new ReplenishmentRequest();
        ReplenishmentResponseDTO replenishmentResponseDTO1 = new ReplenishmentResponseDTO(88L, 50000, null, 34, "123", 6420,
                "folded", 12238, "wall", 31526, "rack", 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", "4321", 3174, 543, 12, 45, 54.0, 34, "L",
                "hanging", 1125, 1125, 1125, 2511, 12.0, 5431);

        replenishmentResponseDTOS.add(replenishmentResponseDTO1);
        replenishmentRequest.setPlanId(12l);
        replenishmentRequest.setChannel("store");
        replenishmentRequest.setFinelineNbr(1021);
        replenishmentRequest.setStyleNbr("34_1021_2_21_2");
        replenishmentRequest.setCcId("34_1021_2_21_2_AURA ORANGE STENCIL");
        when(sizeListReplenishmentRepository.getReplenishmentPlanChannelFinelineCc(12l, 1, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(replenishmentResponseDTOS);
        doNothing().when(replenishmentMapper).mapReplenishmentLvl2Sp(any(), any(), any(), any());
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchSizeListReplenishment(replenishmentRequest);
        Mockito.verify(replenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(replenishmentMapper).mapReplenishmentLvl2Sp(replenishmentResponseDTOArgumentCaptor.capture(), replenishmentResponseArgumentCaptor.capture(), finelineNbrCaptor.capture(), Mockito.any());
        ReplenishmentResponseDTO replenishmentResponseDTO = replenishmentResponseDTOArgumentCaptor.getValue();
        ReplenishmentResponse replenishmentResponse1 = replenishmentResponseArgumentCaptor.getValue();
        Integer fineLineNbr = finelineNbrCaptor.getValue();
        assertNotNull(replenishmentResponseDTO);
        assertNotNull(replenishmentResponse1);
        assertNotNull(fineLineNbr);
    }

    @Test
    void testFetchFinelineReplenishment() {
        replenishmentRequest = new ReplenishmentRequest();
        ReplenishmentResponseDTO replenishmentResponseDTO1 = new ReplenishmentResponseDTO(88L, 50000, null, 34, "123", 6420,
                "folded", 12238, "wall", 31526, "rack", 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", "4321", 3174, 543, 12, 45, 54.0, 34, "L",
                "hanging", 1125, 1125, 1125, 2511, 12.0, 5431);

        replenishmentResponseDTOS.add(replenishmentResponseDTO1);
        replenishmentRequest.setPlanId(12l);
        replenishmentRequest.setChannel("store");
        when(fineLineReplenishmentRepository
                .getByPlanChannel(12l, 1)).thenReturn(replenishmentResponseDTOS);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchFinelineReplenishment(replenishmentRequest);
        Mockito.verify(replenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(replenishmentMapper).mapReplenishmentLvl2Sp(replenishmentResponseDTOArgumentCaptor.capture(), replenishmentResponseArgumentCaptor.capture(), finelineNbrCaptor.capture(), Mockito.any());
        ReplenishmentResponseDTO replenishmentResponseDTO = replenishmentResponseDTOArgumentCaptor.getValue();
        ReplenishmentResponse replenishmentResponse1 = replenishmentResponseArgumentCaptor.getValue();
        Integer fineLineNbr = finelineNbrCaptor.getValue();
        assertNotNull(replenishmentResponseDTO);
        assertNotNull(replenishmentResponse1);
        assertNull(fineLineNbr);
    }


    @Test
    void testFetchCcReplenishment() {
        replenishmentRequest = new ReplenishmentRequest();
        ReplenishmentResponseDTO replenishmentResponseDTO1 = new ReplenishmentResponseDTO(88L, 50000, null, 34, "123", 6420,
                "folded", 12238, "wall", 31526, "rack", 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", "4321", 3174, 543, 12, 45, 54.0, 34, "L",
                "hanging", 1125, 1125, 1125, 2511, 12.0, 5431);

        replenishmentResponseDTOS.add(replenishmentResponseDTO1);
        replenishmentRequest.setPlanId(12l);
        replenishmentRequest.setChannel("store");
        replenishmentRequest.setFinelineNbr(1021);
        when(spCustomerChoiceReplenishmentRepository
                .getReplenishmentByPlanChannelFineline(12l, 1, 1021)).thenReturn(replenishmentResponseDTOS);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchCcReplenishment(replenishmentRequest);
        Mockito.verify(replenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(replenishmentMapper).mapReplenishmentLvl2Sp(replenishmentResponseDTOArgumentCaptor.capture(), replenishmentResponseArgumentCaptor.capture(), finelineNbrCaptor.capture(), Mockito.any());
        ReplenishmentResponseDTO replenishmentResponseDTO = replenishmentResponseDTOArgumentCaptor.getValue();
        ReplenishmentResponse replenishmentResponse1 = replenishmentResponseArgumentCaptor.getValue();
        Integer fineLineNbr = finelineNbrCaptor.getValue();
        assertNotNull(replenishmentResponseDTO);
        assertNotNull(replenishmentResponse1);
        assertNotNull(fineLineNbr);
    }


    @Test
    void testFetchSizeListReplenishmentFullHierarchy() {
        replenishmentRequest = new ReplenishmentRequest();
        ReplenishmentResponseDTO replenishmentResponseDTO1 = new ReplenishmentResponseDTO(88L, 50000, null, 34, "123", 6420,
                "folded", 12238, "wall", 31526, "rack", 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", "4321", 3174, 543, 12, 45, 54.0, 34, "L",
                "hanging", 1125, 1125, 1125, 2511, 12.0, 5431);

        replenishmentResponseDTOS.add(replenishmentResponseDTO1);
        replenishmentRequest.setPlanId(12l);
        replenishmentRequest.setChannel("store");
        replenishmentRequest.setFinelineNbr(1021);
        when(sizeLevelReplenishmentRepository
                .getReplnFullHierarchyByPlanFineline(12l, 1, 1021)).thenReturn(replenishmentResponseDTOS);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchSizeListReplenishmentFullHierarchy(replenishmentRequest);
        Mockito.verify(sizeLevelReplenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(sizeLevelReplenishmentMapper).mapReplenishmentLvl2Sp(replenishmentResponseDTOArgumentCaptor.capture(), replenishmentResponseArgumentCaptor.capture(), finelineNbrCaptor.capture());
        ReplenishmentResponseDTO replenishmentResponseDTO = replenishmentResponseDTOArgumentCaptor.getValue();
        ReplenishmentResponse replenishmentResponse1 = replenishmentResponseArgumentCaptor.getValue();
        Integer fineLineNbr = finelineNbrCaptor.getValue();
        assertNotNull(replenishmentResponseDTO);
        assertNotNull(replenishmentResponse1);
        assertNotNull(fineLineNbr);
    }

    @Test
    void test_fetchFinelineReplenishmentShouldReturnRollupValuesForReplenishmentUnitsAndPacks() throws JsonProcessingException {
        List<ReplenishmentResponseDTO> replenishmentResponseDTOS = getFineLineReplenishmentByPlanAndChannel();
        when(fineLineReplenishmentRepository.getByPlanChannel(anyLong(), anyInt())).thenReturn(replenishmentResponseDTOS);
        ReplenishmentRequest request = new ReplenishmentRequest();
        request.setChannel("store");
        request.setPlanId(73L);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchFinelineReplenishment(request);
        List<MetricsDto> metricsDtos = replenishmentResponse.getLvl3List().stream().map(Lvl3Dto::getLvl4List)
                .flatMap(Collection::stream).map(Lvl4Dto::getFinelines).flatMap(Collection::stream).map(FinelineDto::getMetrics).collect(Collectors.toList());
        metricsDtos.forEach(metricsDto -> {
            assertEquals(metricsDto.getReplenishmentPacks(), (metricsDto.getFinalReplenishmentQty() / metricsDto.getVendorPack()));
        });
    }

    @Test
    void test_fetchReplnByPlanFinelineShouldReturnRollupValuesForReplenishmentUnitsAndPacks() throws JsonProcessingException {
        List<ReplenishmentResponseDTO> replenishmentResponseDTOS = getReplenishmentByPlanIdChannelAndFineLineNbr();
        when(spCustomerChoiceReplenishmentRepository
                .getReplenishmentByPlanChannelFineline(anyLong(), anyInt(), anyInt())).thenReturn(replenishmentResponseDTOS);
        ReplenishmentRequest request = new ReplenishmentRequest();
        request.setChannel("store");
        request.setPlanId(73L);
        request.setFinelineNbr(2852);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchCcReplenishment(request);
        List<MetricsDto> styleMetricsDtos = replenishmentResponse.getLvl3List().stream().map(Lvl3Dto::getLvl4List).flatMap(Collection::stream)
                .map(Lvl4Dto::getFinelines).flatMap(Collection::stream).map(FinelineDto::getStyles).flatMap(Collection::stream).map(StyleDto::getMetrics).collect(Collectors.toList());
        List<MetricsDto> ccMetrics = replenishmentResponse.getLvl3List().stream().map(Lvl3Dto::getLvl4List).flatMap(Collection::stream)
                .map(Lvl4Dto::getFinelines).flatMap(Collection::stream).map(FinelineDto::getStyles).flatMap(Collection::stream).map(StyleDto::getCustomerChoices)
                .flatMap(Collection::stream).map(CustomerChoiceDto::getMetrics).collect(Collectors.toList());

        styleMetricsDtos.forEach(metricsDto -> assertEquals(metricsDto.getReplenishmentPacks(), (metricsDto.getFinalReplenishmentQty() / metricsDto.getVendorPack())));
        ccMetrics.forEach(metricsDto -> assertEquals(metricsDto.getReplenishmentPacks(), (metricsDto.getFinalReplenishmentQty() / metricsDto.getVendorPack())));

        assertEquals(styleMetricsDtos.iterator().next().getReplenishmentPacks(), ccMetrics.stream().mapToInt(MetricsDto::getReplenishmentPacks).sum());
        assertEquals(styleMetricsDtos.iterator().next().getFinalReplenishmentQty(), ccMetrics.stream().mapToInt(MetricsDto::getFinalReplenishmentQty).sum());
    }

    @Test
    void test_fetchSizeListReplenishmentShouldReturnRollupValuesForReplenishmentUnitsAndPacks() throws JsonProcessingException {
        List<ReplenishmentResponseDTO> replenishmentResponseDTOS = getReplenishmentByPlanChannelIdFinelineStyleCc();
        when(sizeListReplenishmentRepository
                .getReplenishmentPlanChannelFinelineCc(anyLong(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(replenishmentResponseDTOS);
        ReplenishmentRequest request = new ReplenishmentRequest();
        request.setChannel("store");
        request.setPlanId(73L);
        request.setFinelineNbr(2852);
        request.setStyleNbr("34_2852_4_19_2");
        request.setCcId("34_2852_4_19_2_GEMSLT");
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchSizeListReplenishment(request);

        List<MerchMethodsDto> merchMethodsDtoList = replenishmentResponse.getLvl3List().stream()
                .map(Lvl3Dto::getLvl4List).flatMap(Collection::stream)
                .map(Lvl4Dto::getFinelines).flatMap(Collection::stream).map(FinelineDto::getStyles)
                .flatMap(Collection::stream).map(StyleDto::getCustomerChoices)
                .flatMap(Collection::stream).map(CustomerChoiceDto::getMerchMethods).flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedList::new));
        assertEquals(2, merchMethodsDtoList.size());
        for (MerchMethodsDto merchMethodsDto : merchMethodsDtoList) {
            MetricsDto metricsDto = merchMethodsDto.getMetrics();
            List<SizeDto> sizeDto = merchMethodsDto.getSizes();
            assertEquals(metricsDto.getFinalReplenishmentQty(), sizeDto.stream().mapToInt(val -> val.getMetrics().getFinalReplenishmentQty()).sum());
            assertEquals(metricsDto.getReplenishmentPacks(), sizeDto.stream().mapToInt(val -> val.getMetrics().getReplenishmentPacks()).sum());
        }
    }

    private List<ReplenishmentResponseDTO> getFineLineReplenishmentByPlanAndChannel() throws JsonProcessingException {
        String response = "[{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":1096890,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":1633620,\"lvl3ReplPack\":109689,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":882280,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":1205290,\"lvl4ReplPack\":88228,\"finelineNbr\":2830,\"finelineDesc\":\"2830 - TS STRIPE RIB CREW LAYERING T\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":309174,\"finelineReplQty\":200640,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":20064,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":1096890,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":1633620,\"lvl3ReplPack\":109689,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":882280,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":1205290,\"lvl4ReplPack\":88228,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":23167,\"finelineReplQty\":23280,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":2328,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":98100,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":98028,\"lvl3ReplPack\":9810,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":98100,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":98028,\"lvl4ReplPack\":9810,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":98028,\"finelineReplQty\":98100,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":9810,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":1096890,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":1633620,\"lvl3ReplPack\":109689,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":882280,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":1205290,\"lvl4ReplPack\":88228,\"finelineNbr\":2856,\"finelineDesc\":\"2856 - TS LTWT PULLOVER HOODIE\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":382159,\"finelineReplQty\":189300,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":18930,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":1096890,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":1633620,\"lvl3ReplPack\":109689,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":882280,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":1205290,\"lvl4ReplPack\":88228,\"finelineNbr\":2862,\"finelineDesc\":\"2862 - TS LS WOVEN FLOWY TOP\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":245338,\"finelineReplQty\":245410,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":24541,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":1096890,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":1633620,\"lvl3ReplPack\":109689,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":882280,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":1205290,\"lvl4ReplPack\":88228,\"finelineNbr\":2965,\"finelineDesc\":\"2965 - TS SMOCKED CUFF TOP\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":245452,\"finelineReplQty\":223650,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":22365,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":1096890,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":1633620,\"lvl3ReplPack\":109689,\"lvl4Nbr\":31511,\"lvl4Desc\":\"Sweaters Tops Plus Womens\",\"lvl4ReplQty\":178960,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":230082,\"lvl4ReplPack\":17896,\"finelineNbr\":2919,\"finelineDesc\":\"2919 - TS BRSHED VNCK SWTR\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":230082,\"finelineReplQty\":178960,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":17896,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":1096890,\"lvl3VenderPackCount\":10,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":5.0,\"lvl3finalBuyQty\":1633620,\"lvl3ReplPack\":109689,\"lvl4Nbr\":31515,\"lvl4Desc\":\"Fleece Tops Plus Womens\",\"lvl4ReplQty\":35650,\"lvl4VenderPackCount\":10,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":5.0,\"lvl4finalBuyQty\":109725,\"lvl4ReplPack\":3565,\"finelineNbr\":5131,\"finelineDesc\":\"5131 - TS FULL ZIP FLEECE HOODIE\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":109725,\"finelineReplQty\":35650,\"finelineVenderPackCount\":10,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":5.0,\"finelineReplPack\":3565,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12231,\"lvl3Desc\":\"Dresses And Rompers Plus Womens\",\"lvl3ReplQty\":275569,\"lvl3VenderPackCount\":12,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":6.0,\"lvl3finalBuyQty\":324255,\"lvl3ReplPack\":22964,\"lvl4Nbr\":31514,\"lvl4Desc\":\"Ls Dresses Plus Womens\",\"lvl4ReplQty\":275569,\"lvl4VenderPackCount\":12,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":6.0,\"lvl4finalBuyQty\":324255,\"lvl4ReplPack\":22964,\"finelineNbr\":5141,\"finelineDesc\":\"5141 - TS COLORBLOCK SWEATER DRESS\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":247880,\"finelineReplQty\":213650,\"finelineVenderPackCount\":12,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":6.0,\"finelineReplPack\":17804,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12231,\"lvl3Desc\":\"Dresses And Rompers Plus Womens\",\"lvl3ReplQty\":275569,\"lvl3VenderPackCount\":12,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":6.0,\"lvl3finalBuyQty\":324255,\"lvl3ReplPack\":22964,\"lvl4Nbr\":31514,\"lvl4Desc\":\"Ls Dresses Plus Womens\",\"lvl4ReplQty\":275569,\"lvl4VenderPackCount\":12,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":6.0,\"lvl4finalBuyQty\":324255,\"lvl4ReplPack\":22964,\"finelineNbr\":5147,\"finelineDesc\":\"5147 - TS LS TIE DYE BABYDOLL DRESS\",\"finelineAltDesc\":\"5147 - TS LS TIE DYE BABYDOLL DRESS CR\",\"finelineFinalBuyUnits\":76375,\"finelineReplQty\":61919,\"finelineVenderPackCount\":12,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":6.0,\"finelineReplPack\":5159,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12234,\"lvl3Desc\":\"Bottoms Plus Womens\",\"lvl3ReplQty\":2768120,\"lvl3VenderPackCount\":12,\"lvl3WhsePackCount\":2,\"lvl3vnpkWhpkRatio\":6.0,\"lvl3finalBuyQty\":2879380,\"lvl3ReplPack\":230676,\"lvl4Nbr\":31517,\"lvl4Desc\":\"Jeans Bottoms Plus Womens\",\"lvl4ReplQty\":2768120,\"lvl4VenderPackCount\":12,\"lvl4WhsePackCount\":2,\"lvl4vnpkWhpkRatio\":6.0,\"lvl4finalBuyQty\":2879380,\"lvl4ReplPack\":230676,\"finelineNbr\":3631,\"finelineDesc\":\"3631 - TS HR FLARE JEAN\",\"finelineAltDesc\":null,\"finelineFinalBuyUnits\":2879380,\"finelineReplQty\":2768120,\"finelineVenderPackCount\":12,\"finelineWhsePackCount\":2,\"finelineVnpkWhpkRatio\":6.0,\"finelineReplPack\":230676,\"styleNbr\":null,\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":null,\"colorName\":null,\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null}]\n";
        List<ReplenishmentResponseDTO> res = mapper.readValue(response, new TypeReference<>() {});
        return res;
    }

    private List<ReplenishmentResponseDTO> getReplenishmentByPlanIdChannelAndFineLineNbr() throws JsonProcessingException, JsonProcessingException {
        String dbResponse = "[{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":23167,\"styleReplQty\":23280,\"styleVenderPackCount\":10,\"styleWhsePackCount\":2,\"styleVnpkWhpkRatio\":5.0,\"styleReplPack\":2328,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":11029,\"ccReplQty\":11100,\"ccVenderPackCount\":10,\"ccWhsePackCount\":2,\"ccVnpkWhpkRatio\":5.0,\"ccReplPack\":1110,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":23167,\"styleReplQty\":23280,\"styleVenderPackCount\":10,\"styleWhsePackCount\":2,\"styleVnpkWhpkRatio\":5.0,\"styleReplPack\":2328,\"ccId\":\"34_2852_4_19_2_PURPRL\",\"colorName\":\"ISLAND PURPLE\",\"ccFinalBuyUnits\":12138,\"ccReplQty\":12180,\"ccVenderPackCount\":10,\"ccWhsePackCount\":2,\"ccVnpkWhpkRatio\":5.0,\"ccReplPack\":1218,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":98028,\"styleReplQty\":98100,\"styleVenderPackCount\":10,\"styleWhsePackCount\":2,\"styleVnpkWhpkRatio\":5.0,\"styleReplPack\":9810,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":46715,\"ccReplQty\":46750,\"ccVenderPackCount\":10,\"ccWhsePackCount\":2,\"ccVnpkWhpkRatio\":5.0,\"ccReplPack\":4675,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":98028,\"styleReplQty\":98100,\"styleVenderPackCount\":10,\"styleWhsePackCount\":2,\"styleVnpkWhpkRatio\":5.0,\"styleReplPack\":9810,\"ccId\":\"34_2852_4_19_2_PURPRL\",\"colorName\":\"ISLAND PURPLE\",\"ccFinalBuyUnits\":51313,\"ccReplQty\":51350,\"ccVenderPackCount\":10,\"ccWhsePackCount\":2,\"ccVnpkWhpkRatio\":5.0,\"ccReplPack\":5135,\"merchMethod\":null,\"ahsSizeId\":null,\"sizeDesc\":null,\"ccSpFinalBuyUnits\":null,\"ccSpReplQty\":null,\"ccSpVenderPackCount\":null,\"ccSpWhsePackCount\":null,\"ccMmSpVenderPackCount\":null,\"ccMmSpWhsePackCount\":null,\"ccSpVnpkWhpkRatio\":null,\"ccMmSpVnpkWhpkRatio\":null,\"ccSpReplPack\":null,\"ccMmSpFinalBuyUnits\":null,\"ccMMSpReplQty\":null,\"ccMmSpReplPack\":null,\"replenObject\":null}]\n";
        List<ReplenishmentResponseDTO> res = mapper.readValue(dbResponse, new TypeReference<>() {});
        return res;
    }

    private List<ReplenishmentResponseDTO> getReplenishmentByPlanChannelIdFinelineStyleCc() throws JsonProcessingException {
        String dbResponse = "[{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":1,\"ahsSizeId\":234,\"sizeDesc\":\"0X\",\"ccSpFinalBuyUnits\":1654,\"ccSpReplQty\":1670,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":167,\"ccMmSpFinalBuyUnits\":11029,\"ccMMSpReplQty\":11100,\"ccMmSpReplPack\":1110,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":1670,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":1,\"ahsSizeId\":235,\"sizeDesc\":\"1X\",\"ccSpFinalBuyUnits\":2537,\"ccSpReplQty\":2550,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":255,\"ccMmSpFinalBuyUnits\":11029,\"ccMMSpReplQty\":11100,\"ccMmSpReplPack\":1110,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":2550,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":1,\"ahsSizeId\":236,\"sizeDesc\":\"2X\",\"ccSpFinalBuyUnits\":3309,\"ccSpReplQty\":3320,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":332,\"ccMmSpFinalBuyUnits\":11029,\"ccMMSpReplQty\":11100,\"ccMmSpReplPack\":1110,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":3320,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":1,\"ahsSizeId\":237,\"sizeDesc\":\"3X\",\"ccSpFinalBuyUnits\":2647,\"ccSpReplQty\":2660,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":266,\"ccMmSpFinalBuyUnits\":11029,\"ccMMSpReplQty\":11100,\"ccMmSpReplPack\":1110,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":2660,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":1,\"ahsSizeId\":238,\"sizeDesc\":\"4X\",\"ccSpFinalBuyUnits\":882,\"ccSpReplQty\":900,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":90,\"ccMmSpFinalBuyUnits\":11029,\"ccMMSpReplQty\":11100,\"ccMmSpReplPack\":1110,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":900,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":2,\"ahsSizeId\":234,\"sizeDesc\":\"0X\",\"ccSpFinalBuyUnits\":7007,\"ccSpReplQty\":7010,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":701,\"ccMmSpFinalBuyUnits\":46715,\"ccMMSpReplQty\":46750,\"ccMmSpReplPack\":4675,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":7010,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":2,\"ahsSizeId\":235,\"sizeDesc\":\"1X\",\"ccSpFinalBuyUnits\":10744,\"ccSpReplQty\":10750,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":1075,\"ccMmSpFinalBuyUnits\":46715,\"ccMMSpReplQty\":46750,\"ccMmSpReplPack\":4675,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":10750,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":2,\"ahsSizeId\":236,\"sizeDesc\":\"2X\",\"ccSpFinalBuyUnits\":14015,\"ccSpReplQty\":14030,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":1403,\"ccMmSpFinalBuyUnits\":46715,\"ccMMSpReplQty\":46750,\"ccMmSpReplPack\":4675,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":14030,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":2,\"ahsSizeId\":237,\"sizeDesc\":\"3X\",\"ccSpFinalBuyUnits\":11212,\"ccSpReplQty\":11220,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":1122,\"ccMmSpFinalBuyUnits\":46715,\"ccMMSpReplQty\":46750,\"ccMmSpReplPack\":4675,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":11220,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"},{\"planId\":73,\"channelId\":null,\"lvl0Nbr\":50000,\"lvl0Desc\":\"Apparel\",\"lvl1Nbr\":34,\"lvl1Desc\":\"D34 - Womens Apparel\",\"lvl2Nbr\":6419,\"lvl2Desc\":\"Plus Womens\",\"lvl3Nbr\":12228,\"lvl3Desc\":\"Tops Plus Womens\",\"lvl3ReplQty\":null,\"lvl3VenderPackCount\":null,\"lvl3WhsePackCount\":null,\"lvl3vnpkWhpkRatio\":null,\"lvl3finalBuyQty\":null,\"lvl3ReplPack\":null,\"lvl4Nbr\":31507,\"lvl4Desc\":\"Ls Tops Plus Womens\",\"lvl4ReplQty\":null,\"lvl4VenderPackCount\":null,\"lvl4WhsePackCount\":null,\"lvl4vnpkWhpkRatio\":null,\"lvl4finalBuyQty\":null,\"lvl4ReplPack\":null,\"finelineNbr\":2852,\"finelineDesc\":\"2852 - TS MIXED MEDIA WITH LACE TOP\",\"finelineAltDesc\":\"LACE TOP TEST\",\"finelineFinalBuyUnits\":null,\"finelineReplQty\":null,\"finelineVenderPackCount\":null,\"finelineWhsePackCount\":null,\"finelineVnpkWhpkRatio\":null,\"finelineReplPack\":null,\"styleNbr\":\"34_2852_4_19_2\",\"styleFinalBuyUnits\":null,\"styleReplQty\":null,\"styleVenderPackCount\":null,\"styleWhsePackCount\":null,\"styleVnpkWhpkRatio\":null,\"styleReplPack\":null,\"ccId\":\"34_2852_4_19_2_GEMSLT\",\"colorName\":\"VIVID WHITE\",\"ccFinalBuyUnits\":null,\"ccReplQty\":null,\"ccVenderPackCount\":null,\"ccWhsePackCount\":null,\"ccVnpkWhpkRatio\":null,\"ccReplPack\":null,\"merchMethod\":2,\"ahsSizeId\":238,\"sizeDesc\":\"4X\",\"ccSpFinalBuyUnits\":3737,\"ccSpReplQty\":3740,\"ccSpVenderPackCount\":10,\"ccSpWhsePackCount\":2,\"ccMmSpVenderPackCount\":10,\"ccMmSpWhsePackCount\":2,\"ccSpVnpkWhpkRatio\":5.0,\"ccMmSpVnpkWhpkRatio\":5.0,\"ccSpReplPack\":374,\"ccMmSpFinalBuyUnits\":46715,\"ccMMSpReplQty\":46750,\"ccMmSpReplPack\":4675,\"replenObject\":\"[{\\\"replnWeek\\\":12301,\\\"replnWeekDesc\\\":\\\"FYE2024WK01\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12302,\\\"replnWeekDesc\\\":\\\"FYE2024WK02\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12303,\\\"replnWeekDesc\\\":\\\"FYE2024WK03\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12304,\\\"replnWeekDesc\\\":\\\"FYE2024WK04\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12244,\\\"replnWeekDesc\\\":\\\"FYE2023WK44\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":3740,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12245,\\\"replnWeekDesc\\\":\\\"FYE2023WK45\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12246,\\\"replnWeekDesc\\\":\\\"FYE2023WK46\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12247,\\\"replnWeekDesc\\\":\\\"FYE2023WK47\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12248,\\\"replnWeekDesc\\\":\\\"FYE2023WK48\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12249,\\\"replnWeekDesc\\\":\\\"FYE2023WK49\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12250,\\\"replnWeekDesc\\\":\\\"FYE2023WK50\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12251,\\\"replnWeekDesc\\\":\\\"FYE2023WK51\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null},{\\\"replnWeek\\\":12252,\\\"replnWeekDesc\\\":\\\"FYE2023WK52\\\",\\\"replnUnits\\\":null,\\\"adjReplnUnits\\\":0,\\\"remainingUnits\\\":null,\\\"dcInboundUnits\\\":null,\\\"dcInboundAdjUnits\\\":null}]\"}]\n";
        List<ReplenishmentResponseDTO> res = mapper.readValue(dbResponse, new TypeReference<>() {});
        return res;
    }
}