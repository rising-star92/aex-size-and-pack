package com.walmart.aex.sp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
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
public class CcReplPackId implements Serializable
{
    @Embedded
    private StyleReplPackId styleReplPackId;

    @Column(name="customer_choice",nullable = false)
    @Convert( converter = CharConverter.class)
    private String customerChoice;

   public CcReplPackId(StyleReplPackId styleReplPackId) {
      this.styleReplPackId = styleReplPackId;
   }
}
