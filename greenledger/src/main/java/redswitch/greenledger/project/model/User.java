package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
@Getter
@Setter
@Document(collection="user")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String company;
    private String org_id;
    private String role;
    private String creationDate;
    private String updateDate;
    private String userName;
    private String password;



}
