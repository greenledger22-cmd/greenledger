package redswitch.greenledger.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.repository.UserRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }



    public ResponseEntity<String > addUser(User  user){

        if (user!=null && !user.toString().isEmpty()){
            String generatedId = user.getCompany().replace(" ","_") + "_" + user.getEmail().replace("@","_").replace(".","_") +"_" + user.getName();

            LocalDate today=LocalDate.now();
            user.setCreationDate(today.getYear()+"_"+today.getMonth());
            user.setUpdateDate(today.getYear()+"_"+today.getMonth());
            user.setId(generatedId);


            userRepository.insert(user);
        }
        return   ResponseEntity.status(HttpStatus.CREATED).body( "user added successfully ");
    }

    public ResponseEntity<List<User>> getAllUser(String userName){
                if(userName!=null && !userName.isBlank() ){
                    return  ResponseEntity.ok(Collections.singletonList(userRepository.findByName(userName)));
                }

        return   ResponseEntity.ok(userRepository.findAll());
    }



    public ResponseEntity<User> getUserById(String userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return   ResponseEntity.ok(user);
    }



    public ResponseEntity<String > updateUser(User  user,String userId){

        Optional<User> userExist = userRepository.findById(userId);
        if (userExist.isPresent()) {
            User userDb=userExist.get();
            LocalDate today = LocalDate.now();
            userDb.setRole(user.getRole());
            userDb.setUpdateDate(today.getYear() + "_" + today.getMonth());
            userRepository.save(userDb);
           // System.out.println(userDb.getId());
        }else return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found");

        return   ResponseEntity.status(HttpStatus.CREATED).body( "user updated successfully ");
    }

}
