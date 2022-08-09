package com.walmart.aex.sp.enums;


public enum RunStatusCodeType {
    NOT_SENT_TO_ANALYTICS(0, "NOT SENT TO ANALYTICS"),
    SENT_TO_ANALYTICS(3, "SENT TO ANALYTICS"),
    ANALYTICS_RUN_COMPLETED(6, "ANALYTICS RUN COMPLETED"),
    ANALYTICS_ERROR(10, "ANALYTICS ERROR");

    private Integer id;
    private String description;

    private RunStatusCodeType(Integer id, String description) {
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
}
