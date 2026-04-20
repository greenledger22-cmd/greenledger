package redswitch.greenledger.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.repository.Scope1DataIngestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
public class Scope1DataIngestService {


    private static final Logger logger = LoggerFactory.getLogger(Scope1DataIngestService.class);
    @Autowired
    MongoTemplate mongoTemplate = null;
    private final Scope1DataIngestRepository scope1DataIngestRepository;
    private final Scope1EmissionReportService scope1EmissionReportService;
    public Scope1DataIngestService(Scope1DataIngestRepository scope1DataIngestRepository,
                                   Scope1EmissionReportService scope1EmissionReportService) {
        this.scope1DataIngestRepository = scope1DataIngestRepository;
        this.scope1EmissionReportService = scope1EmissionReportService;
    }



    public  ResponseEntity<ApiResponse> ingest(Scope1ActivityDataIngest scope1ActivityDataIngest){
        try {
            Optional<Scope1ActivityDataIngest> scope1ActivityDataIngest1=scope1DataIngestRepository
                    .findByFuelNameAndFuelTypeAndYearMonthContainingIgnoreCase(scope1ActivityDataIngest.getFuelName(),scope1ActivityDataIngest.getFuelType(),scope1ActivityDataIngest.getYearMonth());

            if (scope1ActivityDataIngest1.isPresent()){
                return  ResponseEntity.status(CONFLICT)
                        .body(new ApiResponse("Failure", CONFLICT.value(), "Fuel type/ fuel name already exists"));

            }else {
                scope1ActivityDataIngest.setYear(scope1ActivityDataIngest.getYearMonth().split("-")[0]);
                scope1DataIngestRepository.insert(scope1ActivityDataIngest);
            }

            scope1EmissionReportService.newReport(scope1ActivityDataIngest.getFuelName(),scope1ActivityDataIngest.getFuelType(),scope1ActivityDataIngest.getYearMonth(),scope1ActivityDataIngest.getUnit());

        }catch (Exception e){
            logger.error(e.getMessage());
            return   ResponseEntity.status(NOT_IMPLEMENTED)
                    .body(new ApiResponse("unextected error", NOT_IMPLEMENTED.value(), "Unable to save data"));

        }
        return   ResponseEntity.status(CREATED)
                .body(new ApiResponse("success", CREATED.value(), "report "+scope1ActivityDataIngest.getFuelName()+" ingested successfully"));

    }

    public  ResponseEntity<ApiResponse> update(Scope1ActivityDataIngest scope1ActivityDataIngest){
        try {
            Optional<Scope1ActivityDataIngest> scope1ActivityDataIngest1=scope1DataIngestRepository.findById(scope1ActivityDataIngest.getId());
            if (scope1ActivityDataIngest1.isPresent()) {
                Scope1ActivityDataIngest updateIngest=scope1ActivityDataIngest1.get();
                updateIngest.setQuantity(scope1ActivityDataIngest.getQuantity());
                updateIngest.setUnit(scope1ActivityDataIngest.getUnit());
                scope1DataIngestRepository.save(updateIngest);

            }
            else   ResponseEntity.status(NOT_IMPLEMENTED)
                    .body(new ApiResponse("failure", NOT_IMPLEMENTED.value(), "No data found  to update "));




        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(NOT_IMPLEMENTED)
                    .body(new ApiResponse("failure", NOT_IMPLEMENTED.value(), "Unable to update data"));


        }
        return  ResponseEntity.status(CREATED)
                .body(new ApiResponse("Success", CREATED.value(), scope1ActivityDataIngest.getFuelName()+"updated successfully"));


    }

    public ResponseEntity<ApiResponse> getIngestedData(String fuelName, String fuelType, String yearMonth){
        List<Scope1ActivityDataIngest> allData=new ArrayList<>();
        try {

            allData = getAllData(fuelName, fuelType, yearMonth);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return  ResponseEntity.status(OK)
                .body(new ApiResponse("success", OK.value(), allData));


    }



    public  List<Scope1ActivityDataIngest> getAllData(String fuelName, String fuelType, String yearMonth){

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (fuelName != null && !fuelName.isEmpty()) {
            criteriaList.add(Criteria.where("fuelName").is(fuelName));
        }

        if (fuelType != null && !fuelType.isEmpty()) {
            criteriaList.add(Criteria.where("fuelType").is(fuelType));
        }

        if (yearMonth != null && !yearMonth.isEmpty()) {
            criteriaList.add(Criteria.where("yearMonth").regex(yearMonth, "i"));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Scope1ActivityDataIngest.class);


    }






}
