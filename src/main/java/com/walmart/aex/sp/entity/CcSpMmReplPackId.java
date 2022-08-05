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
public class CcSpMmReplPackId implements Serializable
{
    @Embedded
    private CcMmReplPackId ccMmReplPackId;

    @Column(name="ahs_size_id",nullable = false)
    private Integer ahsSizeId;
}
