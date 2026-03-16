package redswitch.greenledger.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "scope1_fuel_factor")

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


    private String convertTo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFuelName() {
        return fuelName;
    }

    public void setFuelName(String fuelName) {
        this.fuelName = fuelName;
    }

    public String getFuelType() {
        return fuelType;
    }

    public String getConvertTo() {
        return convertTo;
    }

    public void setConvertTo(String convertTo) {
        this.convertTo = convertTo;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getCo2eTotal() {
        return co2eTotal;
    }

    public void setCo2eTotal(double co2eTotal) {
        this.co2eTotal = co2eTotal;
    }

    public double getCo2Factor() {
        return co2Factor;
    }

    public void setCo2Factor(double co2Factor) {
        this.co2Factor = co2Factor;
    }

    public double getCh4Factor() {
        return ch4Factor;
    }

    public void setCh4Factor(double ch4Factor) {
        this.ch4Factor = ch4Factor;
    }

    public double getN2oFactor() {
        return n2oFactor;
    }

    public void setN2oFactor(double n2oFactor) {
        this.n2oFactor = n2oFactor;
    }


}
