package redswitch.greenledger.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;

@Document(collection="user")
public class User {
    @Id
    String id;
    String name;
    String email;
    String company;
}
