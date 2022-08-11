package com.walmart.aex.sp.dto.integrationhub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationHubResponseDTO {
     private String completed_time;
     private String errorMsg;
     private String jobId;
     private List<String> notification_recipients;
     private int progress;
     private String scenario;
     private String started_time;
     private String status;
     private String submitted_time;
     private WFAttributes wf_attributes;
     private String wf_running_id;
}
