package com.walmart.aex.sp.repository;

import com.walmart.aex.sp.dto.appmessage.ValidationResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.entity.AppMessageText;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppMessageTextRepository extends JpaRepository<AppMessageText, Integer> {

    @Query(value = "select new com.walmart.aex.sp.dto.appmessage.ValidationResponseDTO( amt.id as code, amtt.desc as type, amt.longDesc as message ) " +
            "FROM AppMessageText amt " +
            "INNER JOIN AppMessageType amtt " +
            "ON amt.appMessageType=amtt.id ")
    List<ValidationResponseDTO> getValidationsByAppMessageCodes();

}
