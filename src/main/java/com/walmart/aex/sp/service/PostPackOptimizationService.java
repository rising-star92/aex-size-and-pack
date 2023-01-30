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
import com.walmart.aex.sp.service.helper.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        List<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSize = spCustomerChoiceChannelFixtureSizeRepository.getSpCcChanFixtrDataByPlanFineline(planId,finelineNbr);

        List<CcSpMmReplPack> updatedCcSpMmReplPacks = new ArrayList<>();

        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> cc.getFixtures().forEach(fixture -> fixture.getSizes().forEach(size -> {
            List<CcSpMmReplPack> ccSpMmReplPacks = ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(planId, finelineNbr, cc.getCcId(), size.getSizeDesc()).orElse(Collections.emptyList());
            ccSpMmReplPacks.stream()
                  .filter(ccSpMmReplPack -> getMerchMethodCode(ccSpMmReplPack.getCcSpReplPackId())
                        .equals(MerchMethod.getMerchMethodIdFromDescription(fixture.getMerchMethod()))).forEach(ccSpMmReplPack -> {

                      if (ccSpMmReplPack.getReplUnits() > 0) {
                          Integer updatedReplnQty = getUpdatedReplnQty(size.getOptFinalInitialSetQty(),ccSpMmReplPack.getCcSpReplPackId().getCcMmReplPackId().getCcReplPackId(),spCustomerChoiceChannelFixtureSize);
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

    private Integer getUpdatedReplnQty(Integer optFinalInitialSetQty, CcReplPackId ccReplPackId, List<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSize) {
        if (!spCustomerChoiceChannelFixtureSize.isEmpty()) {
            SpCustomerChoiceChannelFixtureSize spCcChanFixtrSize = spCustomerChoiceChannelFixtureSize.stream()
                    .filter(spCcChanFixSize -> {
                                SpCustomerChoiceChannelFixtureId ccFixtrId = spCcChanFixSize.getSpCustomerChoiceChannelFixtureSizeId().getSpCustomerChoiceChannelFixtureId();
                                SpStyleChannelFixtureId styleFixtrId = ccFixtrId.getSpStyleChannelFixtureId();
                                SpFineLineChannelFixtureId finelineFixtrId = styleFixtrId.getSpFineLineChannelFixtureId();

                                FinelineReplPackId finelineReplPackId = ccReplPackId.getStyleReplPackId().getFinelineReplPackId();
                                MerchCatgReplPackId merchCatgReplPackId = finelineReplPackId.getSubCatgReplPackId().getMerchCatgReplPackId();
                                SubCatgReplPackId subCatgReplPackId = finelineReplPackId.getSubCatgReplPackId();

                                return ccFixtrId.getCustomerChoice().equals(ccReplPackId.getCustomerChoice()) &&
                                        styleFixtrId.getStyleNbr().equals(ccReplPackId.getStyleReplPackId().getStyleNbr()) &&
                                        finelineFixtrId.getFineLineNbr().equals(finelineReplPackId.getFinelineNbr()) &&
                                        finelineFixtrId.getPlanId().equals(merchCatgReplPackId.getPlanId()) &&
                                        finelineFixtrId.getFixtureTypeRollUpId().getFixtureTypeRollupId().equals(merchCatgReplPackId.getFixtureTypeRollupId()) &&
                                        finelineFixtrId.getLvl0Nbr().equals(merchCatgReplPackId.getRepTLvl0()) &&
                                        finelineFixtrId.getLvl1Nbr().equals(merchCatgReplPackId.getRepTLvl1()) &&
                                        finelineFixtrId.getLvl2Nbr().equals(merchCatgReplPackId.getRepTLvl2()) &&
                                        finelineFixtrId.getLvl3Nbr().equals(merchCatgReplPackId.getRepTLvl3()) &&
                                        finelineFixtrId.getLvl4Nbr().equals(subCatgReplPackId.getRepTLvl4());
                            }
                    ).findFirst().orElse(null);
            if (spCcChanFixtrSize !=null && spCcChanFixtrSize.getInitialSetQty() != null)
                return spCcChanFixtrSize.getInitialSetQty() - optFinalInitialSetQty;
        }
        return null;
    }


    public static void isTrue(Object value1,Object value2, Action action) {
        if (value1.equals(value2))
            action.execute();
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
