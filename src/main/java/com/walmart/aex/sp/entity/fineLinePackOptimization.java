package com.walmart.aex.sp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fineline_pkopt_cons", schema = "dbo")
@Embeddable
public class fineLinePackOptimization {

    @EmbeddedId
    fineLinePackOptimizationID finelinePackOptId;

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
