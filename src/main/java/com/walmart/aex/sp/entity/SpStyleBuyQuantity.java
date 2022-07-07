package com.walmart.aex.sp.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sp_style_buy_qty", schema = "dbo")
@Embeddable
public class SpStyleBuyQuantity {
	
	    @EmbeddedId
	    @EqualsAndHashCode.Include
	    SpStyleBuyQuantityId spStyleBuyQuantityId;
	    
	    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	    @JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
	    @JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
	    @JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
	    @JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
	    @JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
	    @JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
	    @JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false, insertable = false, updatable = false)
	    @JsonIgnore
	    private SpFineLineBuyQuantity spFineLineBuyQuantity;
	    
	    @OneToMany(mappedBy = "spStyleQBuyQuatity", fetch = FetchType.LAZY,
	            cascade = CascadeType.ALL, orphanRemoval = true)
	    private Set<SpCCBuyQuantity> spCCBuyQuantities;

	    @Column(name="weeks_supply")
	    private Integer weeksSupply;
	    
	    @Column(name="fineline_desc")
	    private Integer finelineDesc;
	    
	    @Column(name = "avg_sp_pct", nullable = false)
	    private Integer avgsppct;
	    
	    
	   @Column(name = "adj_sp_pct", nullable = false)
	    private Integer adjsppct;

	    
	    @Column(name = "buy_qty", nullable = false)
	    private Integer buyqty;
	    
	    
	    @Column(name = "final_buy_qty", nullable = false)
	    private Integer finalbuyqty;

	    
	    
	    
	    
	    
	    
	    
	    
}




