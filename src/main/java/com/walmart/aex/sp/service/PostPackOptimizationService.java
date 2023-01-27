package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.MerchMethod;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceChannelFixtureSizeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PostPackOptimizationService {
    private final ObjectMapper objectMapper;
    private final UpdateReplnConfigMapper updateReplnConfigMapper;
    private final ReplenishmentService replenishmentService;
    private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
    private final SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;

    private final SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository;

    public PostPackOptimizationService(ObjectMapper objectMapper, UpdateReplnConfigMapper updateReplnConfigMapper, ReplenishmentService replenishmentService, CcSpReplnPkConsRepository ccSpReplnPkConsRepository, SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository, SpCustomerChoiceChannelFixtureSizeRepository spCustomerChoiceChannelFixtureSizeRepository) {
        this.objectMapper = objectMapper;
        this.updateReplnConfigMapper = updateReplnConfigMapper;
        this.replenishmentService = replenishmentService;
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
        this.spCustomerChoiceChannelFixtureRepository = spCustomerChoiceChannelFixtureRepository;
        this.spCustomerChoiceChannelFixtureSizeRepository = spCustomerChoiceChannelFixtureSizeRepository;
    }

    @Transactional
    public void updateInitialSetAndBumpPackAty(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        log.info("Update Replenishment qty {}", isAndBPQtyDTO);
        List<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixture = spCustomerChoiceChannelFixtureRepository.getSpChanFixtrDataByPlanFineline(planId,finelineNbr);
        List<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSize = spCustomerChoiceChannelFixtureSizeRepository.getSpCcChanFixtrDataByPlanFineline(planId,finelineNbr);

        List<CcSpMmReplPack> updatedCcSpMmReplPacks = new ArrayList<>();

        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> cc.getFixtures().forEach(fixture -> fixture.getSizes().forEach(size -> {
            List<CcSpMmReplPack> ccSpMmReplPacks = ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(planId, finelineNbr, cc.getCcId(), size.getSizeDesc()).orElse(Collections.emptyList());
            ccSpMmReplPacks.stream()
                  .filter(ccSpMmReplPack -> getMerchMethodCode(ccSpMmReplPack.getCcSpReplPackId())
                        .equals(MerchMethod.getMerchMethodIdFromDescription(fixture.getMerchMethod()))).forEach(ccSpMmReplPack -> {

                      if (ccSpMmReplPack.getReplUnits() > 0) {
                          Integer updatedReplnQty = getUpdatedReplnQty(size.getOptFinalInitialSetQty(),ccSpMmReplPack.getCcSpReplPackId().getCcMmReplPackId().getCcReplPackId(),spCustomerChoiceChannelFixture ,spCustomerChoiceChannelFixtureSize);
                          //If optimized buy quantity exceeds replenishment amount, then we'll set the replenishment to 0
                          if(null!=updatedReplnQty){
                              ccSpMmReplPack.setReplUnits(Math.max(updatedReplnQty, 0));
                              String replnWeeksObj = ccSpMmReplPack.getReplenObj();
                              if (StringUtils.isNotEmpty(replnWeeksObj)) {
                                  try {
                                      List<Replenishment> replObj = objectMapper.readValue(ccSpMmReplPack.getReplenObj(), new TypeReference<>() {});
                                      Long totalReplnUnits = replObj.stream().mapToLong(Replenishment::getAdjReplnUnits).sum();
                                      replObj.forEach(replWeekObj -> replWeekObj.setAdjReplnUnits(totalReplnUnits == 0 ? 0 : (updatedReplnQty * ((replWeekObj.getAdjReplnUnits() * 100) / totalReplnUnits) / 100)));
                                      ccSpMmReplPack.setReplenObj(objectMapper.writeValueAsString(replObj));
                                  } catch (JsonProcessingException jpe) {
                                      log.error("Could not convert Replenishment Object Json for week disaggregation ", jpe);
                                  }
                              }
                              updatedCcSpMmReplPacks.add(ccSpMmReplPack);
                          }else{
                              log.info("Could not update Replenishment Quantity for planId {} and finelineNbr {} ", planId,finelineNbr);
                          }

                      }
                  });
        })));
        if (!updatedCcSpMmReplPacks.isEmpty()) {
            final Integer lvl3Nbr = getLvl3Nbr(updatedCcSpMmReplPacks.get(0).getCcSpReplPackId());
            updateReplnConfigMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(updatedCcSpMmReplPacks);
            replenishmentService.updateVnpkWhpkForCatgReplnCons(planId, ChannelType.STORE.getId(),lvl3Nbr);
        }
    }

    private Integer getUpdatedReplnQty(Integer optFinalInitialSetQty, CcReplPackId ccReplPackId, List<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixture, List<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSize) {
        if (!spCustomerChoiceChannelFixture.isEmpty() && !spCustomerChoiceChannelFixtureSize.isEmpty()) {
            SpCustomerChoiceChannelFixture spCustomerCCFixtr = spCustomerChoiceChannelFixture.stream()
                    .filter(spCustChanFixtr -> spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals(ccReplPackId.getCustomerChoice()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getStyleNbr().equals(ccReplPackId.getStyleReplPackId().getStyleNbr()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getFineLineNbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getFinelineNbr()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getPlanId().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getPlanId()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getFixtureTypeRollupId()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl0Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl0()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl1Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl1()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl2Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl2()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl3Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl3()) &&
                            spCustChanFixtr.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl4Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getRepTLvl4())
                    ).findFirst().orElse(null);
            SpCustomerChoiceChannelFixtureSize spCcChanFixtrSize = spCustomerChoiceChannelFixtureSize.stream()
                    .filter(spCcChanFixSize -> spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getCustomerChoice().equals(ccReplPackId.getCustomerChoice()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getStyleNbr().equals(ccReplPackId.getStyleReplPackId().getStyleNbr()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getFineLineNbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getFinelineNbr()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getPlanId().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getPlanId()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getFixtureTypeRollupId()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl0Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl0()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl1Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl1()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl2Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl2()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl3Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getMerchCatgReplPackId().getRepTLvl3()) &&
                            spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getLvl4Nbr().equals(ccReplPackId.getStyleReplPackId().getFinelineReplPackId().getSubCatgReplPackId().getRepTLvl4())
                    ).findFirst().orElse(null);
            if (spCustomerCCFixtr != null && spCcChanFixtrSize != null && spCustomerCCFixtr.getInitialSetQty().equals(spCcChanFixtrSize.getInitialSetQty()))
                return spCustomerCCFixtr.getInitialSetQty() - optFinalInitialSetQty;
        }
        return null;
    }

    private Integer getLvl3Nbr(CcSpMmReplPackId ccSpMmReplPackId) {
        return ccSpMmReplPackId.getCcMmReplPackId()
              .getCcReplPackId()
              .getStyleReplPackId()
              .getFinelineReplPackId()
              .getSubCatgReplPackId()
              .getMerchCatgReplPackId()
              .getRepTLvl3();
    }
    private Integer getMerchMethodCode(SubCatgReplPackId subCatgReplPackId) {
        return subCatgReplPackId.getMerchCatgReplPackId().getFixtureTypeRollupId();
    }
    private Integer getMerchMethodCode(FinelineReplPackId finelineReplPackId) {
        return getMerchMethodCode(finelineReplPackId.getSubCatgReplPackId());
    }
    private Integer getMerchMethodCode(StyleReplPackId styleReplPackId) {
        return getMerchMethodCode(styleReplPackId.getFinelineReplPackId());
    }
    private Integer getMerchMethodCode(CcReplPackId ccReplPackId) {
        return getMerchMethodCode(ccReplPackId.getStyleReplPackId());
    }
    private Integer getMerchMethodCode(CcMmReplPackId ccMmReplPackId) {
        return getMerchMethodCode(ccMmReplPackId.getCcReplPackId());
    }
    private Integer getMerchMethodCode(CcSpMmReplPackId ccSpMmReplPackId) {
        return getMerchMethodCode(ccSpMmReplPackId.getCcMmReplPackId());
    }

}
