package com.walmart.aex.sp.dto.packoptimization.sourcingFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Audit {
    @JsonProperty("FactoryId")
    private Integer factoryId;
    @JsonProperty("RequestId")
    private Integer requestId;
    @JsonProperty("CertTypeCode")
    private Integer certTypeCode;
    @JsonProperty("CertTypeDescription")
    private String certTypeDescription;
    @JsonProperty("AuditStatusCode")
    private Integer auditStatusCode;
    @JsonProperty("AuditStatusDescription")
    private String auditStatusDescription;
    @JsonProperty("AuditDate")
    private String auditDate;
    @JsonProperty("AssessmentDate")
    private String assessmentDate;
}
