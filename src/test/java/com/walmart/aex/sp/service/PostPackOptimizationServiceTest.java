package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.isbpqty.CustomerChoices;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Fixtures;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
import org.jetbrains.annotations.NotNull;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
	CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

	@Mock
	UpdateReplnConfigMapper updateReplnConfigMapper;

	@Mock
	ReplenishmentService replenishmentService;

	@Mock
	SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
	@Mock
	SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;

	private static Integer lvl0Nbr = 50000;
	private static Integer lvl1Nbr = 34;
	private static Integer lvl2Nbr = 6419;
	private static Integer lvl3Nbr = 12228;
	private static Integer lvl4Nbr = 31507;
	@Test
	public void ccFixSizePostPackOptTest() {
		//what exists in db
		CcSpMmReplPack ccSpMmReplPackHanging = ccSpMmReplPack(1, 1630, 618);
		Optional<List<CcSpMmReplPack>> optional = Optional.of(List.of(ccSpMmReplPackHanging));

//		SpCustomerChoiceChannelFixtureSize spCustomerChoiceChannelFixtureSize = new SpCustomerChoiceChannelFixtureSize();
//		spCustomerChoiceChannelFixtureSize.setInitialSetQty(600);
//		spCustomerChoiceChannelFixtureSizeList.add(spCustomerChoiceChannelFixtureSize);
		Mockito.when(ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(34L, 2852,"34_2852_4_19_2_GEMSLT", "0X")).thenReturn(optional);
		Mockito.when(spCustomerChoiceChannelFixtureRepository.getSpChanFixtrDataByPlanFineline(Mockito.any(),Mockito.any())).thenReturn(getSpCustomerChoiceChannelFixtureList());
		Mockito.when(spCustomerChoiceChannelFixtureSizeRepository.getSpCcChanFixtrDataByPlanFineline(Mockito.any(),Mockito.any())).thenReturn(spCustomerChoiceChannelFixtureSizeList);
		postPackOptimizationService.updateInitialSetAndBumpPackAty(34L, 2852, createPostPackDto(1112, 1112));

		ArgumentCaptor<List<CcSpMmReplPack>> ccspCaptor = ArgumentCaptor.forClass(List.class);
		verify(updateReplnConfigMapper, times(1)).updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccspCaptor.capture());
		verify(replenishmentService, times(1)).updateVnpkWhpkForCatgReplnCons(any(), any(), any());
		List<CcSpMmReplPack> ccSpMmRepls = ccspCaptor.getValue();
		assertEquals(518, ccSpMmRepls.get(0).getReplUnits(), "Repln units should be reduced to 518 for 0X Hanging");

	}
