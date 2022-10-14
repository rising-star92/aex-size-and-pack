package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.initialsetqty.InitialSetQtyData;

@ExtendWith(MockitoExtension.class)
public class InitialSetQtyMapperTest {

	@InjectMocks
	InitialSetQtyMapper initialSetQtyMapper;

	@Test
	public void getInitialSetQtyDataTest() {
		BuyQtyResponse buyQtyResponse = new BuyQtyResponse();

		InitialSetQtyData initialSetQtyData = getInitialSetQtyDetails();

		initialSetQtyMapper.mapInitialSetQtyLvl2Sp(initialSetQtyData, buyQtyResponse);
		assertNotNull(buyQtyResponse);

		Integer finalInitialSetQty = buyQtyResponse.getLvl3List().get(0).getLvl4List().get(0).getFinelines().get(0)
				.getStyles().get(0).getCustomerChoices().get(0).getMerchMethods().get(0).getSizes().get(0).getMetrics()
				.getFinalInitialSetQty();

		assertEquals(1326, finalInitialSetQty);

	}

	private InitialSetQtyData getInitialSetQtyDetails() {
		InitialSetQtyData initialSetQtyData = new InitialSetQtyData();

		initialSetQtyData.setPlanId(483L);
		initialSetQtyData.setLvl0Nbr(0);
		initialSetQtyData.setLvl1Nbr(0);
		initialSetQtyData.setLvl2Nbr(0);
		initialSetQtyData.setLvl3Nbr(0);
		initialSetQtyData.setLvl4Nbr(0);
		initialSetQtyData.setFinelineNbr(5141);
		initialSetQtyData.setStyleNbr("34_5141_4_21_11");
		initialSetQtyData.setCcId("34_5141_4_21_11_BLACK SOOT MARL");
		initialSetQtyData.setMerchMethodDesc("FOLDED");
		initialSetQtyData.setSizeDesc("L");
		initialSetQtyData.setFinalInitialSetQty(1326);

		return initialSetQtyData;
	}

}
