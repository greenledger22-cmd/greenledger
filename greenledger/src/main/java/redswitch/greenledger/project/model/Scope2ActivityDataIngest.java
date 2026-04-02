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
    private String emissionType;
    private double quantityConsume;
    private String unit;
    private double amount;
    private String amountType;
    private String outputUnit;
    private String email;
    private String yearMonth;
    private int status;
    private String errorMsg;
    private LocalDate createDate;
    private LocalDate reportDate;
    private LocalDate updateDate;


}
