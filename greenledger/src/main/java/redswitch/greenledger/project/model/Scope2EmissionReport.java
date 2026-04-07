package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Scope2EmissionReport {


    private String emissionType;
    private Double quantityConsume;
    private Double cost;
    private String unit;
    private String provider;
    private String yearMonth;

    private LocalDate reportDate;
}
