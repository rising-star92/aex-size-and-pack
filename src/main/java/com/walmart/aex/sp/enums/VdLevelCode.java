package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum VdLevelCode {
    FINELINE(1, "Fineline"),
    SUB_CATEGORY(2, "Sub_Category"),
    CATEGORY(3, "Category");

    private Integer id;
    private String description;

    private VdLevelCode(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * @return the id
     */
    public final Integer getId() {
        return id;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    public static String getVdLevelCodeFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.id.equals(id))
                .findFirst().map(VdLevelCode::getDescription).orElse(null);
    }

    public static VdLevelCode getVdLevelCode(Integer id) {
        return Stream.of(values())
                .filter(e -> e.id.equals(id))
                .findFirst().orElse(null);
    }

    public static Integer getVdLevelCodeIdFromName(String vdLevelCodeDesc) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(vdLevelCodeDesc))
                .findFirst().map(VdLevelCode::getId).orElse(null);
    }


}
