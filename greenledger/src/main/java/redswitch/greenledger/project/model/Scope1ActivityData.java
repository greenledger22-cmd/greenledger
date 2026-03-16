package redswitch.greenledger.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "scope1_activity_data")
public class Scope1ActivityData {
    @Id
    private String id;

    private String fuelName;
    private String fuelType;
    private double quantity;
    private String unit;

}
