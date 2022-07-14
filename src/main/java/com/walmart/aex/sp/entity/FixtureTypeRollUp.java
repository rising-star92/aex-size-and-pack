package com.walmart.aex.sp.entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fixturetype_rollup", schema = "dbo")
@Embeddable
public class FixtureTypeRollUp  {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private FixtureTypeRollUpId fixtureTypeRollUpId;

    @Column(name="fixturetype_rollup_name", nullable = false)
    private String fixtureTypeRollupName;

    @Column(name="fixturetype_rollup_desc", nullable = false)
    private String fixtureTypeRollupDesc;


    @OneToMany(mappedBy = "fixtureTypeRollUp", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SpFineLineChannelFixture> spFineLineChannelFixtures;

}
