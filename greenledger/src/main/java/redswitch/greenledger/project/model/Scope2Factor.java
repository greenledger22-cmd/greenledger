package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection="scope2_factor")
public class Scope2Factor {

    private String factorSource;
    private String version;
    private int year;
    private String inputUnit;
    private double factor;
    private String email;
    private String addedBy;
    private String updatedBy;


}
