package redswitch.greenledger.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "scope2_activity_data")
public class Scope2ActivityDataIngest {

    @Id
    private String id;

    //private String emissionCategory;
   // private String emissionType;
    private double quantityConsume;
    private double outPutQuantityConsume;
    private String unit;
    private String fuelName;
    //private double amount;
   // private String amountType;
    private String outputUnit;
    private Double cost;
    private String facilityName;
    private String orgName;
    private String email;
    private String yearMonth;
    private String year;
    private int status;
    private String errorMsg;
    private LocalDate createDate;
    private LocalDate reportDate;

    private LocalDate updateDate;
    private String updatedby;


}
