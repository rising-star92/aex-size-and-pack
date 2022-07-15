package com.walmart.aex.sp.entity;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subcatg_pkopt_cons", schema = "dbo")
@Embeddable
public class SubCatgPackOptimization {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private SubCatgPackOptimizationID subCatgPackOptimizationID;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private MerchantPackOptimization merchantPackOptimization;
    
    @OneToMany(mappedBy = "subCatgPackOptimization", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<fineLinePackOptimization> finelinepackOptimization;
    
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
