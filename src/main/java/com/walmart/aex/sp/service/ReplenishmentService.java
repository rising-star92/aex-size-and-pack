package com.walmart.aex.sp.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import org.springframework.stereotype.Service;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponseDTO;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.entity.CcMmReplPack;
import com.walmart.aex.sp.entity.CcReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.FinelineReplPack;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.StyleReplPack;
import com.walmart.aex.sp.entity.SubCatgReplPack;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.CatgReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FineLineReplenishmentRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.SizeListReplenishmentRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceReplenishmentRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReplenishmentService  {
    public static final String FAILED_STATUS = "Failed";
    public static final String SUCCESS_STATUS = "Success";
    public static Double vnpkwhpkRatio=0.0;

    private final FineLineReplenishmentRepository fineLineReplenishmentRepository;
    private final SpCustomerChoiceReplenishmentRepository  spCustomerChoiceReplenishmentRepository;
    private final SizeListReplenishmentRepository sizeListReplenishmentRepository;
    private final CatgReplnPkConsRepository catgReplnPkConsRepository;
    private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;
    private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;
    private final StyleReplnPkConsRepository styleReplnConsRepository;
    private final CcReplnPkConsRepository ccReplnConsRepository;
    private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;
    private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;
    private final BuyQuantityMapper buyQuantityMapper;

    private final ReplenishmentMapper replenishmentMapper;
    private final UpdateReplnConfigMapper updateReplnConfigMapper;

    public ReplenishmentService(FineLineReplenishmentRepository fineLineReplenishmentRepository,
                                SpCustomerChoiceReplenishmentRepository  spCustomerChoiceReplenishmentRepository,
                                SizeListReplenishmentRepository sizeListReplenishmentRepository,
                                CatgReplnPkConsRepository catgReplnPkConsRepository,
                                SubCatgReplnPkConsRepository subCatgReplnPkConsRepository,
                                FinelineReplnPkConsRepository finelineReplnPkConsRepository,
                                StyleReplnPkConsRepository styleReplnConsRepository,
                                CcReplnPkConsRepository ccReplnConsRepository,
                                CcMmReplnPkConsRepository ccMmReplnPkConsRepository,
                                CcSpReplnPkConsRepository ccSpReplnPkConsRepository,
                                ReplenishmentMapper replenishmentMapper,
                                UpdateReplnConfigMapper updateReplnConfigMapper,
                                BuyQuantityMapper buyQuantityMapper) {
        this.fineLineReplenishmentRepository = fineLineReplenishmentRepository;
        this.spCustomerChoiceReplenishmentRepository=spCustomerChoiceReplenishmentRepository;
        this.sizeListReplenishmentRepository=sizeListReplenishmentRepository;
        this.catgReplnPkConsRepository = catgReplnPkConsRepository;
        this.subCatgReplnPkConsRepository = subCatgReplnPkConsRepository;
        this.finelineReplnPkConsRepository = finelineReplnPkConsRepository;
        this.styleReplnConsRepository = styleReplnConsRepository;
        this.ccReplnConsRepository = ccReplnConsRepository;
        this.ccMmReplnPkConsRepository = ccMmReplnPkConsRepository;
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
        this.replenishmentMapper = replenishmentMapper;
        this.updateReplnConfigMapper = updateReplnConfigMapper;
        this.buyQuantityMapper = buyQuantityMapper;
    }

    public ReplenishmentResponse fetchFinelineReplenishment(ReplenishmentRequest replenishmentRequest) {
        ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
        Integer channelId=ChannelType.getChannelIdFromName(replenishmentRequest.getChannel());

        try {
            List<ReplenishmentResponseDTO> replenishmentResponseDTOS = fineLineReplenishmentRepository
                    .getByPlanChannel(replenishmentRequest.getPlanId(),channelId );
            Optional.of(replenishmentResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ReplenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(ReplenishmentResponseDTO, replenishmentResponse, null,null));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline Replenishment :", e);
            throw new CustomException("Failed to fetch Fineline Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment Fineline response: {}", replenishmentResponse);
        return replenishmentResponse;
    }

    public BuyQtyResponse fetchOnlineFinelineBuyQnty(BuyQtyRequest buyQtyRequest) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        try {
            List<BuyQntyResponseDTO> buyQntyResponseDTOS = fineLineReplenishmentRepository.getBuyQntyByPlanChannelOnline(buyQtyRequest.getPlanId(),
                    ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()));

            Optional.of(buyQntyResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(buyQntyResponseDTO -> buyQuantityMapper
                            .mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponse, null));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline Buy Qunatities :", e);
            throw new CustomException("Failed to fetch Fineline Buy Qunatities, due to" + e);
        }
        log.info("Fetch Buy Qty Fineline response: {}", buyQtyRequest);
        return buyQtyResponse;
    }

    public ReplenishmentResponse fetchCcReplenishment(ReplenishmentRequest replenishmentRequest) {
        ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
        Integer finelineNbr = replenishmentRequest.getFinelineNbr();
        try {
            List<ReplenishmentResponseDTO> replenishmentResponseDTOS = spCustomerChoiceReplenishmentRepository
                    .getReplenishmentByPlanChannelFineline(replenishmentRequest.getPlanId(),
                            ChannelType.getChannelIdFromName(replenishmentRequest.getChannel()),replenishmentRequest.getFinelineNbr());
            Optional.of(replenishmentResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ReplenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(ReplenishmentResponseDTO, replenishmentResponse,finelineNbr,null));
        } catch (Exception e) {
            log.error("Exception While fetching style and CC Replenishment :", e);
            throw new CustomException("Failed to fetch style and CC Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment style and CC response: {}", replenishmentResponse);
        return replenishmentResponse;
    }

    public BuyQtyResponse fetchOnlineCcBuyQnty(BuyQtyRequest buyQtyRequest, Integer finelineNbr) {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        try {

            List<BuyQntyResponseDTO> buyQntyResponseDTOS = spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()),
                    finelineNbr);
            Optional.of(buyQntyResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(buyQntyResponseDTO -> buyQuantityMapper
                            .mapBuyQntyLvl2Sp(buyQntyResponseDTO, buyQtyResponse, finelineNbr));
        } catch (Exception e) {
            log.error("Exception While fetching CC Buy Qunatities :", e);
            throw new CustomException("Failed to fetch CC Buy Qunatities, due to" + e);
        }
        log.info("Fetch Buy Qty CC response: {}", buyQtyResponse);
        return buyQtyResponse;
    }


    public ReplenishmentResponse fetchSizeListReplenishment(ReplenishmentRequest replenishmentRequest) {
        ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
        String ccId=replenishmentRequest.getCcId();
        String styleNbr= replenishmentRequest.getStyleNbr();
        Integer finelineNbr = replenishmentRequest.getFinelineNbr();
        try {
            List<ReplenishmentResponseDTO> replenishmentResponseDTOS = sizeListReplenishmentRepository
                    .getReplenishmentPlanChannelFinelineCc(replenishmentRequest.getPlanId(), ChannelType.getChannelIdFromName(replenishmentRequest.getChannel()),
                            finelineNbr,styleNbr,ccId);
            Optional.of(replenishmentResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(ReplenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(ReplenishmentResponseDTO, replenishmentResponse,finelineNbr,ccId));
        } catch (Exception e) {
            log.error("Exception While fetching MerchMethod Replenishment :", e);
            throw new CustomException("Failed to fetch MerchMethod Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment MerchMethod response: {}", replenishmentResponse);
        return replenishmentResponse;
    }

    public void updateVnpkWhpkForCatgReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<MerchCatgReplPack> catgReplnPkConsList = catgReplnPkConsRepository.getCatgReplnConsData(planId, channelId, lvl3Nbr);
        updateReplnConfigMapper.updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, vnpk, whpk);

    }


    public void updateVnpkWhpkForSubCatgReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<SubCatgReplPack> SubcatgReplnPkConsList = subCatgReplnPkConsRepository.getSubCatgReplnConsData(planId, channelId, lvl3Nbr,lvl4Nbr);
        updateReplnConfigMapper.updateVnpkWhpkForSubCatgReplnConsMapper(SubcatgReplnPkConsList, vnpk, whpk);

    }
    public void updateVnpkWhpkForFinelineReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<FinelineReplPack> ccReplnPkConsList = finelineReplnPkConsRepository.getFinelineReplnConsData(planId, channelId, lvl3Nbr, lvl4Nbr, fineline);

        updateReplnConfigMapper.updateVnpkWhpkForFinelineReplnConsMapper(ccReplnPkConsList, vnpk, whpk);

    }


    public void updateVnpkWhpkForStyleReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
        String style = updateVnPkWhPkReplnRequest.getStyle();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<StyleReplPack> styleReplnPkConsList = styleReplnConsRepository.getStyleReplnConsData(planId,
                channelId, lvl3Nbr, lvl4Nbr, fineline, style);

        updateReplnConfigMapper.updateVnpkWhpkForStyleReplnConsMapper(styleReplnPkConsList, vnpk, whpk);
    }

    public void updateVnpkWhpkForCcReplnPkCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer fineline = updateVnPkWhPkReplnRequest.getFineline();
        String style = updateVnPkWhPkReplnRequest.getStyle();
        String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<CcReplPack> ccReplnPkConsList = ccReplnConsRepository.getCcReplnConsData(planId, channelId, lvl3Nbr, lvl4Nbr, fineline, style, customerChoice);

        updateReplnConfigMapper.updateVnpkWhpkForCcReplnPkConsMapper(ccReplnPkConsList, vnpk, whpk);
    }

    public void updateVnPkWhPkCcMerchMethodReplnCon(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer lvl3nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer finelineNbr = updateVnPkWhPkReplnRequest.getFineline();
        String stylenbr = updateVnPkWhPkReplnRequest.getStyle();
        String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
        String merchmethodDesc = updateVnPkWhPkReplnRequest.getMerchMethodDesc();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<CcMmReplPack> ccMmReplnPkConsList = ccMmReplnPkConsRepository.getCcMmReplnPkConsData(planId, channelId, lvl3nbr, lvl4nbr, finelineNbr, stylenbr,
                customerChoice, merchmethodDesc);

        updateReplnConfigMapper.updateVnpkWhpkForCcMmReplnPkConsMapper(ccMmReplnPkConsList, vnpk, whpk);
    }


    public void updateVnPkWhPkCcSpSizeReplnCon(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer lvl3nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer finelineNbr = updateVnPkWhPkReplnRequest.getFineline();
        String stylenbr = updateVnPkWhPkReplnRequest.getStyle();
        String customerChoice = updateVnPkWhPkReplnRequest.getCustomerChoice();
        Integer ahsSizeId=updateVnPkWhPkReplnRequest.getAhsSizeId();
        String merchmethodDesc = updateVnPkWhPkReplnRequest.getMerchMethodDesc();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<CcSpMmReplPack> ccSpReplnPkConsList = ccSpReplnPkConsRepository.getCcSpMmReplnPkConsData(planId, channelId, lvl3nbr, lvl4nbr, finelineNbr, 
				stylenbr, customerChoice, merchmethodDesc, ahsSizeId);

        updateReplnConfigMapper.updateVnpkWhpkForCcSpMmReplnPkConsMapper(ccSpReplnPkConsList, vnpk, whpk);
    }

    private Integer getChannelId(String channel)
    {
        Integer channelId = 0;

        if("Store".equalsIgnoreCase(channel))
            channelId = 1;
        else if("Online".equalsIgnoreCase(channel))
            channelId = 2;

        return channelId;

    }

}



