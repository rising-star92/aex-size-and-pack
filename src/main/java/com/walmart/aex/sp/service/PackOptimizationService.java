package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.CategoryType;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;

import static com.walmart.aex.sp.util.SizeAndPackConstants.FAILED_STATUS;
import static com.walmart.aex.sp.util.SizeAndPackConstants.SUCCESS_STATUS;

@Service
@Slf4j
public class PackOptimizationService {

    private final FineLinePackOptimizationRepository finelinePackOptimizationRepository;
    private final PackOptimizationMapper packOptimizationMapper;
    private final FinelinePackOptRepository packOptfineplanRepo;
    private final AnalyticsMlSendRepository analyticsMlSendRepository;
    private final MerchPackOptimizationRepository merchPackOptimizationRepository;
    private final UpdatePackOptimizationMapper updatePackOptimizationMapper;
    private final StyleCcPackOptConsRepository styleCcPackOptConsRepository;
    private final PackOptConstraintMapper packOptConstraintMapper;

    Function<Object, String> ifNullThenEmpty = o -> Objects.nonNull(o) ? o.toString() : "";

    public PackOptimizationService(FineLinePackOptimizationRepository finelinePackOptimizationRepository,
                                   FinelinePackOptRepository packOptfineplanRepo,
                                   AnalyticsMlSendRepository analyticsMlSendRepository,
                                   PackOptimizationMapper packOptimizationMapper, 
                                   StyleCcPackOptConsRepository styleCcPackOptConsRepository, 
                                   PackOptConstraintMapper packOptConstraintMapper,
                                   MerchPackOptimizationRepository merchPackOptimizationRepository, UpdatePackOptimizationMapper updatePackOptimizationMapper){
        this.finelinePackOptimizationRepository = finelinePackOptimizationRepository;
        this.packOptfineplanRepo = packOptfineplanRepo;
        this.packOptimizationMapper = packOptimizationMapper;
        this.analyticsMlSendRepository = analyticsMlSendRepository;
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
        this.updatePackOptimizationMapper = updatePackOptimizationMapper;
        this.styleCcPackOptConsRepository = styleCcPackOptConsRepository;
        this.packOptConstraintMapper = packOptConstraintMapper;
    }

