package com.walmart.aex.sp.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import com.walmart.aex.sp.dto.packoptimization.isbpqty.CustomerChoices;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Fixtures;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostPackOptimizationServiceTest {


	@InjectMocks
	@Spy
	PostPackOptimizationService postPackOptimizationService;

	@Mock
	ISAndBPQtyDTO isAndBPQtyDTO;

	@Mock
	private ObjectMapper objectMapper;
	@Mock
	FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	@Mock
	MerchCatgReplPackRepository merchCatgReplPackRepository;
	@Mock
	SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	@Mock
	StyleReplnPkConsRepository styleReplnPkConsRepository;
	@Mock
	CcReplnPkConsRepository ccReplnPkConsRepository;
	@Mock
	CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
	@Mock
	CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

	@Mock
	ReplenishmentsOptimizationService replenishmentsOptimizationService;
	
	Optional<FinelineReplPack> optional;
	Optional<MerchCatgReplPack> optional1;
	Optional<SubCatgReplPack> optional2;
	Optional<CcReplPack> optional3;
	Optional<StyleReplPack> optional4;
	Optional<CcMmReplPack> optional5;
	Optional<CcSpMmReplPack> optional6;
	
	@Test
	public void testUpdateInitialSetAndBumpPackAty() throws JsonProcessingException {
		
		 List<CustomerChoices> customerChoices = new ArrayList<>();
		 List<Fixtures> fixtures = new ArrayList<>();
		 List<Size> sizes = new ArrayList<>();

		 
		 Size sz = new Size();
		 sz.setSizeDesc("SMALL");
		 sz.setOptFinalBuyQty(5000);
		 sz.setOptFinalInitialSetQty(13000);
		 sz.setOptFinalBumpSetQty(13000);
		 sizes.add(sz);

		 Fixtures fix = new Fixtures();
		 fix.setFixtureType("ENDCAPS");
		 fix.setMerchMethod("HANGING");
		 fix.setSizes(sizes);
		 fixtures.add(fix);

		 CustomerChoices ccs = new CustomerChoices();
		 ccs.setCcId("34_1021_2_21_2_AURA ORANGE STENCIL");
		 ccs.setFixtures(fixtures);
		 customerChoices.add(ccs);

		isAndBPQtyDTO = new ISAndBPQtyDTO();
		isAndBPQtyDTO.setCustomerChoices(customerChoices);

		FinelineReplPack finelineReplPack = new FinelineReplPack();
		finelineReplPack.setFinalBuyUnits(12000);
		finelineReplPack.setReplUnits(6000);
		optional = Optional.of(finelineReplPack);
		MerchCatgReplPack merchCatgReplPack = new MerchCatgReplPack();
		merchCatgReplPack.setReplUnits(2000);
		optional1 = Optional.of(merchCatgReplPack);
		SubCatgReplPack subCatgReplPack = new SubCatgReplPack();
		subCatgReplPack.setReplUnits(2000);
		optional2 = Optional.of(subCatgReplPack);
		CcReplPack ccReplPack = new CcReplPack();
		ccReplPack.setFinalBuyUnits(12000);
		ccReplPack.setReplUnits(8000);
		optional3 = Optional.of(ccReplPack);
		StyleReplPack styleReplPack = new StyleReplPack();
		styleReplPack.setReplUnits(8000);
		optional4 = Optional.of(styleReplPack);
		CcMmReplPack ccMmReplPack = new CcMmReplPack();
		ccMmReplPack.setFinalBuyUnits(12000);
		optional5 = Optional.of(ccMmReplPack);
		CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
		ccSpMmReplPack.setFinalBuyUnits(12000);
		ccSpMmReplPack.setReplenObj("[{\"replnWeek\":12244,\"replnWeekDesc\":\"FYE2023WK44\",\"replnUnits\":null,\"adjReplnUnits\":4000,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12245,\"replnWeekDesc\":\"FYE2023WK45\",\"replnUnits\":null,\"adjReplnUnits\":4000,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}]");
		optional6 = Optional.of(ccSpMmReplPack);

		objectMapper = new ObjectMapper();
		Integer updatedReplenishmentQty = 7000;
		List<Replenishment> replObj = objectMapper.readValue(ccSpMmReplPack.getReplenObj(), new TypeReference<>() {});
		Long total = 8000l;
		List<Replenishment> updateReplObj = replObj.stream()
				.peek(replenishment -> replenishment.setAdjReplnUnits((updatedReplenishmentQty*(((replenishment.getAdjReplnUnits()*100)/total))/100)))
				.collect(Collectors.toList());

		Mockito.when(replenishmentsOptimizationService.getUpdatedReplenishmentsPack(updateReplObj,ccMmReplPack.getVnpkWhpkRatio())).thenReturn(updateReplObj);
		Mockito.when(finelineReplnPkConsRepository.findByPlanIdAndFinelineNbr(471l, 1021)).thenReturn(optional);
		Mockito.when(merchCatgReplPackRepository.findByPlanIdAndFinelineNbr(471l, 1021)).thenReturn(optional1);
		Mockito.when(subCatgReplnPkConsRepository.findByPlanIdAndFinelineNbr(471l, 1021)).thenReturn(optional2);
		Mockito.when(ccReplnPkConsRepository.findByPlanIdAndCCId(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(optional3);
		Mockito.when(styleReplnPkConsRepository.findByPlanIdAndCCId(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(optional4);
		Mockito.when(ccMmReplnPkConsRepository.findCcMmReplnPkConsData(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL",1,2)).thenReturn(optional5);
		Mockito.when(ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL",1,2,"SMALL")).thenReturn(optional6);
		postPackOptimizationService = new PostPackOptimizationService(merchCatgReplPackRepository,finelineReplnPkConsRepository,subCatgReplnPkConsRepository,styleReplnPkConsRepository,ccReplnPkConsRepository,ccMmReplnPkConsRepository,ccSpReplnPkConsRepository,objectMapper,replenishmentsOptimizationService);
		postPackOptimizationService.updateInitialSetAndBumpPackAty(471l, 1021, isAndBPQtyDTO);

		String resJson = "[{\"replnWeek\":12244,\"replnWeekDesc\":\"FYE2023WK44\",\"replnUnits\":null,\"adjReplnUnits\":3500,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12245,\"replnWeekDesc\":\"FYE2023WK45\",\"replnUnits\":null,\"adjReplnUnits\":3500,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}]";
		assertEquals(resJson,ccSpMmReplPack.getReplenObj());

	}


}
