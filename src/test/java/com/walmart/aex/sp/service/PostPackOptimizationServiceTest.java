package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.CustomerChoices;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Fixtures;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

	@InjectMocks
	ReplenishmentsOptimizationService replenishmentsOptimizationService;

	
	Optional<List<FinelineReplPack>> optional;
	Optional<List<MerchCatgReplPack>> optional1;
	Optional<List<SubCatgReplPack>> optional2;
	Optional<List<CcReplPack>> optional3;
	Optional<List<StyleReplPack>> optional4;
	Optional<List<CcMmReplPack>> optional5;
	Optional<List<CcSpMmReplPack>> optional6;
	
	@BeforeEach
	void testUpdateInitialSetAndBumpPackAty() {
//
//		 List<CustomerChoices> customerChoices = new ArrayList<>();
//		 List<Fixtures> fixtures = new ArrayList<>();
//		 List<Size> sizes = new ArrayList<>();
//
//
//		 Size sz = new Size();
//		 sz.setSizeDesc("SMALL");
//		 sz.setOptFinalBuyQty(5000);
//		 sz.setOptFinalInitialSetQty(13000);
//		 sz.setOptFinalBumpSetQty(13000);
//		 sizes.add(sz);
//
//		 Fixtures fix = new Fixtures();
//		 fix.setFixtureType("ENDCAPS");
//		 fix.setMerchMethod("HANGING");
//		 fix.setSizes(sizes);
//		 fixtures.add(fix);
//
//		 CustomerChoices ccs = new CustomerChoices();
//		 ccs.setCcId("34_1021_2_21_2_AURA ORANGE STENCIL");
//		 ccs.setFixtures(fixtures);
//		 customerChoices.add(ccs);
//
//		isAndBPQtyDTO = new ISAndBPQtyDTO();
//		isAndBPQtyDTO.setCustomerChoices(customerChoices);
//
//
//		MerchCatgReplPack merchCatgReplPack = new MerchCatgReplPack();
//		merchCatgReplPack.setReplUnits(2000);
//		optional1 = Optional.of(List.of(merchCatgReplPack));
//		SubCatgReplPack subCatgReplPack = new SubCatgReplPack();
//		subCatgReplPack.setReplUnits(2000);
//		optional2 = Optional.of(List.of(subCatgReplPack));
//		CcReplPack ccReplPack = new CcReplPack();
//		ccReplPack.setFinalBuyUnits(12000);
//		ccReplPack.setReplUnits(8000);
//		optional3 = Optional.of(List.of(ccReplPack));
//		StyleReplPack styleReplPack = new StyleReplPack();
//		styleReplPack.setReplUnits(8000);
//		optional4 = Optional.of(List.of(styleReplPack));
//		CcMmReplPack ccMmReplPack = new CcMmReplPack();
//		ccMmReplPack.setFinalBuyUnits(12000);
//		optional5 = Optional.of(List.of(ccMmReplPack));
//		CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
//		ccSpMmReplPack.setFinalBuyUnits(12000);
//		ccSpMmReplPack.setReplenObj("[{\"replnWeek\":12244,\"replnWeekDesc\":\"FYE2023WK44\",\"replnUnits\":null,\"adjReplnUnits\":4000,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12245,\"replnWeekDesc\":\"FYE2023WK45\",\"replnUnits\":null,\"adjReplnUnits\":4000,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}]");
//		ccSpMmReplPack.setVendorPackCnt(2);
//		optional6 = Optional.of(List.of(ccSpMmReplPack));
//
//		//Mockito.when(merchCatgReplPackRepository.findByPlanIdAndFinelineNbr(471l, 1021)).thenReturn(optional1);
//		Mockito.when(subCatgReplnPkConsRepository.findByPlanIdAndFinelineNbr(471l, 1021)).thenReturn(optional2);
//		Mockito.when(ccReplnPkConsRepository.findByPlanIdAndCCId(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(optional3);
//		Mockito.when(styleReplnPkConsRepository.findByPlanIdAndCCId(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(optional4);
//		Mockito.when(ccMmReplnPkConsRepository.findCcMmReplnPkConsData(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL")).thenReturn(optional5);
//		Mockito.when(ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(471l, 1021,"34_1021_2_21_2_AURA ORANGE STENCIL","SMALL")).thenReturn(optional6);
//		objectMapper = new ObjectMapper();
//		postPackOptimizationService = new PostPackOptimizationService(merchCatgReplPackRepository,finelineReplnPkConsRepository,subCatgReplnPkConsRepository,styleReplnPkConsRepository,ccReplnPkConsRepository,ccMmReplnPkConsRepository,ccSpReplnPkConsRepository,objectMapper,replenishmentsOptimizationService);
//		postPackOptimizationService.updateInitialSetAndBumpPackAty(471l, 1021, isAndBPQtyDTO);
//
//		String resJson = "[{\"replnWeek\":12244,\"replnWeekDesc\":\"FYE2023WK44\",\"replnUnits\":null,\"adjReplnUnits\":3500,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null},{\"replnWeek\":12245,\"replnWeekDesc\":\"FYE2023WK45\",\"replnUnits\":null,\"adjReplnUnits\":3500,\"remainingUnits\":null,\"dcInboundUnits\":null,\"dcInboundAdjUnits\":null}]";
//		assertEquals(resJson,ccSpMmReplPack.getReplenObj());

	}

	@Test
	public void finelinePostPackOptTest() {
		//what exists in db
		FinelineReplPack finelineReplPackHanging = finelineReplPack(1, 5000,1000);
		FinelineReplPack finelineReplPackFolded = finelineReplPack(2, 10000,2000);
		optional = Optional.of(List.of(finelineReplPackHanging, finelineReplPackFolded));

		CustomerChoices ccs = new CustomerChoices();
		ccs.setCcId("34_1021_2_21_2_AURA ORANGE STENCIL");
		ccs.setFixtures(new ArrayList<>());
		//updated buy qty from pack optimization
		Fixtures fixHanging = fixture("HANGING");
		Size szHanging = size("SMALL", 4000);
		fixHanging.getSizes().add(szHanging);
		//ccs.getFixtures().add(fixHanging);

		Fixtures fixFolded = fixture("FOLDED");
		Size szFolded = size("SMALL", 12000);
		fixFolded.getSizes().add(szFolded);
		ccs.getFixtures().add(fixFolded);

		isAndBPQtyDTO = new ISAndBPQtyDTO();
		isAndBPQtyDTO.setCustomerChoices(List.of(ccs));

		Mockito.when(finelineReplnPkConsRepository.findByPlanIdAndFinelineNbr(34L, 1021)).thenReturn(optional);
		Map<Integer, Integer> replnDifferenceByMerchMethod = postPackOptimizationService.updateRCMerchFineline(34L, 1021, isAndBPQtyDTO);
	}

	private FinelineReplPack finelineReplPack(int fixtureTypeId, int finalBuyUnits, int replUnits) {
		FinelineReplPack frp = new FinelineReplPack();
		frp.setFinelineReplPackId(new FinelineReplPackId());
		frp.getFinelineReplPackId().setSubCatgReplPackId(new SubCatgReplPackId());
		frp.getFinelineReplPackId().getSubCatgReplPackId().setMerchCatgReplPackId(new MerchCatgReplPackId());
		frp.getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().setFixtureTypeRollupId(fixtureTypeId);
		frp.setReplUnits(replUnits);
		frp.setFinalBuyUnits(finalBuyUnits);
		return frp;
	}

	private Fixtures fixture(String merchMethod){
		Fixtures fixtures = new Fixtures();
		fixtures.setSizes(new ArrayList<>());
		fixtures.setMerchMethod(merchMethod);
		fixtures.setFixtureType("DEFAULT");
		return fixtures;
	}

	private Size size(String sizeDesc, int finalBuyQty) {
		Size size = new Size();
		size.setSizeDesc(sizeDesc);
		size.setOptFinalBuyQty(finalBuyQty);
		return size;
	}


}
