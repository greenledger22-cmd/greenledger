package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "scope1_factor_data")
@Getter
@Setter
public class Scope1FactorData {

    @Id
    private String id;

    private String fuelName;
    private String fuelType;
    private String unit;
    private double co2eTotal;

    private double co2Factor;
    private double ch4Factor;
    private double n2oFactor;
    //private String facilityName;
    private EmissionStandard emissionStandard;
    private String year;
    private String convertTo;
    private String creationDateString;
    private String UpdateDateString;
    private String Updatedby;



}
