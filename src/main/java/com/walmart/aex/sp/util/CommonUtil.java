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
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return objectMapper.readValue(Jsoup.clean(StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeSql(objectMapper.writeValueAsString(planSizeAndPackDTO))),
                Whitelist.basic()), PlanSizeAndPackDTO.class);
    }

    public static Date getDateFromString(String dateStr) {
        Date date = null;
        if (StringUtils.isNotEmpty(dateStr)) {
            try {
                Instant startDateInstant = Instant.parse(dateStr);
                if (startDateInstant != null) {
                    date = Date.from(startDateInstant);
                }
            } catch (Exception ex) {
                log.info("Error converting the start Date string: {}", ex.getMessage());
            }
        }
        return date;
    }

    public static HttpHeaders getHttpHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.setCacheControl(CacheControl.noCache());
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::add);
        }
        return httpHeaders;
    }

    public static List<Integer> getNumbersFromString(String finelineNbr) {
        Matcher matcher = Pattern.compile("\\d+").matcher(finelineNbr);
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group()));
        }
        return numbers;
    }
}
