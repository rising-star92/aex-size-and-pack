package com.walmart.aex.sp.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class FixtureTypeRollUpId implements Serializable {

    @Column(name="fixturetype_rollup_id", nullable = false)
    private Integer fixtureTypeRollupId;
}
