package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }


    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@RequestBody User user,
                                          @RequestHeader("email") String email){


        if (user.getName()==null || user.getName().isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("user name can't be empty");
        if (email==null || email.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("user email can't be empty");
        else user.setEmail(email);


        return userService.addUser(user);
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<List<User>> getUser(@RequestParam("id") String userName){

        return userService.getAllUser(userName);
    }
//    @GetMapping("/getUserById/{id}")
//    public ResponseEntity<User> getById( @PathVariable("id") String userId){
//
//        return userService.getUserById(userId);
//    }


    @PostMapping("/updateUser/{id}")
    public ResponseEntity<String> updateUser(@RequestBody User user,
                                          @RequestHeader("email") String email,
                                          @PathVariable("id") String userId){



        return userService.updateUser(user, userId);
    }

    }