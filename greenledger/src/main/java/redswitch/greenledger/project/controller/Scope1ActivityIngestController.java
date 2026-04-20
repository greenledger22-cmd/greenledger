package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.service.Scope1DataIngestService;
import redswitch.greenledger.project.service.Scope1FactorService;
import org.springframework.security.core.Authentication;

import java.time.YearMonth;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/scope1Ingest")
public class Scope1ActivityIngestController {


    private static final Logger logger = LoggerFactory.getLogger(Scope1ActivityIngestController.class);

    private final Scope1DataIngestService scope1DataIngestService;

    public Scope1ActivityIngestController(Scope1DataIngestService scope1DataIngestService) {
        this.scope1DataIngestService = scope1DataIngestService;
    }

    @PostMapping("/ingestEmission")
    public ResponseEntity<ApiResponse> addData(@RequestBody Scope1ActivityDataIngest scope1ActivityDataIngest,
                                          //@RequestHeader("email") String email
                                                Authentication authentication
    ){

        String email = authentication.getName();  //  THIS IS  EMAIL

        try {
            YearMonth inputYm = YearMonth.parse(scope1ActivityDataIngest.getYearMonth());
            YearMonth currentYm = YearMonth.now();

           if(inputYm.isAfter(currentYm)) {

                ResponseEntity.status(NOT_ACCEPTABLE)
                        .body(new ApiResponse("failure", NOT_ACCEPTABLE.value(), "Year can't be future"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        scope1ActivityDataIngest.setEmail(email); //  set from token


        if (scope1ActivityDataIngest.getFuelName()==null || scope1ActivityDataIngest.getFuelName().isEmpty())
            return ResponseEntity.status(NOT_ACCEPTABLE)
                    .body(new ApiResponse("failure", NOT_ACCEPTABLE.value(), "fuel name can't be empty"));


        //scope1ActivityDataIngest.setEmail(email);

        return scope1DataIngestService.ingest(scope1ActivityDataIngest);
    }



    @PostMapping("/updateEmission")
    public ResponseEntity<ApiResponse> updateEmission(@RequestBody Scope1ActivityDataIngest scope1ActivityDataIngest,
                                         // @RequestHeader("email") String email,
                                                 @RequestParam(required = false)String id){


        if (scope1ActivityDataIngest.getFuelName()==null || scope1ActivityDataIngest.getFuelName().isEmpty())
            return ResponseEntity.status(NOT_ACCEPTABLE)
                    .body(new ApiResponse("failure", NOT_ACCEPTABLE.value(), "fuel name can't be empty"));


        //scope1ActivityDataIngest.setEmail(email);
        scope1ActivityDataIngest.setId(id);
        return scope1DataIngestService.update(scope1ActivityDataIngest);
    }

    @GetMapping("/getAllIngest")
    public ResponseEntity<ApiResponse> getEmission(

                                            @RequestParam(required = false)String fuelName,
                                            @RequestParam(required = false)String fuelType,
                                            @RequestParam(required = false)String yearMonth){



        //if (fuelName==null || scope1ActivityDataIngest.getFuelName().isEmpty())
           // return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("fuel name can't be empty");

        return scope1DataIngestService.getIngestedData(fuelName,fuelType,yearMonth);
    }




}
