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
@Table(name = "sp_cc_sp_buy_qty", schema = "dbo")
@Embeddable
public class SpCcSpBuyQuantity {

	@EmbeddedId
	@EqualsAndHashCode.Include
	SpCcSpBuyQuantityId spCcSpBuyQuantityId;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "plan_id", referencedColumnName = "plan_id", nullable = false, insertable = false, updatable = false)
	@JoinColumn(name = "rpt_lvl_0_nbr", referencedColumnName = "rpt_lvl_0_nbr", nullable = false, insertable = false, updatable = false)
	@JoinColumn(name = "rpt_lvl_1_nbr", referencedColumnName = "rpt_lvl_1_nbr", nullable = false, insertable = false, updatable = false)
	@JoinColumn(name = "rpt_lvl_2_nbr", referencedColumnName = "rpt_lvl_2_nbr", nullable = false, insertable = false, updatable = false)
	@JoinColumn(name = "rpt_lvl_3_nbr", referencedColumnName = "rpt_lvl_3_nbr", nullable = false, insertable = false, updatable = false)
	@JoinColumn(name = "rpt_lvl_4_nbr", referencedColumnName = "rpt_lvl_4_nbr", nullable = false, insertable = false, updatable = false)
	@JoinColumn(name = "fineline_nbr", referencedColumnName = "fineline_nbr", nullable = false, insertable = false, updatable = false)

	@JoinColumn(name = "style_nbr", referencedColumnName = "style_nbr", nullable = false, insertable = false, updatable = false)

	@JoinColumn(name = "customer_choice", referencedColumnName = "customer_choice", nullable = false, insertable = false, updatable = false)

	@JsonIgnore
	private SpCCBuyQuantity spCCBuyQuantity;

	@Column(name = "ahs_size_desc", nullable = false)
	private Integer ahssizedesc;

	@Column(name = "weeks_supply", nullable = false)
	private Integer weekssupply;

	@Column(name = "avg_sp_pct", nullable = false)
	private Integer avgsppct;

	@Column(name = "adj_sp_pct", nullable = false)
	private Integer adjsppct;

	@Column(name = "buy_qty", nullable = false)
	private Integer buyqty;

	@Column(name = "final_buy_qty", nullable = false)
	private Integer finalbuyqty;
}