package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
public class PackOptimizationService {


    private final PackOptimizationRepository packOptRepo;
    private final FineLinePackOptimizationRepository finelinePackOptimizationRepository;
    private final PackOptimizationMapper packOptimizationMapper;
    private final FinelinePackOptRepository packOptfineplanRepo;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private final StylePackOptimizationRepository stylePackOptimizationRepository;
    private final AnalyticsMlSendRepository analyticsMlSendRepository;
    Function<Object, String> ifNullThenEmpty = o -> Objects.nonNull(o) ? o.toString() : "";

    public PackOptimizationService(PackOptimizationRepository packOptRepo,
                                   FineLinePackOptimizationRepository finelinePackOptimizationRepository,
                                   FinelinePackOptRepository packOptfineplanRepo, CcPackOptimizationRepository ccPackOptimizationRepository,
                                   StylePackOptimizationRepository stylePackOptimizationRepository, AnalyticsMlSendRepository analyticsMlSendRepository, PackOptimizationMapper packOptimizationMapper) {
        this.packOptRepo = packOptRepo;
        this.finelinePackOptimizationRepository = finelinePackOptimizationRepository;
        this.packOptfineplanRepo = packOptfineplanRepo;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
        this.stylePackOptimizationRepository = stylePackOptimizationRepository;
        this.packOptimizationMapper = packOptimizationMapper;
        this.analyticsMlSendRepository = analyticsMlSendRepository;
    }

