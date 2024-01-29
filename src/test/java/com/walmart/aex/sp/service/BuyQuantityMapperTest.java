package com.walmart.aex.sp.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.appmessage.AppMessageTextResponse;
import com.walmart.aex.sp.dto.appmessage.ValidationResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.SizeAndPackException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Mock
    AppMessageTextService appMessageTextService;

    @Spy
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
        buyQntyResponseDTO.setLvl0Nbr(23);
        BuyQtyResponse fetchFineLineResponse= getBuyQtyResponse(buyQntyResponseDTO,null);
        buyQunatityMapper.mapBuyQntyLvl2Sp(buyQntyResponseDTO,fetchFineLineResponse,null);
        assertNotNull(fetchFineLineResponse);
        assertEquals(471l,fetchFineLineResponse.getPlanId());
        MetricsDto metricsDto = fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getMetrics();
        assertEquals(metricsDto.getBuyQty(),(888));
        assertEquals(23,fetchFineLineResponse.getLvl0Nbr());
        assertEquals(11, fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getLvl4Nbr());
        assertEquals(123, fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
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
        assertEquals(metricsDto.getBuyQty(),(934));
        assertEquals(12,fetchFineLineResponse.getLvl2Nbr());
        assertEquals(11, fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getLvl4Nbr());
        assertEquals(2816, fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getFinelineNbr());
        assertEquals("34_2816_2_19_2", fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getStyleNbr());
        assertEquals("34_2816_2_19_2_CHARCOAL GREY HEATHER", fetchFineLineResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles().get(0).getCustomerChoices().get(0).getCcId());
    }

    @Test
    public void testGetMetadataDto_WhenTypesAreDifferent() throws JsonProcessingException {
        String messageObj = "{\"codes\":[150,151]}";
        List<AppMessageTextResponse> appMessageTextResponseList = new ArrayList<>();
        AppMessageTextResponse appMessageTextResponse1 =  AppMessageTextResponse.builder().id(150).desc("MISSING_SIZE_ASSOCIATION_FINELINE_LEVEL").typeDesc("Warning").longDesc("One or more CCs are missing size association. Please ensure all CCs have sizes associated and retrigger calculation").build();
        AppMessageTextResponse appMessageTextResponse2 =  AppMessageTextResponse.builder().id(151).desc("MISSING_MERCH_METHOD_FINELINE_LEVEL").typeDesc("Error").longDesc("Merch method is missing for one or more Fixture types. Please ensure all fixture types are associated to a merch method and retrigger calculation").build();
        appMessageTextResponseList.add(appMessageTextResponse1);
        appMessageTextResponseList.add(appMessageTextResponse2);
        ValidationResult validationResult = ValidationResult.builder().codes(Set.of(150,151)).build();
        Mockito.doReturn(validationResult).when(mapper).readValue(messageObj,ValidationResult.class);
        Mockito.when(appMessageTextService.getAllAppMessageText()).thenReturn(appMessageTextResponseList);
        Metadata metadata = buyQunatityMapper.getMetadataDto(messageObj);
        assertEquals(2,metadata.getValidations().size());
        assertEquals(1,metadata.getValidations().get(0).getMessages().size());
        assertEquals(2,metadata.getValidations().stream().map(validationObj -> validationObj.getType()).collect(Collectors.toSet()).size());
    }

    @Test
    public void testGetMetadataDto_WhenTypesAreSame() throws JsonProcessingException {
        String messageObj = "{\"codes\":[150,151]}";
        List<AppMessageTextResponse> appMessageTextResponseList = new ArrayList<>();
        AppMessageTextResponse appMessageTextResponse1 =  AppMessageTextResponse.builder().id(150).desc("MISSING_SIZE_ASSOCIATION_FINELINE_LEVEL").typeDesc("Warning").longDesc("One or more CCs are missing size association. Please ensure all CCs have sizes associated and retrigger calculation").build();
        AppMessageTextResponse appMessageTextResponse2 =  AppMessageTextResponse.builder().id(151).desc("MISSING_MERCH_METHOD_FINELINE_LEVEL").typeDesc("Warning").longDesc("Merch method is missing for one or more Fixture types. Please ensure all fixture types are associated to a merch method and retrigger calculation").build();
        appMessageTextResponseList.add(appMessageTextResponse1);
        appMessageTextResponseList.add(appMessageTextResponse2);
        ValidationResult validationResult = ValidationResult.builder().codes(Set.of(150,151)).build();
        Mockito.doReturn(validationResult).when(mapper).readValue(messageObj,ValidationResult.class);
        Mockito.when(appMessageTextService.getAllAppMessageText()).thenReturn(appMessageTextResponseList);
        Metadata metadata = buyQunatityMapper.getMetadataDto(messageObj);
        assertEquals(1,metadata.getValidations().size());
        assertEquals(2,metadata.getValidations().get(0).getMessages().size());
        assertEquals(1,metadata.getValidations().stream().map(validationObj -> validationObj.getType()).collect(Collectors.toSet()).size());
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