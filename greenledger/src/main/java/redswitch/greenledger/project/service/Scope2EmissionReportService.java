package redswitch.greenledger.project.service;


import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import redswitch.greenledger.project.model.*;
import redswitch.greenledger.project.repository.*;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Service
public class Scope2EmissionReportService {
    @Autowired
    private MongoTemplate mongoTemplate;
    private final Scope2ReportRepository scope2ReportRepository;
    private final Scope2FactorRepository scope2FactorRepository;
    private static final Logger logger = LoggerFactory.getLogger(Scope2EmissionReportService.class);
    private final Scope2DataIngestRepository scope2DataIngestRepository;

    public Scope2EmissionReportService(Scope2ReportRepository scope2ReportRepository,
                                       Scope2DataIngestRepository scope2DataIngestRepository,Scope2FactorRepository scope2FactorRepository) {
        this.scope2ReportRepository = scope2ReportRepository;
        this.scope2DataIngestRepository=scope2DataIngestRepository;
        this.scope2FactorRepository=scope2FactorRepository;


    }
    public List<Scope2EmissionReport>  getAllReport(){
        return  scope2ReportRepository.findAll();

    }
    public List<Scope2EmissionReport> getReportsInRange(String start, String end) {
        Query query = new Query();
        query.addCriteria(Criteria.where("yearMonth").gte(start).lte(end));

        return mongoTemplate.find(query, Scope2EmissionReport.class);
    }

    public ResponseEntity<ApiResponse> newReport(Scope2ActivityDataIngest scope2ActivityDataIngest){

        try {
            Scope2Factor scope2Factor=scope2FactorRepository.findTopByOrderByYearDesc();

            Scope2EmissionReport scope2EmissionReport=new Scope2EmissionReport();
            String year = String.valueOf(YearMonth.parse(scope2ActivityDataIngest.getYearMonth()).getYear() - 1);
            scope2EmissionReport.setOutputUnit("tonne");
            if (scope2ActivityDataIngest.getUnit().toLowerCase().trim().contains("kwh")) {
                scope2EmissionReport.setQuantityConsume(scope2ActivityDataIngest.getOutPutQuantityConsume());

                scope2EmissionReport.setCo2eTotal(scope2ActivityDataIngest.getOutPutQuantityConsume()*scope2Factor.getFactor());
//                scope2EmissionReport.setOutputUnit("tCO2e");

            }else {
                scope2EmissionReport.setQuantityConsume(scope2ActivityDataIngest.getOutPutQuantityConsume());
                scope2EmissionReport.setCo2eTotal(scope2ActivityDataIngest.getQuantityConsume() * scope2Factor.getFactor());
            }
            //scope2EmissionReport.setQuantityConsume(scope2ActivityDataIngest.getQuantityConsume());
//            scope2EmissionReport.setOutputUnit(scope2ActivityDataIngest.getOutputUnit());
            scope2EmissionReport.setScope2ActivityDataIngest(scope2ActivityDataIngest);
            scope2EmissionReport.setYear(year);
            scope2EmissionReport.setCost(scope2ActivityDataIngest.getCost());
            scope2EmissionReport.setYearMonth(scope2ActivityDataIngest.getYearMonth());
            scope2EmissionReport.setScope2Factor(scope2Factor);

            LocalDate today = LocalDate.now(ZoneOffset.UTC);

            scope2EmissionReport.setCreateDateString(today.toString());
            scope2EmissionReport.setOrgName(scope2ActivityDataIngest.getOrgName());
            scope2EmissionReport.setFacilityName(scope2ActivityDataIngest.getFacilityName());
            scope2EmissionReport.setIngest_reference_id(scope2ActivityDataIngest.getId());
            scope2EmissionReport.setFuelName(scope2ActivityDataIngest.getFuelName());
            scope2EmissionReport.setUnit(scope2ActivityDataIngest.getUnit());
            scope2ReportRepository.insert(scope2EmissionReport);
            scope2ActivityDataIngest.setStatus(10);
            scope2DataIngestRepository.save(scope2ActivityDataIngest);
            //scope2EmissionReport.setEmissionType(scope2ActivityDataIngest.get());

        }catch (Exception e){
            logger.error(e.getMessage());
            scope2ActivityDataIngest.setStatus(-5);
            scope2ActivityDataIngest.setErrorMsg("Error,No data added in report section");
            scope2DataIngestRepository.save(scope2ActivityDataIngest);
            throw new RuntimeException( " Error,No data added in report section"+e.getMessage());

        }


        return  ResponseEntity.status(CREATED)
                .body(new ApiResponse("success", CREATED.value(), " report data saved"));
    }


    public CsvResponse generateScope2ReportCsv(String reportType, String startMonth,String endMonth){



        List<Scope2EmissionReport> reports =
                getReportsInRange(startMonth, endMonth);
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);
        System.out.println(reports);
        // Step 1: headers
        List<String> headers = List.of(
                "Emission Factor Source","version","Organization","Facility","Electricity Cost" ,
                "Raw Input Value","Input Unit(kWh/MWh)","Normalised(MWh)","EF(tCO2e)/MWH",
                "CO2e Total(tCO2e)","Output Unit","Cost","Method", "Report Date"

        );

        csvWriter.writeNext(headers.toArray(new String[0]));


        for (Scope2EmissionReport r : reports) {
            String[] row = new String[]{
                    safe(r.getScope2Factor().getFactorSource()),
                    safe(r.getScope2Factor().getVersion()),
                    safe(r.getOrgName()),

                    safe(r.getFacilityName()),
                    String.valueOf(r.getCost()),
                    String.valueOf(r.getScope2ActivityDataIngest().getQuantityConsume()), //Raw Input Value
                    String.valueOf(r.getScope2ActivityDataIngest().getUnit()),//Input Unit(kWh/MWh)

                    String.valueOf(r.getScope2ActivityDataIngest().getOutputUnit()),//Normalised(MWh)


                    String.valueOf(r.getScope2Factor().getFactor()),//EF(tCO₂e / MWh)
                    String.valueOf(r.getCo2eTotal()),//CO₂e Total(tCO₂e)
//                    safe(r.getOutputUnit()),
                    "tCO2e",


                    "Location-Based",
                    String.valueOf(r.getYearMonth())
            };

            csvWriter.writeNext(row);
        }
        String fileName = reportType + "-" + startMonth + "-to-" + endMonth + ".csv";
        String collection = "emissions_report";
        Map<String, Object> payload=new HashMap<>();
        payload.put("File name",fileName);
        payload.put("Report type",reportType);
        payload.put("Start month",startMonth);
        payload.put("End month",endMonth);
        payload.put("Creation date",LocalDate.now());
        saveDataToCollection(payload, collection);


        return new CsvResponse(fileName,writer.toString()) ;

    }
    private double getConvert(String unit,double input){
        if (unit.equalsIgnoreCase("kwh"))
            return input/1000;
        else return  input;



    }

    private String safe(String value) {
        return value == null ? "" : value;
    }


    public void saveDataToCollection(Map<String, Object> data, String collectionName) {
        // Option A: Pass the Map directly
        try {
            mongoTemplate.insert(data, collectionName);
        }catch (Exception e ){
            logger.error("error while saving data to collectionName"+e.getMessage());
        }
        System.out.println("Data successfully inserted into " + collectionName);
    }






        }
