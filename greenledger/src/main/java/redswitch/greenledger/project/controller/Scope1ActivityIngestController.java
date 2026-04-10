package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.service.Scope1DataIngestService;
import redswitch.greenledger.project.service.Scope1FactorService;

import java.util.List;

@RestController
@RequestMapping("/scope1Ingest")
public class Scope1ActivityIngestController {


    private static final Logger logger = LoggerFactory.getLogger(Scope1ActivityIngestController.class);

    private final Scope1DataIngestService scope1DataIngestService;

    public Scope1ActivityIngestController(Scope1DataIngestService scope1DataIngestService) {
        this.scope1DataIngestService = scope1DataIngestService;
    }

    @PostMapping("/ingestEmission")
    public ResponseEntity<String> addData(@RequestBody Scope1ActivityDataIngest scope1ActivityDataIngest,
                                          @RequestHeader("email") String email){


        if (scope1ActivityDataIngest.getFuelName()==null || scope1ActivityDataIngest.getFuelName().isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("fuel name can't be empty");

        scope1ActivityDataIngest.setEmail(email);

        return scope1DataIngestService.ingest(scope1ActivityDataIngest);
    }



    @PostMapping("/updateEmission")
    public ResponseEntity<String> updateEmission(@RequestBody Scope1ActivityDataIngest scope1ActivityDataIngest,
                                          @RequestHeader("email") String email,
                                                 @RequestParam(required = false)String id){


        if (scope1ActivityDataIngest.getFuelName()==null || scope1ActivityDataIngest.getFuelName().isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("fuel name can't be empty");

        scope1ActivityDataIngest.setEmail(email);
        scope1ActivityDataIngest.setId(id);
        return scope1DataIngestService.update(scope1ActivityDataIngest);
    }

    @GetMapping("/getAllIngest")
    public ResponseEntity<List<Scope1ActivityDataIngest>> getEmission(
                                            @RequestHeader("email") String email,
                                            @RequestParam(required = false)String fuelName,
                                            @RequestParam(required = false)String fuelType,
                                            @RequestParam(required = false)String yearMonth){



        //if (fuelName==null || scope1ActivityDataIngest.getFuelName().isEmpty())
           // return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("fuel name can't be empty");

        return scope1DataIngestService.getIngestedData(fuelName,fuelType,yearMonth);
    }




}
