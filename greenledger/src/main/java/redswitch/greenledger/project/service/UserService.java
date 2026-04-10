package redswitch.greenledger.project.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.model.JwtUtil;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.http.HttpStatus.*;

@Service
public class UserService {

    private final BCryptPasswordEncoder encoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder encoder,
                       JwtUtil jwtUtil,EmailService emailService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService=emailService;
    }
    final Map<String, String> otpMap = new HashMap<>();
    public ResponseEntity<ApiResponse> addUser(User  user){

        if (user!=null && !user.toString().isEmpty()){
            String generatedId = user.getCompany().replace(" ","_") + "_" + user.getEmail().replace("@","_").replace(".","_") +"_" + user.getName();

            LocalDate today=LocalDate.now();
            user.setCreationDate(today.getYear()+"_"+today.getMonth());
            user.setUpdateDate(today.getYear()+"_"+today.getMonth());
            user.setId(generatedId);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            String rawPassword = user.getPassword();

            // Encode (hash)
            String encodedPassword = encoder.encode(rawPassword);
            user.setPassword(encodedPassword.trim());

            Optional<User> exists= userRepository.findByEmail(user.getEmail());
            if (exists.isPresent() && exists.get().getEmail().trim().equals(user.getEmail().trim()))
                return ResponseEntity.status(CONFLICT)
                        .body(new ApiResponse("Email already exists", CONFLICT.value(), exists.get().getEmail()));



            userRepository.insert(user);
        }


        return   ResponseEntity.ok(
                new ApiResponse("user added successfully", HttpStatus.CREATED.value(), user));
    }

    public ResponseEntity<ApiResponse> getAllUser(String userName){
                if(userName!=null && !userName.isBlank() ){
                    return  ResponseEntity.ok(

                            new ApiResponse("Success", HttpStatus.OK.value(),
                                    Collections.singletonList(userRepository.findByName(userName))

                            ));
                }

        return  ResponseEntity.ok(
                new ApiResponse("Success", HttpStatus.OK.value(), userRepository.findAll()));
    }



    public ResponseEntity<ApiResponse> getUserById(String userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return     ResponseEntity.ok(
                new ApiResponse("Found user", OK.value(), user));

    }



    public ResponseEntity<ApiResponse> updateUser(User  user,String userId,String email){

        Optional<User> userExist = userRepository.findById(userId);
        if (userExist.isPresent()) {
            User userDb=userExist.get();
            LocalDate today = LocalDate.now();
            userDb.setRole(user.getRole());
            userDb.setName(user.getName());
            userDb.setUserName(user.getUserName());
            userDb.setName(user.getName());
            userDb.setUpdateDate(today.getYear() + "_" + today.getMonth());
            userDb.setUpdatedBy(email);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isMatch = encoder.matches(user.getPassword().trim(), userDb.getPassword().trim());

            if (isMatch){
                return ResponseEntity.ok(
                        new ApiResponse("same password", HttpStatus.NOT_ACCEPTABLE.value(), userRepository.findAll()));

                }
            else {
                userDb.setPassword(encoder.encode(user.getPassword()));
                userRepository.save(userDb);
            }
           // System.out.println(userDb.getId());
        }else return ResponseEntity.ok(
                new ApiResponse("User not found", HttpStatus.NOT_FOUND.value(), null));

        return ResponseEntity.ok(
                new ApiResponse("user updated successfully ", HttpStatus.CREATED.value(), null));
    }

    public ResponseEntity<ApiResponse> login(String userName, String email,String password) {

        Optional<User> userData;
        if (email != null) {
            userData = userRepository.findByEmail(email);
        } else {
            userData = userRepository.findByUserName(userName);
        }

        if (!userData.isPresent() ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("User not found", HttpStatus.NOT_FOUND.value(), null));
        }
        User user=userData.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new ApiResponse("Invalid password", HttpStatus.UNAUTHORIZED.value(), user.getPassword()));
        }

        String token="";
        //jwt token
        if (email!=null && !email.isEmpty())
             token = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name()
            );
        else if(userName!=null && !userName.isEmpty())
            token = jwtUtil.generateToken(
                    user.getUserName(),
                    user.getRole().name()
            );



        //System.out.println("delete api Issued At: " + new Date());
        ///System.out.println("Expiry: " + expirationDate);
        return ResponseEntity.status(OK)
                .body(new ApiResponse("User authenticated", OK.value(),token ));
    }

    public ResponseEntity<ApiResponse> delete(String id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("User not found with id:"+ id, NOT_FOUND.value() , id));
        }
        //System.out.println(" delete api Server time: " + new Date());
        userRepository.deleteById(id);
        return ResponseEntity.ok(new ApiResponse( "User deleted successfully", OK.value(), id));
    }

    public ResponseEntity<ApiResponse> sendOtp(String email) {
        System.out.println("otp");
        String otp = emailService.generateOtp();

        emailService.saveOtp(otpMap,email,otp);
        emailService.sendOtp(email,otp);
        return ResponseEntity.ok(new ApiResponse( "Otp send successfully", OK.value(), email));
    }

    public ResponseEntity<ApiResponse> verifyOtp(String email,String otp) {

        emailService.getOtp(otpMap,email);
        emailService.sendOtp(email,otp);
        return ResponseEntity.ok(new ApiResponse( "Otp validated successfully", OK.value(), email));
    }






    }






