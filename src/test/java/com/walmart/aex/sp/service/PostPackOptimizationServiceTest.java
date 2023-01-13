package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.isbpqty.CustomerChoices;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Fixtures;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import com.walmart.aex.sp.entity.CcMmReplPackId;
import com.walmart.aex.sp.entity.CcReplPackId;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPackId;
import com.walmart.aex.sp.entity.FinelineReplPackId;
import com.walmart.aex.sp.entity.MerchCatgReplPackId;
import com.walmart.aex.sp.entity.StyleReplPackId;
import com.walmart.aex.sp.entity.SubCatgReplPackId;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
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

	@Test
	public void ccFixSizePostPackOptTest() {
		//what exists in db
		CcSpMmReplPack ccSpMmReplPackHanging = ccSpMmReplPack(1, 1630, 618);
		Optional<List<CcSpMmReplPack>> optional = Optional.of(List.of(ccSpMmReplPackHanging));

		Mockito.when(ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(34L, 2852,"34_2852_4_19_2_GEMSLT", "0X")).thenReturn(optional);
		postPackOptimizationService.updateInitialSetAndBumpPackAty(34L, 2852, createPostPackDto(1112, 1112));

		ArgumentCaptor<List<CcSpMmReplPack>> ccspCaptor = ArgumentCaptor.forClass(List.class);
		verify(updateReplnConfigMapper, times(1)).updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccspCaptor.capture());
		verify(replenishmentService, times(1)).updateVnpkWhpkForCatgReplnCons(any(), any(), any());
		List<CcSpMmReplPack> ccSpMmRepls = ccspCaptor.getValue();
		assertEquals(518, ccSpMmRepls.get(0).getReplUnits(), "Repln units should be reduced to 518 for 0X Hanging");

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
