package com.walmart.aex.sp.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "merchcatg_pkopt_cons", schema = "dbo")
@Embeddable
public class MerchantPackOptimization {

    /*@Id
    @Column(name="plan_id", nullable = false)
    private Long planId;
    @Column(name="rpt_lvl_0_nbr",nullable = false)
    private Integer repTLvl0;
    @Column(name="rpt_lvl_1_nbr",nullable = false)
    private Integer repTLvl1;
    @Column(name="rpt_lvl_2_nbr",nullable = false)
    private Integer repTLvl2;
    @Column(name="rpt_lvl_3_nbr",nullable = false)
    private Integer repTLvl3;*/
	
	@EmbeddedId
	@EqualsAndHashCode.Include
	MerchantPackOptimizationID merchantPackOptimizationID;

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

    @JoinColumn(name = "run_status_code", insertable = false, updatable = false)
    @ManyToOne(targetEntity = RunStatusText.class, fetch = FetchType.LAZY)
    private RunStatusText runStatusText;
    
    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    @ManyToOne(targetEntity = ChannelText.class, fetch = FetchType.LAZY)
    private ChannelText channelText;





}
