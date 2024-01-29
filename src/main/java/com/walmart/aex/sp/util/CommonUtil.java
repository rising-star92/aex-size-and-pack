package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyObj;
import com.walmart.aex.sp.dto.buyquantity.ValidationResult;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommonUtil {

    private CommonUtil() {

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

    public static LocalDateTime getLocalDateTime(Date result) {
        return result.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
