package redswitch.greenledger.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "scope1_emissions_report")
public class Scope1EmissionReport {


    @Id
    private String id;

    private String companyId;
    private String companyName;
    private String facilityId;
    private String sourceType;
    private String sourceName;
    private String fuelType;

    private Scope1ActivityData activityData;
//    private EmissionFactor emissionFactor;
//    private EmissionResult emissionResult;
//
//    private ReportingPeriod reportingPeriod;
    private String reportDate;
    private Instant createdAt;





}
