package com.walmart.aex.sp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDeleteDTO;
import com.walmart.aex.sp.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class CommonUtil {
    private final ObjectMapper objectMapper;

    public CommonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static String getRequestedFlChannel(Lvl3 lvl3) {
        return Optional.ofNullable(lvl3.getLvl4List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Fineline::getChannel)
                .orElse(null);

    }

    public static Integer getFixtureRollUpId(String fixtureType) {
        switch (fixtureType.toUpperCase()) {
            case "ONLINE_FIXTURE": {
                return 0;
            }
            case "WALLS": {
                return 1;
            }
            case "ENDCAPS": {
                return 2;
            }
            case "RACKS": {
                return 3;
            }
            case "TABLES": {
                return 4;
            }
            default: {
                throw new CustomException("Fixture Type does not Match");
            }
        }
    }

    public static Integer getMerchMethod(String merchMethod) {
        switch (merchMethod.toUpperCase()) {
            case "FOLDED": {
                return 2;
            }
            case "HANGING": {
                return 1;
            }
            case "ONLINE_MERCH_METHOD": {
                return 0;
            }
            default: {
                throw new CustomException("Merch Method does not Match");
            }
        }
    }

    public static String getMerchMethod(Integer merchMethod) {
        switch (merchMethod) {
            case 2: {
                return "FOLDED";
            }
            case 1: {
                return "HANGING";
            }
            case 0: {
                return "ONLINE_MERCH_METHOD";
            }
            default: {
                throw new CustomException("Merch Method does not Match");
            }
        }

    }

    public static Integer getChannelId(String channelDesc) {
        switch (channelDesc.toUpperCase()) {
            case "STORE": {
                return 1;
            }
            case "ONLINE": {
                return 2;
            }
            case "OMNI": {
                return 3;
            }
            default: {
                throw new CustomException("Channel Type does not Match");
            }
        }
    }

    public PlanSizeAndPackDeleteDTO cleanSPDeleteRequest(PlanSizeAndPackDeleteDTO planSizeAndPackDeleteDTO) throws IOException {
        return objectMapper.readValue(Jsoup.clean(StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeSql(objectMapper.writeValueAsString(planSizeAndPackDeleteDTO))),
                Whitelist.basic()), PlanSizeAndPackDeleteDTO.class);
    }

    public PlanSizeAndPackDTO cleanSPRequest(PlanSizeAndPackDTO planSizeAndPackDTO) throws IOException {
        return objectMapper.readValue(Jsoup.clean(StringEscapeUtils.unescapeHtml(StringEscapeUtils.escapeSql(objectMapper.writeValueAsString(planSizeAndPackDTO))),
                Whitelist.basic()), PlanSizeAndPackDTO.class);
    }
}
