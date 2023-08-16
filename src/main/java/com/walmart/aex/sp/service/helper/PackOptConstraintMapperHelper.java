package com.walmart.aex.sp.service.helper;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.walmart.aex.sp.util.SizeAndPackConstants.BUMP_PACK;

@Service
@Slf4j
@Transactional
public class PackOptConstraintMapperHelper {

    public List<String> getRunStatusLongDescriptions(FineLineMapperDto fineLineMapperDto, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        Map<Integer, String> runStatusLongDescriptions = new HashMap<>();
        List<String> runStatusLongDesc = new ArrayList<>();
        if (null != fineLineMapperDto.getChildRunStatusCode() && RunStatusCodeType.ANALYTICS_ERRORS_LIST.contains(fineLineMapperDto.getChildRunStatusCode())) {

            if(null != finelineBumpStatusMap.get(fineLineMapperDto.getFineLineNbr())) {
                runStatusLongDescriptions = finelineBumpStatusMap.get(fineLineMapperDto.getFineLineNbr());
                runStatusLongDescriptions.put(fineLineMapperDto.getBumpPackNbr(), RunStatusCodeType.getRunStatusFromId(fineLineMapperDto.getChildRunStatusCode()));
            }
            else {
                runStatusLongDescriptions.put(fineLineMapperDto.getBumpPackNbr(), RunStatusCodeType.getRunStatusFromId(fineLineMapperDto.getChildRunStatusCode()));
                finelineBumpStatusMap.put(fineLineMapperDto.getFineLineNbr(), runStatusLongDescriptions);
            }
        }
        if(null != finelineBumpStatusMap.get(fineLineMapperDto.getFineLineNbr()))
            for(Map.Entry<Integer,String> entry : finelineBumpStatusMap.get(fineLineMapperDto.getFineLineNbr()).entrySet()) {
                String runStatusLongDescForBumpPack = BUMP_PACK + entry.getKey() + " : " + entry.getValue();
                runStatusLongDesc.add(runStatusLongDescForBumpPack);
            }
        return runStatusLongDesc;
    }
}
