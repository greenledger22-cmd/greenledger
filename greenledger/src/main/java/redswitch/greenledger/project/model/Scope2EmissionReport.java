package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "scope2_emissions_report")
public class Scope2EmissionReport {


    private String emissionType;
    private Double quantityConsume;
    private Double cost;
    private String unit;
    private String fuelName;
    private String outputUnit;
    private double co2eTotal;
    private String facilityName;
    private String orgName;
    private Scope2Factor scope2Factor;
    private String provider;
    private String yearMonth;
    private String ingest_reference_id;
    private String year;
    private String createDateString;
    private LocalDate reportDate;
}
