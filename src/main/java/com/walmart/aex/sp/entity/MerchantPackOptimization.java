package com.walmart.aex.sp.entity;

import lombok.*;

import java.util.Set;

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

	
	@EmbeddedId
	@EqualsAndHashCode.Include
	MerchantPackOptimizationID merchantPackOptimizationID;
	
	@OneToMany(mappedBy = "merchantPackOptimization", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SubCatgPackOptimization> subCatgPackOptimization;

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
