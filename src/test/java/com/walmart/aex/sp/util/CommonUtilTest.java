package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommonUtilTest {

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

}
