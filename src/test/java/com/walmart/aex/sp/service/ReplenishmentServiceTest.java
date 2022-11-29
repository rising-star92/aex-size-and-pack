package com.walmart.aex.sp.service;

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
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ReplenishmentServiceTest {

    @Mock
    private  FineLineReplenishmentRepository fineLineReplenishmentRepository;

    @Mock
    private  SpCustomerChoiceReplenishmentRepository spCustomerChoiceReplenishmentRepository;

    @Mock
    private  SizeListReplenishmentRepository sizeListReplenishmentRepository;

    @Mock
    private  CatgReplnPkConsRepository catgReplnPkConsRepository;

    @Mock
    private  SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

    @Mock
    private  FinelineReplnPkConsRepository finelineReplnPkConsRepository;

    @Mock
    private  StyleReplnPkConsRepository styleReplnConsRepository;

    @Mock
    private  CcReplnPkConsRepository ccReplnConsRepository;

    @Mock
    private  CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    @Mock
    private  ReplenishmentMapper replenishmentMapper;

    @Mock
    private  UpdateReplnConfigMapper updateReplnConfigMapper;

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
    List<MerchCatgReplPack> catgReplnPkConsList;
    
    @Mock
    List<SubCatgReplPack> SubcatgReplnPkConsList;
    
    @Mock
    List<FinelineReplPack> finelineReplnPkConsList;
    
    @Mock
    List<StyleReplPack> styleReplnPkConsList; 
    
    @Mock
    List<CcReplPack> ccReplnPkConsList1;
    
    @Mock
    CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
    
    @Mock
    List<CcMmReplPack> ccMmReplnPkConsList;
    
    @Mock
    List<CcSpMmReplPack> ccSpReplnPkConsList;
    
    @Mock
    List<ReplenishmentResponseDTO> replenishmentResponseDTOS;
    
    @Test
    public void updateVnpkWhpkForCatgReplnConsTest(){
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
        BuyQtyResponse buyQtyFinalResponse= new BuyQtyResponse();

        Mockito.when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineFinelineBuyQnty(buyQtyRequest);
        assertEquals(471,buyQtyRequest.getPlanId());
    }

    @Test
    public void fetchCcBuyQtyTest() throws IOException, SizeAndPackException
    {
        List<BuyQntyResponseDTO> buyQntyResponseDTOS = BuyQtyResponseInputs.buyQtyStyleCcInput();
        Mockito.when(spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(471l, 2,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = BuyQtyResponseInputs.fetchBuyQtyRequestForOnline();
        buyQtyRequest.setFinelineNbr(2855);
        BuyQtyResponse buyQtyResponse1 = BuyQtyResponseInputs.buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest,2855)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471,buyQtyRequest.getPlanId());
  }

    @Test
    public void fetchSizeBuyQtyTest() throws IOException, SizeAndPackException {

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(88L, 50000, 34, 6420,
                12238, 31526, 5471, "34_5471_3_24_001", "34_5471_3_24_001_CHINO TAN",3174,"L",
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

        Mockito.verify(buyQuantityMapper, Mockito.times(5)).mapBuyQntySizeSp(Mockito.any(),Mockito.any());
    }
    
   
  
    @Test
    public void testUpdateVnpkWhpkForCatgReplnCons() {
    	UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
    	updateVnPkWhPkReplnRequest.setPlanId(12l);
    	updateVnPkWhPkReplnRequest.setChannel("online");
    	updateVnPkWhPkReplnRequest.setLvl3Nbr(3);
    	updateVnPkWhPkReplnRequest.setVnpk(2);
    	updateVnPkWhPkReplnRequest.setWhpk(1);
    	Mockito.when(catgReplnPkConsRepository.getCatgReplnConsData(12l, 2, 3)).thenReturn(catgReplnPkConsList);
    	replenishmentService1.updateVnpkWhpkForCatgReplnCons(updateVnPkWhPkReplnRequest);	
        Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCatgReplnConsMapper(Mockito.any(),Mockito.any(),Mockito.any());
    }
    
    @Test
    public void testUpdateVnpkWhpkForSubCatgReplnCons() {
    	UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
    	SubcatgReplnPkConsList = new ArrayList<>();
    	updateVnPkWhPkReplnRequest.setPlanId(12l);
    	updateVnPkWhPkReplnRequest.setChannel("store");
    	updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
    	updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
    	updateVnPkWhPkReplnRequest.setVnpk(2);
    	updateVnPkWhPkReplnRequest.setWhpk(1);
    	Mockito.when(subCatgReplnPkConsRepository.getSubCatgReplnConsData(12l, 1, 12231,31516)).thenReturn(SubcatgReplnPkConsList);
    	replenishmentService1.updateVnpkWhpkForSubCatgReplnCons(updateVnPkWhPkReplnRequest);	
    	Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForSubCatgReplnConsMapper(Mockito.any(),Mockito.any(),Mockito.any());

    }
       
    @Test
    public void testUpdateVnpkWhpkForFinelineReplnCons() {
    	UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
    	finelineReplnPkConsList = new ArrayList<>();
    	updateVnPkWhPkReplnRequest.setPlanId(12l);
    	updateVnPkWhPkReplnRequest.setChannel("store");
    	updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
    	updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
    	updateVnPkWhPkReplnRequest.setFineline(1021);
    	updateVnPkWhPkReplnRequest.setVnpk(2);
    	updateVnPkWhPkReplnRequest.setWhpk(1);
    	Mockito.when(finelineReplnPkConsRepository.getFinelineReplnConsData(12l, 1, 12231, 31516, 1021)).thenReturn(finelineReplnPkConsList);
    	replenishmentService1.updateVnpkWhpkForFinelineReplnCons(updateVnPkWhPkReplnRequest);
    	Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForFinelineReplnConsMapper(Mockito.any(),Mockito.any(),Mockito.any());    	
    }
       
    @Test
    public void testUpdateVnpkWhpkForStyleReplnCons() {
    	UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
    	styleReplnPkConsList = new ArrayList<>();
    	updateVnPkWhPkReplnRequest.setPlanId(12l);
    	updateVnPkWhPkReplnRequest.setChannel("store");
    	updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
    	updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
    	updateVnPkWhPkReplnRequest.setFineline(1021);
    	updateVnPkWhPkReplnRequest.setStyle("34_1021_2_21_2");
    	updateVnPkWhPkReplnRequest.setVnpk(2);
    	updateVnPkWhPkReplnRequest.setWhpk(1);
    	Mockito.when(styleReplnConsRepository.getStyleReplnConsData(12l, 1, 12231, 31516, 1021,"34_1021_2_21_2")).thenReturn(styleReplnPkConsList);
    	replenishmentService1.updateVnpkWhpkForStyleReplnCons(updateVnPkWhPkReplnRequest);	
    	Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForStyleReplnConsMapper(Mockito.any(),Mockito.any(),Mockito.any());    	
    	 
    }
  
    
    @Test
    public void testUpdateVnpkWhpkForCcReplnPkCons() {
    	UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
    	ccReplnPkConsList1 = new ArrayList<>();
    	updateVnPkWhPkReplnRequest.setPlanId(12l);
    	updateVnPkWhPkReplnRequest.setChannel("store");
    	updateVnPkWhPkReplnRequest.setLvl3Nbr(12231);
    	updateVnPkWhPkReplnRequest.setLvl4Nbr(31516);
    	updateVnPkWhPkReplnRequest.setFineline(1021);
    	updateVnPkWhPkReplnRequest.setStyle("34_1021_2_21_2");
    	updateVnPkWhPkReplnRequest.setCustomerChoice("34_1021_2_21_2_AURA ORANGE STENCIL");
    	updateVnPkWhPkReplnRequest.setVnpk(2);
    	updateVnPkWhPkReplnRequest.setWhpk(1);
    	Mockito.when(ccReplnConsRepository.getCcReplnConsData(12l, 1, 12231, 31516, 1021,"34_1021_2_21_2","34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(ccReplnPkConsList1);
    	replenishmentService1.updateVnpkWhpkForCcReplnPkCons(updateVnPkWhPkReplnRequest);	
    	Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcReplnPkConsMapper(Mockito.any(),Mockito.any(),Mockito.any());    	        
   
    }
    
    
    @Test
    public void testUpdateVnPkWhPkCcMerchMethodReplnCon() {
    	UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
    	ccMmReplnPkConsList = new ArrayList<>();
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
    	Mockito.when(ccMmReplnPkConsRepository.getCcMmReplnPkConsData(12l, 1, 12231, 31516, 1021,"34_1021_2_21_2","34_1021_2_21_2_AURA ORANGE STENCIL","folded")).thenReturn(ccMmReplnPkConsList);
    	replenishmentService1.updateVnPkWhPkCcMerchMethodReplnCon(updateVnPkWhPkReplnRequest);	
    	Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcMmReplnPkConsMapper(Mockito.any(),Mockito.any(),Mockito.any());    	            	   
    }
       
    @Test
    public void testUpdateVnPkWhPkCcSpSizeReplnCon() {
    	UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest = new UpdateVnPkWhPkReplnRequest();
    	ccSpReplnPkConsList = new ArrayList<>();
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
    	Mockito.when(ccSpReplnPkConsRepository.getCcSpMmReplnPkConsData(12l, 1, 12231, 31516, 1021,"34_1021_2_21_2","34_1021_2_21_2_AURA ORANGE STENCIL","folded",246)).thenReturn(ccSpReplnPkConsList);
    	replenishmentService1.updateVnPkWhPkCcSpSizeReplnCon(updateVnPkWhPkReplnRequest);	
    	Mockito.verify(updateReplnConfigMapper, Mockito.times(1)).updateVnpkWhpkForCcSpMmReplnPkConsMapper(Mockito.any(),Mockito.any(),Mockito.any());    	                
    }
}