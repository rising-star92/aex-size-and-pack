package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum FixtureTypeRollup {

    ONLINE_FIXTURE(0, "ONLINE_FIXTURE"),
    WALLS(1, "WALLS"),
    ENDCAPS(2, "ENDCAPS"),
    RACKS(3, "RACKS"),
    TABLES(4, "TABLES");

    private Integer code;
    private String description;

    private FixtureTypeRollup(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * @return the id
     */
    public final Integer getCode() {
        return code;
    }

    /**
     * @param code the id to set
     */
    public final void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }


    public static String getFixtureTypeFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.code.equals(id))
                .findFirst().map(FixtureTypeRollup::getDescription).orElse(null);
    }

    public static Integer getFixtureIdFromName(String fixtureType) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(fixtureType))
                .findFirst().map(FixtureTypeRollup::getCode).orElse(null);
    }
}
