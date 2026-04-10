package redswitch.greenledger.project.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.ApiResponse;
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
    public  ResponseEntity<ApiResponse> addFactorData(
                                                      @RequestBody Scope1FactorData scope1FactorData) {


        if (scope1FactorData.getFuelName().isEmpty() || scope1FactorData.getFuelType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ApiResponse("failure", HttpStatus.NOT_ACCEPTABLE.value(), "factor name or factor type can't be empty"));


        }

        return scope1FactorService.addFactor(scope1FactorData);
    }


    @PostMapping("/updateFactor")
    public  ResponseEntity<ApiResponse> updateFactorData(@RequestHeader("email") String email,
                                                @RequestBody Scope1FactorData scope1FactorData) {


        if (scope1FactorData.getFuelName().isEmpty() || scope1FactorData.getFuelType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ApiResponse("failure", HttpStatus.NOT_ACCEPTABLE.value(), "factor name or factor type can't be empty"));

        }

        return scope1FactorService.updateFactor(scope1FactorData);
    }






//can find by name or find by fuel type any of these two.
    @GetMapping("/getFactor")
    public  ResponseEntity<ApiResponse> getFactorData(@RequestParam(required = false) String fuelName,
                                                      @RequestParam(required = false) String fuelType) {



        return scope1FactorService.getFactorData(fuelName,fuelType);
    }









}