    public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelId) {
        try {
            List<FineLineMapperDto> finePlanPackOptimizationList = packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelId);
            return packOptDetails(finePlanPackOptimizationList,planId, channelId);
        } catch (Exception e) {
            log.error("Error Occurred while fetching Pack Opt", e);
            throw e;
        }

    }

    public PackOptimizationResponse packOptDetails(
            List<FineLineMapperDto> fineLineMapperDtos,
            Long planId,
            Integer channelId) {

        PackOptimizationResponse packOptResp = new PackOptimizationResponse();
        packOptResp.setPlanId(planId);
        packOptResp.setChannel(channelId);
        List<Lvl3> lvl3List = new ArrayList<>();
        fineLineMapperDtos.forEach(fineLineMapperDto ->
                mapPackOptLvl2(fineLineMapperDto,lvl3List,packOptResp)
        );

        return packOptResp;
    }
    public void mapPackOptLvl2(FineLineMapperDto fineLineMapperDto, List<Lvl3> lvl3List,PackOptimizationResponse response) {
        if (response.getPlanId() == null) {
            response.setPlanId(fineLineMapperDto.getPlanId());
        }
        if (response.getLvl0Nbr() == null) {
            response.setLvl0Nbr(fineLineMapperDto.getLvl0Nbr());
            response.setLvl0Desc(fineLineMapperDto.getLvl0Desc());
        }
        if (response.getLvl1Nbr() == null) {
            response.setLvl1Nbr(fineLineMapperDto.getLvl1Nbr());
            response.setLvl1Desc(fineLineMapperDto.getLvl1Desc());
        }
        if (response.getLvl2Nbr() == null) {
            response.setLvl2Nbr(fineLineMapperDto.getLvl2Nbr());
            response.setLvl2Desc(fineLineMapperDto.getLvl2Desc());
        }
        if (response.getChannel() == null) {
            response.setChannel(fineLineMapperDto.getChannelId());
        }
        response.setLvl3List(maplvl3PackOpResponse(fineLineMapperDto ,lvl3List));
    }

    private List<Lvl3> maplvl3PackOpResponse(FineLineMapperDto fineLineMapperDto, List<Lvl3> lvl3List) {
        lvl3List.stream()
                .filter(lvl3 -> fineLineMapperDto.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(maplvl4PackOp(fineLineMapperDto, lvl3, fineLineMapperDto.getFineLineNbr())),
                        () -> setLvl3SP(fineLineMapperDto, lvl3List));
        return lvl3List;
    }


    private void setLvl3SP(FineLineMapperDto fineLineMapperDto, List<Lvl3> lvl3List) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl0Nbr(fineLineMapperDto.getLvl0Nbr());
        lvl3.setLvl1Nbr(fineLineMapperDto.getLvl1Nbr());
        lvl3.setLvl2Nbr(fineLineMapperDto.getLvl2Nbr());
        lvl3.setLvl3Nbr(fineLineMapperDto.getLvl3Nbr());
        lvl3.setLvl3Name(fineLineMapperDto.getLvl3Desc());
        lvl3.setConstraints(getConstraints(fineLineMapperDto, CategoryType.MERCHANT));
        lvl3List.add(lvl3);
        lvl3.setLvl4List(maplvl4PackOp(fineLineMapperDto, lvl3, fineLineMapperDto.getFineLineNbr()));
    }

    private List<Lvl4> maplvl4PackOp(FineLineMapperDto fineLineMapperDto, Lvl3 lvl3, Integer finelineNbr) {
        List<Lvl4> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> fineLineMapperDto.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapFLPackOp(fineLineMapperDto, lvl4, finelineNbr)),
                        () -> setLvl4SP(fineLineMapperDto, lvl4DtoList, finelineNbr));
        return lvl4DtoList;
    }

    private void setLvl4SP(FineLineMapperDto fineLineMapperDto, List<Lvl4> lvl4DtoList, Integer finelineNbr) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(fineLineMapperDto.getLvl4Nbr());
        lvl4.setLvl4Name(fineLineMapperDto.getLvl4Desc());
        lvl4.setConstraints(getConstraints(fineLineMapperDto, CategoryType.SUB_CATEGORY));
        lvl4DtoList.add(lvl4);
        lvl4.setFinelines(mapFLPackOp(fineLineMapperDto, lvl4, finelineNbr));
    }

    private List<Fineline> mapFLPackOp(FineLineMapperDto fineLineMapperDto, Lvl4 lvl4, Integer finelineNbr) {
        List<Fineline> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<Fineline>());

        Fineline fineline = finelineDtoList.stream()
                .filter(f -> finelineNbr.equals(f.getFinelineNbr()))
                .findAny()
                .orElse(null);

        if (fineline == null) {
            setFinelineSP(fineLineMapperDto, finelineDtoList);
        } else {
            if (fineline.getOptimizationDetails().get(0).getStartTs().compareTo(fineLineMapperDto.getStartTs()) < 0) {
                finelineDtoList.remove(fineline);
                setFinelineSP(fineLineMapperDto, finelineDtoList);
            }
        }
        return finelineDtoList;
    }

    private Fineline setFinelineSP(FineLineMapperDto fineLineMapperDto, List<Fineline> finelineDtoList) {
        Fineline fineline = new Fineline();
        String status = Optional.ofNullable(fineLineMapperDto.getRunStatusDesc()).orElse("NOT SENT");
        fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
        fineline.setFinelineName(fineLineMapperDto.getFineLineDesc());
        fineline.setAltFinelineName(fineLineMapperDto.getAltfineLineDesc());
        fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
        fineline.setPackOptimizationStatus(status);
        fineline.setOptimizationDetails(setOptimizationDetails(fineLineMapperDto));
        fineline.setConstraints(getConstraints(fineLineMapperDto, CategoryType.FINE_LINE));
        finelineDtoList.add(fineline);
        return fineline;
    }

    private List<RunOptimization> setOptimizationDetails(FineLineMapperDto f) {
        RunOptimization opt = new RunOptimization();
        opt.setName(f.getFirstName());
        opt.setReturnMessage(f.getReturnMessage());
        opt.setRunStatusCode(f.getRunStatusCode());
        opt.setStartTs(f.getStartTs());
        return List.of(opt);
    }

    public Constraints getConstraints(FineLineMapperDto fineLineMapperDto, CategoryType type) {

        Constraints constraints = new Constraints();
        switch (type) {
            case MERCHANT:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getMerchMaxNbrOfPacks(),fineLineMapperDto.getMerchMaxUnitsPerPack()));
               constraints.setColorCombinationConstraints(new ColorCombinationConstraints(fineLineMapperDto.getMerchSupplierName(), fineLineMapperDto.getMerchFactoryId(),
                       fineLineMapperDto.getMerchOriginCountryName(), fineLineMapperDto.getMerchPortOfOriginName(),
                       fineLineMapperDto.getMerchSinglePackInd(), fineLineMapperDto.getMerchColorCombination()));
                 break;
            case SUB_CATEGORY:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getSubCatMaxNbrOfPacks(),fineLineMapperDto.getSubCatMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(fineLineMapperDto.getSubCatSupplierName(), fineLineMapperDto.getSubCatFactoryId(),
                        fineLineMapperDto.getSubCatOriginCountryName(), fineLineMapperDto.getSubCatPortOfOriginName(),
                        fineLineMapperDto.getSubCatSinglePackInd(), fineLineMapperDto.getSubCatColorCombination()));
                break;
            default:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getFineLineMaxNbrOfPacks(),fineLineMapperDto.getFineLineMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(fineLineMapperDto.getFineLineSupplierName(), fineLineMapperDto.getFineLineFactoryId(),
                        fineLineMapperDto.getFineLineOriginCountryName(), fineLineMapperDto.getFineLinePortOfOriginName(),
                        fineLineMapperDto.getFineLineSinglePackInd(), fineLineMapperDto.getFineLineColorCombination()));
                break;
        }
        return constraints;

    }

    public Constraints getSubCatConstraintsDetails(SubCatgPackOptimization subCatPackOpt) {

        Constraints constraints = new Constraints();
        SupplierConstraints supplierConstraints = new SupplierConstraints();
        supplierConstraints.setSupplierName(subCatPackOpt.getVendorName());
        supplierConstraints.setFactoryIds(subCatPackOpt.getFactoryId());
        supplierConstraints.setCountryOfOrigin(subCatPackOpt.getOriginCountryName());
        supplierConstraints.setPortOfOrigin(subCatPackOpt.getPortOfOriginName());

        CcLevelConstraints ccLevelConstraints = new CcLevelConstraints();
        ccLevelConstraints.setMaxPacks(subCatPackOpt.getMaxNbrOfPacks());
        ccLevelConstraints.setMaxUnitsPerPack(subCatPackOpt.getMaxUnitsPerPack());
        ccLevelConstraints.setSinglePackIndicator(subCatPackOpt.getSinglePackInd());
        ccLevelConstraints.setColorCombination(subCatPackOpt.getColorCombination());

        constraints.setSupplierConstraints(supplierConstraints);
        constraints.setCcLevelConstraints(ccLevelConstraints);
        return constraints;

    }

    public Constraints getMerchantPkOptConstraintDetails(MerchantPackOptimization merchPackOpt) {

        Constraints constraints = new Constraints();
        SupplierConstraints supplierConstraints = new SupplierConstraints();
        supplierConstraints.setSupplierName(merchPackOpt.getVendorName());
        supplierConstraints.setFactoryIds(merchPackOpt.getFactoryId());
        supplierConstraints.setCountryOfOrigin(merchPackOpt.getOriginCountryName());
        supplierConstraints.setPortOfOrigin(merchPackOpt.getPortOfOriginName());

        CcLevelConstraints ccLevelConstraints = new CcLevelConstraints();
        ccLevelConstraints.setMaxPacks(merchPackOpt.getMaxNbrOfPacks());
        ccLevelConstraints.setMaxUnitsPerPack(merchPackOpt.getMaxUnitsPerPack());
        ccLevelConstraints.setSinglePackIndicator(merchPackOpt.getSinglePackInd());
        ccLevelConstraints.setColorCombination(merchPackOpt.getColorCombination());

        constraints.setSupplierConstraints(supplierConstraints);
        constraints.setCcLevelConstraints(ccLevelConstraints);
        return constraints;

    }

    public Constraints getFinelinePkOptConstraintDetails(FineLinePackOptimization fineLinePackOpt) {

        Constraints constraints = new Constraints();
        SupplierConstraints supplierConstraints = new SupplierConstraints();
        supplierConstraints.setSupplierName(fineLinePackOpt.getVendorName());
        supplierConstraints.setFactoryIds(fineLinePackOpt.getFactoryId());
        supplierConstraints.setCountryOfOrigin(fineLinePackOpt.getOriginCountryName());
        supplierConstraints.setPortOfOrigin(fineLinePackOpt.getPortOfOriginName());

        CcLevelConstraints ccLevelConstraints = new CcLevelConstraints();
        ccLevelConstraints.setMaxPacks(fineLinePackOpt.getMaxNbrOfPacks());
        ccLevelConstraints.setMaxUnitsPerPack(fineLinePackOpt.getMaxUnitsPerPack());
        ccLevelConstraints.setSinglePackIndicator(fineLinePackOpt.getSinglePackInd());
        ccLevelConstraints.setColorCombination(fineLinePackOpt.getColorCombination());

        constraints.setSupplierConstraints(supplierConstraints);
        constraints.setCcLevelConstraints(ccLevelConstraints);
        return constraints;

    }

    public Constraints getStylePkOptConstraintDetails(StylePackOptimization stylePackOpt) {

        Constraints constraints = new Constraints();
        SupplierConstraints supplierConstraints = new SupplierConstraints();
        supplierConstraints.setSupplierName(stylePackOpt.getVendorName());
        supplierConstraints.setFactoryIds(stylePackOpt.getFactoryId());
        supplierConstraints.setCountryOfOrigin(stylePackOpt.getOriginCountryName());
        supplierConstraints.setPortOfOrigin(stylePackOpt.getPortOfOriginName());

        CcLevelConstraints ccLevelConstraints = new CcLevelConstraints();
        ccLevelConstraints.setMaxPacks(stylePackOpt.getMaxNbrOfPacks());
        ccLevelConstraints.setMaxUnitsPerPack(stylePackOpt.getMaxUnitsPerPack());
        ccLevelConstraints.setSinglePackIndicator(stylePackOpt.getSinglePackInd());
        ccLevelConstraints.setColorCombination(stylePackOpt.getColorCombination());

        constraints.setSupplierConstraints(supplierConstraints);
        constraints.setCcLevelConstraints(ccLevelConstraints);
        return constraints;

    }

    public Constraints getCcPkOptConstraintDetails(CcPackOptimization ccPackOpt) {

        Constraints constraints = new Constraints();
        SupplierConstraints supplierConstraints = new SupplierConstraints();
        supplierConstraints.setSupplierName(ccPackOpt.getVendorName());
        supplierConstraints.setFactoryIds(ccPackOpt.getFactoryId());
        supplierConstraints.setCountryOfOrigin(ccPackOpt.getOriginCountryName());
        supplierConstraints.setPortOfOrigin(ccPackOpt.getPortOfOriginName());

        CcLevelConstraints ccLevelConstraints = new CcLevelConstraints();
        ccLevelConstraints.setMaxPacks(ccPackOpt.getMaxNbrOfPacks());
        ccLevelConstraints.setMaxUnitsPerPack(ccPackOpt.getMaxUnitsPerPack());
        ccLevelConstraints.setSinglePackIndicator(ccPackOpt.getSinglePackInd());
        ccLevelConstraints.setColorCombination(ccPackOpt.getColorCombination());

        constraints.setSupplierConstraints(supplierConstraints);
        constraints.setCcLevelConstraints(ccLevelConstraints);
        return constraints;

    }

    public List<Lvl4> subCategoryResponseList(Set<SubCatgPackOptimization> subCatgList, Set<FineLinePackOptimization> finelineList,
                                              Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList) {
        List<Lvl4> lvl4list = new ArrayList<>();

        for (SubCatgPackOptimization subctgpkopt : subCatgList) {
            Lvl4 lvl4 = new Lvl4();
            lvl4.setConstraints(getSubCatConstraintsDetails(subctgpkopt));

            List<Fineline> fineLinelist = finelineResponseList(finelineList, stylePkOptList, ccPkOptList);
            lvl4.setFinelines(fineLinelist);

            lvl4list.add(lvl4);
        }
        return lvl4list;
    }

    public List<Fineline> finelineResponseList(Set<FineLinePackOptimization> finelinePkOptList,
                                               Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList) {
        List<Fineline> finelineList = new ArrayList<>();

        for (FineLinePackOptimization fineLinePkOpt : finelinePkOptList) {
            Fineline fineListObj = new Fineline();
            fineListObj.setFinelineNbr(fineLinePkOpt.getFinelinePackOptId().getFinelineNbr());

            List<Style> styleList = styleResponseList(stylePkOptList, ccPkOptList);
            fineListObj.setConstraints(getFinelinePkOptConstraintDetails(fineLinePkOpt));
            fineListObj.setStyles(styleList);

            finelineList.add(fineListObj);
        }

        return finelineList;
    }

    public List<Style> styleResponseList(Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList) {
        List<Style> styleList = new ArrayList<>();

        for (StylePackOptimization stylePkOptObj : stylePkOptList) {
            Style style = new Style();
            List<CustomerChoice> customerChoiceList = customerChoiceResponseList(ccPkOptList);

            style.setStyleNbr(stylePkOptObj.getStylePackoptimizationId().getStyleNbr());
            style.setConstraints(getStylePkOptConstraintDetails(stylePkOptObj));
            style.setCustomerChoices(customerChoiceList);
            styleList.add(style);

        }

        return styleList;
    }

    public List<CustomerChoice> customerChoiceResponseList(Set<CcPackOptimization> ccPkOptList) {
        List<CustomerChoice> customerChoiceList = new ArrayList<> ();

        for (CcPackOptimization ccpkOptObj : ccPkOptList) {
            CustomerChoice customerChoice = new CustomerChoice();
            customerChoice.setCcId(ccpkOptObj.getCcPackOptimizationId().getCustomerChoice());
            customerChoice.setColorName(null);
            customerChoice.setConstraints(getCcPkOptConstraintDetails(ccpkOptObj));
            customerChoiceList.add(customerChoice);

        }

        return customerChoiceList;
    }


    public FineLinePackOptimizationResponse getPackOptFinelineDetails(Long planId, Integer finelineNbr) {
        FineLinePackOptimizationResponse finelinePackOptimizationResponse = new FineLinePackOptimizationResponse();

        try {
            List<FineLinePackOptimizationResponseDTO> finelinePackOptimizationResponseDTOS = finelinePackOptimizationRepository.getPackOptByFineline(planId, finelineNbr);
            Optional.of(finelinePackOptimizationResponseDTOS)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(FinelinePackOptimizationResponseDTO -> packOptimizationMapper.
                            mapPackOptimizationFineline(FinelinePackOptimizationResponseDTO, finelinePackOptimizationResponse,planId));


        } catch (Exception e) {
            log.error("Exception While fetching Fineline pack Optimization :", e);
            throw new CustomException("Failed to fetch Fineline Pack Optimization , due to" + e);
        }
        log.info("Fetch Pack Optimization Fineline response: {}", finelinePackOptimizationResponse);
        return finelinePackOptimizationResponse;
    }

    public void UpdatePkOptServiceStatus(Long planId, Integer finelineNbr, Integer status) {
        analyticsMlSendRepository.updateStatus(planId, finelineNbr, status);
    }

    public StatusResponse updatePackOptConstraints(UpdatePackOptConstraintRequestDTO request) {
        StatusResponse response = new StatusResponse();
        if (isRequestValid(request)) {
            Integer channelId = ChannelType.getChannelIdFromName(request.getChannel());
            log.debug("Check if a MerchCatPackOptimization for planID : {} already exists or not", request.getPlanId().toString());
            List<MerchantPackOptimization> merchantPackOptimizationList = merchPackOptimizationRepository.findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl3AndChannelText_channelId(request.getPlanId(), request.getLvl3Nbr(),channelId);
            if (!CollectionUtils.isEmpty(merchantPackOptimizationList)) {
                updatePackOptimizationMapper.updateCategoryPackOptCons(request, merchantPackOptimizationList);
                response.setStatus(SUCCESS_STATUS);
            } else {
                log.warn("MerchCatPackOptimization for planID : {} doesn't exists and therefore cannot update", request.getPlanId().toString());
                response.setStatus(FAILED_STATUS);
            }
        } else {
            response.setStatus(FAILED_STATUS);
        }
        return response;
    }

    private boolean isRequestValid(UpdatePackOptConstraintRequestDTO request) {
        if(request.getLvl3Nbr()==null){
            log.warn("Invalid Request: Lvl3Nbr cannot be NULL in the request {}", request.toString());
            return false;
        }else if(request.getLvl4Nbr()!=null && request.getLvl3Nbr()==null){
            log.warn("Invalid Request: Lvl3Nbr cannot be NULL when lvl4Nbr is not NULL in the request {}", request.toString());
            return false;
        }else if(request.getLvl4Nbr()==null && request.getFinelineNbr()!=null){
            log.warn("Invalid Request: Lvl4Nbr cannot be NULL when finelineNbr is not NULL in the request {}", request.toString());
            return false;
        }else if(request.getFinelineNbr()==null && request.getStyleNbr()!=null){
            log.warn("Invalid Request: finelineNbr cannot be NULL when styleNbr is not NULL in the request {}", request.toString());
            return false;
        }else if(request.getStyleNbr()==null && request.getCcId()!=null){
            log.warn("Invalid Request: styleNbr cannot be NULL when ccId is not NULL in the request {}", request.toString());
            return false;
        }
        return true;
    }

    public PackOptimizationResponse getPackOptConstraintDetails(PackOptConstraintRequest request)
    {
        PackOptimizationResponse packOptimizationResponse = new PackOptimizationResponse();
        try {
            List<PackOptConstraintResponseDTO> packOptConstraintResponseDTO = styleCcPackOptConsRepository
                    .findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(request.getPlanId(), ChannelType.getChannelIdFromName(request.getChannel()),request.getFinelineNbr());
            Optional.of(packOptConstraintResponseDTO)
                    .stream()
                    .flatMap(Collection::stream)
                    .forEach(constraintResponseDTO -> packOptConstraintMapper
                            .mapPackOptLvl2(constraintResponseDTO, packOptimizationResponse, request.getFinelineNbr()));
        } catch (Exception e) {
            log.error("Exception While fetching Fineline PackOpt :", e);
            throw new CustomException("Failed to fetch Fineline PackOpt, due to" + e);
        }
        log.info("Fetch PackOpt Fineline response: {}", packOptimizationResponse);
        return packOptimizationResponse;

    }
}