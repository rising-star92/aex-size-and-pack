package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @InjectMocks
    private ReplenishmentService replenishmentService1;

    @Mock
    private BuyQuantityMapper buyQuantityMapper;

    @Mock
    private StrategyFetchService strategyFetchService;

    private ObjectMapper mapper = new ObjectMapper();

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
        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(471l, 2, 50000, null, 34, null, 6419,
                null, 12228, null, 31507, null, 2855, null,
                1125, 1125, 1125, null);

        BuyQntyResponseDTO buyQntyResponseDTO2 = new BuyQntyResponseDTO(471l, 2, 50000, null, 34, null, 6419,
                null, 12229, null, 31508, null, 2855, null,
                1125, 1125, 1125, null);

        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO(471l, 2, 50000, null, 34, null, 6419,
                null, 12229, null, 31508, null, 2760, null,
                1125, 1125, 1125, null);


        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        buyQntyResponseDTOS.add(buyQntyResponseDTO2);
        buyQntyResponseDTOS.add(buyQntyResponseDTO1);

        Mockito.when(fineLineReplenishmentRepository.getBuyQntyByPlanChannelOnline(471l, 2)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(471l);
        buyQtyRequest.setChannel("Online");

        BuyQtyResponse buyQtyResponse1 = buyQtyResponseFromJson("/buyQtySizeResponse");
        BuyQtyResponse buyQtyResponse12= new BuyQtyResponse();

        Mockito.when(strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest)).thenReturn(buyQtyResponse1);
        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineFinelineBuyQnty(buyQtyRequest);
        assertEquals(471,buyQntyResponseDTO.getPlanId());
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO,new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO2,new BuyQtyResponse(),null);
        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTO1,new BuyQtyResponse(),null);

    }

    @Test
    public void fetchCcBuyQtyTest() throws IOException, SizeAndPackException {

        BuyQntyResponseDTO buyQntyResponseDTO1 = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12228, 31507, 2855, "34_2855_4_19_8", "34_2855_4_19_8_BLACK SOOT",
                1125, 1125, 1125, 1125,1125,1125,2);

        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12228, 31507, 2855, "34_2855_4_19_8", "34_5471_3_24_001_CHINO TAN",
                1125, 1125, 1125, 1125,1125,1125,2);

        BuyQntyResponseDTO buyQntyResponseDTO2 = new BuyQntyResponseDTO(471l, 50000, 34, 6419,
                12229, 31508, 2855, "34_2855_4_20_8", "34_2855_4_20_8_BLACK SOOT",
                1125, 1125, 1125, 1125,1125,1125,2);

        List<BuyQntyResponseDTO> buyQntyResponseDTOS = new ArrayList<>();
        buyQntyResponseDTOS.add(buyQntyResponseDTO);
        buyQntyResponseDTOS.add(buyQntyResponseDTO1);
        buyQntyResponseDTOS.add(buyQntyResponseDTO2);
        Mockito.when(spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(471l, 2,
                2855)).thenReturn(buyQntyResponseDTOS);

        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(471l);
        buyQtyRequest.setChannel("Online");
        buyQtyRequest.setFinelineNbr(2855);

        BuyQtyResponse buyQtyResponse1 = buyQtyResponseFromJson("/buyQtySizeResponse");

        Mockito.when(strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest,2855)).thenReturn(buyQtyResponse1);

        BuyQtyResponse buyQtyResponse = replenishmentService1.fetchOnlineCcBuyQnty(buyQtyRequest, 2855);
        assertEquals(471,buyQtyRequest.getPlanId());

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO1,new BuyQtyResponse(),2855);

        Mockito.verify(buyQuantityMapper, Mockito.times(1)).mapBuyQntyLvl2Sp(buyQntyResponseDTO2,new BuyQtyResponse(),2855);

        Mockito.verify(buyQuantityMapper, Mockito.times(0)).mapBuyQntyLvl2Sp(buyQntyResponseDTO,new BuyQtyResponse(),2855);
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

        BuyQtyResponse buyQtyResponse = buyQtyResponseFromJson("/sizeProfileResponse");

        Mockito.when(strategyFetchService.getBuyQtyResponseSizeProfile(buyQtyRequest)).thenReturn(buyQtyResponse);

        BuyQtyResponse buyQtyResponse1 = replenishmentService1.fetchOnlineSizeBuyQnty(buyQtyRequest);

        Mockito.verify(buyQuantityMapper, Mockito.times(5)).mapBuyQntySizeSp(Mockito.any(),Mockito.any());
    }

    private String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }

    private BuyQtyResponse buyQtyResponseFromJson(String path) throws IOException {
        return mapper.readValue(readJsonFileAsString(path), BuyQtyResponse.class);
    }

}