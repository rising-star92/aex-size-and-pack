package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.CustomerChoices;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Fixtures;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.MerchMethod;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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

	@Test
	public void finelinePostPackOptTest() {
		//what exists in db
		FinelineReplPack finelineReplPackHanging = finelineReplPack(1, 1630,618);
		FinelineReplPack finelineReplPackFolded = finelineReplPack(2, 7279,2023);
		Optional<List<FinelineReplPack>> optional = Optional.of(List.of(finelineReplPackHanging, finelineReplPackFolded));

		Mockito.when(finelineReplnPkConsRepository.findByPlanIdAndFinelineNbr(34L, 2852)).thenReturn(optional);
		Map<Integer, Integer> replnDifferenceByMerchMethod = postPackOptimizationService.updateRCMerchFineline(34L, 2852, createPostPackDto(1012, 5356));
		assertEquals(replnDifferenceByMerchMethod.get(MerchMethod.HANGING.getId()), 0, "Should be no change in repl for hanging");
		assertEquals(replnDifferenceByMerchMethod.get(MerchMethod.FOLDED.getId()), -100, "Should be -100 in repl for folded");
	}

	@Test
	public void merchCatgPostPackOptTest() {
		//what exists in db
		MerchCatgReplPack merchCatgReplPackHanging = merchCatgReplPack(1, 1415358, 949122);
		MerchCatgReplPack merchCatgReplPackFolded = merchCatgReplPack(2, 424588, 203833);
		Optional<List<MerchCatgReplPack>> optional = Optional.of(List.of(merchCatgReplPackHanging, merchCatgReplPackFolded));
		Mockito.when(merchCatgReplPackRepository.findByPlanIdAndFinelineNbr(34L, 2852)).thenReturn(optional);

		Map<Integer, Integer> replnRollupDifferenceByMerchMethod = Map.of(1,0, 2, -3833);
		postPackOptimizationService.updateRCMerchCatg(34L, 2852, replnRollupDifferenceByMerchMethod);
		ArgumentCaptor<List<MerchCatgReplPack>> merchCatgCaptor = ArgumentCaptor.forClass(List.class);
		verify(merchCatgReplPackRepository).saveAll(merchCatgCaptor.capture());
		List<MerchCatgReplPack> merchCatgReplns = merchCatgCaptor.getValue();

		assertEquals(merchCatgReplns.stream()
						.filter(merchRepln -> merchRepln.getMerchCatgReplPackId().getFixtureTypeRollupId().equals(MerchMethod.HANGING.getId()))
						.findFirst()
						.get().getReplUnits(), 949122, "Should be 949122 units");

		assertEquals(merchCatgReplns.stream()
				.filter(merchRepln -> merchRepln.getMerchCatgReplPackId().getFixtureTypeRollupId().equals(MerchMethod.FOLDED.getId()))
				.findFirst()
				.get().getReplUnits(), 200000, "Should be 200000 repl units");
	}

	@Test
	public void merchSubcatgPostPackOptTest() {
		SubCatgReplPack subCatgReplPackHanging = subCatgReplPack(1, 987028,685292);
		SubCatgReplPack subCatgReplPackFolded = subCatgReplPack(2, 424588,203833);
		Optional<List<SubCatgReplPack>> optional = Optional.of(List.of(subCatgReplPackHanging, subCatgReplPackFolded));
		Mockito.when(subCatgReplnPkConsRepository.findByPlanIdAndFinelineNbr(34L, 2852)).thenReturn(optional);

		Map<Integer, Integer> replnRollupDifferenceByMerchMethod = Map.of(1,0, 2, -3833);
		postPackOptimizationService.updateRCMerchSubCatg(34L, 2852, replnRollupDifferenceByMerchMethod);
		ArgumentCaptor<List<SubCatgReplPack>> subcatgCaptor = ArgumentCaptor.forClass(List.class);
		verify(subCatgReplnPkConsRepository).saveAll(subcatgCaptor.capture());
		List<SubCatgReplPack> merchCatgReplns = subcatgCaptor.getValue();

		assertEquals(merchCatgReplns.stream()
				.filter(merchRepln -> merchRepln.getSubCatgReplPackId().getMerchCatgReplPackId().getFixtureTypeRollupId().equals(MerchMethod.HANGING.getId()))
				.findFirst()
				.get().getReplUnits(), 685292, "Should be 685292 units");

		assertEquals(merchCatgReplns.stream()
				.filter(merchRepln -> merchRepln.getSubCatgReplPackId().getMerchCatgReplPackId().getFixtureTypeRollupId().equals(MerchMethod.FOLDED.getId()))
				.findFirst()
				.get().getReplUnits(), 200000, "Should be 200000 repl units");
	}

	@Test
	public void ccFixSizePostPackOptTest() {
		//what exists in db
		CcSpMmReplPack ccSpMmReplPackHanging = ccSpMmReplPack(1, 1630, 618);
		Optional<List<CcSpMmReplPack>> optional = Optional.of(List.of(ccSpMmReplPackHanging));

		Mockito.when(ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(34L, 2852,"34_2852_4_19_2_GEMSLT", "0X")).thenReturn(optional);
		postPackOptimizationService.updateRCMerchMethodCCFixtureSize(34L, 2852, createPostPackDto(1112, 1112));

		ArgumentCaptor<List<CcSpMmReplPack>> ccspCaptor = ArgumentCaptor.forClass(List.class);
		verify(ccSpReplnPkConsRepository, times(2)).saveAll(ccspCaptor.capture());
		List<CcSpMmReplPack> ccSpMmRepls = ccspCaptor.getValue();
		assertEquals(518, ccSpMmRepls.get(0).getReplUnits(), "Repln units should be reduced to 518 for 0X Hanging");

	}

	@Test
	public void ccStylePostPackOptTest() {
		final String ccReplPack_GEMSLT = "34_2852_4_19_2_GEMSLT";
		final String ccReplPack_PURPRL = "34_2852_4_19_2_PURPRL";
		final String styleNbr_19_2 = "34_2852_4_19_2";
		CcReplPack ccReplPackHanging = ccReplPack(1, 10000, 4000);
		ccReplPackHanging.getCcReplPackId().setCustomerChoice(ccReplPack_GEMSLT);
		ccReplPackHanging.getCcReplPackId().getStyleReplPackId().setStyleNbr(styleNbr_19_2);
		CcReplPack ccReplPackHanging2 = ccReplPack(1, 1000, 500);
		ccReplPackHanging2.getCcReplPackId().setCustomerChoice(ccReplPack_PURPRL);
		ccReplPackHanging2.getCcReplPackId().getStyleReplPackId().setStyleNbr(styleNbr_19_2);
		CcReplPack ccReplPackFolded = ccReplPack(2,47000,13000);
		ccReplPackFolded.getCcReplPackId().setCustomerChoice(ccReplPack_GEMSLT);
		ccReplPackFolded.getCcReplPackId().getStyleReplPackId().setStyleNbr(styleNbr_19_2);
		Optional<List<CcReplPack>> optionalCcReplPacks = Optional.of(List.of(ccReplPackHanging, ccReplPackFolded, ccReplPackHanging2));

		StyleReplPack styleReplPackHanging = styleReplPack(1, 11000, 4500);
		styleReplPackHanging.getStyleReplPackId().setStyleNbr(styleNbr_19_2);
		StyleReplPack styleReplPackFolded = styleReplPack(2, 47000, 13000);
		styleReplPackFolded.getStyleReplPackId().setStyleNbr(styleNbr_19_2);
		Optional<List<StyleReplPack>> optionalStyleReplPacks = Optional.of(List.of(styleReplPackHanging, styleReplPackFolded));

		Mockito.when(ccReplnPkConsRepository.findByPlanIdAndCCId(34L, 2852, ccReplPack_GEMSLT)).thenReturn(optionalCcReplPacks);
		Mockito.when(ccReplnPkConsRepository.findByPlanIdAndCCId(34L, 2852, ccReplPack_PURPRL)).thenReturn(optionalCcReplPacks);
		Mockito.when(styleReplnPkConsRepository.findByPlanIdAndCCId(34L,2852,ccReplPack_GEMSLT)).thenReturn(optionalStyleReplPacks);
		Mockito.when(styleReplnPkConsRepository.findByPlanIdAndCCId(34L,2852,ccReplPack_PURPRL)).thenReturn(optionalStyleReplPacks);

		ISAndBPQtyDTO isAndBPQtyDTO = createPostPackDto(11000, 50000);
		CustomerChoices cc2 = new CustomerChoices();
		cc2.setCcId(ccReplPack_PURPRL);
		Fixtures f2 = new Fixtures();
		f2.setMerchMethod("HANGING");
		f2.setSizes(new ArrayList<>());
		Size size = new Size();
		size.setSizeDesc("0X");
		size.setOptFinalBuyQty(1500);
		f2.getSizes().add(size);
		cc2.setFixtures(new ArrayList<>());
		cc2.getFixtures().add(f2);
		isAndBPQtyDTO.getCustomerChoices().add(cc2);

		postPackOptimizationService.updateRCStyleAndCustomerChoice(34L, 2852, isAndBPQtyDTO);

		ArgumentCaptor<List<CcReplPack>> ccCaptor = ArgumentCaptor.forClass(List.class);
		verify(ccReplnPkConsRepository, times(2)).saveAll(ccCaptor.capture());
		List<CcReplPack> ccReplPacks = ccCaptor.getValue();

		assertEquals(3000, getFromCCReplPacks(ccReplPack_GEMSLT, MerchMethod.HANGING.getId(), ccReplPacks).getReplUnits(), "Should be 3000 repln units");
		assertEquals(10000, getFromCCReplPacks(ccReplPack_GEMSLT, MerchMethod.FOLDED.getId(), ccReplPacks).getReplUnits(), "Should be 10000 repln units");
		assertEquals(0, getFromCCReplPacks(ccReplPack_PURPRL, MerchMethod.HANGING.getId(), ccReplPacks).getReplUnits(), "Should be 0 repln units");

		assertEquals(3000, styleReplPackHanging.getReplUnits(), "Hanging should be reduced by 1500");
		assertEquals(10000, styleReplPackFolded.getReplUnits(), "Folded should be reduced by 3000");
	}

	private CcReplPack getFromCCReplPacks(String ccId, int merchMethodCode, List<CcReplPack> ccReplPacks) {
		return ccReplPacks.stream()
				.filter(ccReplPack -> ccReplPack.getCcReplPackId().getCustomerChoice().equalsIgnoreCase(ccId))
				.filter(ccReplPack -> ccReplPack.getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getFixtureTypeRollupId().equals(merchMethodCode))
				.findFirst()
				.get();
	}

	private CcReplPack ccReplPack(int fixtureTypeId, int finalBuyUnits, int replUnits) {
		CcReplPack ccReplPack = new CcReplPack();
		ccReplPack.setCcReplPackId(ccReplPackId(fixtureTypeId));
		ccReplPack.setReplUnits(replUnits);
		ccReplPack.setFinalBuyUnits(finalBuyUnits);
		return ccReplPack;
	}

	private StyleReplPack styleReplPack(int fixtureTypeId, int finalBuyUnits, int replUnits) {
		StyleReplPack styleReplPack = new StyleReplPack();
		styleReplPack.setStyleReplPackId(styleReplPackId(fixtureTypeId));
		styleReplPack.setReplUnits(replUnits);
		styleReplPack.setFinalBuyUnits(finalBuyUnits);
		return styleReplPack;
	}

	private CcSpMmReplPack ccSpMmReplPack(int fixtureTypeId, int finalBuyUnits, int replUnits) {
		CcSpMmReplPackId ccId = new CcSpMmReplPackId();
		CcSpMmReplPack ccSpMMReplPack = new CcSpMmReplPack();
		ccId.setCcMmReplPackId(new CcMmReplPackId());
		ccId.getCcMmReplPackId().setCcReplPackId(new CcReplPackId());
		ccId.getCcMmReplPackId().getCcReplPackId().setStyleReplPackId(new StyleReplPackId());
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().setFinelineReplPackId(finelineReplPackId(fixtureTypeId));
		ccSpMMReplPack.setCcSpReplPackId(ccId);
		ccSpMMReplPack.setReplPackCnt(replUnits);
		ccSpMMReplPack.setFinalBuyUnits(finalBuyUnits);
		return ccSpMMReplPack;
	}

	private MerchCatgReplPack merchCatgReplPack(int fixtureTypeId, int finalBuyUnits, int replUnits) {
		MerchCatgReplPack mrp = new MerchCatgReplPack();
		mrp.setMerchCatgReplPackId(merchCatgReplPackId(fixtureTypeId));
		mrp.setReplUnits(replUnits);
		mrp.setFinalBuyUnits(finalBuyUnits);
		return mrp;
	}

	private SubCatgReplPack subCatgReplPack(int fixtureTypeId, int finalBuyUnits, int replUnits) {
		SubCatgReplPack subCatgReplPack = new SubCatgReplPack();
		subCatgReplPack.setSubCatgReplPackId(subCatgReplPackId(fixtureTypeId));
		subCatgReplPack.setReplUnits(replUnits);
		subCatgReplPack.setFinalBuyUnits(finalBuyUnits);
		return subCatgReplPack;
	}

	private MerchCatgReplPackId merchCatgReplPackId(int fixtureTypeId) {
		MerchCatgReplPackId mrpid = new MerchCatgReplPackId();
		mrpid.setFixtureTypeRollupId(fixtureTypeId);
		return mrpid;
	}
	private SubCatgReplPackId subCatgReplPackId(int fixtureTypeId) {
		return new SubCatgReplPackId(merchCatgReplPackId(fixtureTypeId));
	}
	private FinelineReplPackId finelineReplPackId(int fixtureTypeId) {
		return new FinelineReplPackId(subCatgReplPackId(fixtureTypeId));
	}
	private StyleReplPackId styleReplPackId(int fixtureTypeId) {
		return new StyleReplPackId(finelineReplPackId(fixtureTypeId));
	}
	private CcReplPackId ccReplPackId(int fixtureTypeId) {
		return new CcReplPackId(styleReplPackId(fixtureTypeId));
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

	private ISAndBPQtyDTO createPostPackDto(int optimizedHangingBuyQty, int optimizedFoldedBuyQty) {
		CustomerChoices ccs = new CustomerChoices();
		ccs.setCcId("34_2852_4_19_2_GEMSLT");
		ccs.setFixtures(new ArrayList<>());
		//updated buy qty from pack optimization
		Fixtures fixHanging = fixture("HANGING");
		Size szHanging = size("0X", optimizedHangingBuyQty);
		fixHanging.getSizes().add(szHanging);
		ccs.getFixtures().add(fixHanging);

		Fixtures fixFolded = fixture("FOLDED");
		Size szFolded = size("0X", optimizedFoldedBuyQty);
		fixFolded.getSizes().add(szFolded);
		ccs.getFixtures().add(fixFolded);

		isAndBPQtyDTO = new ISAndBPQtyDTO();
		isAndBPQtyDTO.setCustomerChoices(new ArrayList<>());
		isAndBPQtyDTO.getCustomerChoices().add(ccs);
		return isAndBPQtyDTO;
	}
}
