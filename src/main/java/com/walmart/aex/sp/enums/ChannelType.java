package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum ChannelType {
    STORE(1, "Store"),
    ONLINE(2, "Online"),
    OMNI(3, "Omni");

    private Integer id;
    private String description;

    private ChannelType(Integer id, String description) {
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
     * @param id the id to set
     */
    public final void setId(Integer id) {
        this.id = id;
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


    public static String getChannelNameFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.id.equals(id))
                .findFirst().map(ChannelType::getDescription).orElse(null);
    }
}
