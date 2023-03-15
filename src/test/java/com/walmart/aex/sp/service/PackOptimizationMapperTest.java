package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;

@Slf4j
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
		packOptimizationMapper.mapPackOptimizationFineline(fineLinePackOptimizationResponseDTO,fineLinePackOptimizationResponse,471l, 1,1);
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

	@Test
	public void testMapMultiBumpPack() {

		FineLinePackOptimizationResponse fineLinePackOptimizationResponse= new FineLinePackOptimizationResponse();
		fineLinePackOptimizationResponseDTO=new FineLinePackOptimizationResponseDTO();
		fineLinePackOptimizationResponseDTO.setPlanId(planId);
		fineLinePackOptimizationResponseDTO.setFinelineNbr(finelineNbr);
		fineLinePackOptimizationResponseDTO.setCcId("34_2816_2_19_2_CHARCOAL GREY HEATHER                                                                ");
		fineLinePackOptimizationResponseDTO.setFixtureTypeRollupName("ENDCAPS");
		fineLinePackOptimizationResponseDTO.setMerchMethod(1);
		fineLinePackOptimizationResponseDTO.setAhsSizeDesc("small");
		fineLinePackOptimizationResponseDTO.setStoreObj("{\n" +
				"  \"buyQuantities\": [\n" +
				"    {\n" +
				"      \"isUnits\": 1234,\n" +
				"      \"storeList\": [\n" +
				"        1,\n" +
				"        2,\n" +
				"        3\n" +
				"      ],\n" +
				"      \"sizeCluster\": 1,\n" +
				"      \"volumeCluster\": 1,\n" +
				"      \"bumpSets\": [\n" +
				"        {\n" +
				"          \"setNbr\": 1,\n" +
				"          \"wmYearWeek\": 1,\n" +
				"          \"weekDesc\": \"1\",\n" +
				"          \"bsUnits\": 12345\n" +
				"        }\n" +
				"      ]\n" +
				"    },\n" +
				"    {\n" +
				"      \"isUnits\": 1234,\n" +
				"      \"storeList\": [\n" +
				"        4,\n" +
				"        5,\n" +
				"        6\n" +
				"      ],\n" +
				"      \"sizeCluster\": 1,\n" +
				"      \"volumeCluster\": 1,\n" +
				"      \"bumpSets\": [\n" +
				"        {\n" +
				"          \"setNbr\": 1,\n" +
				"          \"wmYearWeek\": 1,\n" +
				"          \"weekDesc\": \"1\",\n" +
				"          \"bsUnits\": 12345\n" +
				"        }\n" +
				"      ]\n" +
				"    },\n" +
				"    {\n" +
				"      \"isUnits\": 235,\n" +
				"      \"storeList\": [\n" +
				"        6,\n" +
				"        8,\n" +
				"        9\n" +
				"      ],\n" +
				"      \"sizeCluster\": 2,\n" +
				"      \"volumeCluster\": 1,\n" +
				"      \"bumpSets\": [\n" +
				"        {\n" +
				"          \"setNbr\": 1,\n" +
				"          \"wmYearWeek\": 1,\n" +
				"          \"weekDesc\": \"1\",\n" +
				"          \"bsUnits\": 4879\n" +
				"        }\n" +
				"      ]\n" +
				"    },\n" +
				"    {\n" +
				"      \"isUnits\": 1234,\n" +
				"      \"storeList\": [\n" +
				"        8,\n" +
				"        9,\n" +
				"        0\n" +
				"      ],\n" +
				"      \"sizeCluster\": 1,\n" +
				"      \"volumeCluster\": 1,\n" +
				"      \"bumpSets\": [\n" +
				"        {\n" +
				"          \"setNbr\": 1,\n" +
				"          \"wmYearWeek\": 1,\n" +
				"          \"weekDesc\": \"1\",\n" +
				"          \"bsUnits\": 12345\n" +
				"        },\n" +
				"        {\n" +
				"          \"setNbr\": 2,\n" +
				"          \"wmYearWeek\": 2,\n" +
				"          \"weekDesc\": \"2\",\n" +
				"          \"bsUnits\": 123456\n" +
				"        }\n" +
				"      ]\n" +
				"    }\n" +
				"  ]\n" +
				"}");
		fineLinePackOptimizationResponseDTO.setMaxUnitsPerPack(20);
		fineLinePackOptimizationResponseDTO.setMaxNbrOfPacks(5);
		fineLinePackOptimizationResponseDTO.setFactoryId("0121");
		fineLinePackOptimizationResponseDTO.setColorCombination("offWhite");
		fineLinePackOptimizationResponseDTO.setSinglePackInd(1);
		packOptimizationMapper.mapPackOptimizationFineline(fineLinePackOptimizationResponseDTO,fineLinePackOptimizationResponse,471l, 2,1);
		log.info("test: {}",fineLinePackOptimizationResponse);
		assertEquals(123456,fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getFixtures().get(0).getSizes().get(0)
				.getMetrics().stream().filter(metricsPackDto -> metricsPackDto.getStoreList().contains(0)
						&& metricsPackDto.getStoreList().contains(8) && metricsPackDto.getStoreList().contains(9)).findFirst().orElse(null).getBumpSet());
		assertEquals(0,fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getFixtures().get(0).getSizes().get(0)
				.getMetrics().get(0).getInitialSet());
	}

	@Test
	public void testNullBumpPack() {

		FineLinePackOptimizationResponse fineLinePackOptimizationResponse= new FineLinePackOptimizationResponse();
		fineLinePackOptimizationResponseDTO=new FineLinePackOptimizationResponseDTO();
		fineLinePackOptimizationResponseDTO.setPlanId(planId);
		fineLinePackOptimizationResponseDTO.setFinelineNbr(finelineNbr);
		fineLinePackOptimizationResponseDTO.setCcId("34_2816_2_19_2_CHARCOAL GREY HEATHER                                                                ");
		fineLinePackOptimizationResponseDTO.setFixtureTypeRollupName("ENDCAPS");
		fineLinePackOptimizationResponseDTO.setMerchMethod(1);
		fineLinePackOptimizationResponseDTO.setAhsSizeDesc("small");
		fineLinePackOptimizationResponseDTO.setStoreObj("{\n" +
				" \"buyQuantities\": [\n" +
				"   {\n" +
				"     \"isUnits\": 1234,\n" +
				"     \"storeList\": [\n" +
				"       1,\n" +
				"       2,\n" +
				"       3\n" +
				"     ],\n" +
				"     \"sizeCluster\": 1,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"setNbr\": 1,\n" +
				"         \"wmYearWeek\": 1,\n" +
				"         \"weekDesc\": \"1\",\n" +
				"         \"bsUnits\": 12345\n" +
				"       }\n" +
				"     ]\n" +
				"   },\n" +
				"   {\n" +
				"     \"isUnits\": 1234,\n" +
				"     \"storeList\": [\n" +
				"       4,\n" +
				"       5,\n" +
				"       6\n" +
				"     ],\n" +
				"     \"sizeCluster\": 1,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"setNbr\": 1,\n" +
				"         \"wmYearWeek\": 1,\n" +
				"         \"weekDesc\": \"1\",\n" +
				"         \"bsUnits\": 12345\n" +
				"       }\n" +
				"     ]\n" +
				"   },\n" +
				"   {\n" +
				"     \"isUnits\": 235,\n" +
				"     \"storeList\": [\n" +
				"       6,\n" +
				"       8,\n" +
				"       9\n" +
				"     ],\n" +
				"     \"sizeCluster\": 2,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"setNbr\": 1,\n" +
				"         \"wmYearWeek\": 1,\n" +
				"         \"weekDesc\": \"1\",\n" +
				"         \"bsUnits\": 4879\n" +
				"       }\n" +
				"     ]\n" +
				"   },\n" +
				"   {\n" +
				"     \"isUnits\": 1234,\n" +
				"     \"storeList\": [\n" +
				"       8,\n" +
				"       9,\n" +
				"       0\n" +
				"     ],\n" +
				"     \"sizeCluster\": 1,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"setNbr\": null,\n" +
				"         \"wmYearWeek\": 2,\n" +
				"         \"weekDesc\": \"2\",\n" +
				"         \"bsUnits\": 123456\n" +
				"       }\n" +
				"     ]\n" +
				"   }\n" +
				" ]\n" +
				"}");
		fineLinePackOptimizationResponseDTO.setMaxUnitsPerPack(20);
		fineLinePackOptimizationResponseDTO.setMaxNbrOfPacks(5);
		fineLinePackOptimizationResponseDTO.setFactoryId("0121");
		fineLinePackOptimizationResponseDTO.setColorCombination("offWhite");
		fineLinePackOptimizationResponseDTO.setSinglePackInd(1);
		packOptimizationMapper.mapPackOptimizationFineline(fineLinePackOptimizationResponseDTO,fineLinePackOptimizationResponse,471l, 1,1);
		log.info("test: {}",fineLinePackOptimizationResponse);
		assertEquals(123456,fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getFixtures().get(0).getSizes().get(0)
				.getMetrics().stream().filter(metricsPackDto -> metricsPackDto.getStoreList().contains(0)
						&& metricsPackDto.getStoreList().contains(8) && metricsPackDto.getStoreList().contains(9)).findFirst().orElse(null).getBumpSet());
	}

	@Test
	public void test2NullBumpPack() {

		FineLinePackOptimizationResponse fineLinePackOptimizationResponse= new FineLinePackOptimizationResponse();
		fineLinePackOptimizationResponseDTO=new FineLinePackOptimizationResponseDTO();
		fineLinePackOptimizationResponseDTO.setPlanId(planId);
		fineLinePackOptimizationResponseDTO.setFinelineNbr(finelineNbr);
		fineLinePackOptimizationResponseDTO.setCcId("34_2816_2_19_2_CHARCOAL GREY HEATHER                                                                ");
		fineLinePackOptimizationResponseDTO.setFixtureTypeRollupName("ENDCAPS");
		fineLinePackOptimizationResponseDTO.setMerchMethod(1);
		fineLinePackOptimizationResponseDTO.setAhsSizeDesc("small");
		fineLinePackOptimizationResponseDTO.setStoreObj("{\n" +
				" \"buyQuantities\": [\n" +
				"   {\n" +
				"     \"isUnits\": 1234,\n" +
				"     \"storeList\": [\n" +
				"       1,\n" +
				"       2,\n" +
				"       3\n" +
				"     ],\n" +
				"     \"sizeCluster\": 1,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"setNbr\": 1,\n" +
				"         \"wmYearWeek\": 1,\n" +
				"         \"weekDesc\": \"1\",\n" +
				"         \"bsUnits\": 12345\n" +
				"       }\n" +
				"     ]\n" +
				"   },\n" +
				"   {\n" +
				"     \"isUnits\": 1234,\n" +
				"     \"storeList\": [\n" +
				"       4,\n" +
				"       5,\n" +
				"       6\n" +
				"     ],\n" +
				"     \"sizeCluster\": 1,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"setNbr\": 1,\n" +
				"         \"wmYearWeek\": 1,\n" +
				"         \"weekDesc\": \"1\",\n" +
				"         \"bsUnits\": 12345\n" +
				"       }\n" +
				"     ]\n" +
				"   },\n" +
				"   {\n" +
				"     \"isUnits\": 235,\n" +
				"     \"storeList\": [\n" +
				"       6,\n" +
				"       8,\n" +
				"       9\n" +
				"     ],\n" +
				"     \"sizeCluster\": 2,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"setNbr\": 1,\n" +
				"         \"wmYearWeek\": 1,\n" +
				"         \"weekDesc\": \"1\",\n" +
				"         \"bsUnits\": 4879\n" +
				"       }\n" +
				"     ]\n" +
				"   },\n" +
				"   {\n" +
				"     \"isUnits\": 1234,\n" +
				"     \"storeList\": [\n" +
				"       8,\n" +
				"       9,\n" +
				"       0\n" +
				"     ],\n" +
				"     \"sizeCluster\": 1,\n" +
				"     \"volumeCluster\": 1,\n" +
				"     \"bumpSets\": [\n" +
				"       {\n" +
				"         \"wmYearWeek\": 2,\n" +
				"         \"weekDesc\": \"2\",\n" +
				"         \"bsUnits\": 123456\n" +
				"       }\n" +
				"     ]\n" +
				"   }\n" +
				" ]\n" +
				"}");
		fineLinePackOptimizationResponseDTO.setMaxUnitsPerPack(20);
		fineLinePackOptimizationResponseDTO.setMaxNbrOfPacks(5);
		fineLinePackOptimizationResponseDTO.setFactoryId("0121");
		fineLinePackOptimizationResponseDTO.setColorCombination("offWhite");
		fineLinePackOptimizationResponseDTO.setSinglePackInd(1);
		packOptimizationMapper.mapPackOptimizationFineline(fineLinePackOptimizationResponseDTO,fineLinePackOptimizationResponse,471l, 1,1);
		log.info("test: {}",fineLinePackOptimizationResponse);
		assertEquals(123456,fineLinePackOptimizationResponse.getFinelines().get(0).getCustomerChoices().get(0).getFixtures().get(0).getSizes().get(0)
				.getMetrics().stream().filter(metricsPackDto -> metricsPackDto.getStoreList().contains(0)
						&& metricsPackDto.getStoreList().contains(8) && metricsPackDto.getStoreList().contains(9)).findFirst().orElse(null).getBumpSet());
	}
}
