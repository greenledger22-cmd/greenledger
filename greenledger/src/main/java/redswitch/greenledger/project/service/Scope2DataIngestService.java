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
import redswitch.greenledger.project.model.Scope2ActivityDataIngest;
import redswitch.greenledger.project.repository.Scope1DataIngestRepository;
import redswitch.greenledger.project.repository.Scope2DataIngestRepository;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Service
public class Scope2DataIngestService {

    private static final Logger logger = LoggerFactory.getLogger(Scope2DataIngestService.class);
    @Autowired
    MongoTemplate mongoTemplate = null;
    private final Scope2DataIngestRepository scope2DataIngestRepository;

    public Scope2DataIngestService(Scope2DataIngestRepository scope2DataIngestRepository
                                   //Scope1EmissionReportService scope1EmissionReportService
    ) {
        this.scope2DataIngestRepository = scope2DataIngestRepository;
        //this.scope1EmissionReportService = scope1EmissionReportService;
    }

    public ResponseEntity<String> ingest(Scope2ActivityDataIngest scope2ActivityDataIngest){
        try {
            Optional<Scope2ActivityDataIngest> scope2ActivityDataIngest1=scope2DataIngestRepository
                    .findByEmissionTypeAndYearMonth(scope2ActivityDataIngest.getEmissionType(),scope2ActivityDataIngest.getYearMonth());

            if (scope2ActivityDataIngest1.isPresent()){
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Fuel type/ fuel name already exists");
            }else {
                try {
                    LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
                    scope2ActivityDataIngest.setCreateDate(todayUtc);
                    scope2ActivityDataIngest.setUpdateDate(todayUtc);
                    LocalDate date = LocalDate.parse(scope2ActivityDataIngest.getReportDate().toString());
//                    Instant endOfDay = date
//                            .atTime(LocalTime.MAX)   // 23:59:59.999999999
//                            .toInstant(ZoneOffset.UTC);
                    System.out.println(date +"  "+ scope2ActivityDataIngest.getReportDate());
                    scope2ActivityDataIngest.setReportDate(date);
                    scope2DataIngestRepository.insert(scope2ActivityDataIngest);
                }catch (Exception e){
                    logger.error(e.getMessage());
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Unable to save data");
                }
            }

            //scope1EmissionReportService.newReport(scope1ActivityDataIngest.getFuelName(),scope1ActivityDataIngest.getFuelType(),scope1ActivityDataIngest.getYearMonth());

        }catch (Exception e){
            logger.error(e.getMessage());
            return   ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Unable to save data");
        }
        return   ResponseEntity.status(HttpStatus.CREATED).body(scope2ActivityDataIngest.getEmissionType()+" added successfully");

    }


    public ResponseEntity<String> update(Scope2ActivityDataIngest scope2ActivityDataIngest){
        try {
            Optional<Scope2ActivityDataIngest> scope2ActivityDataIngest1=scope2DataIngestRepository.findById(scope2ActivityDataIngest.getId());
            if (scope2ActivityDataIngest1.isPresent()) {
                Scope2ActivityDataIngest updateIngest=scope2ActivityDataIngest1.get();
                updateIngest.setAmount(scope2ActivityDataIngest.getAmount());
                updateIngest.setQuantityConsume(scope2ActivityDataIngest.getQuantityConsume());
                updateIngest.setEmissionType(scope2ActivityDataIngest.getEmissionType());
                updateIngest.setUnit(scope2ActivityDataIngest.getUnit());
                updateIngest.setYearMonth(scope2ActivityDataIngest.getYearMonth());
                scope2DataIngestRepository.save(updateIngest);

            }
            else  ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("No data found  to update ");


        }catch (Exception e){
            logger.error(e.getMessage());
            return   ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Unable to update data");
        }
        return   ResponseEntity.status(HttpStatus.CREATED).body(scope2ActivityDataIngest.getEmissionType()+"updated successfully");

    }

    public ResponseEntity<List<Scope2ActivityDataIngest>> getIngestedData(String emissionType,String yearMonth){
        List<Scope2ActivityDataIngest> allData=new ArrayList<>();
        try {

            allData = getAllData(emissionType, yearMonth);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return  ResponseEntity.status(HttpStatus.OK).body(allData);

    }
    public List<Scope2ActivityDataIngest> getAllData(String emissionType, String yearMonth){

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (emissionType != null && !emissionType.isEmpty()) {
            criteriaList.add(Criteria.where("emissionType").regex(emissionType, "i"));
        }


        if (yearMonth != null && !yearMonth.isEmpty()) {
            criteriaList.add(Criteria.where("yearMonth").regex(yearMonth, "i"));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().orOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Scope2ActivityDataIngest.class);


    }





}