//	SpCustomerChoiceChannelFixture spCustomerCCFixtr

	private List<SpCustomerChoiceChannelFixture> getSpCustomerChoiceChannelFixtureList() {
		SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture = new SpCustomerChoiceChannelFixture();
		List<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixtureList = new ArrayList<>();
		spCustomerChoiceChannelFixture.setInitialSetQty(600);
		SpCustomerChoiceChannelFixtureId spCustomerChoiceChannelFixtureId = getSpCustomerChoiceChannelFixtureId();
		SpStyleChannelFixtureId spStyleChannelFixtureId = new SpStyleChannelFixtureId();
		spStyleChannelFixtureId.setStyleNbr("34_2852_4_19_2");
		spCustomerChoiceChannelFixtureId.setSpStyleChannelFixtureId(spStyleChannelFixtureId);
		SpFineLineChannelFixtureId spFineLineChannelFixtureId = getSpFineLineChannelFixtureId();
		spStyleChannelFixtureId.setSpFineLineChannelFixtureId(spFineLineChannelFixtureId);
		spCustomerChoiceChannelFixtureList.add(spCustomerChoiceChannelFixture);
		return spCustomerChoiceChannelFixtureList;
	}

	@NotNull
	private SpFineLineChannelFixtureId getSpFineLineChannelFixtureId() {
		SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId();
		spFineLineChannelFixtureId.setChannelId(1);
		spFineLineChannelFixtureId.setFineLineNbr(2702);
		FixtureTypeRollUpId fixtureTypeRollUpId = new FixtureTypeRollUpId();
		fixtureTypeRollUpId.setFixtureTypeRollupId(1);
		spFineLineChannelFixtureId.setFixtureTypeRollUpId(fixtureTypeRollUpId);
		spFineLineChannelFixtureId.setPlanId(12l);
		spFineLineChannelFixtureId.setLvl0Nbr(lvl0Nbr);
		spFineLineChannelFixtureId.setLvl1Nbr(lvl1Nbr);
		spFineLineChannelFixtureId.setLvl2Nbr(lvl2Nbr);
		spFineLineChannelFixtureId.setLvl3Nbr(lvl3Nbr);
		spFineLineChannelFixtureId.setLvl4Nbr(lvl4Nbr);
		return spFineLineChannelFixtureId;
	}

	@NotNull
	private SpCustomerChoiceChannelFixtureId getSpCustomerChoiceChannelFixtureId() {
		SpCustomerChoiceChannelFixtureId spCustomerChoiceChannelFixtureId = new SpCustomerChoiceChannelFixtureId();
		spCustomerChoiceChannelFixtureId.setCustomerChoice("34_2852_4_19_2_GEMSLT");
		return spCustomerChoiceChannelFixtureId;
	}


	private List<SpCustomerChoiceChannelFixtureSize> getSpCustomerChoiceChannelFixtureSizeList() {
		SpCustomerChoiceChannelFixtureSize spCustomerChoiceChannelFixtureSize = new SpCustomerChoiceChannelFixtureSize();
		List<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizeList = new ArrayList<>();
		spCustomerChoiceChannelFixtureSize.setInitialSetQty(600);
		SpCustomerChoiceChannelFixtureSizeId spCustomerChoiceChannelFixtureSizeId = new SpCustomerChoiceChannelFixtureSizeId();
		spCustomerChoiceChannelFixtureSizeId.setSpCustomerChoiceChannelFixtureId(getSpCustomerChoiceChannelFixtureId());
		spCustomerChoiceChannelFixtureSizeId.setAhsSizeId(234);
		spCustomerChoiceChannelFixtureSize.setSpCustomerChoiceChannelFixtureSizeId(spCustomerChoiceChannelFixtureSizeId);
		getSpStyleChannelFixtureId();
//		spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getStyleNbr().equals(ccReplPackId.getStyleReplPackId().getStyleNbr()) &&


		spCustomerChoiceChannelFixtureList.add(spCustomerChoiceChannelFixture);
		return spCustomerChoiceChannelFixtureList;
	}

	private SpStyleChannelFixtureId getSpStyleChannelFixtureId() {
		SpStyleChannelFixtureId spStyleChannelFixtureId = new SpStyleChannelFixtureId();
		spStyleChannelFixtureId.setSpFineLineChannelFixtureId(getSpFineLineChannelFixtureId());
		spStyleChannelFixtureId.setStyleNbr("34_2852_4_19_2");
		return spStyleChannelFixtureId;
	}

	private CcSpMmReplPack ccSpMmReplPack(int fixtureTypeId, int finalBuyUnits, int replUnits) {
		CcSpMmReplPackId ccId = new CcSpMmReplPackId();
		CcSpMmReplPack ccSpMMReplPack = new CcSpMmReplPack();
		ccId.setCcMmReplPackId(new CcMmReplPackId());
		ccId.getCcMmReplPackId().setCcReplPackId(new CcReplPackId());
		ccId.getCcMmReplPackId().getCcReplPackId().setStyleReplPackId(new StyleReplPackId());
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().setFinelineReplPackId(finelineReplPackId(fixtureTypeId));
		ccSpMMReplPack.setCcSpReplPackId(ccId);
		ccSpMMReplPack.setReplUnits(replUnits);
		ccSpMMReplPack.setFinalBuyUnits(finalBuyUnits);
		return ccSpMMReplPack;
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

	private Fixtures fixture(String merchMethod){
		Fixtures fixtures = new Fixtures();
		fixtures.setSizes(new ArrayList<>());
		fixtures.setMerchMethod(merchMethod);
		fixtures.setFixtureType("DEFAULT");
		return fixtures;
	}
	private Size size(String sizeDesc, int finalBuyQty, int optFinalInitialSetQty) {
		Size size = new Size();
		size.setSizeDesc(sizeDesc);
		size.setOptFinalBuyQty(finalBuyQty);
		size.setOptFinalInitialSetQty(optFinalInitialSetQty);
		return size;
	}

	private ISAndBPQtyDTO createPostPackDto(int optimizedHangingBuyQty, int optimizedFoldedBuyQty) {
		CustomerChoices ccs = new CustomerChoices();
		ccs.setCcId("34_2852_4_19_2_GEMSLT");
		ccs.setFixtures(new ArrayList<>());
		//updated buy qty from pack optimization
		Fixtures fixHanging = fixture("HANGING");
		Size szHanging = size("0X", optimizedHangingBuyQty,82);
		fixHanging.getSizes().add(szHanging);
		ccs.getFixtures().add(fixHanging);

		Fixtures fixFolded = fixture("FOLDED");
		Size szFolded = size("0X", optimizedFoldedBuyQty,100);
		fixFolded.getSizes().add(szFolded);
		ccs.getFixtures().add(fixFolded);

		isAndBPQtyDTO = new ISAndBPQtyDTO();
		isAndBPQtyDTO.setCustomerChoices(new ArrayList<>());
		isAndBPQtyDTO.getCustomerChoices().add(ccs);
		return isAndBPQtyDTO;
	}
}
