package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.buyquantity.MetricsDto;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;

@ExtendWith(MockitoExtension.class)
public class ReplenishmentMapperTest {

	@InjectMocks
	ReplenishmentMapper replenishmentMapper;

	@Mock
	ReplenishmentResponseDTO replenishmentResponseDTO;
	
	@Mock
	BuyQuantityMapper buyQuantityMapper;

	private static final Integer finelineNbr = 3470;
	private static final Long planId = 471l;

	private static final Integer replPack = 4701;
	private static final Integer finalBQY = 76064;
	private static final Double PackRation = 3.0;

	@Test
	public void testMapReplenishmentLvl2Sp() {

		ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
		replenishmentResponseDTO = new ReplenishmentResponseDTO();
		replenishmentResponseDTO.setChannelId(2);
		replenishmentResponseDTO.setPlanId(planId);
		replenishmentResponseDTO.setMerchMethod(1);
		replenishmentMapper.mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse, finelineNbr,
				"black");
		assertNotNull(replenishmentResponse);
		assertEquals(replenishmentResponse.getPlanId(), 471l);
	}

	@Test
	public void testMapReplenishmentLvl4Sp() {

		ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
		replenishmentResponseDTO = new ReplenishmentResponseDTO();
		replenishmentResponseDTO.setChannelId(2);
		replenishmentResponseDTO.setPlanId(planId);
		replenishmentResponseDTO.setMerchMethod(1);
		replenishmentResponseDTO.setLvl3finalBuyQty(finalBQY);
		replenishmentResponseDTO.setLvl3vnpkWhpkRatio(PackRation);
		replenishmentResponseDTO.setLvl3ReplPack(replPack);
		replenishmentResponseDTO.setLvl4Nbr(31514);
		List<Lvl3Dto> lvl3List = new ArrayList<>();
		List<Lvl4Dto> lvl4DtoList = new ArrayList<>();
		Lvl4Dto lvl4List = new Lvl4Dto();
		
		List<FinelineDto> finelineDtoList= new ArrayList<>();
		FinelineDto fineLineDto = new FinelineDto();
		fineLineDto.setFinelineNbr(finelineNbr);
		finelineDtoList.add(fineLineDto);
		
		MetricsDto metricsDto = new MetricsDto();
		metricsDto.setPackRatio(PackRation);
		lvl4List.setMetrics(metricsDto);
		lvl4List.setLvl4Nbr(31514);
		lvl4List.setFinelines(finelineDtoList);
		
		Lvl3Dto lvl3 = new Lvl3Dto();
		lvl3.setLvl3Nbr(3074);
		lvl3.setMetrics(metricsDto);
		lvl3.setLvl4List(lvl4DtoList);
		lvl3List.add(lvl3);
		lvl4DtoList.add(lvl4List);

		//passing finelineNBR and CCID as null so that finalBQY,PackRation,ReplenishmentPacks are set 
		replenishmentMapper.mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse, null, null);
		assertNotNull(replenishmentResponse);
		assertEquals(replenishmentResponse.getPlanId(), 471l);
		assertNotNull(replenishmentResponse.getLvl3List());
		
		
	}
}
