package com.walmart.aex.sp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl1;
import com.walmart.aex.sp.dto.planhierarchy.Lvl2;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDeleteDTO;
import com.walmart.aex.sp.dto.planhierarchy.StrongKey;
import com.walmart.aex.sp.dto.planhierarchy.Style;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;




@ExtendWith(MockitoExtension.class)
class CommonUtilTest {

	@InjectMocks
	private CommonUtil commonUtil;

	@Spy
	ObjectMapper objectMapper;

	@Test
	void testGetRequestedFlChannel(){

		List<Lvl3> lvl3List = new ArrayList<>();
		List<Lvl4> lvl4DtoList = new ArrayList<>();

		Lvl4 lvl4List = new Lvl4();
		List<Fineline> finelineDtoList= new ArrayList<>();

		Fineline fineLineDto = new Fineline();
		fineLineDto.setFinelineNbr(1234);
		fineLineDto.setChannel("1");

		finelineDtoList.add(fineLineDto);
		lvl4List.setLvl4Nbr(31514);
		lvl4List.setFinelines(finelineDtoList);
		lvl4DtoList.add(lvl4List);
		Lvl3 lvl3 = new Lvl3();
		lvl3.setLvl3Nbr(3074);

		lvl3.setLvl4List(lvl4DtoList);
		lvl3List.add(lvl3);  

		String response = CommonUtil.getRequestedFlChannel(lvl3);
		assertEquals("1",response);

	}

	@Test
	void testGetIntMerchMethod(){

		String case1 = CommonUtil.getMerchMethod(2);
		String case2 = CommonUtil.getMerchMethod(1); 
		String case3 = CommonUtil.getMerchMethod(0);

		assertEquals("FOLDED",case1);
		assertEquals("HANGING",case2);
		assertEquals("ONLINE_MERCH_METHOD",case3);

		Exception exception = assertThrows(RuntimeException.class, () -> CommonUtil.getMerchMethod(4));

		String expectedMessage = "Merch Method does not Match";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void testGetChannelId(){

		int case1 = CommonUtil.getChannelId("store");
		int case2 = CommonUtil.getChannelId("online"); 
		int case3 = CommonUtil.getChannelId("omni");

		assertEquals(1,case1);
		assertEquals(2,case2);
		assertEquals(3,case3);

		Exception exception = assertThrows(RuntimeException.class, () -> CommonUtil.getChannelId("hi"));

		String expectedMessage = "Channel Type does not Match";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void testCleanSPDeleteRequest() throws IOException{

		PlanSizeAndPackDTO planSizePackDto=getPlanSizeAndPackDTO();

		StrongKey strongKey = new StrongKey();
		PlanSizeAndPackDeleteDTO planSizePackDeleteDto = new PlanSizeAndPackDeleteDTO();
		planSizePackDeleteDto.setSizeAndPackPayloadDTO(planSizePackDto);
		strongKey.setPlanId(100L);
		planSizePackDeleteDto.setStrongKey(strongKey);

		planSizePackDeleteDto=commonUtil.cleanSPDeleteRequest(planSizePackDeleteDto);

		String planSizePackDeleteString= String.valueOf(planSizePackDeleteDto);

		assertTrue(planSizePackDeleteString.contains("finelineNbr=1234"));
		assertTrue(planSizePackDeleteString.contains("styleNbr=6578u&amp;"));
		assertTrue(planSizePackDeleteString.contains("ccId=567890&amp;"));

	}

	@Test
	void testCleanSPRequest() throws IOException{

		PlanSizeAndPackDTO planSizePackDto=getPlanSizeAndPackDTO();

		planSizePackDto=commonUtil.cleanSPRequest(planSizePackDto);

		String planSizePackString= String.valueOf(planSizePackDto);

		assertTrue(planSizePackString.contains("finelineNbr=1234"));
		assertTrue(planSizePackString.contains("styleNbr=6578u&"));
		assertTrue(planSizePackString.contains("ccId=567890&"));


	}

	public PlanSizeAndPackDTO getPlanSizeAndPackDTO() {

		List<Lvl1> lvl1List = new ArrayList<>();
		List<Lvl2> lvl2List = new ArrayList<>();
		List<Lvl3> lvl3List = new ArrayList<>();
		List<Lvl4> lvl4DtoList = new ArrayList<>();
		List<Fineline> finelineDtoList= new ArrayList<>();
		List<Style> styleList= new ArrayList<>();
		List<CustomerChoice> ccList= new ArrayList<>();

		PlanSizeAndPackDTO planSizePackDto = new PlanSizeAndPackDTO();
		Lvl1 lvl1= new Lvl1();
		Lvl2 lvl2 = new Lvl2();
		Lvl3 lvl3 = new Lvl3();
		Lvl4 lvl4 = new Lvl4();
		Fineline fineLine = new Fineline();
		Style style= new Style();
		CustomerChoice customerChoice= new CustomerChoice();

		customerChoice.setCcId("567890&90875");
		ccList.add(customerChoice);
		style.setCustomerChoices(ccList);

		style.setStyleNbr("6578u&");
		styleList.add(style);
		fineLine.setStyles(styleList);

		fineLine.setFinelineNbr(1234);
		fineLine.setChannel("1");
		finelineDtoList.add(fineLine);
		lvl4.setFinelines(finelineDtoList);

		lvl4.setLvl4Nbr(31514);
		lvl4DtoList.add(lvl4);
		lvl3.setLvl4List(lvl4DtoList);


		lvl3.setLvl3Nbr(3074);
		lvl3List.add(lvl3);
		lvl2.setLvl3List(lvl3List);

		lvl2.setLvl2Name("xyz");
		lvl2.setLvl2Nbr(123);
		lvl2List.add(lvl2);
		lvl1.setLvl2List(lvl2List);

		lvl1.setLvl1Name("test");
		lvl1.setLvl1Nbr(234);
		lvl1List.add(lvl1);
		planSizePackDto.setLvl1List(lvl1List);

		planSizePackDto.setPlanId(481L);
		planSizePackDto.setPlanDesc("ytui");
		planSizePackDto.setLvl0Name("abc");
		planSizePackDto.setLvl0Nbr(345);
		return planSizePackDto;
	}

}
