package com.walmart.aex.sp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSizeId;


public interface FineLinePackOptimizationRepository  extends JpaRepository<SpCustomerChoiceChannelFixtureSize, SpCustomerChoiceChannelFixtureSizeId>{

	@Query(value="select new com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO" +
			"(sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId as planId, " +
			"sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr as finelineNbr, " +
			"sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.customerChoice as ccId, " +
			"sccfs.spCustomerChoiceChannelFixture.spStyleChannelFixture.spFineLineChannelFixture.fixtureTypeRollUp.fixtureTypeRollupName as fixtureTypeRollupName, " +
			"sccfs.merchMethodCode as merchMethod, " + "sccfs.ahsSizeDesc, " +
			"sccfs.storeObj, " + "fp.maxUnitsPerPack as maxUnitsPerPack, " + "fp.maxNbrOfPacks as maxNbrOfPacks, " + "ccp.factoryId as factoryId, "+"ccp.colorCombination as colorCombination, "+""
					+ "ccp.singlePackInd as singlePackInd) " +
			"from SpCustomerChoiceChannelFixtureSize sccfs " + 
			"left join " + "FineLinePackOptimization fp " + "ON " +
			"sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId  = fp.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.planId " +
			"AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr = fp.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 " +
			"AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr = fp.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 " +
			"AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr  = fp.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 " +
			"AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr = fp.finelinePackOptId.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 " +
			"AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr = fp.finelinePackOptId.subCatgPackOptimizationID.repTLvl4 " +
			"AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr = fp.finelinePackOptId.finelineNbr " +
			"AND  sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = fp.channelText.channelId " +
			
			"left join " + "CcPackOptimization ccp " + "ON " +
			"sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.planId " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl0Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl0 " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl1Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl1 " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl2Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl2 " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl3Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.merchantPackOptimizationID.repTLvl3 " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.lvl4Nbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.subCatgPackOptimizationID.repTLvl4 " + 
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr = ccp.ccPackOptimizationId.stylePackOptimizationID.finelinePackOptimizationID.finelineNbr " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.styleNbr= ccp.ccPackOptimizationId.stylePackOptimizationID.styleNbr " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.customerChoice = ccp.ccPackOptimizationId.customerChoice " +
	      "AND sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.channelId = ccp.channelText.channelId " +
	      
	      "where sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId = :planId and " +
			"sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr =:finelineNbr and " +
			"(sccfs.initialSetQty + sccfs.bumpPackQty > 0 OR sccfs.buyQty > 0)")
	List<FineLinePackOptimizationResponseDTO>getPackOptByFineline(@Param("planId") Long planId,@Param("finelineNbr")Integer finelineNbr);



}
