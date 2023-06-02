package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum FixtureTypeRollup {

    DEFAULT(-1, "DEFAULT"),
    ONLINE_FIXTURE(0, "ONLINE_FIXTURE"),
    WALLS(1, "WALLS"),
    ENDCAPS(2, "ENDCAPS"),
    RACKS(3, "RACKS"),
    TABLES(4, "TABLES"),
    H_RACKS(5, "H-RACK");

    
    private final Integer code;
    private final String description;

    FixtureTypeRollup(Integer code, String description) {
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
     * @return the description
     */
    public final String getDescription() {
        return description;
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
