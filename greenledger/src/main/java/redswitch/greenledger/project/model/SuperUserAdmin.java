package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@Document(collection="superUser")
public class SuperUserAdmin {
    @Id
    private String id;
    private String name;
    private String email;
    private String userName;
    private String password;
    private String phone;
    private Role role;
    private String lastLogin;
    private LocalDate lastLoginDate;




}
