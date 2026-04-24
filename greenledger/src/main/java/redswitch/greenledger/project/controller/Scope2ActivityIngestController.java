package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.Scope2ActivityDataIngest;
import redswitch.greenledger.project.model.Scope2Factor;
import redswitch.greenledger.project.service.Scope1DataIngestService;
import redswitch.greenledger.project.service.Scope2DataIngestService;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@RestController
@RequestMapping("/scope2Ingest")
public class Scope2ActivityIngestController {
    private static final Logger logger = LoggerFactory.getLogger(Scope2ActivityIngestController.class);
    private final Scope2DataIngestService scope2DataIngestService;
    public Scope2ActivityIngestController(Scope2DataIngestService scope2DataIngestService) {
        this.scope2DataIngestService=scope2DataIngestService;
    }

    @PostMapping("/ingestEmission")
    public ResponseEntity<ApiResponse> addData(@RequestBody Scope2ActivityDataIngest scope2ActivityDataIngest,
                                          Authentication authentication){



        try {
            YearMonth inputYm = YearMonth.parse(scope2ActivityDataIngest.getYearMonth());
            YearMonth currentYm = YearMonth.now();

            if(inputYm.isAfter(currentYm)) {

                return ResponseEntity.status(NOT_ACCEPTABLE)
                        .body(new ApiResponse("failure", NOT_ACCEPTABLE.value(), "Year can't be future"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        scope2ActivityDataIngest.setEmail(authentication.getName());

        //bulk  if true then upload csv or excel else
        return scope2DataIngestService.ingest(scope2ActivityDataIngest);
    }


    @PostMapping("/updateEmission/{id}")
    public ResponseEntity<ApiResponse> updateEmission(@RequestBody Scope2ActivityDataIngest scope2ActivityDataIngest,
                                                      Authentication authentication,
                                                      @RequestParam(required = false)String id){

        scope2ActivityDataIngest.setUpdatedby(authentication.getName());

        return scope2DataIngestService.update(scope2ActivityDataIngest);
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<Scope2ActivityDataIngest>> getAll(@RequestBody Scope2ActivityDataIngest scope2ActivityDataIngest,
                                                 @RequestHeader("email") String email,
                                         @RequestParam(required = false)String emissionType,
                                         @RequestParam(required = false)String yearMonth){


        return scope2DataIngestService.getIngestedData(emissionType,yearMonth);
    }

    @PostMapping("/addScope2Emission/version")
    public ResponseEntity<ApiResponse> addEmissionfactor (@RequestBody Scope2Factor scope2Factor,
                                                      Authentication authentication
                                                      ){

        scope2Factor.setAddedBy(authentication.getName());
        scope2Factor.setEmail(authentication.getName());

        return scope2DataIngestService.addfactor(scope2Factor);
    }

    @PostMapping("/getFactor/version")
    public ResponseEntity<ApiResponse> getFactor (@RequestBody Scope2Factor scope2Factor){


        return scope2DataIngestService.getFactor();
    }





}
