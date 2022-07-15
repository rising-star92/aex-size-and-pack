package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cc_pkopt_cons", schema = "dbo")

public class CcPackOptimization {

    @EmbeddedId
    @EqualsAndHashCode.Include
    CcPackOptimizationID ccPackOptimizationId;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "style_nbr", referencedColumnName = "style_nbr", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private StylePackOptimization stylePackOptimization;

    @Column(name="vendor_nbr_6")
    private Integer vendorNbr6;

    @Column(name="vendor_nbr_9")
    private Integer vendorNbr9;

     @Column(name="vendor_name")
    private String vendorName;

    @Column(name="origin_country_code")
    private String originCountryCode;

    @Column(name="origin_country_name")
    private String originCountryName;
    
    @Column(name="factory_id")
    private String factoryId;
    
    @Column(name="factory_name")
    private String factoryName;

    @Column(name="single_pack_ind")
    private Integer singlePackInd;

    @Column(name="port_of_origin_id")
    private Integer portOfOriginId;

    @Column(name="port_of_origin_name")
    private String portOfOriginName;

    @Column(name="max_units_per_pack")
    private Integer maxUnitsPerPack;

    @Column(name="max_nbr_of_packs")
    private Integer maxNbrOfPacks;

    @Column(name="color_combination")
    private String colorCombination;

    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
    private ChannelText channelText;


}
