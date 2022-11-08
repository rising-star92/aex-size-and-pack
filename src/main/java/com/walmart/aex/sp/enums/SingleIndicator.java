package com.walmart.aex.sp.enums;


//selected - 1
//unselected - 0
//partial - 2
public enum SingleIndicator {
    SELECTED(1, "SELECTED"),
    UNSELECTED(0, "UNSELECTED"),
    PARTIAL(2, "PARTIAL SELECTED");


    private Integer id;
    private String description;

    private SingleIndicator(Integer id, String description) {
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
}

