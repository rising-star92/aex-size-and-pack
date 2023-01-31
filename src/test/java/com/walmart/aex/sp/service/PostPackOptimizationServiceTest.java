package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.isbpqty.CustomerChoices;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Fixtures;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
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
	private PostPackOptimizationService postPackOptimizationService;

	@Mock
	private ISAndBPQtyDTO isAndBPQtyDTO;
	@Mock
	private CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

	@Mock
	private UpdateReplnConfigMapper updateReplnConfigMapper;

	@Mock
	private ReplenishmentService replenishmentService;

	@Mock
	private SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;

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
		Mockito.when(ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(34L, 2852,"34_2852_4_19_2_GEMSLT", "0X")).thenReturn(optional);
		Mockito.when(spCustomerChoiceChannelFixtureSizeRepository.getSpCcChanFixtrDataByPlanFineline(Mockito.any(),Mockito.any())).thenReturn(getSpCustomerChoiceChannelFixtureSizeList());
		postPackOptimizationService.updateInitialSetAndBumpPackAty(34L, 2852, createPostPackDto(1112, 1112));
		ArgumentCaptor<List<CcSpMmReplPack>> ccspCaptor = ArgumentCaptor.forClass(List.class);
		verify(updateReplnConfigMapper, times(1)).updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccspCaptor.capture());
		verify(replenishmentService, times(1)).updateVnpkWhpkForCatgReplnCons(any(), any(), any());
		List<CcSpMmReplPack> ccSpMmRepls = ccspCaptor.getValue();
		assertEquals(612, ccSpMmRepls.get(0).getReplUnits(), "Repln units should be reduced to 518 for 0X Hanging");

	}

	private SpFineLineChannelFixtureId getSpFineLineChannelFixtureId() {
		SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId();
		spFineLineChannelFixtureId.setChannelId(1);
		spFineLineChannelFixtureId.setFineLineNbr(2852);
		FixtureTypeRollUpId fixtureTypeRollUpId = new FixtureTypeRollUpId();
		fixtureTypeRollUpId.setFixtureTypeRollupId(1);
		spFineLineChannelFixtureId.setFixtureTypeRollUpId(fixtureTypeRollUpId);
		spFineLineChannelFixtureId.setPlanId(34L);
		spFineLineChannelFixtureId.setLvl0Nbr(lvl0Nbr);
		spFineLineChannelFixtureId.setLvl1Nbr(lvl1Nbr);
		spFineLineChannelFixtureId.setLvl2Nbr(lvl2Nbr);
		spFineLineChannelFixtureId.setLvl3Nbr(lvl3Nbr);
		spFineLineChannelFixtureId.setLvl4Nbr(lvl4Nbr);
		return spFineLineChannelFixtureId;
	}

	private SpCustomerChoiceChannelFixtureId getSpCustomerChoiceChannelFixtureId() {
		SpCustomerChoiceChannelFixtureId spCustomerChoiceChannelFixtureId = new SpCustomerChoiceChannelFixtureId();
		spCustomerChoiceChannelFixtureId.setCustomerChoice("34_2852_4_19_2_GEMSLT");
		spCustomerChoiceChannelFixtureId.setSpStyleChannelFixtureId(getSpStyleChannelFixtureId());
		return spCustomerChoiceChannelFixtureId;
	}


	private List<SpCustomerChoiceChannelFixtureSize> getSpCustomerChoiceChannelFixtureSizeList() {
		List<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizeList = new ArrayList<>();

		SpCustomerChoiceChannelFixtureSize spCustomerChoiceChannelFixtureSize = new SpCustomerChoiceChannelFixtureSize();
		spCustomerChoiceChannelFixtureSize.setInitialSetQty(1000);
		spCustomerChoiceChannelFixtureSize.setReplnQty(500);
		spCustomerChoiceChannelFixtureSize.setAhsSizeDesc("0X");
		SpCustomerChoiceChannelFixtureSizeId spCustomerChoiceChannelFixtureSizeId = new SpCustomerChoiceChannelFixtureSizeId();
		spCustomerChoiceChannelFixtureSizeId.setSpCustomerChoiceChannelFixtureId(getSpCustomerChoiceChannelFixtureId());
		spCustomerChoiceChannelFixtureSizeId.setAhsSizeId(234);
		spCustomerChoiceChannelFixtureSize.setSpCustomerChoiceChannelFixtureSizeId(spCustomerChoiceChannelFixtureSizeId);

		SpCustomerChoiceChannelFixtureSize spCustomerChoiceChannelFixtureSizeObjDiffSize = new SpCustomerChoiceChannelFixtureSize();
		spCustomerChoiceChannelFixtureSizeObjDiffSize.setInitialSetQty(1000);
		spCustomerChoiceChannelFixtureSizeObjDiffSize.setAhsSizeDesc("1X");
		spCustomerChoiceChannelFixtureSizeObjDiffSize.setReplnQty(500);
		SpCustomerChoiceChannelFixtureSizeId spCustomerChoiceChannelFixtureSizeIdDiffSize = new SpCustomerChoiceChannelFixtureSizeId();
		spCustomerChoiceChannelFixtureSizeIdDiffSize.setSpCustomerChoiceChannelFixtureId(getSpCustomerChoiceChannelFixtureId());
		spCustomerChoiceChannelFixtureSizeObjDiffSize.setSpCustomerChoiceChannelFixtureSizeId(spCustomerChoiceChannelFixtureSizeIdDiffSize);

		spCustomerChoiceChannelFixtureSizeList.add(spCustomerChoiceChannelFixtureSize);
		spCustomerChoiceChannelFixtureSizeList.add(spCustomerChoiceChannelFixtureSizeObjDiffSize);
		return spCustomerChoiceChannelFixtureSizeList;
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
		ccId.getCcMmReplPackId().getCcReplPackId().setCustomerChoice("34_2852_4_19_2_GEMSLT");
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().setStyleNbr("34_2852_4_19_2");
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().setFinelineNbr(2852);
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().setPlanId(34L);
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().setFixtureTypeRollupId(1);
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().setRepTLvl0(lvl0Nbr);
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().setRepTLvl1(lvl1Nbr);
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().setRepTLvl2(lvl2Nbr);
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().setRepTLvl3(lvl3Nbr);
		ccId.getCcMmReplPackId().getCcReplPackId().getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().setRepTLvl4(lvl4Nbr);
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
