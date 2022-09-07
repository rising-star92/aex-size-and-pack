package com.walmart.aex.sp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSizeId;


public interface FineLinePackOptimizationRepository  extends JpaRepository<SpCustomerChoiceChannelFixtureSize, SpCustomerChoiceChannelFixtureSizeId>{


	@Query(value="select new com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponseDTO"
			+ "(sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId as planId, " +
			"sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr as finelineNbr, " +
			"sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.customerChoice as ccId, " +
			"sccfs.spCustomerChoiceChannelFixture.spStyleChannelFixture.spFineLineChannelFixture.fixtureTypeRollUp.fixtureTypeRollupName as fixtureTypeRollupName, " +
			"sccfs.merchMethodCode as merchMethod, " +
			"sccfs.ahsSizeDesc, " +
			"sccfs.storeObj) " +
			"from SpCustomerChoiceChannelFixtureSize sccfs " +
			"where sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.planId = :planId and "
			+ "sccfs.spCustomerChoiceChannelFixtureSizeId.spCustomerChoiceChannelFixtureId.spStyleChannelFixtureId.spFineLineChannelFixtureId.fineLineNbr =:finelineNbr")
	List<FineLinePackOptimizationResponseDTO> getPackOptByFineline(@Param("planId") Long planId,@Param("finelineNbr") Integer finelineNbr);
}
