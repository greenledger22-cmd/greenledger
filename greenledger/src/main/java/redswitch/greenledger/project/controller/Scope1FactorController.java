package redswitch.greenledger.project.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.Scope1FactorData;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.service.Scope1FactorService;
import redswitch.greenledger.project.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/factor")
public class Scope1FactorController {


    private static final Logger logger = LoggerFactory.getLogger(Scope1FactorController.class);

    private final Scope1FactorService scope1FactorService;

    public Scope1FactorController(Scope1FactorService scope1FactorService) {
        this.scope1FactorService = scope1FactorService;
    }


    @PostMapping("/addFactor")
    public ResponseEntity<String> addFactorData(@RequestHeader("email") String email,
                                          @RequestBody Scope1FactorData scope1FactorData) {

        if (email == null || email.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("user email can't be empty");
        else if (scope1FactorData.getFuelName().isEmpty() || scope1FactorData.getFuelType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("factor name or factor type can't be empty");
        }

        return scope1FactorService.addFactor(scope1FactorData);
    }

//can find by name or find by fuel type any of these two.
    @GetMapping("/getFactor")
    public ResponseEntity<List<Scope1FactorData>> getFactorData(@RequestHeader("email") String email,
                                                                @RequestParam(required = false) String fuelName, @RequestParam(required = false) String fuelType) {



        return scope1FactorService.getFactorData(fuelName,fuelType);
    }









}
