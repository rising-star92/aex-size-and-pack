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

public class SizeLevelReplenishmentMapperTest {
	@InjectMocks
	SizeLevelReplenishmentMapper sizeLevelReplenishmentMapper;

	@Mock
	ReplenishmentResponseDTO replenishmentResponseDTO;

	@Mock
	BuyQuantityMapper buyQuantityMapper;

	@Mock
	ReplenishmentResponse replenishmentResponse;
	
	@Mock
	MetricsDto metricsDto;
	
	//private static final Integer finelineNbr = 2818;
	private static final Long planId = 471l;

	private static final Integer replPack = 4701;
	private static final Integer finalBQY = 76064;
	private static final Double PackRation = 3.0;

	@Test
	public void testMapReplenishmentLvl2Sp() {

	    replenishmentResponse = new ReplenishmentResponse();
		replenishmentResponseDTO = new ReplenishmentResponseDTO();
		replenishmentResponseDTO.setChannelId(2);
		replenishmentResponseDTO.setPlanId(planId);
		replenishmentResponseDTO.setMerchMethod(1);
		sizeLevelReplenishmentMapper.mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse, 2818);
		assertNotNull(replenishmentResponse);
		assertEquals(replenishmentResponse.getPlanId(), 471l);
	}

	@Test
	public void testMapReplenishmentLvl4Sp() {

	   replenishmentResponse = new ReplenishmentResponse();
	   replenishmentResponseDTO = new ReplenishmentResponseDTO();
	   replenishmentResponseDTO.setPlanId(planId);
	   replenishmentResponseDTO.setLvl3Nbr(3074);
	   replenishmentResponseDTO.setLvl3VenderPackCount(12);
	   replenishmentResponseDTO.setLvl3WhsePackCount(2);
	   replenishmentResponseDTO.setLvl3vnpkWhpkRatio(PackRation);
	   replenishmentResponseDTO.setLvl4Nbr(31514);
	   replenishmentResponseDTO.setLvl4VenderPackCount(12);
	   replenishmentResponseDTO.setLvl4WhsePackCount(2);
	   replenishmentResponseDTO.setLvl4vnpkWhpkRatio(PackRation);
	   replenishmentResponseDTO.setFinelineNbr(2818);
	   replenishmentResponseDTO.setFinelineVenderPackCount(12);
	   replenishmentResponseDTO.setFinelineVnpkWhpkRatio(PackRation);
	   replenishmentResponseDTO.setFinelineWhsePackCount(2);
	    MetricsDto metricsDto = new MetricsDto();
	   metricsDto.setPackRatio(PackRation);
	   metricsDto.setVendorPack(12);
	   metricsDto.setWarehousePack(2);
	   List<Lvl3Dto> lvl3List = new ArrayList<>();
	   List<Lvl4Dto> lvl4DtoList = new ArrayList<>();
	   Lvl4Dto lvl4List = new Lvl4Dto();
	   List<FinelineDto> finelineDtoList= new ArrayList<>();
	   FinelineDto fineLineDto = new FinelineDto();
	   fineLineDto.setFinelineNbr(null);
	   fineLineDto.setMetrics(metricsDto);
	   finelineDtoList.add(fineLineDto);
	   lvl4List.setLvl4Nbr(31514);
	   lvl4List.setFinelines(finelineDtoList);
	   lvl4List.setMetrics(metricsDto); 
	   lvl4DtoList.add(lvl4List);
	   Lvl3Dto lvl3 = new Lvl3Dto();
	   lvl3.setLvl3Nbr(3074);
	   lvl3.setMetrics(metricsDto);
	   lvl3.setLvl4List(lvl4DtoList);
	   lvl3List.add(lvl3);  
	   sizeLevelReplenishmentMapper.mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse, null);
	   assertNotNull(replenishmentResponse);
	   assertEquals(replenishmentResponse.getPlanId(), 471l);
	   assertNotNull(replenishmentResponse.getLvl3List());
	}
}