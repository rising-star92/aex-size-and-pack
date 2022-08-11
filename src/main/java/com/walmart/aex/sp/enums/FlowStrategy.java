package com.walmart.aex.sp.enums;

import java.util.stream.Stream;

public enum FlowStrategy {
    INITIAL_SET(1, "INITIAL_SET"),
    BUMP_SET(2, "BUMP_SET"),
    REPLENISHMENT_SET(3, "REPLENISHMENT_SET"),
	BUMP_REPLENISHMENT(4,"BUMP_REPLENISHMENT"),
	INITIAL_BUMP(5,"INITIAL_BUMP"),
	INITIAL_REPLENISHMENT(6,"INITIAL_REPLENISHMENT"),
	INITIAL_BUMP_REPLENISHMENT(7,"INITIAL_BUMP_REPLENISHMENT");

    private Integer id;
    private String description;

    private FlowStrategy(Integer id, String description) {
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


    public static String getFlowStrategyFromId(Integer id) {
        return Stream.of(values())
                .filter(e -> e.id.equals(id))
                .findFirst().map(FlowStrategy::getDescription).orElse(null);
    }
    
    public static FlowStrategy getFlowStrategy(Integer id) {
        return Stream.of(values())
                .filter(e -> e.id.equals(id))
                .findFirst().orElse(null);
    }

    public static Integer getFlowStrategyIdFromName(String flowDesc) {
        return Stream.of(values())
                .filter(e -> e.description.equalsIgnoreCase(flowDesc))
                .findFirst().map(FlowStrategy::getId).orElse(null);
    }


}
