package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum ExcludeOffshoreMkt {
    PR(1, "PR"),
    AK(2, "AK"),
    HI(3, "HI");

    private Integer marketSelectCode;
    private String marketValue;

    private ExcludeOffshoreMkt(Integer marketSelectCode, String marketValue) {
        this.marketSelectCode = marketSelectCode;
        this.marketValue = marketValue;
    }

    /**
     * @return the id
     */
    public final Integer getMarketSelectCode() {
        return marketSelectCode;
    }

    /**
     * @param marketSelectCode the id to set
     */
    public final void setMarketSelectCode(Integer marketSelectCode) {
        this.marketSelectCode = marketSelectCode;
    }

    /**
     * @return the description
     */
    public final String getMarketValue() {
        return marketValue;
    }

    /**
     * @param marketValue the description to set
     */
    public final void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }


    public static String getChannelNameFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.marketSelectCode.equals(id))
                .findFirst().map(ExcludeOffshoreMkt::getMarketValue).orElse(null);
    }

    public static Integer getChannelIdFromName(String value) {
        return Stream.of(values())
                .filter(e -> e.marketValue.equalsIgnoreCase(value))
                .findFirst().map(ExcludeOffshoreMkt::getMarketSelectCode).orElse(null);
    }
}
