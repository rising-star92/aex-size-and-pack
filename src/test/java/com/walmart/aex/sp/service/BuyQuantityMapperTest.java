package com.walmart.aex.sp.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.SizeAndPackException;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
public class BuyQuantityMapperTest {

    @InjectMocks
    BuyQuantityMapper buyQunatityMapper;
    @Mock
    BuyQntyResponseDTO buyQntyResponseDTO;

    @Mock
    StrategyFetchService strategyFetchService;

    private ObjectMapper mapper = new ObjectMapper();

    private static final Integer finelineNbr=3470;
    private static final Long planId=471l;
    @Test
    public void testMapBuyQntyLvl2Sp() {

        BuyQtyResponse fetchFineLineResponse= new BuyQtyResponse();
        buyQntyResponseDTO=new BuyQntyResponseDTO();
        buyQntyResponseDTO.setChannelId(2);
        buyQntyResponseDTO.setPlanId(planId);
        buyQntyResponseDTO.setBuyQty(654);
        buyQntyResponseDTO.setBumpPackQty(14);
        buyQntyResponseDTO.setAdjReplnQty(234);
        buyQntyResponseDTO.setBumpPackQty(654);
        buyQntyResponseDTO.setCcBumpQty(64);
        buyQntyResponseDTO.setCcFlowStrategy(765);
        buyQntyResponseDTO.setLvl2Nbr(12);
        buyQntyResponseDTO.setLvl3Nbr(13);
        buyQntyResponseDTO.setLvl4Nbr(11);
        buyQntyResponseDTO.setReplnQty(14);
        buyQntyResponseDTO.setInitialSetQty(10);
        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,finelineNbr);
        assertNotNull(fetchFineLineResponse);
        assertEquals(fetchFineLineResponse.getPlanId(),471l);

    }

    @Test
    public void testMapBuyQntyLvl2SpLvl4Metrics() {

        BuyQtyResponse fetchFineLineResponse= new BuyQtyResponse();
        buyQntyResponseDTO=new BuyQntyResponseDTO();
        buyQntyResponseDTO.setChannelId(2);
        buyQntyResponseDTO.setPlanId(planId);
        buyQntyResponseDTO.setBuyQty(654);
        buyQntyResponseDTO.setBumpPackQty(12);
        buyQntyResponseDTO.setAdjReplnQty(234);
        buyQntyResponseDTO.setBumpPackQty(654);
        buyQntyResponseDTO.setCcBumpQty(64);
        buyQntyResponseDTO.setCcFlowStrategy(765);
        buyQntyResponseDTO.setLvl2Nbr(12);
        buyQntyResponseDTO.setLvl3Nbr(13);
        buyQntyResponseDTO.setLvl4Nbr(11);
        buyQntyResponseDTO.setReplnQty(14);
        buyQntyResponseDTO.setInitialSetQty(10);
        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,null);
        assertNotNull(fetchFineLineResponse);
        assertEquals(fetchFineLineResponse.getPlanId(),471l);
        MetricsDto metricsDto = fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getMetrics();
        assertEquals(metricsDto.getBuyQty(),654);
    }

    @Test
    public void testMapBuyQntyUpdateFineline() {


        buyQntyResponseDTO=new BuyQntyResponseDTO();
        buyQntyResponseDTO.setChannelId(1);
        buyQntyResponseDTO.setReplnQty(14);
        buyQntyResponseDTO.setInitialSetQty(10);
        buyQntyResponseDTO.setFinelineNbr(123);
        buyQntyResponseDTO.setPlanId(planId);
        buyQntyResponseDTO.setBuyQty(654);
        buyQntyResponseDTO.setBumpPackQty(12);
        buyQntyResponseDTO.setAdjReplnQty(234);
        buyQntyResponseDTO.setBumpPackQty(654);
        buyQntyResponseDTO.setCcBumpQty(64);
        buyQntyResponseDTO.setCcFlowStrategy(765);
        buyQntyResponseDTO.setLvl2Nbr(12);
        buyQntyResponseDTO.setLvl3Nbr(13);
        buyQntyResponseDTO.setLvl4Nbr(11);
        buyQntyResponseDTO.setReplnQty(14);
        buyQntyResponseDTO.setInitialSetQty(10);
        BuyQtyResponse fetchFineLineResponse= getBuyQtyResponse(buyQntyResponseDTO,null);
        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,null);
        assertNotNull(fetchFineLineResponse);
        assertEquals(471l,fetchFineLineResponse.getPlanId());
        MetricsDto metricsDto = fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getMetrics();
        assertEquals(metricsDto.getBuyQty(),(654+234));
    }

    @Test
    public void testMapBuyQntyLvl2SpLvl3Metrics() {

        BuyQtyResponse fetchFineLineResponse= new BuyQtyResponse();
        buyQntyResponseDTO=new BuyQntyResponseDTO();
        buyQntyResponseDTO.setChannelId(2);
        buyQntyResponseDTO.setPlanId(planId);
        buyQntyResponseDTO.setBuyQty(654);
        buyQntyResponseDTO.setBumpPackQty(12);
        buyQntyResponseDTO.setAdjReplnQty(234);
        buyQntyResponseDTO.setBumpPackQty(654);
        buyQntyResponseDTO.setCcBumpQty(64);
        buyQntyResponseDTO.setCcFlowStrategy(765);
        buyQntyResponseDTO.setLvl2Nbr(12);
        buyQntyResponseDTO.setLvl3Nbr(13);
        buyQntyResponseDTO.setLvl4Nbr(11);
        buyQntyResponseDTO.setReplnQty(14);
        buyQntyResponseDTO.setInitialSetQty(10);
        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,null);
        assertNotNull(fetchFineLineResponse);
        assertEquals(fetchFineLineResponse.getPlanId(),471l);
        MetricsDto metricsDto = fetchFineLineResponse.getLvl3List().get(0).getMetrics();
        assertEquals(metricsDto.getBuyQty(),654);
    }


    @Test
    public void testMapBuyQntySizeSp() {
        List<SizeDto> sizeDtos = new ArrayList<>();
        BuyQntyResponseDTO buyQntyResponseDTO = new BuyQntyResponseDTO();
        buyQntyResponseDTO.setAhsSizeId(1);
        buyQntyResponseDTO.setBuyQty(100);

        SizeDto sizeDto = new SizeDto();
        sizeDto.setAhsSizeId(1);
        MetricsDto metricsDto = new MetricsDto();
        sizeDto.setMetrics(metricsDto);
        sizeDtos.add(sizeDto);
        buyQunatityMapper.mapBuyQntySizeSp(Arrays.asList(buyQntyResponseDTO),sizeDtos.get(0));
        assertEquals(100,sizeDto.getMetrics().getBuyQty());

    }

    @Test
    public void testMapBuyQntyStyleCcMetrics() throws SizeAndPackException, IOException {

        BuyQtyResponse fetchFineLineResponse= new BuyQtyResponse();
        buyQntyResponseDTO=new BuyQntyResponseDTO();
        buyQntyResponseDTO.setPlanId(12L);
        buyQntyResponseDTO.setLvl2Nbr(12);
        buyQntyResponseDTO.setLvl3Nbr(13);
        buyQntyResponseDTO.setLvl4Nbr(11);
        buyQntyResponseDTO.setFinelineNbr(2816);
        buyQntyResponseDTO.setStyleNbr("34_2816_2_19_2");
        buyQntyResponseDTO.setCcId("34_2816_2_19_2_CHARCOAL GREY HEATHER");
        buyQntyResponseDTO.setStyleFlowStrategy(1);
        buyQntyResponseDTO.setStyleMerchCode(1);
        buyQntyResponseDTO.setStyleBumpQty(650);
        buyQntyResponseDTO.setStyleIsQty(50);
        buyQntyResponseDTO.setStyleBuyQty(700);
        buyQntyResponseDTO.setCcFlowStrategy(1);
        buyQntyResponseDTO.setCcMerchCode(1);
        buyQntyResponseDTO.setCcBumpQty(650);
        buyQntyResponseDTO.setCcIsQty(50);
        buyQntyResponseDTO.setCcBuyQty(700);
        buyQntyResponseDTO.setChannelId(1);

        BuyQtyRequest newBuyReq = new BuyQtyRequest();
        newBuyReq.setPlanId(buyQntyResponseDTO.getPlanId());
        newBuyReq.setChannel(ChannelType.getChannelNameFromId(buyQntyResponseDTO.getChannelId()));
        newBuyReq.setLvl3Nbr(buyQntyResponseDTO.getLvl3Nbr());
        newBuyReq.setLvl4Nbr(buyQntyResponseDTO.getLvl4Nbr());
        newBuyReq.setFinelineNbr(buyQntyResponseDTO.getFinelineNbr());
        newBuyReq.setStyleNbr(buyQntyResponseDTO.getStyleNbr());
        newBuyReq.setCcId(buyQntyResponseDTO.getCcId());

        BuyQtyResponse buyQtyResponse = sizeProfileResponseFromJson("ccSizeProfile");

        Mockito.when(strategyFetchService.getBuyQtyResponseSizeProfile(newBuyReq)).thenReturn(buyQtyResponse);

        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,2816);
        assertNotNull(fetchFineLineResponse);
        assertEquals(fetchFineLineResponse.getPlanId(),12);
        MetricsDto metricsDto = fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getMetrics();
        assertEquals(metricsDto.getAdjAvgSizeProfilePct(),101);
        assertEquals(metricsDto.getAvgSizeProfilePct(),100);
    }

    @Test
    public void testMapBuyQntyUpdateStyleCcMetrics() {

        buyQntyResponseDTO=new BuyQntyResponseDTO();
        buyQntyResponseDTO.setPlanId(12L);
        buyQntyResponseDTO.setLvl2Nbr(12);
        buyQntyResponseDTO.setLvl3Nbr(13);
        buyQntyResponseDTO.setLvl4Nbr(11);
        buyQntyResponseDTO.setFinelineNbr(2816);
        buyQntyResponseDTO.setStyleNbr("34_2816_2_19_2");
        buyQntyResponseDTO.setCcId("34_2816_2_19_2_CHARCOAL GREY HEATHER");
        buyQntyResponseDTO.setStyleFlowStrategy(1);
        buyQntyResponseDTO.setStyleMerchCode(1);
        buyQntyResponseDTO.setStyleBumpQty(650);
        buyQntyResponseDTO.setStyleIsQty(50);
        buyQntyResponseDTO.setStyleBuyQty(700);
        buyQntyResponseDTO.setCcFlowStrategy(1);
        buyQntyResponseDTO.setCcMerchCode(1);
        buyQntyResponseDTO.setCcBumpQty(650);
        buyQntyResponseDTO.setCcIsQty(50);
        buyQntyResponseDTO.setCcBuyQty(700);
        buyQntyResponseDTO.setCcReplnQty(234);
        buyQntyResponseDTO.setChannelId(1);


        List<StyleDto> styleDtoList= new ArrayList<>();
        StyleDto styleDto = new StyleDto();
        List<CustomerChoiceDto> customerChoiceDtoList = new ArrayList<>();
        CustomerChoiceDto customerChoiceDto= new CustomerChoiceDto();
        customerChoiceDto.setCcId(buyQntyResponseDTO.getCcId());
        customerChoiceDto.setChannelId(1);
        customerChoiceDto.setMetrics(getMetricsInputs());
        customerChoiceDtoList.add(customerChoiceDto);
        styleDto.setStyleNbr(buyQntyResponseDTO.getStyleNbr());
        styleDto.setChannelId(1);
        styleDto.setMetrics(getMetricsInputs());
        styleDto.setCustomerChoices(customerChoiceDtoList);
        styleDtoList.add(styleDto);
        BuyQtyResponse fetchFineLineResponse= getBuyQtyResponse(buyQntyResponseDTO,styleDtoList);
        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,2816);
        assertEquals(12,fetchFineLineResponse.getPlanId());
        MetricsDto metricsDto = fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getMetrics();
        assertEquals(metricsDto.getBuyQty(),(700+234));
    }

    private BuyQtyResponse sizeProfileResponseFromJson(String filename) throws IOException {
        return mapper.readValue(readJsonFileAsString(filename), BuyQtyResponse.class);
    }

    private String readJsonFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/data/" + fileName + ".json")));
    }

    private BuyQtyResponse getBuyQtyResponse(BuyQntyResponseDTO buyQntyResponseDTO, List<StyleDto> styleDtoList)
    {
        BuyQtyResponse fetchFineLineResponse= new BuyQtyResponse();
        List<Lvl3Dto> lvl3List = new ArrayList<>();
        List<Lvl4Dto> lvl4DtoList = new ArrayList<>();
        Lvl4Dto lvl4Dto = new Lvl4Dto();
        List<FinelineDto> finelineDtoList = new ArrayList<>();
        FinelineDto finelineDto= new FinelineDto();
        finelineDto.setFinelineNbr(buyQntyResponseDTO.getFinelineNbr());
        finelineDto.setChannelId(1);
        finelineDto.setMetrics(getMetricsInputs());
        finelineDto.setStyles(styleDtoList);
        finelineDtoList.add(finelineDto);
        lvl4Dto.setLvl4Nbr(buyQntyResponseDTO.getLvl4Nbr());
        lvl4Dto.setFinelines(finelineDtoList);
        lvl4DtoList.add(lvl4Dto);
        Lvl3Dto lvl3Dto = new Lvl3Dto();
        lvl3Dto.setLvl3Nbr(buyQntyResponseDTO.getLvl3Nbr());
        lvl3Dto.setLvl4List(lvl4DtoList);
        lvl3List.add(lvl3Dto);
        fetchFineLineResponse.setLvl3List(lvl3List);
        return fetchFineLineResponse;
    }

    private MetricsDto getMetricsInputs()
    {
        MetricsDto metricsDto= new MetricsDto();
        metricsDto.setReplenishmentPacks(12);
        metricsDto.setFinalBuyQty(123);
        metricsDto.setBuyQty(234);
        metricsDto.setBumpPackQty(321);
        metricsDto.setFinalInitialSetQty(342);
        metricsDto.setFinalReplenishmentQty(321);
        return metricsDto;
    }
}