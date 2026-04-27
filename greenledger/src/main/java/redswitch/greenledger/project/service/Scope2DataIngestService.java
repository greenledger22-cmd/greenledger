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
import redswitch.greenledger.project.model.Scope2ActivityDataIngest;
import redswitch.greenledger.project.model.Scope2Factor;
import redswitch.greenledger.project.repository.Scope1DataIngestRepository;
import redswitch.greenledger.project.repository.Scope2DataIngestRepository;
import redswitch.greenledger.project.repository.Scope2FactorRepository;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Service
public class Scope2DataIngestService {

    private static final Logger logger = LoggerFactory.getLogger(Scope2DataIngestService.class);
    @Autowired
    MongoTemplate mongoTemplate = null;
    private final Scope2FactorRepository scope2FactorRepository;
    private final Scope2DataIngestRepository scope2DataIngestRepository;
    private final Scope2EmissionReportService scope2EmissionReportService;
    public Scope2DataIngestService(Scope2DataIngestRepository scope2DataIngestRepository,Scope2EmissionReportService scope2EmissionReportService,
                                   Scope2FactorRepository scope2FactorRepository) {
        this.scope2DataIngestRepository = scope2DataIngestRepository;
        this.scope2EmissionReportService = scope2EmissionReportService;
        this.scope2FactorRepository=scope2FactorRepository;
    }

        public ResponseEntity<ApiResponse> ingest(Scope2ActivityDataIngest scope2ActivityDataIngest){
            try {
                Optional<Scope2ActivityDataIngest> scope2ActivityDataIngest1=scope2DataIngestRepository
                        .findByYearMonthAndOrgNameAndFacilityNameAndFuelNameAndStatus(scope2ActivityDataIngest.getYearMonth(),scope2ActivityDataIngest.getOrgName(),scope2ActivityDataIngest.getFacilityName(),scope2ActivityDataIngest.getFuelName(),10);
                Scope2ActivityDataIngest saved;
                if (scope2ActivityDataIngest1.isPresent()){
                    return  ResponseEntity.status(CONFLICT)
                            .body(new ApiResponse("Failure", CONFLICT.value(), "Fuel type/ fuel name already exists"));

                }else {

                        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
                        if (scope2ActivityDataIngest.getUnit().equalsIgnoreCase("kwh")){
                            scope2ActivityDataIngest.setOutPutQuantityConsume(scope2ActivityDataIngest.getQuantityConsume()/1000);
                            scope2ActivityDataIngest.setOutputUnit("mwh");
                        }else {
                            scope2ActivityDataIngest.setOutputUnit("mwh");
                            scope2ActivityDataIngest.setOutPutQuantityConsume(scope2ActivityDataIngest.getQuantityConsume());
                        }



                        scope2ActivityDataIngest.setCreateDate(todayUtc);
                        scope2ActivityDataIngest.setUpdateDate(todayUtc);
                        scope2ActivityDataIngest.setYear(scope2ActivityDataIngest.getYearMonth().split("-")[0]);
                        scope2ActivityDataIngest.setStatus(0);
                       // LocalDate date = LocalDate.parse(scope2ActivityDataIngest.getReportDate().toString());
    //                    Instant endOfDay = date
    //                            .atTime(LocalTime.MAX)   // 23:59:59.999999999
    //                            .toInstant(ZoneOffset.UTC);
                        //System.out.println(date +"  "+ scope2ActivityDataIngest.getReportDate());
                        //scope2ActivityDataIngest.setReportDate(date);


                     saved=scope2DataIngestRepository.insert(scope2ActivityDataIngest);

                }
                scope2EmissionReportService.newReport(saved);


            }catch (Exception e){
                logger.error(e.getMessage());
                return  ResponseEntity.status(CONFLICT)
                        .body(new ApiResponse("Failure", CONFLICT.value(), "Unable to save data"));

            }
            return  ResponseEntity.status(CREATED)
                    .body(new ApiResponse("Success", CREATED.value(), "Ingested successfully"));


        }


        public ResponseEntity<ApiResponse> update(Scope2ActivityDataIngest scope2ActivityDataIngest){
            try {
                Optional<Scope2ActivityDataIngest> scope2ActivityDataIngest1=scope2DataIngestRepository.findById(scope2ActivityDataIngest.getId());
                if (scope2ActivityDataIngest1.isPresent()) {
                    Scope2ActivityDataIngest updateIngest=scope2ActivityDataIngest1.get();
                   // updateIngest.setAmount(scope2ActivityDataIngest.getAmount());
                    updateIngest.setQuantityConsume(scope2ActivityDataIngest.getQuantityConsume());
                    //updateIngest.setEmissionType(scope2ActivityDataIngest.getEmissionType());
                    //updateIngest.setUnit(scope2ActivityDataIngest.getUnit());
                    updateIngest.setYearMonth(scope2ActivityDataIngest.getYearMonth());
                    updateIngest.setYear(scope2ActivityDataIngest.getYear());
                    LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
                    updateIngest.setUpdateDate(todayUtc);

                    scope2DataIngestRepository.save(updateIngest);

                }
                else
                    return ResponseEntity.status(NOT_IMPLEMENTED)
                            .body(new ApiResponse("failure", NOT_IMPLEMENTED.value(), "No data found  to update "));



            }catch (Exception e){
                logger.error(e.getMessage());
                return ResponseEntity.status(NOT_IMPLEMENTED)
                        .body(new ApiResponse("failure", NOT_IMPLEMENTED.value(), "Unable to update data"));

            }
            return  ResponseEntity.status(CREATED)
                    .body(new ApiResponse("Success", CREATED.value(), "updated successfully"));


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
    public ResponseEntity<ApiResponse> addfactor(Scope2Factor scope2Factor){

        scope2FactorRepository.insert(scope2Factor);
        return  ResponseEntity.status(CREATED)
                .body(new ApiResponse("Success", CREATED.value(), "scope 2 factor added successfully"));

    }
    public ResponseEntity<ApiResponse> getFactor(){


        return  ResponseEntity.status(OK)
                .body(new ApiResponse("Success", OK.value(), scope2FactorRepository.findAll()));

    }




}
