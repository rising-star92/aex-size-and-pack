package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.FinelineReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.SubCatgReplPack;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UpdateReplnConfigMapper {
	private final CatgReplnPkConsRepository catgReplnPkConsRepository;
	private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
	private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;
	private final StyleReplnPkConsRepository styleReplnConsRepository;
	private final CcReplnPkConsRepository ccReplnPkConsRepository;
	private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
	private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
	private final ReplenishmentsOptimizationService replenishmentsOptimizationService;

	private final ObjectMapper objectMapper;
	public UpdateReplnConfigMapper(CatgReplnPkConsRepository catgReplnPkConsRepository, SubCatgReplnPkConsRepository subCatgReplnPkConsRepository,
								   FinelineReplnPkConsRepository finelineReplnPkConsRepository,
								   StyleReplnPkConsRepository styleReplnConsRepository, CcReplnPkConsRepository ccReplnPkConsRepository,
								   CcMmReplnPkConsRepository ccMmReplnPkConsRepository,
								   CcSpReplnPkConsRepository ccSpReplnPkConsRepository,
								   ReplenishmentsOptimizationService replenishmentsOptimizationService, ObjectMapper objectMapper) {
	   this.catgReplnPkConsRepository = catgReplnPkConsRepository;
	   this.subCatgReplnPkConsRepository = subCatgReplnPkConsRepository;
	   this.finelineReplnPkConsRepository = finelineReplnPkConsRepository;
	   this.styleReplnConsRepository = styleReplnConsRepository;
	   this.ccReplnPkConsRepository = ccReplnPkConsRepository;
	   this.ccMmReplnPkConsRepository = ccMmReplnPkConsRepository;
	   this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
		this.replenishmentsOptimizationService = replenishmentsOptimizationService;
		this.objectMapper = objectMapper;
	}

	public void updateVnpkWhpkForCatgReplnConsMapper(List<MerchCatgReplPack> catgReplnPkConsList, Integer vnpk, Integer whpk) {
		List<MerchCatgReplPack> udpatedMerchCatgReplPack = new ArrayList<>();
		catgReplnPkConsList.forEach(catgReplnPkCons -> {
			Set<SubCatgReplPack> subReplPack = catgReplnPkCons.getSubReplPack();
			long replUnits = updateVnpkWhpkForSubCatgReplnConsMapper(new ArrayList<>(subReplPack), vnpk, whpk);
			if (vnpk != null)
				catgReplnPkCons.setVendorPackCnt(vnpk);

			if (whpk != null)
				catgReplnPkCons.setWhsePackCnt(whpk);

			catgReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(catgReplnPkCons.getVendorPackCnt(), catgReplnPkCons.getWhsePackCnt()));
			catgReplnPkCons.setReplUnits(Math.toIntExact(replUnits));
			catgReplnPkCons.setReplPackCnt(getReplenishmentPackCount((int)replUnits, catgReplnPkCons.getVendorPackCnt()));
			udpatedMerchCatgReplPack.add(catgReplnPkCons);
		});

		catgReplnPkConsRepository.saveAll(udpatedMerchCatgReplPack);
	}

	public long updateVnpkWhpkForSubCatgReplnConsMapper(List<SubCatgReplPack> subCatgReplPackList, Integer vnpk, Integer whpk) {
		long totalReplUnits = 0L;
		List<SubCatgReplPack> updatedSubCatgReplPack = new ArrayList<>();
		for (SubCatgReplPack subCatgReplnPkCons: subCatgReplPackList) {
			Set<FinelineReplPack> finelineReplPack = subCatgReplnPkCons.getFinelineReplPack();
			long replUnits = updateVnpkWhpkForFinelineReplnConsMapper(new ArrayList<>(finelineReplPack), vnpk, whpk);

			if (vnpk != null)
				subCatgReplnPkCons.setVendorPackCnt(vnpk);

			if (whpk != null)
				subCatgReplnPkCons.setWhsePackCnt(whpk);

			subCatgReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(subCatgReplnPkCons.getVendorPackCnt(), subCatgReplnPkCons.getWhsePackCnt()));
			subCatgReplnPkCons.setReplUnits(Math.toIntExact(replUnits));
			subCatgReplnPkCons.setReplPackCnt(getReplenishmentPackCount((int)replUnits, subCatgReplnPkCons.getVendorPackCnt()));
			updatedSubCatgReplPack.add(subCatgReplnPkCons);
			totalReplUnits += replUnits;
		}

		subCatgReplnPkConsRepository.saveAll(updatedSubCatgReplPack);
		return totalReplUnits;
	}

	public long updateVnpkWhpkForFinelineReplnConsMapper(List<FinelineReplPack> finelineReplnPkConsList, Integer vnpk, Integer whpk) {
		long totalReplUnits = 0L;
		List<FinelineReplPack> updatedFinelineReplPack = new ArrayList<>();
		for(FinelineReplPack finelieneReplnPkCons : finelineReplnPkConsList) {
			Set<StyleReplPack> styleReplPack = finelieneReplnPkCons.getStyleReplPack();
			long replUnits = updateVnpkWhpkForStyleReplnConsMapper(new ArrayList<>(styleReplPack), vnpk, whpk);

			if (vnpk != null)
				finelieneReplnPkCons.setVendorPackCnt(vnpk);

			if (whpk != null)
				finelieneReplnPkCons.setWhsePackCnt(whpk);

			finelieneReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(finelieneReplnPkCons.getVendorPackCnt(), finelieneReplnPkCons.getWhsePackCnt()));
			finelieneReplnPkCons.setReplUnits(Math.toIntExact(replUnits));
			finelieneReplnPkCons.setReplPackCnt(getReplenishmentPackCount((int)replUnits, finelieneReplnPkCons.getVendorPackCnt()));
			updatedFinelineReplPack.add(finelieneReplnPkCons);
			totalReplUnits += replUnits;
		}

		finelineReplnPkConsRepository.saveAll(updatedFinelineReplPack);
		return totalReplUnits;
	}


	public long updateVnpkWhpkForStyleReplnConsMapper(List<StyleReplPack> styleReplnPkConsList, Integer vnpk, Integer whpk) {
		long totalReplUnits = 0L;
		List<StyleReplPack> updatedStyleReplPack = new ArrayList<>();
		for (StyleReplPack styleReplnPkCons: styleReplnPkConsList){
			Set<CcReplPack> ccReplPack = styleReplnPkCons.getCcReplPack();
			long replUnits = updateVnpkWhpkForCcReplnPkConsMapper(new ArrayList<>(ccReplPack), vnpk, whpk);

			if (vnpk != null)
				styleReplnPkCons.setVendorPackCnt(vnpk);

			if (whpk != null) {
				styleReplnPkCons.setWhsePackCnt(whpk);
			}

			styleReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(styleReplnPkCons.getVendorPackCnt(), styleReplnPkCons.getWhsePackCnt()));
			styleReplnPkCons.setReplUnits(Math.toIntExact(replUnits));
			styleReplnPkCons.setReplPackCnt(getReplenishmentPackCount((int)replUnits, styleReplnPkCons.getVendorPackCnt()));
			updatedStyleReplPack.add(styleReplnPkCons);
			totalReplUnits += replUnits;
		}

		styleReplnConsRepository.saveAll(updatedStyleReplPack);
		return totalReplUnits;
	}

	public long updateVnpkWhpkForCcReplnPkConsMapper(List<CcReplPack> ccReplnPkConsList, Integer vnpk, Integer whpk) {
		long totalReplUnits = 0L;
		List<CcReplPack> updatedCcReplPack = new ArrayList<>();
		for (CcReplPack ccReplnPkCons: ccReplnPkConsList) {
			Set<CcMmReplPack> ccMmReplPack = ccReplnPkCons.getCcMmReplPack();
			long replUnits = updateVnpkWhpkForCcMmReplnPkConsMapper(new ArrayList<>(ccMmReplPack), vnpk, whpk);

			if (vnpk != null)
				ccReplnPkCons.setVendorPackCnt(vnpk);

			if (whpk != null)
				ccReplnPkCons.setWhsePackCnt(whpk);

			ccReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(ccReplnPkCons.getVendorPackCnt(), ccReplnPkCons.getWhsePackCnt()));
			ccReplnPkCons.setReplUnits(Math.toIntExact(replUnits));
			ccReplnPkCons.setReplPackCnt(getReplenishmentPackCount((int)replUnits, ccReplnPkCons.getVendorPackCnt()));
			updatedCcReplPack.add(ccReplnPkCons);
			totalReplUnits += replUnits;
		}

		ccReplnPkConsRepository.saveAll(updatedCcReplPack);
		return totalReplUnits;
	}

	public long updateVnpkWhpkForCcMmReplnPkConsMapper(List<CcMmReplPack> ccMmReplnPkConsList, Integer vnpk, Integer whpk) {
		long totalReplUnits = 0L;
		List<CcMmReplPack> updatedCcMmReplPack = new ArrayList<>();
		for (CcMmReplPack ccMmReplnPkCons: ccMmReplnPkConsList) {
			Set<CcSpMmReplPack> ccSpMmReplPack = ccMmReplnPkCons.getCcSpMmReplPack();
			long replUnits = updateVnpkWhpkForCcSpMmReplnPkConsMapper(new ArrayList<>(ccSpMmReplPack), vnpk, whpk);

			if (vnpk != null)
				ccMmReplnPkCons.setVendorPackCnt(vnpk);

			if (whpk != null)
				ccMmReplnPkCons.setWhsePackCnt(whpk);

			ccMmReplnPkCons.setVnpkWhpkRatio(getVnpkWhpkRatio(ccMmReplnPkCons.getVendorPackCnt(), ccMmReplnPkCons.getWhsePackCnt()));
			ccMmReplnPkCons.setReplUnits(Math.toIntExact(replUnits));
			ccMmReplnPkCons.setReplPackCnt(getReplenishmentPackCount((int)replUnits, ccMmReplnPkCons.getVendorPackCnt()));
			updatedCcMmReplPack.add(ccMmReplnPkCons);
			totalReplUnits += replUnits;
		}

		ccMmReplnPkConsRepository.saveAll(updatedCcMmReplPack);
		return totalReplUnits;
	}

	public long updateVnpkWhpkForCcSpMmReplnPkConsMapper(List<CcSpMmReplPack> ccSpReplnPkConsList) {
		return updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpReplnPkConsList, null, null);
	}
	public long updateVnpkWhpkForCcSpMmReplnPkConsMapper(List<CcSpMmReplPack> ccSpReplnPkConsList, Integer vnpk, Integer whpk) {
		long totalReplUnits = 0L;
		List<CcSpMmReplPack> updatedCcSpMmReplPack = new ArrayList<>();
		for (CcSpMmReplPack ccSpReplnCons: ccSpReplnPkConsList) {
			String replObjJson = ccSpReplnCons.getReplenObj();
			long replUnits = 0L;
			if (replObjJson != null && !replObjJson.isEmpty()) {
				try {
					List<Replenishment> replObj = objectMapper.readValue(replObjJson, new TypeReference<>() {});
					ccSpReplnCons.setVendorPackCnt(vnpk == null ? ccSpReplnCons.getVendorPackCnt() : vnpk);
					ccSpReplnCons.setWhsePackCnt(whpk == null ? ccSpReplnCons.getWhsePackCnt() : whpk);
					replObj = replenishmentsOptimizationService.getUpdatedReplenishmentsPack(replObj, ccSpReplnCons.getVendorPackCnt());
					replUnits = replObj.stream().mapToLong(Replenishment::getAdjReplnUnits).sum();
					ccSpReplnCons.setReplenObj(objectMapper.writeValueAsString(replObj));
				} catch (JsonProcessingException e) {
					log.error("Could not convert Replenishment Object Json for week disaggregation ", e);
				}
			}

			ccSpReplnCons.setVnpkWhpkRatio(getVnpkWhpkRatio(ccSpReplnCons.getVendorPackCnt(), ccSpReplnCons.getWhsePackCnt()));
			ccSpReplnCons.setReplUnits(Math.toIntExact(replUnits));
			ccSpReplnCons.setReplPackCnt(getReplenishmentPackCount((int)replUnits, ccSpReplnCons.getVendorPackCnt()));
			updatedCcSpMmReplPack.add(ccSpReplnCons);
			totalReplUnits += replUnits;
		}

		ccSpReplnPkConsRepository.saveAll(updatedCcSpMmReplPack);
		return totalReplUnits;
	}

  public static Integer getReplenishmentPackCount(Integer replenishmentUnits, Integer vnpk) {
    Integer replenishmentPackCount = null;
      if (replenishmentUnits != null && replenishmentUnits != 0 && vnpk != null && vnpk != 0) {
        replenishmentPackCount = (replenishmentUnits / vnpk);
      }
    return replenishmentPackCount;
  }

	private Double getVnpkWhpkRatio(Integer vnpk, Integer whpk) {
		Double vnwhpkRatio = null;

		if (vnpk != 0 && whpk != 0) {
			vnwhpkRatio = ((double) vnpk / whpk);
		}

		return vnwhpkRatio;

	}
}