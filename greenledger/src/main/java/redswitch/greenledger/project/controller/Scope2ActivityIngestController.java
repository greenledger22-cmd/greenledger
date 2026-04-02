package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.Scope2ActivityDataIngest;
import redswitch.greenledger.project.service.Scope1DataIngestService;
import redswitch.greenledger.project.service.Scope2DataIngestService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/scope2Ingest")
public class Scope2ActivityIngestController {
    private static final Logger logger = LoggerFactory.getLogger(Scope2ActivityIngestController.class);
    private final Scope2DataIngestService scope2DataIngestService;
    public Scope2ActivityIngestController(Scope2DataIngestService scope2DataIngestService) {
        this.scope2DataIngestService=scope2DataIngestService;
    }

    @PostMapping("/ingestEmission")
    public ResponseEntity<String> addData(@RequestBody Scope2ActivityDataIngest scope2ActivityDataIngest,
                                          @RequestHeader("email") String email
                                          //@RequestParam(required = false)boolean bulk
                                            ){


        if (scope2ActivityDataIngest.getEmissionType()==null || scope2ActivityDataIngest.getEmissionType().isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("emission type can't be empty");

        scope2ActivityDataIngest.setEmail(email);

        //bulk  if true then upload csv or excel else
        return scope2DataIngestService.ingest(scope2ActivityDataIngest);
    }


    @PostMapping("/updateEmission")
    public ResponseEntity<String> updateEmission(@RequestBody Scope2ActivityDataIngest scope2ActivityDataIngest,
                                                 @RequestHeader("email") String email,
                                                 @RequestParam(required = false)String id){


        if (scope2ActivityDataIngest.getEmissionType()==null || scope2ActivityDataIngest.getEmissionType().isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("fuel name can't be empty");

        scope2ActivityDataIngest.setEmail(email);
        scope2ActivityDataIngest.setId(id);
        return scope2DataIngestService.update(scope2ActivityDataIngest);
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<Scope2ActivityDataIngest>> getAll(@RequestBody Scope2ActivityDataIngest scope2ActivityDataIngest,
                                                 @RequestHeader("email") String email,
                                         @RequestParam(required = false)String emissionType,
                                         @RequestParam(required = false)String yearMonth){


        return scope2DataIngestService.getIngestedData(emissionType,yearMonth);
    }







}
