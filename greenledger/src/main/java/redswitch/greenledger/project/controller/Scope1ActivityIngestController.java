package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.service.Scope1DataIngestService;
import redswitch.greenledger.project.service.Scope1FactorService;

@RestController
@RequestMapping("/scope1Ingest")
public class Scope1ActivityIngestController {


    private static final Logger logger = LoggerFactory.getLogger(Scope1ActivityIngestController.class);

    private final Scope1DataIngestService scope1DataIngestService;

    public Scope1ActivityIngestController(Scope1DataIngestService scope1DataIngestService) {
        this.scope1DataIngestService = scope1DataIngestService;
    }

    @PostMapping("/ingestEmission")
    public ResponseEntity<String> addUser(@RequestBody User user,
                                          @RequestHeader("email") String email){


        if (user.getName()==null || user.getName().isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("user name can't be empty");
        if (email==null || email.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("user email can't be empty");
        else user.setEmail(email);


        return userService.addUser(user);
    }






}
