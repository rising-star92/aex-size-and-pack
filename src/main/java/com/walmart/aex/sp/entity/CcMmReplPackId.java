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
public class CcMmReplPackId implements Serializable
{
    @Embedded
    private CcReplPackId ccReplPackId;

    @Column(name="merch_method_code",nullable = false)
    private Integer merchMethodCode;
}
