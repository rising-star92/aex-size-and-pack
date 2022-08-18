package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class CommonUtil {
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
        switch (fixtureType.toUpperCase()){
            case "WALLS":{
                return 1;
            }
            case "ENDCAPS":{
                return 2;
            }
            case "RACKS":{
                return 3;
            }
            case "TABLES": {
                return 4;
            }
        }
        throw new RuntimeException("Fixture Type does not Match");
    }

    public static Integer getMerchMethod(String merchMethod) {
        switch (merchMethod.toUpperCase()){
            case "FOLDED":{
                return 2;
            }
            case "HANGING":{
                return 1;
            }
        }
        throw new RuntimeException("Merch Method does not Match");
    }

    public static String getMerchMethod(Integer merchMethod) {
        switch (merchMethod){
            case 2 :{
                return "FOLDED";
            }
            case 1:{
                return "HANGING";
            }
        }
        throw new RuntimeException("Merch Method does not Match");
    }

    public static Integer getChannelId(String channelDesc) {
        switch (channelDesc.toUpperCase()){
            case "STORE":{
                return 1;
            }
            case "ONLINE":{
                return 2;
            }
            case "OMNI":{
                return 3;
            }
        }
        throw new RuntimeException("Channel Type does not Match");
    }
}
