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
import redswitch.greenledger.project.repository.Scope1DataIngestRepository;
import redswitch.greenledger.project.repository.Scope1FactorRepository;
import redswitch.greenledger.project.repository.Scope1ReportRepository;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.*;

@Service
public class Scope1EmissionReportService {

    private static final Logger logger = LoggerFactory.getLogger(Scope1DataIngestService.class);

    @Autowired
    private MongoTemplate mongoTemplate;
    private final Scope1ReportRepository scope1ReportRepository;
    private final Scope1FactorRepository scope1FactorRepository;
    private final Scope1DataIngestRepository scope1DataIngestRepository;

    public Scope1EmissionReportService(Scope1ReportRepository scope1ReportRepository,
                                       Scope1FactorRepository scope1FactorRepository,
                                       Scope1DataIngestRepository scope1DataIngestRepository) {
        this.scope1ReportRepository = scope1ReportRepository;
        this.scope1FactorRepository=scope1FactorRepository;
        this.scope1DataIngestRepository=scope1DataIngestRepository;
    }


    public List<Scope1EmissionReport> getReportsInRange(String start, String end) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reportDate").gte(start).lte(end));

        return mongoTemplate.find(query, Scope1EmissionReport.class);
    }

    public ResponseEntity<ApiResponse> newReport(String fuelName,String fuelType,String yearMonth,String unit){
        Scope1ActivityDataIngest existsDataIngest = null;
        try {
            Optional<Scope1ActivityDataIngest> scope1ActivityDataIngest1 = scope1DataIngestRepository
                    .findByFuelNameAndFuelTypeAndYearMonthAndStatus(fuelName, fuelType,yearMonth,0);

            int  year = YearMonth.parse(yearMonth).getYear()-1;

            Optional<Scope1FactorData> scope1FactorData = scope1FactorRepository
                    .findByFuelTypeAndFuelNameAndUnitAndYear(fuelType.trim(), fuelName.trim(),unit, String.valueOf(year).trim());



            Scope1FactorData emissionFactor = null;

            if (scope1ActivityDataIngest1.isPresent())
                existsDataIngest = scope1ActivityDataIngest1.get();//got ingested data

            if (scope1FactorData.isPresent())
                emissionFactor = scope1FactorData.get();//got factor  data
            else {
                existsDataIngest.setStatus(-5);
                existsDataIngest.setErrorMsg("No matching factor found for fuel "+existsDataIngest.getFuelName());
                //System.out.println("insoide newReport() catch, runtime excep thrown ");
                scope1DataIngestRepository.save(existsDataIngest);
                //throw  new  RuntimeException( "No matching factor found for fuel, no report data saved");
            }

            Scope1EmissionReport scope1EmissionReport = new Scope1EmissionReport();

            if (existsDataIngest != null)
                scope1EmissionReport.setActivityData(existsDataIngest);
            if (emissionFactor != null)
                scope1EmissionReport.setScope1FactorData(emissionFactor);
            LocalDate today = LocalDate.now(ZoneOffset.UTC);

            scope1EmissionReport.setCreateDateString(today.toString());
            scope1EmissionReport.setUpdateDateString(today.toString());
            scope1EmissionReport.setFuelType(existsDataIngest.getFuelType());
            scope1EmissionReport.setFuelName(existsDataIngest.getFuelName());
            scope1EmissionReport.setCo2Factor(existsDataIngest.getQuantity() * emissionFactor.getCo2Factor());
            scope1EmissionReport.setCh4Factor(existsDataIngest.getQuantity() * emissionFactor.getCh4Factor());
            scope1EmissionReport.setN2oFactor(existsDataIngest.getQuantity() * emissionFactor.getN2oFactor());
            scope1EmissionReport.setCo2eTotal(existsDataIngest.getQuantity() * emissionFactor.getCo2eTotal());
            scope1EmissionReport.setReportDate(existsDataIngest.getYearMonth());
            scope1EmissionReport.setInputUnit(existsDataIngest.getUnit());
            scope1EmissionReport.setCost(existsDataIngest.getCost());

            String outputUnit=emissionFactor.getConvertTo();
            if (outputUnit.equalsIgnoreCase("kg")) {
                scope1EmissionReport.setOutputUnit("tonne");
                scope1EmissionReport.setCo2eTotal(scope1EmissionReport.getCo2eTotal()/1000.0);
            }

            scope1EmissionReport.setOrgName(existsDataIngest.getOrgName());
            scope1EmissionReport.setFacilityName(existsDataIngest.getFacilityName());
            scope1EmissionReport.setIngest_reference_id(existsDataIngest.getId());
            scope1ReportRepository.insert(scope1EmissionReport);
            existsDataIngest.setStatus(10);
            scope1DataIngestRepository.save(existsDataIngest);


        }catch (Exception e){
            logger.error(e.getMessage());
            existsDataIngest.setStatus(-5);
            existsDataIngest.setErrorMsg("Error,No data added in report section");
            scope1DataIngestRepository.save(existsDataIngest);
            throw new RuntimeException( " Error,No data added in report section"+e.getMessage());
        }

        return  ResponseEntity.status(CREATED)
                .body(new ApiResponse("success", CREATED.value(), " report data saved"));
    }

    public List<Scope1EmissionReport>  getAllReport(){
        return  scope1ReportRepository.findAll();

    }








    public CsvResponse generateScope1ReportCsv(String reportType, String startMonth,String endMonth){



        List<Scope1EmissionReport> reports =
                getReportsInRange(startMonth, endMonth);
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);
        System.out.println(reports);
        // Step 1: headers
        List<String> headers = List.of("Emission factor source","GWP Basis","Version",
                "Facility", "Fuel Name", "Fuel Type","Input Fuel","Fuel Cost","Cost Currency",
                "CO2 Factor", "CH4 Factor","N2O Factor","CO2e Total", "Input Unit", "Output Unit", "Report Date"

        );

        csvWriter.writeNext(headers.toArray(new String[0]));


        for (Scope1EmissionReport r : reports) {
            String[] row = new String[]{
                    safe(r.getScope1FactorData().getEmissionStandard().getSource()),

                    safe(r.getScope1FactorData().getEmissionStandard().getGwpBasis()),
                    safe(r.getScope1FactorData().getEmissionStandard().getVersion()),

                    safe(r.getFacilityName()),
                    safe(r.getFuelName()),
                    safe(r.getFuelType()),
                    String.valueOf(r.getActivityData().getQuantity()),
                    String.valueOf(r.getCost()),
                    String.valueOf("Rs"),
                    String.valueOf(r.getCo2Factor()),
                    String.valueOf(r.getCh4Factor()),
                    String.valueOf(r.getN2oFactor()),
                    String.valueOf(r.getCo2eTotal()),
                    safe(r.getInputUnit()),
                    safe(r.getOutputUnit()),
                    safe(r.getReportDate())
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
