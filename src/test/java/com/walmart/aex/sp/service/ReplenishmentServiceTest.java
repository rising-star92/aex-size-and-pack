package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(MockitoExtension.class)
public class ReplenishmentServiceTest {

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

    @Mock
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
    CcMmReplnPkConsRepository ccMmReplnPkConsRepository;

    List<ReplenishmentResponseDTO> replenishmentResponseDTOS = new ArrayList<>();
    @Mock
    ReplenishmentRequest replenishmentRequest;

    @Mock
    SizeLevelReplenishmentRepository sizeLevelReplenishmentRepository;
    @Mock
    SizeLevelReplenishmentMapper sizeLevelReplenishmentMapper;

    @BeforeEach
    public void init() {
        replenishmentResponseDTOS.clear();
    }


    @Test
    public void updateVnpkWhpkForCatgReplnConsTest() {
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
    public void fetchFinelineBuyQtyTest() throws IOException, SizeAndPackException {

        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyFinelineInput();
        Mockito.when(fineLineReplenishmentRepository.getBuyQntyByPlanChannelOnline(471l, 2)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForOnline();
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyFinalResponse = new BuyQtyResponse();

        Mockito.when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineFinelineBuyQnty(buyQtyRequest);
        assertEquals(471, buyQtyRequest.getPlanId());
    }

    @Test
    public void fetchCcBuyQtyTest() throws IOException, SizeAndPackException {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        Mockito.when(spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(471l, 2,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForOnline();
        buyQtyRequest.setFinelineNbr(2855);
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest, 2855)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471, buyQtyRequest.getPlanId());
    }

    @Test
    public void fetchSizeBuyQtyTest() throws IOException, SizeAndPackException {

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(88L, 50000, 34, 6420,
                12238, 31526, 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", 3174, "L",
                1125, 1125, 1125);
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        Mockito.when(sizeListReplenishmentRepository.getSizeBuyQntyByPlanChannelOnlineCc(88L, 2,
                "34_5471_3_24_001_CHINO TAN")).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(88L);
        buyQtyRequest.setChannel("Online");
        buyQtyRequest.setCcId("34_5471_3_24_001_CHINO TAN");

        BuyQtyResponse buyQtyResponse = BuyQtyResponseInputs.buyQtyResponseFromJson("/sizeProfileResponse");

        Mockito.when(strategyFetchService.getBuyQtyResponseSizeProfile(buyQtyRequest)).thenReturn(buyQtyResponse);

        BuyQtyResponse buyQtyResponse1 = replenishmentService1.fetchOnlineSizeBuyQnty(buyQtyRequest);

        Mockito.verify(buyQuantityMapper, Mockito.times(5)).mapBuyQntySizeSp(Mockito.any(), Mockito.any());
    }

    @Test
    public void testUpdateVnpkWhpkForCatgReplnCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<MerchCatgReplPack> catgReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("online");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(3);
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        Mockito.when(catgReplnPkConsRepository.getCatgReplnConsData(12l, 2, 3)).thenReturn(catgReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForCatgReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCatgReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testUpdateVnpkWhpkForSubCatgReplnCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<SubCatgReplPack> subCatgReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        Mockito.when(subCatgReplnPkConsRepository.getSubCatgReplnConsData(12l, 1, 12231, 31516)).thenReturn(subCatgReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForSubCatgReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForSubCatgReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testUpdateVnpkWhpkForFinelineReplnCons() {
        UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
        List<FinelineReplPack> finelineReplnPkConsList = new ArrayList<>();
        updateVnPkWhPkReplnRequest.setPlanId(12l);
        updateVnPkWhPkReplnRequest.setChannel("store");
        updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
        updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
        updateVnPkWhPkReplnRequest.setFineline(1021);
        updateVnPkWhPkReplnRequest.setVnpk(2);
        updateVnPkWhPkReplnRequest.setWhpk(1);
        Mockito.when(finelineReplnPkConsRepository.getFinelineReplnConsData(12l, 1, 12231, 31516, 1021)).thenReturn(finelineReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForFinelineReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForFinelineReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testUpdateVnpkWhpkForStyleReplnCons() {
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
        Mockito.when(styleReplnConsRepository.getStyleReplnConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2")).thenReturn(styleReplnPkConsList);
        replenishmentService1.updateVnpkWhpkForStyleReplnCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForStyleReplnConsMapper(Mockito.any(), Mockito.any(), Mockito.any());

    }


    @Test
    public void testUpdateVnpkWhpkForCcReplnPkCons() {
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
        Mockito.when(ccReplnConsRepository.getCcReplnConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(ccReplnPkConsList1);
        replenishmentService1.updateVnpkWhpkForCcReplnPkCons(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcReplnPkConsMapper(Mockito.any(), Mockito.any(), Mockito.any());

    }


    @Test
    public void testUpdateVnPkWhPkCcMerchMethodReplnCon() {
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
        Mockito.when(ccMmReplnPkConsRepository.getCcMmReplnPkConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL", "folded")).thenReturn(ccMmReplnPkConsList);
        replenishmentService1.updateVnPkWhPkCcMerchMethodReplnCon(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcMmReplnPkConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testUpdateVnPkWhPkCcSpSizeReplnCon() {
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
        Mockito.when(ccSpReplnPkConsRepository.getCcSpMmReplnPkConsData(12l, 1, 12231, 31516, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL", "folded", 246)).thenReturn(ccSpReplnPkConsList);
        replenishmentService1.updateVnPkWhPkCcSpSizeReplnCon(updateVnPkWhPkReplnRequest);
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcSpMmReplnPkConsMapper(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testfetchSizeListReplenishment() {
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
        Mockito.when(sizeListReplenishmentRepository.getReplenishmentPlanChannelFinelineCc(12l, 1, 1021, "34_1021_2_21_2", "34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(replenishmentResponseDTOS);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchSizeListReplenishment(replenishmentRequest);
        Mockito.verify(replenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testFetchFinelineReplenishment() {
        replenishmentRequest = new ReplenishmentRequest();
        ReplenishmentResponseDTO replenishmentResponseDTO1 = new ReplenishmentResponseDTO(88L, 50000, null, 34, "123", 6420,
                "folded", 12238, "wall", 31526, "rack", 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", "4321", 3174, 543, 12, 45, 54.0, 34, "L",
                "hanging", 1125, 1125, 1125, 2511, 12.0, 5431);

        replenishmentResponseDTOS.add(replenishmentResponseDTO1);
        replenishmentRequest.setPlanId(12l);
        replenishmentRequest.setChannel("store");
        Mockito.when(fineLineReplenishmentRepository
                .getByPlanChannel(12l, 1)).thenReturn(replenishmentResponseDTOS);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchFinelineReplenishment(replenishmentRequest);
        Mockito.verify(replenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());

    }

    @Test
    public void testFetchCcReplenishment() {
        replenishmentRequest = new ReplenishmentRequest();
        ReplenishmentResponseDTO replenishmentResponseDTO1 = new ReplenishmentResponseDTO(88L, 50000, null, 34, "123", 6420,
                "folded", 12238, "wall", 31526, "rack", 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", "4321", 3174, 543, 12, 45, 54.0, 34, "L",
                "hanging", 1125, 1125, 1125, 2511, 12.0, 5431);

        replenishmentResponseDTOS.add(replenishmentResponseDTO1);
        replenishmentRequest.setPlanId(12l);
        replenishmentRequest.setChannel("store");
        replenishmentRequest.setFinelineNbr(1021);
        Mockito.when(spCustomerChoiceReplenishmentRepository
                .getReplenishmentByPlanChannelFineline(12l, 1, 1021)).thenReturn(replenishmentResponseDTOS);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchCcReplenishment(replenishmentRequest);
        Mockito.verify(replenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testFetchSizeListReplenishmentFullHierarchy() {
        replenishmentRequest = new ReplenishmentRequest();
        ReplenishmentResponseDTO replenishmentResponseDTO1 = new ReplenishmentResponseDTO(88L, 50000, null, 34, "123", 6420,
                "folded", 12238, "wall", 31526, "rack", 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN", "4321", 3174, 543, 12, 45, 54.0, 34, "L",
                "hanging", 1125, 1125, 1125, 2511, 12.0, 5431);

        replenishmentResponseDTOS.add(replenishmentResponseDTO1);
        replenishmentRequest.setPlanId(12l);
        replenishmentRequest.setChannel("store");
        replenishmentRequest.setFinelineNbr(1021);
        Mockito.when(sizeLevelReplenishmentRepository
                .getReplnFullHierarchyByPlanFineline(12l, 1, 1021)).thenReturn(replenishmentResponseDTOS);
        ReplenishmentResponse replenishmentResponse = replenishmentService1.fetchSizeListReplenishmentFullHierarchy(replenishmentRequest);
        Mockito.verify(sizeLevelReplenishmentMapper, Mockito.times(1)).mapReplenishmentLvl2Sp(Mockito.any(), Mockito.any(), Mockito.any());

    }
}