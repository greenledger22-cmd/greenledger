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
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.repository.Scope1DataIngestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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



    public ResponseEntity<String> ingest(Scope1ActivityDataIngest scope1ActivityDataIngest){
        try {
            Optional<Scope1ActivityDataIngest> scope1ActivityDataIngest1=scope1DataIngestRepository
                    .findByFuelNameAndFuelTypeAndYearMonthContainingIgnoreCase(scope1ActivityDataIngest.getFuelName(),scope1ActivityDataIngest.getFuelType(),scope1ActivityDataIngest.getYearMonth());

            if (scope1ActivityDataIngest1.isPresent()){
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Fuel type/ fuel name already exists");
            }else
               scope1DataIngestRepository.insert(scope1ActivityDataIngest);

            scope1EmissionReportService.newReport(scope1ActivityDataIngest.getFuelName(),scope1ActivityDataIngest.getFuelType(),scope1ActivityDataIngest.getYearMonth());

        }catch (Exception e){
            logger.error(e.getMessage());
            return   ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Unable to save data");
        }
        return   ResponseEntity.status(HttpStatus.CREATED).body("scope factor  "+scope1ActivityDataIngest.getFuelName()+" added successfully");

    }

    public ResponseEntity<String> update(Scope1ActivityDataIngest scope1ActivityDataIngest){
        try {
            Optional<Scope1ActivityDataIngest> scope1ActivityDataIngest1=scope1DataIngestRepository.findById(scope1ActivityDataIngest.getId());
            if (scope1ActivityDataIngest1.isPresent()) {
                Scope1ActivityDataIngest updateIngest=scope1ActivityDataIngest1.get();
                updateIngest.setQuantity(scope1ActivityDataIngest.getQuantity());
                updateIngest.setUnit(scope1ActivityDataIngest.getUnit());
                scope1DataIngestRepository.save(updateIngest);

            }
            else  ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("No data found  to update ");


        }catch (Exception e){
            logger.error(e.getMessage());
            return   ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Unable to update data");
        }
        return   ResponseEntity.status(HttpStatus.CREATED).body(scope1ActivityDataIngest.getFuelName()+"updated successfully");

    }

    public ResponseEntity<List<Scope1ActivityDataIngest>> getIngestedData(String fuelName, String fuelType, String yearMonth){
        List<Scope1ActivityDataIngest> allData=new ArrayList<>();
        try {

            allData = getAllData(fuelName, fuelType, yearMonth);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return  ResponseEntity.status(HttpStatus.OK).body(allData);

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
