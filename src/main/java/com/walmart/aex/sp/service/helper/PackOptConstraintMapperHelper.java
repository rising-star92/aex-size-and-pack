package com.walmart.aex.sp.service.helper;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
@Transactional
public class PackOptConstraintMapperHelper {

    public List<String> getRunStatusLongDescriptions(FineLineMapperDto fineLineMapperDto, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        Map<Integer, String> runStatusLongDescriptions = finelineBumpStatusMap.getOrDefault(fineLineMapperDto.getFineLineNbr(), new HashMap<>());
        if (null != fineLineMapperDto.getChildRunStatusCode() && RunStatusCodeType.ANALYTICS_ERRORS_LIST.contains(fineLineMapperDto.getChildRunStatusCode())) {
            runStatusLongDescriptions.put(fineLineMapperDto.getBumpPackNbr(), fineLineMapperDto.getChildRunStatusCodeDesc());
            finelineBumpStatusMap.put(fineLineMapperDto.getFineLineNbr(), runStatusLongDescriptions);
        }

        Map<String, Set<Integer>> errorsToBumpPacks = new HashMap<>();
        for (Map.Entry<Integer, String> entry : runStatusLongDescriptions.entrySet()) {
            int bumpPackNbr = entry.getKey();
            String errorDescription = entry.getValue();
            errorsToBumpPacks.computeIfAbsent(errorDescription, k -> new HashSet<>()).add(bumpPackNbr);
        }

        List<String> runStatusLongDesc = new ArrayList<>();
        for (Map.Entry<String, Set<Integer>> entry : errorsToBumpPacks.entrySet()) {
            String errorDescription = entry.getKey();
            Set<Integer> bumpPacks = entry.getValue();
            String bumpPacksString = bumpPacks.stream()
                    .map(bumpPack ->  bumpPack == 1 ? getInitialSetOrBumpPack(bumpPacks) : BUMP_PACK + bumpPack)
                    .collect(Collectors.joining(", "));
            String combinedDescription = bumpPacksString + " : " + errorDescription;
            runStatusLongDesc.add(combinedDescription);
        }
        return runStatusLongDesc;

    }

    private String getInitialSetOrBumpPack(Set<Integer> bumpPacks) {
        return bumpPacks.size() > 1 ? INITIAL_SET_AND_BUMP_PACK : INITIAL_SET;
    }
}
