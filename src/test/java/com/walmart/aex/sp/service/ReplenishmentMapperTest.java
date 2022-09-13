package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;

@ExtendWith(MockitoExtension.class)
public class ReplenishmentMapperTest {

	@InjectMocks
	ReplenishmentMapper replenishmentMapper;

	@Mock
	ReplenishmentResponseDTO replenishmentResponseDTO;

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

		//passing finelineNBR and CCID as null so that finalBQY,PackRation,ReplenishmentPacks are set 
		replenishmentMapper.mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse, null, null);
		assertNotNull(replenishmentResponse);
		assertEquals(replenishmentResponse.getPlanId(), 471l);
		assertNotNull(replenishmentResponse.getLvl3List());
		
		Integer finalBuyQty = replenishmentResponse.getLvl3List().get(0).getMetrics().getFinalBuyQty();
		Double vpnkWhpkRation = replenishmentResponse.getLvl3List().get(0).getMetrics().getPackRatio();
		Integer replenishmentPack = replenishmentResponse.getLvl3List().get(0).getMetrics().getReplenishmentPacks();

		assertEquals(finalBuyQty, finalBQY);
		assertEquals(vpnkWhpkRation, PackRation);
		assertEquals(replenishmentPack, replPack);
	}
}
