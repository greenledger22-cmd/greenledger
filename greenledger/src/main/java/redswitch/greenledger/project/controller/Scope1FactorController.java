package redswitch.greenledger.project.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/superAdmin/addFactor")
    public  ResponseEntity<ApiResponse> addFactorData(
                                                      @RequestBody Scope1FactorData scope1FactorData) {


        if (scope1FactorData.getFuelName().isEmpty() || scope1FactorData.getFuelType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ApiResponse("failure", HttpStatus.NOT_ACCEPTABLE.value(), "factor name or factor type can't be empty"));


        }

        return scope1FactorService.addFactor(scope1FactorData);
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/superAdmin/uploadFactor")
    public  ResponseEntity<ApiResponse> uploadFactorData(
            @RequestParam("file") MultipartFile file, Authentication authentication) {


        return scope1FactorService.uploadFactor(file,authentication);
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @PostMapping("/superAdmin/updateFactor/{id}")
    public  ResponseEntity<ApiResponse> updateFactorData(
                                                @RequestBody Scope1FactorData scope1FactorData,
                                                @PathVariable("id") String userId,Authentication authentication) {


        if (scope1FactorData.getFuelName().isEmpty() || scope1FactorData.getFuelType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(new ApiResponse("failure", HttpStatus.NOT_ACCEPTABLE.value(), "factor name or factor type can't be empty"));

        }

        return scope1FactorService.updateFactor(scope1FactorData,authentication,userId);
    }






//can find by name or find by fuel type any of these two.
    @GetMapping("/getFactor")
    public  ResponseEntity<ApiResponse> getFactorData(@RequestParam(required = false,value = "fuelName") String fuelName,
                                                      @RequestParam(required = false,value = "fuelType") String fuelType) {



        return scope1FactorService.getFactorData(fuelName,fuelType);
    }









}
