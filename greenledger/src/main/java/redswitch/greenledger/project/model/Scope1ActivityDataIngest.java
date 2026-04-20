package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Document(collection = "scope1_activity_data")
public class Scope1ActivityDataIngest {
    @Id
    private String id;

    private String fuelName;
    private String fuelType;
    private double quantity;
    private double cost;
    private String facilityName;
    private String orgName;
    private String unit;
    private String email;
    private String yearMonth;
    private int status;
    private String errorMsg;
    private String year;

    private LocalDate createDate;
    private LocalDate updateDate;






}
