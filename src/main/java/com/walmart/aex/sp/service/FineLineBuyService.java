package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.*;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.FineLineBuyRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;




@Service
@Slf4j
public class FineLineBuyService {




    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
    @Autowired
    private FineLineBuyRepo fineLineBuyRepo;

    public FetchFineLineResponse fineLineResponse(BuyQtyRequest buyQtyRequest)
    {
        Long planId = buyQtyRequest.getPlanId();
        String planDesc = buyQtyRequest.getPlanDesc();
        int channelId = getChannelId(buyQtyRequest.getChannel());

        List<SpFineLineChannelFixture> spFineLineChannelFixtureList= fineLineBuyRepo.findBySpFineLineChannelFixtureIdPlanIdAndSpFineLineChannelFixtureIdChannelId(planId, channelId);

        Set<SpFineLineChannelFixture> spLvl4List = spFineLineChannelFixtureList.stream().filter(distinctByKey(x->x.getSpFineLineChannelFixtureId().getRepTLvl4())).collect(Collectors.toSet());



        return fineLineDetails(spFineLineChannelFixtureList,spLvl4List);
    }


    public FetchFineLineResponse fineLineDetails(List<SpFineLineChannelFixture> spFineLineChannelFixtureList,Set<SpFineLineChannelFixture> lvl4Set)
    {
        List<Integer> lvlList= new ArrayList<>();

        int index=0;
        SpFineLineChannelFixture spFineLineChannelFixture= spFineLineChannelFixtureList.get(index);
        FetchFineLineResponse fetchFlRespDetails = fetchFlDetails(spFineLineChannelFixture,lvl4Set,spFineLineChannelFixtureList);
        return fetchFlRespDetails;
    }
    public FetchFineLineResponse fetchFlDetails(SpFineLineChannelFixture spFineLineChannelFixture,Set<SpFineLineChannelFixture> lvl4Set,List<SpFineLineChannelFixture> spFineLineChannelFixtureList)

    {

        FetchFineLineResponse fineLineResponse= new FetchFineLineResponse();
        fineLineResponse.setPlanId(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getPlanId());
        fineLineResponse.setLvl0Nbr(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getRepTLvl0());
        fineLineResponse.setLvl1Nbr(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getRepTLvl1());
        fineLineResponse.setLvl2Nbr(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getRepTLvl2());

        List<lvl3Dto> lvl3ListDto= new ArrayList<>();
        lvl3Dto lvl3List=new lvl3Dto();
        lvl3List.setLvl3Nbr(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getRepTLvl3());


        List<lvl4Dto> lvl4ListDtoList = new ArrayList<>();
        lvl4ListDtoList=Lvl4ResponseList(lvl4Set,spFineLineChannelFixtureList);

        lvl3List.setLvl4List(lvl4ListDtoList);
        lvl3ListDto.add(lvl3List);
        fineLineResponse.setLvl3List(lvl3ListDto);
        return fineLineResponse;

    }
    private List<lvl4Dto> Lvl4ResponseList(Set<SpFineLineChannelFixture> lvl4Set, List<SpFineLineChannelFixture> spFineLineChannelFixtureList) {
        List<lvl4Dto> lvl4ListList = new ArrayList<>();
        List<Integer> lvl4ListNbr = new ArrayList<>();

        for (SpFineLineChannelFixture spFineLineChannelFixture : lvl4Set) {
            lvl4Dto lvl4List = new lvl4Dto();
            lvl4List.setLvl4Nbr(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getRepTLvl4());
            lvl4List.setLvl4Desc(null);
            Integer nbr=spFineLineChannelFixture.getSpFineLineChannelFixtureId().getRepTLvl4();
            lvl4ListNbr.add(nbr);
            List<FinelineDto> fineLineList = new ArrayList<>();
            fineLineList = FineLineResponseList(nbr,spFineLineChannelFixtureList);
            lvl4List.setFinelines(fineLineList);
            lvl4ListList.add(lvl4List);
        }
        return lvl4ListList;
    }

    private List<FinelineDto> FineLineResponseList(Integer nbr, List<SpFineLineChannelFixture> spFineLineChannelFixtureList) {
        List<FinelineDto> fineLineList = new ArrayList<>();
        for (SpFineLineChannelFixture spFineLineChannelFixture : spFineLineChannelFixtureList) {
            if(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getRepTLvl4().equals(nbr)) {
                FinelineDto fineLine = new FinelineDto();
                fineLine.setFinelineNbr(spFineLineChannelFixture.getSpFineLineChannelFixtureId().getFineLineNbr());
                fineLine.setFinelineDesc(null);

                MetricsDto metricsObj = new MetricsDto();

                metricsObj.setBuyQty(spFineLineChannelFixture.getBuyQty());
                metricsObj.setFinalInitialSetQty(spFineLineChannelFixture.getInitialSetQty());
                metricsObj.setFinalReplenishmentQty(spFineLineChannelFixture.getReplnQty());
                fineLine.setMetrics(metricsObj);
                fineLineList.add(fineLine);
            }
        }

        return fineLineList;


    }
    private Long planId;
    private int getChannelId(String channel) {
        try {

            if ("Store".equalsIgnoreCase(channel)) {
                return 1;
            } else if ("Online".equalsIgnoreCase(channel)) {
                return 2;
            } else {
                throw new CustomException("Invalid Channel");
            }

        } catch (Exception e) {

            throw new CustomException("Invalid Channel");
        }

    }

}
