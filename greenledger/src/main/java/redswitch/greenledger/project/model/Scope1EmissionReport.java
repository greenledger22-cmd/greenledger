package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "scope1_emissions_report")
public class Scope1EmissionReport {


    @Id
    private String id;

//    private String companyId;
//    private String companyName;
    private String facilityName;
//    private String sourceType;
    private String fuelName;
    private String fuelType;
    private double co2eTotal;

    private double co2Factor;
    private double ch4Factor;
    private double n2oFactor;

    private Scope1ActivityDataIngest activityData;
    private Scope1FactorData scope1FactorData;

    private String reportDate;
//    private Instant createDate;
    private String createDateString;
//    private Instant updateDate;
    private String updateDateString;

    private String inputUnit;
    private String outputUnit;


}
