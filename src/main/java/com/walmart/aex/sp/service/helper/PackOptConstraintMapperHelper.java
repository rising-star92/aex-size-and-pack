package com.walmart.aex.sp.service.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.UpdatePackOptStatusRequest;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.enums.RunStatusCodeType.getPrefixEligibleRunStatusCodes;
import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Service
@Slf4j
@Transactional
public class PackOptConstraintMapperHelper {
    private final ObjectMapper objectMapper;

    public PackOptConstraintMapperHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> getRunStatusLongDescriptions(FineLineMapperDto fineLineMapperDto, Map<Integer, Map<Integer, String>> finelineBumpStatusMap){
        Map<Integer, String> runStatusLongDescriptions = finelineBumpStatusMap.getOrDefault(fineLineMapperDto.getFineLineNbr(), new HashMap<>());
        if (null != fineLineMapperDto.getChildRunStatusCode() && RunStatusCodeType.ANALYTICS_ERRORS_LIST.contains(fineLineMapperDto.getChildRunStatusCode())) {
            runStatusLongDescriptions.put(fineLineMapperDto.getBumpPackNbr(), fineLineMapperDto.getChildRunStatusCodeDesc());
            finelineBumpStatusMap.put(fineLineMapperDto.getFineLineNbr(), runStatusLongDescriptions);
        }

        Map<String, Set<Integer>> errorsToBumpPacks = new HashMap<>();
        for (Map.Entry<Integer, String> entry : runStatusLongDescriptions.entrySet()) {
            int bumpPackNbr = entry.getKey();
            String errorDescription = null;
            if(!StringUtils.isEmpty(fineLineMapperDto.getChildReturnMessage())){
                try{
                    UpdatePackOptStatusRequest statusRequest = objectMapper.readValue(fineLineMapperDto.getChildReturnMessage(),UpdatePackOptStatusRequest.class);
                    if(!StringUtils.isEmpty(statusRequest.getStatusLongDesc()))
                        errorDescription = statusRequest.getStatusLongDesc().trim();
                }catch(JsonProcessingException exception){
                    log.error("Exception while setting the child update pack optimization status for planId:{} and finelineNbr: {} ",fineLineMapperDto.getPlanId(), fineLineMapperDto.getFineLineNbr());
                }
            }
            if(StringUtils.isEmpty(errorDescription))
            {
                errorDescription = entry.getValue();
            }
            errorsToBumpPacks.computeIfAbsent(errorDescription, k -> new HashSet<>()).add(bumpPackNbr);
        }

        List<String> runStatusLongDesc = new ArrayList<>();
        for (Map.Entry<String, Set<Integer>> entry : errorsToBumpPacks.entrySet()) {
            String errorDescription = entry.getKey();
            Set<Integer> bumpPacks = entry.getValue();
            if(getPrefixEligibleRunStatusCodes().contains(fineLineMapperDto.getChildRunStatusCode())) {
                String bumpPacksString = bumpPacks.stream()
                        .map(bumpPack -> bumpPack == 1 ? getInitialSetOrBumpPackError(bumpPacks, fineLineMapperDto.getChildRunStatusCode()) : BUMP_PACK_ERROR + bumpPack)
                        .collect(Collectors.joining(", "));
                String combinedDescription = bumpPacksString + " : " + errorDescription;
                runStatusLongDesc.add(combinedDescription);
            }else{
                runStatusLongDesc.add(errorDescription);
            }
        }
        return runStatusLongDesc;
    }

    private String getInitialSetOrBumpPackError(Set<Integer> bumpPacks, Integer runStatusCode) {
     
            if(runStatusCode == 14){
                return INITIAL_SET;
            }else if(runStatusCode == 15){
                return BUMP_PACK_ERROR + 1;
            }
           if((bumpPacks.size()>1) && (runStatusCode != 14 || runStatusCode != 15)){
              return INITIAL_SET + " + " + BUMP_PACK_ERROR + 1;
            }
         return INITIAL_SET;
    }

}
