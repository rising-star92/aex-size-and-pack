package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.entity.CcMmReplPackId;
import com.walmart.aex.sp.entity.CcReplPackId;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPackId;
import com.walmart.aex.sp.entity.FinelineReplPackId;
import com.walmart.aex.sp.entity.StyleReplPackId;
import com.walmart.aex.sp.entity.SubCatgReplPackId;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.MerchMethod;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class PostPackOptimizationService {
    private final ObjectMapper objectMapper;
    private final UpdateReplnConfigMapper updateReplnConfigMapper;
    private final ReplenishmentService replenishmentService;
    private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    public PostPackOptimizationService(ObjectMapper objectMapper, UpdateReplnConfigMapper updateReplnConfigMapper, ReplenishmentService replenishmentService, CcSpReplnPkConsRepository ccSpReplnPkConsRepository) {
        this.objectMapper = objectMapper;
        this.updateReplnConfigMapper = updateReplnConfigMapper;
        this.replenishmentService = replenishmentService;
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
    }

    @Transactional
    public void updateInitialSetAndBumpPackAty(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        log.info("Update Replenishment qty {}", isAndBPQtyDTO);

        List<CcSpMmReplPack> updatedCcSpMmReplPacks = new ArrayList<>();

        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> cc.getFixtures().forEach(fixture -> fixture.getSizes().forEach(size -> {
            List<CcSpMmReplPack> ccSpMmReplPacks = ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(planId, finelineNbr, cc.getCcId(), size.getSizeDesc()).orElse(Collections.emptyList());
            ccSpMmReplPacks.stream()
                  .filter(ccSpMmReplPack -> getMerchMethodCode(ccSpMmReplPack.getCcSpReplPackId())
                        .equals(MerchMethod.getMerchMethodIdFromDescription(fixture.getMerchMethod()))).forEach(ccSpMmReplPack -> {

                      if (ccSpMmReplPack.getReplUnits() > 0) {
                          Integer updatedReplnQty = ccSpMmReplPack.getFinalBuyUnits() - size.getOptFinalBuyQty();
                          //If optimized buy quantity exceeds replenishment amount, then we'll set the replenishment to 0
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
                      }
                  });
        })));
        if (!updatedCcSpMmReplPacks.isEmpty()) {
            final Integer lvl3Nbr = getLvl3Nbr(updatedCcSpMmReplPacks.get(0).getCcSpReplPackId());
            updateReplnConfigMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(updatedCcSpMmReplPacks);
            replenishmentService.updateVnpkWhpkForCatgReplnCons(planId, ChannelType.STORE.getId(),lvl3Nbr);
        }
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
