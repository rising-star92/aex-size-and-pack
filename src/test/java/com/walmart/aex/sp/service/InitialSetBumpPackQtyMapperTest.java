package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.initsetbumppkqty.InitialSetBumpPackQtyData;

@ExtendWith(MockitoExtension.class)
public class InitialSetBumpPackQtyMapperTest {

	@InjectMocks
	InitialSetBumpPackQtyMapper initSetBpPkQtyMapper;

	@Test
	public void getInitialSetBumpPackQtyDataTest() {
		BuyQtyResponse buyQtyResponse = new BuyQtyResponse();

		InitialSetBumpPackQtyData initSetBpPkQtyData = getInitialSetBumpPackQtyDetails();

		initSetBpPkQtyMapper.mapInitSetBpPkQtyLvl2Sp(initSetBpPkQtyData, buyQtyResponse);
		assertNotNull(buyQtyResponse);

		Integer finalInitialSetQty = buyQtyResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0)
				.getStyles().get(0).getCustomerChoices().get(0).getMerchMethods().get(0).getSizes().get(0).getMetrics()
				.getFinalInitialSetQty();

		Integer bumpPackQty = buyQtyResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0).getStyles()
				.get(0).getCustomerChoices().get(0).getMerchMethods().get(0).getSizes().get(0).getMetrics()
				.getBumpPackQty();

		assertEquals(1326, finalInitialSetQty);
		assertEquals(0, bumpPackQty);

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
		initSetBpPkQtyData.setBumpPackQty(0);

		return initSetBpPkQtyData;
	}

}
