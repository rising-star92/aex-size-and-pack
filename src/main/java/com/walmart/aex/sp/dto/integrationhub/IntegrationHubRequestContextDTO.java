package com.walmart.aex.sp.dto.integrationhub;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationHubRequestContextDTO {
    private String getPackOptFinelineDetails;
    private String updatePackOptFinelineStatus;
    private Long planId;
    private List<String> finelineNbrs;
    private String env;
}