    private FineLineMapperDto prepareFineLineMapperDto(Object[] object) {
        FineLineMapperDto fineLineMapperDto = new FineLineMapperDto();
        fineLineMapperDto.setPlanId(Long.valueOf(ifNullThenEmpty.apply(object[0])));
        fineLineMapperDto.setChannelId(Integer.valueOf(ifNullThenEmpty.apply(object[1])));
        fineLineMapperDto.setLvl0Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[2])));
        fineLineMapperDto.setLvl1Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[3])));
        fineLineMapperDto.setLvl2Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[4])));
        fineLineMapperDto.setLvl3Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[5])));
        fineLineMapperDto.setLvl4Nbr(Integer.valueOf(ifNullThenEmpty.apply(object[6])));
        fineLineMapperDto.setFineLineNbr(Integer.valueOf(object[7].toString()));
        fineLineMapperDto.setAltfineLineDesc(ifNullThenEmpty.apply(object[8]));

        fineLineMapperDto.setLvl0Desc(ifNullThenEmpty.apply(object[9]));
        fineLineMapperDto.setLvl1Desc(ifNullThenEmpty.apply(object[10]));
        fineLineMapperDto.setLvl2Desc(ifNullThenEmpty.apply(object[11]));
        fineLineMapperDto.setLvl3Desc(ifNullThenEmpty.apply(object[12]));
        fineLineMapperDto.setLvl4Desc(ifNullThenEmpty.apply(object[13]));
        fineLineMapperDto.setFirstName(ifNullThenEmpty.apply(object[15]));
        fineLineMapperDto.setLastName(ifNullThenEmpty.apply(object[16]));
        fineLineMapperDto.setStartTs((Date) object[17]);
        fineLineMapperDto.setReturnMessage(ifNullThenEmpty.apply(object[19]));
        return fineLineMapperDto;
    }

    private void prepareCcPackOptimizationID() {
        CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID();
        StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
        stylePackOptimizationID.setStyleNbr("");
        fineLinePackOptimizationID fineLinePackOptimizationID = new fineLinePackOptimizationID();
        fineLinePackOptimizationID.setFinelineNbr(0);
        SubCatgPackOptimizationID subCatgPackOptimizationID = new SubCatgPackOptimizationID();
        MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID();
        merchantPackOptimizationID.setRepTLvl1(0);
        merchantPackOptimizationID.setRepTLvl2(0);
        merchantPackOptimizationID.setRepTLvl3(0);
        subCatgPackOptimizationID.setMerchantPackOptimizationID(merchantPackOptimizationID);
        fineLinePackOptimizationID.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
        stylePackOptimizationID.setFinelinePackOptimizationID(fineLinePackOptimizationID);
        ccPackOptimizationID.setStylePackOptimizationID(stylePackOptimizationID);
    }


    public PackOptimizationResponse getPackOptDetails(Long planId, Integer channelid) {
        try {
            List<FineLineMapperDto> finePlanPackOptimizationList = packOptfineplanRepo.findByFinePlanPackOptimizationIDPlanIdAndChannelTextChannelId(planId, channelid);
            Set<StylePackOptimization> stylePkOptList = Collections.emptySet();
            List<CcPackOptimization> ccPkOptList = Collections.emptyList();
            return packOptDetails(finePlanPackOptimizationList, stylePkOptList, ccPkOptList, planId, channelid);
        } catch (Exception e) {
            log.error("Error Occurred while fetching Pack Opt", e);
            throw e;
        }

    }


    public PackOptimizationResponse packOptDetails(
            List<FineLineMapperDto> fineLineMapperDtos,
            Set<StylePackOptimization> stylePkOptList,
            List<CcPackOptimization> ccPkOptList,
            Long planId,
            Integer channelId) {

        PackOptimizationResponse packOptResp = new PackOptimizationResponse();
        packOptResp.setPlanId(planId);
        packOptResp.setChannel(channelId);
        List<Lvl3> lvl3List = new ArrayList<>();
        fineLineMapperDtos.forEach(fineLineMapperDto -> {
            packOptResp.setLvl3List(maplvl3PackOpResponse(fineLineMapperDto, lvl3List));
        });

        return packOptResp;
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
        fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
        fineline.setPackOptimizationStatus(status);
        fineline.setOptimizationDetails(setOptimizationDetails(fineLineMapperDto));
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

    public Constraints getConstraintsDetails(SubCatgPackOptimization subCtgPkopt) {

        Constraints cList = new Constraints();
        SupplierConstraints spList = new SupplierConstraints();
        spList.setSupplierName(subCtgPkopt.getVendorName());
        spList.setMaxPacks(subCtgPkopt.getMaxNbrOfPacks());
        spList.setMaxUnitsPerPack(subCtgPkopt.getMaxUnitsPerPack());

        List<CcLevelConstraints> ccLevelList = new ArrayList<>();
        CcLevelConstraints ccLevel = new CcLevelConstraints();
        ccLevel.setFactoryIds(subCtgPkopt.getFactoryId());
        ccLevel.setCountryOfOrigin(subCtgPkopt.getOriginCountryName());
        ccLevel.setPortOfOrigin(subCtgPkopt.getPortOfOriginName());
        ccLevel.setSinglePackIndicator(subCtgPkopt.getSinglePackInd());
        ccLevel.setColorCombination(subCtgPkopt.getColorCombination());
        ccLevelList.add(ccLevel);
        cList.setSupplierConstraints(spList);
        cList.setCcLevelConstraints(ccLevelList);
        return cList;

    }

    public Constraints getMerchantPkOptConstraintDetails(MerchantPackOptimization merchPackOptObj) {

        Constraints cList = new Constraints();
        SupplierConstraints spList = new SupplierConstraints();
        spList.setSupplierName(merchPackOptObj.getVendorName());
        spList.setMaxPacks(merchPackOptObj.getMaxNbrOfPacks());
        spList.setMaxUnitsPerPack(merchPackOptObj.getMaxUnitsPerPack());

        List<CcLevelConstraints> ccLevelList = new ArrayList<>();
        CcLevelConstraints ccLevel = new CcLevelConstraints();
        ccLevel.setFactoryIds(merchPackOptObj.getFactoryId());
        ccLevel.setCountryOfOrigin(merchPackOptObj.getOriginCountryName());
        ccLevel.setPortOfOrigin(merchPackOptObj.getPortOfOriginName());
        ccLevel.setSinglePackIndicator(merchPackOptObj.getSinglePackInd());
        ccLevel.setColorCombination(merchPackOptObj.getColorCombination());
        ccLevelList.add(ccLevel);
        cList.setSupplierConstraints(spList);
        cList.setCcLevelConstraints(ccLevelList);
        return cList;

    }

    public Constraints getFinelinePkOptConstraintDetails(fineLinePackOptimization finelinePackOptObj) {

        Constraints cList = new Constraints();
        SupplierConstraints spList = new SupplierConstraints();
        spList.setSupplierName(finelinePackOptObj.getVendorName());
        spList.setMaxPacks(finelinePackOptObj.getMaxNbrOfPacks());
        spList.setMaxUnitsPerPack(finelinePackOptObj.getMaxUnitsPerPack());

        List<CcLevelConstraints> ccLevelList = new ArrayList<>();
        CcLevelConstraints ccLevel = new CcLevelConstraints();
        ccLevel.setFactoryIds(finelinePackOptObj.getFactoryId());
        ccLevel.setCountryOfOrigin(finelinePackOptObj.getOriginCountryName());
        ccLevel.setPortOfOrigin(finelinePackOptObj.getPortOfOriginName());
        ccLevel.setSinglePackIndicator(finelinePackOptObj.getSinglePackInd());
        ccLevel.setColorCombination(finelinePackOptObj.getColorCombination());
        ccLevelList.add(ccLevel);
        cList.setSupplierConstraints(spList);
        cList.setCcLevelConstraints(ccLevelList);
        return cList;

    }

    public Constraints getStylePkOptConstraintDetails(StylePackOptimization stylePackOptObj) {

        Constraints cList = new Constraints();
        SupplierConstraints spList = new SupplierConstraints();
        spList.setSupplierName(stylePackOptObj.getVendorName());
        spList.setMaxPacks(stylePackOptObj.getMaxNbrOfPacks());
        spList.setMaxUnitsPerPack(stylePackOptObj.getMaxUnitsPerPack());

        List<CcLevelConstraints> ccLevelList = new ArrayList<>();
        CcLevelConstraints ccLevel = new CcLevelConstraints();
        ccLevel.setFactoryIds(stylePackOptObj.getFactoryId());
        ccLevel.setCountryOfOrigin(stylePackOptObj.getOriginCountryName());
        ccLevel.setPortOfOrigin(stylePackOptObj.getPortOfOriginName());
        ccLevel.setSinglePackIndicator(stylePackOptObj.getSinglePackInd());
        ccLevel.setColorCombination(stylePackOptObj.getColorCombination());
        ccLevelList.add(ccLevel);
        cList.setSupplierConstraints(spList);
        cList.setCcLevelConstraints(ccLevelList);
        return cList;

    }

    public Constraints getCcPkOptConstraintDetails(CcPackOptimization ccPackOptObj) {

        Constraints cList = new Constraints();
        SupplierConstraints spList = new SupplierConstraints();
        spList.setSupplierName(ccPackOptObj.getVendorName());
        spList.setMaxPacks(ccPackOptObj.getMaxNbrOfPacks());
        spList.setMaxUnitsPerPack(ccPackOptObj.getMaxUnitsPerPack());

        List<CcLevelConstraints> ccLevelList = new ArrayList<>();
        CcLevelConstraints ccLevel = new CcLevelConstraints();
        ccLevel.setFactoryIds(ccPackOptObj.getFactoryId());
        ccLevel.setCountryOfOrigin(ccPackOptObj.getOriginCountryName());
        ccLevel.setPortOfOrigin(ccPackOptObj.getPortOfOriginName());
        ccLevel.setSinglePackIndicator(ccPackOptObj.getSinglePackInd());
        ccLevel.setColorCombination(ccPackOptObj.getColorCombination());
        ccLevelList.add(ccLevel);
        cList.setSupplierConstraints(spList);
        cList.setCcLevelConstraints(ccLevelList);
        return cList;

    }

    public List<Lvl4> subCategoryResponseList(Set<SubCatgPackOptimization> subCatgList, Set<fineLinePackOptimization> finelineList,
                                              Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList) {
        List<Lvl4> lvl4list = new ArrayList<>();

        for (SubCatgPackOptimization subctgpkopt : subCatgList) {
            Lvl4 lvl4 = new Lvl4();
            lvl4.setConstraints(getConstraintsDetails(subctgpkopt));

            List<Fineline> fineLinelist = new ArrayList<>();
            fineLinelist = finelineResponseList(finelineList, stylePkOptList, ccPkOptList);
            lvl4.setFinelines(fineLinelist);

            lvl4list.add(lvl4);
        }
        return lvl4list;
    }

    public List<Fineline> finelineResponseList(Set<fineLinePackOptimization> finelinePkOptList,
                                               Set<StylePackOptimization> stylePkOptList, Set<CcPackOptimization> ccPkOptList) {
        List<Fineline> finelineList = new ArrayList<>();

        for (fineLinePackOptimization fineLinePkOpt : finelinePkOptList) {
            Fineline fineListObj = new Fineline();
            fineListObj.setFinelineNbr(fineLinePkOpt.getFinelinePackOptId().getFinelineNbr());

            List<Style> styleList = new ArrayList<>();
            styleList = styleResponseList(stylePkOptList, ccPkOptList);
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
            List<CustomerChoice> customerChoiceList = new ArrayList();
            customerChoiceList = customerChoiceResponseList(ccPkOptList);

            style.setStyleNbr(stylePkOptObj.getStylepackoptimizationId().getStyleNbr());
            style.setConstraints(getStylePkOptConstraintDetails(stylePkOptObj));
            style.setCustomerChoices(customerChoiceList);
            styleList.add(style);

        }

        return styleList;
    }

    public List<CustomerChoice> customerChoiceResponseList(Set<CcPackOptimization> ccPkOptList) {
        List<CustomerChoice> customerChoiceList = new ArrayList();

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
}