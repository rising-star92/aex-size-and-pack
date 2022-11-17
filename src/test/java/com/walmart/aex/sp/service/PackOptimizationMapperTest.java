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
		fineLinePackOptimizationResponseDTO.setMerchMethod(1);
		fineLinePackOptimizationResponseDTO.setAhsSizeDesc("small");
		fineLinePackOptimizationResponseDTO.setStoreObj("{\"buyQuantities\":[{\"isUnits\":1234,\"storeList\":[1,2,3],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":12345}]},{\"isUnits\":1234,\"storeList\":[4,5,6],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":12345}]},{\"isUnits\":235,\"storeList\":[6,8,9],\"sizeCluster\":2,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":4879}]},{\"isUnits\":1234,\"storeList\":[8,9,0],\"sizeCluster\":1,\"volumeCluster\":1,\"bumpSets\":[{\"setNbr\":1,\"wmYearWeek\":1,\"weekDesc\":\"1\",\"bsUnits\":12345}]}]}\r\n"
				+ "");
		fineLinePackOptimizationResponseDTO.setMaxUnitsPerPack(20);
		fineLinePackOptimizationResponseDTO.setMaxNbrOfPacks(5);
		fineLinePackOptimizationResponseDTO.setFactoryId("0121");
		fineLinePackOptimizationResponseDTO.setColorCombination("offWhite");
		fineLinePackOptimizationResponseDTO.setSinglePackInd(1);
		packOptimizationMapper.mapPackOptimizationFineline(fineLinePackOptimizationResponseDTO,fineLinePackOptimizationResponse,471l);
		assertNotNull(fineLinePackOptimizationResponse);
		assertEquals(471,fineLinePackOptimizationResponse.getPlanId());
		assertNotNull(fineLinePackOptimizationResponse.getFinelines());
		assertNotNull(fineLinePackOptimizationResponse.getFinelines().get(0).getFinelineLevelConstraints());
		assertNotNull(fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices());
		assertNotNull(fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getColorCombinationConstraints());
		
		assertEquals(20,fineLinePackOptimizationResponse.getFinelines().get(0).getFinelineLevelConstraints().getMaxUnitsPerPack());
		assertEquals(5,fineLinePackOptimizationResponse.getFinelines().get(0).getFinelineLevelConstraints().getMaxPacks());
		assertEquals("0121",fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getColorCombinationConstraints().getFactoryId());
		assertEquals("offWhite",fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getColorCombinationConstraints().getColorCombination());
		assertEquals(1,fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getColorCombinationConstraints().getSinglePackIndicator());
	}
}
