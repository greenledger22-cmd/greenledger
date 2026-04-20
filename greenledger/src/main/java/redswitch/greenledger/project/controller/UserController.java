package redswitch.greenledger.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.parser.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.dto.LoginRequest;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.service.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }


    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse> addUser(@RequestBody User user,
                                          @RequestHeader("email") String email){


        if (user.getName()==null || user.getName().isEmpty())
            return ResponseEntity.status(NOT_ACCEPTABLE)
                    .body(new ApiResponse("user name can't be empty", NOT_ACCEPTABLE.value(), user.getName()));

        if (email==null || email.isEmpty())
            return ResponseEntity.status(NOT_ACCEPTABLE)
                .body(new ApiResponse("user email can't be empty", NOT_ACCEPTABLE.value(), user.getEmail()));

        else user.setEmail(email);


        return userService.addUser(user);
    }
    //@PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/getAllUser")
    public ResponseEntity<ApiResponse> getUser(@RequestParam(value = "name", required = false) String name){

//        if(name==null || name.isBlank() ){
//            return   ResponseEntity.badRequest()
//                    .body(new ApiResponse("Fail", HttpStatus.BAD_REQUEST.value(),"Name can't be blank"));
//        }
        return userService.getAllUser(name);
    }



    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody User user,
                                          @RequestHeader("email") String email,
                                          @PathVariable("id") String userId){



        return userService.updateUser(user, userId,email);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String id) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());

        return userService.delete(id);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {



        return userService.login(request.getUserName(), request.getEmail(), request.getPassword());
    }

    @PostMapping("/sendOtp")
    public ResponseEntity<ApiResponse> sendOtpEmail(
                                               @RequestHeader("email") String email
                                        ){

        return userService.sendOtp(email);
    }
    @PostMapping("/verifyOtp")
    public ResponseEntity<ApiResponse> sendOtp(
            @RequestHeader("email") String email,@RequestHeader("otp") String otp){

        return userService.verifyOtp(email,otp);
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Token not found / No Bearer", HttpStatus.NOT_FOUND.value(), null));
        }



//        return ResponseEntity.ok("Logged out successfully");

        return userService.logoutUser(authHeader);
    }

    }