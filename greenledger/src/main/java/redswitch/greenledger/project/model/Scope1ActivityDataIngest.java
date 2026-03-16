package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "scope1_activity_data")
public class Scope1ActivityDataIngest {
    @Id
    private String id;

    private String fuelName;
    private String fuelType;
    private double quantity;
    private String unit;





}
