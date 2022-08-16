package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;

@ExtendWith(MockitoExtension.class)
public class PackOptimizationMapperTest {
	@InjectMocks
	PackOptimizationMapper packOptimizationMapper;
	@Mock
	FineLinePackOptimizationResponseDTO fineLinePackOptimizationResponseDTO;
	private static final Long planId=471l;
	private static final Integer finelineNbr=2816;
	@Test
	public void testMapReplenishmentLvl2Sp() {

		FineLinePackOptimizationResponse fineLinePackOptimizationResponse= new FineLinePackOptimizationResponse();
		fineLinePackOptimizationResponseDTO=new FineLinePackOptimizationResponseDTO();
		fineLinePackOptimizationResponseDTO.setPlanId(planId);
		fineLinePackOptimizationResponseDTO.setFinelineNbr(finelineNbr);
		fineLinePackOptimizationResponseDTO.setCcId("34_2816_2_19_2_CHARCOAL GREY HEATHER                                                                ");
		fineLinePackOptimizationResponseDTO.setFixtureTypeRollupName("ENDCAPS");
		fineLinePackOptimizationResponseDTO.setMerchMethod("1");
		fineLinePackOptimizationResponseDTO.setFpStrategyText("INITIAL SET");
		fineLinePackOptimizationResponseDTO.setAhsSizeDesc("small");
		fineLinePackOptimizationResponseDTO.setStoreObj("{\"buyQuantities\":[{\"isUnits\":1234,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":12345}]},{\"isUnits\":1234,\"storeList\":[4,5,6],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":12345}]},{\"isUnits\":235,\"storeList\":[6,8,9],\"sizeCluster\":2,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":4879}]},{\"isUnits\":1234,\"storeList\":[8,9,0],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":12345}]}]}\r\n"
				+ "");
		packOptimizationMapper.mapPackOptimizationFineline(fineLinePackOptimizationResponseDTO,fineLinePackOptimizationResponse,471l);
		assertNotNull(fineLinePackOptimizationResponse);
		assertEquals(fineLinePackOptimizationResponse.getPlanId(),471l);
	}
}
