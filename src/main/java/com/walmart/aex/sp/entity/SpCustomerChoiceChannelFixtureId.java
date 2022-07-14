package com.walmart.aex.sp.entity;


import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@EqualsAndHashCode
public class SpCustomerChoiceChannelFixtureId implements Serializable {

    @Embedded
    private SpStyleChannelFixtureId spStyleChannelFixtureId;

    @Column(name="customer_choice", nullable=false)
    private String customerChoice;

}
