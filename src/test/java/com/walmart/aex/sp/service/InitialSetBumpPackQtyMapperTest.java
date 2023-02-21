package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.initsetbumppkqty.InitialSetBumpPackQtyData;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class InitialSetBumpPackQtyMapperTest {

	@InjectMocks
	InitialSetBumpPackQtyMapper initSetBpPkQtyMapper;

	@Test
	public void getInitialSetBumpPackQtyDataTest() {
		BuyQtyResponse buyQtyResponse = new BuyQtyResponse();

		InitialSetBumpPackQtyData initSetBpPkQtyData = getInitialSetBumpPackQtyDetails();
		initSetBpPkQtyData.setCcId("34_5141_4_21_11_PEACH SOOT");
		List<InitialSetBumpPackQtyData> initialSetBumpPackQtyDataList = List.of(getInitialSetBumpPackQtyDetails(), getInitialSetBumpPackQtyDetails(), initSetBpPkQtyData);

		initialSetBumpPackQtyDataList.stream().forEach(initialSetBumpPackQtyData -> {
			initSetBpPkQtyMapper.mapInitSetBpPkQtyLvl2Sp(initialSetBumpPackQtyData, buyQtyResponse);
		});

		assertNotNull(buyQtyResponse);

		MetricsDto metricsDto = buyQtyResponse
				.getLvl3List().stream().filter(lvl3Dto -> lvl3Dto.getLvl3Nbr() == 0).findFirst().orElse(new Lvl3Dto())
				.getLvl4List().stream().filter(lvl4Dto -> lvl4Dto.getLvl4Nbr() == 0).findFirst().orElse(new Lvl4Dto())
				.getFinelines().stream().filter(finelineDto -> finelineDto.getFinelineNbr() == 5141).findFirst().orElse(new FinelineDto())
				.getStyles().stream().filter(styleDto -> styleDto.getStyleNbr().equals("34_5141_4_21_11")).findFirst().orElse(new StyleDto())
				.getCustomerChoices().stream().filter(ccDto -> ccDto.getCcId().equals("34_5141_4_21_11_BLACK SOOT MARL")).findFirst().orElse(new CustomerChoiceDto())
				.getMerchMethods().stream().filter(merchMethodDto -> merchMethodDto.getMerchMethod().equals("FOLDED")).findFirst().orElse(new MerchMethodsDto())
				.getSizes().stream().filter(sizeDto -> sizeDto.getSizeDesc().equals("L")).findFirst().orElse(new SizeDto())
				.getMetrics();

		assertEquals(2652, metricsDto.getFinalInitialSetQty());
		assertEquals(20, metricsDto.getBumpPackQty());

	}

	private InitialSetBumpPackQtyData getInitialSetBumpPackQtyDetails() {
		InitialSetBumpPackQtyData initSetBpPkQtyData = new InitialSetBumpPackQtyData();

		initSetBpPkQtyData.setPlanId(483L);
		initSetBpPkQtyData.setLvl0Nbr(0);
		initSetBpPkQtyData.setLvl1Nbr(0);
		initSetBpPkQtyData.setLvl2Nbr(0);
		initSetBpPkQtyData.setLvl3Nbr(0);
		initSetBpPkQtyData.setLvl4Nbr(0);
		initSetBpPkQtyData.setFinelineNbr(5141);
		initSetBpPkQtyData.setStyleNbr("34_5141_4_21_11");
		initSetBpPkQtyData.setCcId("34_5141_4_21_11_BLACK SOOT MARL");
		initSetBpPkQtyData.setMerchMethodDesc("FOLDED");
		initSetBpPkQtyData.setSizeDesc("L");
		initSetBpPkQtyData.setFinalInitialSetQty(1326);
		initSetBpPkQtyData.setBumpPackQty(10);

		return initSetBpPkQtyData;
	}

}
