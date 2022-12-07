package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
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
import com.walmart.aex.sp.repository.SizeLevelReplenishmentRepository;
import com.walmart.aex.sp.repository.SizeListReplenishmentRepository;
import com.walmart.aex.sp.repository.SpCustomerChoiceReplenishmentRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReplenishmentService  {

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
    private final StrategyFetchService strategyFetchService;

    private final ReplenishmentMapper replenishmentMapper;
    private final UpdateReplnConfigMapper updateReplnConfigMapper;
    private final BuyQtyCommonUtil buyQtyCommonUtil;
    private final SizeLevelReplenishmentMapper sizeLevelReplenishmentMapper;
    private final SizeLevelReplenishmentRepository sizeLevelReplenishmentRepository;

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
                                BuyQuantityMapper buyQuantityMapper,
                                StrategyFetchService strategyFetchService,BuyQtyCommonUtil buyQtyCommonUtil,
                                SizeLevelReplenishmentRepository sizeLevelReplenishmentRepository,
                                SizeLevelReplenishmentMapper sizeLevelReplenishmentMapper) {
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
        this.strategyFetchService = strategyFetchService;
        this.buyQtyCommonUtil = buyQtyCommonUtil;
        this.sizeLevelReplenishmentRepository=sizeLevelReplenishmentRepository;
        this.sizeLevelReplenishmentMapper =sizeLevelReplenishmentMapper;
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
                    .forEach(replenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse, null,null));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline Replenishment :", e);
            throw new CustomException("Failed to fetch Fineline Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment Fineline response: {}", replenishmentResponse);
        return replenishmentResponse;
    }

    public BuyQtyResponse fetchOnlineFinelineBuyQnty(BuyQtyRequest buyQtyRequest)  {
        BuyQtyResponse buyQtyResponse= new BuyQtyResponse();
        try {
            BuyQtyResponse finelinesWithSizesFromStrategy = strategyFetchService.getBuyQtyDetailsForFinelines(buyQtyRequest);
            if (finelinesWithSizesFromStrategy != null) {
                List<BuyQntyResponseDTO> buyQntyResponseDTOS = fineLineReplenishmentRepository.getBuyQntyByPlanChannelOnline(buyQtyRequest.getPlanId(),
                        ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()));

                buyQtyResponse= buyQtyCommonUtil.filterFinelinesWithSizes(buyQntyResponseDTOS,finelinesWithSizesFromStrategy);

            }
            return buyQtyResponse;

        } catch (Exception e) {
            log.error("Exception While fetching Fineline Buy Qunatities with Sizes :", e);
            throw new CustomException("Failed to fetch Fineline Buy Qunatities with Sizes, due to" + e);
        }
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
                    .forEach(replenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse,finelineNbr,null));
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
                BuyQtyResponse stylesCcWithSizesFromStrategy = strategyFetchService.getBuyQtyDetailsForStylesCc(buyQtyRequest,finelineNbr);

                if (stylesCcWithSizesFromStrategy != null) {
                     List<BuyQntyResponseDTO> buyQntyResponseDTOS = spCustomerChoiceReplenishmentRepository.getBuyQntyByPlanChannelOnlineFineline(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()),
                        finelineNbr);

                    buyQtyResponse= buyQtyCommonUtil.filterStylesCcWithSizes(buyQntyResponseDTOS,stylesCcWithSizesFromStrategy,finelineNbr);

                    }

            return buyQtyResponse;

        } catch (Exception e) {
                log.error("Exception While fetching CC Buy Qunatities with Sizes:", e);
                throw new CustomException("Failed to fetch CC Buy Qunatities with Sizes, due to" + e);
        }
    }

	public ReplenishmentResponse fetchSizeListReplenishmentFullHierarchy(ReplenishmentRequest replenishmentRequest) {
		ReplenishmentResponse replenishmentResponse = new ReplenishmentResponse();
		Integer finelineNbr = replenishmentRequest.getFinelineNbr();
		try {
			List<ReplenishmentResponseDTO> replenishmentResponseDTOS = sizeLevelReplenishmentRepository
					.getReplnFullHierarchyByPlanFineline(replenishmentRequest.getPlanId(),
							ChannelType.getChannelIdFromName(replenishmentRequest.getChannel()), finelineNbr);
			Optional.of(replenishmentResponseDTOS).stream().flatMap(Collection::stream)
					.forEach(replenishmentResponseDTO -> sizeLevelReplenishmentMapper
							.mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse, finelineNbr));
		} catch (Exception e) {
			log.error("Exception While fetching size list replenishment full hierarchy :", e);
			throw new CustomException("Failed to fetch size list replenishment full hierarchy, due to" + e);
		}
		log.info("Fetch size list replenishment full hierarchy response: {}", replenishmentResponse);
		return replenishmentResponse;
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
                    .forEach(replenishmentResponseDTO -> replenishmentMapper
                            .mapReplenishmentLvl2Sp(replenishmentResponseDTO, replenishmentResponse,finelineNbr,ccId));
        } catch (Exception e) {
            log.error("Exception While fetching MerchMethod Replenishment :", e);
            throw new CustomException("Failed to fetch MerchMethod Replenishment, due to" + e);
        }
        log.info("Fetch Replenishment MerchMethod response: {}", replenishmentResponse);
        return replenishmentResponse;
    }

    public BuyQtyResponse fetchOnlineSizeBuyQnty(BuyQtyRequest buyQtyRequest) {
        try {
            BuyQtyResponse buyQtyResponse = strategyFetchService.getBuyQtyResponseSizeProfile(buyQtyRequest);

            if (buyQtyResponse != null) {
                List<BuyQntyResponseDTO> buyQntyResponseDTOS = sizeListReplenishmentRepository
                        .getSizeBuyQntyByPlanChannelOnlineCc(buyQtyRequest.getPlanId(), ChannelType.getChannelIdFromName(buyQtyRequest.getChannel()), buyQtyRequest.getCcId());

                List<SizeDto> sizeDtos = BuyQtyCommonUtil.fetchSizes(buyQtyResponse);
                Optional.of(sizeDtos)
                        .stream()
                        .flatMap(Collection::stream)
                        .forEach(sizeDto -> buyQuantityMapper
                                .mapBuyQntySizeSp(buyQntyResponseDTOS,sizeDto));
                log.info("Fetch Buy Qty CC response: {}", buyQtyResponse);
            }
            return buyQtyResponse;
        } catch (Exception e) {
            log.error("Exception While fetching CC Buy Qunatities :", e);
            throw new CustomException("Failed to fetch CC Buy Qunatities, due to" + e);
        }
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

        List<SubCatgReplPack> subCatgReplnConsData = subCatgReplnPkConsRepository.getSubCatgReplnConsData(planId, channelId, lvl3Nbr,lvl4Nbr);
        updateReplnConfigMapper.updateVnpkWhpkForSubCatgReplnConsMapper(subCatgReplnConsData, vnpk, whpk);

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



