package com.walmart.aex.sp.service.helper;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import com.walmart.aex.sp.entity.RunStatusText;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import com.walmart.aex.sp.repository.RunStatusTextRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.walmart.aex.sp.util.SizeAndPackConstants.BUMP_PACK;

@Service
@Slf4j
@Transactional
public class PackOptConstraintMapperHelper {
    private final RunStatusTextRepository runStatusTextRepository;
    private final AnalyticsMlSendRepository analyticsMlSendRepository;

    public PackOptConstraintMapperHelper(RunStatusTextRepository runStatusTextRepository, AnalyticsMlSendRepository analyticsMlSendRepository) {
        this.runStatusTextRepository = runStatusTextRepository;
        this.analyticsMlSendRepository = analyticsMlSendRepository;
    }
    public List<String> getRunStatusLongDescriptions(FineLineMapperDto fineLineMapperDto) {
        Optional<Set<AnalyticsMlChildSend>> analyticsMlSend = analyticsMlSendRepository.findAnalyticsMlSendByPlanIdAndfinelineNbr(fineLineMapperDto.getPlanId(), fineLineMapperDto.getFineLineNbr());
        List<String> runStatusLongDescriptions = new ArrayList<>();
        if (analyticsMlSend.isPresent()) {
            Set<AnalyticsMlChildSend> analyticsMlChildSendList = analyticsMlSend.get();
            for (AnalyticsMlChildSend mlChild : analyticsMlChildSendList) {
                Optional<RunStatusText> runStatusText = runStatusTextRepository.findById(mlChild.getRunStatusCode());
                if(runStatusText.isPresent()){
                    String runStatusLongDescForBumpPack = BUMP_PACK + mlChild.getBumpPackNbr() + " : " + runStatusText.get().getRunStatusLongDesc();
                    runStatusLongDescriptions.add(runStatusLongDescForBumpPack);
                }
            }
            // Adding run status long desc for fineline
            runStatusLongDescriptions.add(fineLineMapperDto.getRunStatusLongDesc());
        }
        return runStatusLongDescriptions;
    }
}
