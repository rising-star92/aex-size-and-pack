package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.*;
import com.walmart.aex.sp.dto.replenishment.cons.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Transactional
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
        catgReplnPkConsRepository.saveAll(catgReplnPkConsList);

    }


    public void updateVnpkWhpkForSubCatgReplnCons(UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
        Long planId = updateVnPkWhPkReplnRequest.getPlanId();
        Integer channelId = getChannelId(updateVnPkWhPkReplnRequest.getChannel());
        Integer lvl3Nbr = updateVnPkWhPkReplnRequest.getLvl3Nbr();
        Integer lvl4Nbr = updateVnPkWhPkReplnRequest.getLvl4Nbr();
        Integer vnpk = updateVnPkWhPkReplnRequest.getVnpk();
        Integer whpk = updateVnPkWhPkReplnRequest.getWhpk();

        List<SubCatgReplPack> subCatgReplnPkConsList = subCatgReplnPkConsRepository.getSubCatgReplnConsData(planId, channelId, lvl3Nbr,lvl4Nbr);

        updateReplnConfigMapper.updateVnpkWhpkForSubCatgReplnConsMapper(subCatgReplnPkConsList, vnpk, whpk);
        subCatgReplnPkConsRepository.saveAllAndFlush(subCatgReplnPkConsList);
        updateVnpkWhpkForCatgReplnCons(planId, channelId, lvl3Nbr);
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

        List<FinelineReplPack> finelineReplnPkConsList = finelineReplnPkConsRepository.getFinelineReplnConsData(planId, channelId, lvl3Nbr, lvl4Nbr, fineline);

        updateReplnConfigMapper.updateVnpkWhpkForFinelineReplnConsMapper(finelineReplnPkConsList, vnpk, whpk);
        finelineReplnPkConsRepository.saveAllAndFlush(finelineReplnPkConsList);
        updateVnpkWhpkForCatgReplnCons(planId, channelId, lvl3Nbr);

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
        styleReplnConsRepository.saveAllAndFlush(styleReplnPkConsList);
        updateVnpkWhpkForCatgReplnCons(planId, channelId, lvl3Nbr);
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
        ccReplnConsRepository.saveAllAndFlush(ccReplnPkConsList);
        updateVnpkWhpkForCatgReplnCons(planId, channelId, lvl3Nbr);
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
        ccMmReplnPkConsRepository.saveAllAndFlush(ccMmReplnPkConsList);
        updateVnpkWhpkForCatgReplnCons(planId, channelId, lvl3nbr);
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
        ccSpReplnPkConsRepository.saveAllAndFlush(ccSpReplnPkConsList);
        updateVnpkWhpkForCatgReplnCons(planId, channelId, lvl3nbr);
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

    public void updateVnpkWhpkForCatgReplnCons(Long planId, Integer channelId, Integer lvl3Nbr) {
        List<MerchCatgReplPack> catgReplnPkConsList = catgReplnPkConsRepository.getCatgReplnConsData(planId, channelId, lvl3Nbr);
        updateReplnConfigMapper.updateVnpkWhpkForCatgReplnConsMapper(catgReplnPkConsList, null, null);
        catgReplnPkConsRepository.saveAll(catgReplnPkConsList);
    }

    public ReplenishmentCons fetchHierarchyReplnCons(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, MerchMethodsDto merchMethodsDto) {
        MerchCatgReplPackId merchCatgReplPackId = new MerchCatgReplPackId(calculateBuyQtyParallelRequest.getPlanId(), calculateBuyQtyParallelRequest.getLvl0Nbr(),
                calculateBuyQtyParallelRequest.getLvl1Nbr(), calculateBuyQtyParallelRequest.getLvl2Nbr(), calculateBuyQtyParallelRequest.getLvl3Nbr(),
                ChannelType.getChannelIdFromName(calculateBuyQtyParallelRequest.getChannel()), merchMethodsDto.getMerchMethodCode());
        SubCatgReplPackId subCatgReplPackId = new SubCatgReplPackId(merchCatgReplPackId, calculateBuyQtyParallelRequest.getLvl4Nbr());
        FinelineReplPackId finelineReplPackId = new FinelineReplPackId(subCatgReplPackId, calculateBuyQtyParallelRequest.getFinelineNbr());
        ReplenishmentCons replenishmentCons = new ReplenishmentCons();
        replenishmentCons.setMerchCatgReplPackCons(getVendorPackAndWhsePackCountForMerchCatg(merchCatgReplPackId));
        replenishmentCons.setSubCatgReplPackCons(getVendorPackAndWhsePackCountForSubCatg(subCatgReplPackId));
        replenishmentCons.setFinelineReplPackCons(getVendorPackAndWhsePackCountForFineline(finelineReplPackId));
        return replenishmentCons;
    }

    public void setStyleReplenishmentCons(ReplenishmentCons replenishmentCons, StyleDto styleDto) {
        StyleReplPackId styleReplPackId = new StyleReplPackId(replenishmentCons.getFinelineReplPackCons().getFinelineReplPackId(), styleDto.getStyleNbr());
        replenishmentCons.setStyleReplPackCons(getVendorPackAndWhsePackCountForStyle(styleReplPackId));
    }

    public void setCcsReplenishmentCons(ReplenishmentCons replenishmentCons, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, MerchMethodsDto merchMethodsDto, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        CcReplPackId ccReplPackId = new CcReplPackId(replenishmentCons.getStyleReplPackCons().getStyleReplPackId(), customerChoiceDto.getCcId());
        replenishmentCons.setCcReplPackCons(getVendorPackAndWhsePackCountForCc(ccReplPackId));
        CcMmReplPackId ccMmReplPackId = new CcMmReplPackId(ccReplPackId, merchMethodsDto.getMerchMethodCode());
        replenishmentCons.setCcMmReplPackCons(getVendorPackAndWhsePackCountForCcMm(ccMmReplPackId));
        replenishmentCons.setCcSpMmReplPackConsMap(getCcSpMmReplPackSizeMap(calculateBuyQtyParallelRequest, styleDto.getStyleNbr(), customerChoiceDto.getCcId(), merchMethodsDto.getMerchMethodCode()));
    }

    private MerchCatgReplPackCons getVendorPackAndWhsePackCountForMerchCatg(MerchCatgReplPackId merchCatgReplPackId) {
        MerchCatgReplPackCons merchCatgReplPackCons = new MerchCatgReplPackCons();
        merchCatgReplPackCons.setMerchCatgReplPackId(merchCatgReplPackId);
        Optional<MerchCatgReplPack> merchCatgReplPackResult = catgReplnPkConsRepository.findById(merchCatgReplPackId);
        if (merchCatgReplPackResult.isPresent()) {
            MerchCatgReplPack merchCatgReplPackFromDb = merchCatgReplPackResult.get();
            merchCatgReplPackCons.setVendorPackCount(merchCatgReplPackFromDb.getVendorPackCnt());
            merchCatgReplPackCons.setWarehousePackCount(merchCatgReplPackFromDb.getWhsePackCnt());
            merchCatgReplPackCons.setVendorPackWareHousePackRatio(merchCatgReplPackFromDb.getVnpkWhpkRatio());
        } else {
            merchCatgReplPackCons.setVendorPackCount(VP_DEFAULT);
            merchCatgReplPackCons.setWarehousePackCount(WP_DEFAULT);
            merchCatgReplPackCons.setVendorPackWareHousePackRatio(VP_WP_RATIO_DEFAULT);
        }
        return merchCatgReplPackCons;
    }

    private SubCatgReplPackCons getVendorPackAndWhsePackCountForSubCatg(SubCatgReplPackId subCatgReplPackId) {
        SubCatgReplPackCons subCatgReplPackCons = new SubCatgReplPackCons();
        subCatgReplPackCons.setSubCatgReplPackId(subCatgReplPackId);
        Optional<SubCatgReplPack> subCatgReplPackResult = subCatgReplnPkConsRepository.findById(subCatgReplPackId);
        if (subCatgReplPackResult.isPresent()) {
            SubCatgReplPack subCatgReplPackFromDB = subCatgReplPackResult.get();
            subCatgReplPackCons.setVendorPackCount(subCatgReplPackFromDB.getVendorPackCnt());
            subCatgReplPackCons.setWarehousePackCount(subCatgReplPackFromDB.getWhsePackCnt());
            subCatgReplPackCons.setVendorPackWareHousePackRatio(subCatgReplPackFromDB.getVnpkWhpkRatio());
        } else {
            subCatgReplPackCons.setVendorPackCount(VP_DEFAULT);
            subCatgReplPackCons.setWarehousePackCount(WP_DEFAULT);
            subCatgReplPackCons.setVendorPackWareHousePackRatio(VP_WP_RATIO_DEFAULT);
        }
        return subCatgReplPackCons;
    }

    private FinelineReplPackCons getVendorPackAndWhsePackCountForFineline(FinelineReplPackId finelineReplPackId) {
        FinelineReplPackCons finelineReplPackCons = new FinelineReplPackCons();
        finelineReplPackCons.setFinelineReplPackId(finelineReplPackId);
        Optional<FinelineReplPack> finelineReplPackResult = finelineReplnPkConsRepository.findById(finelineReplPackId);
        if (finelineReplPackResult.isPresent()) {
            FinelineReplPack finelineReplPackFromDB = finelineReplPackResult.get();
            finelineReplPackCons.setVendorPackCount(finelineReplPackFromDB.getVendorPackCnt());
            finelineReplPackCons.setWarehousePackCount(finelineReplPackFromDB.getWhsePackCnt());
            finelineReplPackCons.setVendorPackWareHousePackRatio(finelineReplPackFromDB.getVnpkWhpkRatio());
        } else {
            finelineReplPackCons.setVendorPackCount(VP_DEFAULT);
            finelineReplPackCons.setWarehousePackCount(WP_DEFAULT);
            finelineReplPackCons.setVendorPackWareHousePackRatio(VP_WP_RATIO_DEFAULT);
        }
        return finelineReplPackCons;
    }

    private StyleReplPackCons getVendorPackAndWhsePackCountForStyle(StyleReplPackId styleReplPackId) {
        StyleReplPackCons styleReplPackCons = new StyleReplPackCons();
        styleReplPackCons.setStyleReplPackId(styleReplPackId);
        Optional<StyleReplPack> stylePackResult = styleReplnConsRepository.findById(styleReplPackId);
        if (stylePackResult.isPresent()) {
            StyleReplPack styleReplPackFromDB = stylePackResult.get();
            styleReplPackCons.setVendorPackCount(styleReplPackFromDB.getVendorPackCnt());
            styleReplPackCons.setWarehousePackCount(styleReplPackFromDB.getWhsePackCnt());
            styleReplPackCons.setVendorPackWareHousePackRatio(styleReplPackFromDB.getVnpkWhpkRatio());
        } else {
            styleReplPackCons.setVendorPackCount(VP_DEFAULT);
            styleReplPackCons.setWarehousePackCount(WP_DEFAULT);
            styleReplPackCons.setVendorPackWareHousePackRatio(VP_WP_RATIO_DEFAULT);
        }
        return styleReplPackCons;
    }

    private CcReplPackCons getVendorPackAndWhsePackCountForCc(CcReplPackId ccReplPackId) {
        CcReplPackCons ccReplPackCons = new CcReplPackCons();
        ccReplPackCons.setCcReplPackId(ccReplPackId);
        Optional<CcReplPack> ccRepPackResult = ccReplnConsRepository.findById(ccReplPackId);
        if (ccRepPackResult.isPresent()) {
            CcReplPack ccReplPackFromDB = ccRepPackResult.get();
            ccReplPackCons.setVendorPackCount(ccReplPackFromDB.getVendorPackCnt());
            ccReplPackCons.setWarehousePackCount(ccReplPackFromDB.getWhsePackCnt());
            ccReplPackCons.setVendorPackWareHousePackRatio(ccReplPackFromDB.getVnpkWhpkRatio());
        } else {
            ccReplPackCons.setVendorPackCount(VP_DEFAULT);
            ccReplPackCons.setWarehousePackCount(WP_DEFAULT);
            ccReplPackCons.setVendorPackWareHousePackRatio(VP_WP_RATIO_DEFAULT);
        }
        return ccReplPackCons;
    }

    private CcMmReplPackCons getVendorPackAndWhsePackCountForCcMm(CcMmReplPackId ccMmReplPackId) {
        CcMmReplPackCons ccMmReplPackCons = new CcMmReplPackCons();
        ccMmReplPackCons.setCcMmReplPackId(ccMmReplPackId);
        Optional<CcMmReplPack> ccMmReplPackResult = ccMmReplnPkConsRepository.findById(ccMmReplPackId);
        if (ccMmReplPackResult.isPresent()) {
            CcMmReplPack ccMmReplPackFromDB = ccMmReplPackResult.get();
            ccMmReplPackCons.setVendorPackCount(ccMmReplPackFromDB.getVendorPackCnt());
            ccMmReplPackCons.setWarehousePackCount(ccMmReplPackFromDB.getWhsePackCnt());
            ccMmReplPackCons.setVendorPackWareHousePackRatio(ccMmReplPackFromDB.getVnpkWhpkRatio());
        } else {
            ccMmReplPackCons.setVendorPackCount(VP_DEFAULT);
            ccMmReplPackCons.setWarehousePackCount(WP_DEFAULT);
            ccMmReplPackCons.setVendorPackWareHousePackRatio(VP_WP_RATIO_DEFAULT);
        }
        return ccMmReplPackCons;
    }

    private Map<Integer, CcSpMmReplPack> getCcSpMmReplPackSizeMap(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, String styleNbr, String ccId, Integer merchCode) {
        List<CcSpMmReplPack> ccSpMmReplPacks = ccSpReplnPkConsRepository.getCcSpMmReplnPkVendorPackAndWhsePackCount(calculateBuyQtyParallelRequest.getPlanId(), ChannelType.getChannelIdFromName(calculateBuyQtyParallelRequest.getChannel()), calculateBuyQtyParallelRequest.getLvl3Nbr(), calculateBuyQtyParallelRequest.getLvl4Nbr(), calculateBuyQtyParallelRequest.getFinelineNbr(), styleNbr, ccId, merchCode);
        return Optional.ofNullable(ccSpMmReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(ccSpMmReplPack -> ccSpMmReplPack.getCcSpReplPackId().getAhsSizeId(), ccSpMmReplPack -> ccSpMmReplPack));
    }

}



