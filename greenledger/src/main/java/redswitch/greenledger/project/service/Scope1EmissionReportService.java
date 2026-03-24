package redswitch.greenledger.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.Scope1EmissionReport;
import redswitch.greenledger.project.model.Scope1FactorData;
import redswitch.greenledger.project.repository.Scope1DataIngestRepository;
import redswitch.greenledger.project.repository.Scope1FactorRepository;
import redswitch.greenledger.project.repository.Scope1ReportRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class Scope1EmissionReportService {

    private static final Logger logger = LoggerFactory.getLogger(Scope1DataIngestService.class);


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


    public void newReport(String fuelName,String fuelType,String yearMonth){
        try {
            Optional<Scope1ActivityDataIngest> scope1ActivityDataIngest1 = scope1DataIngestRepository
                    .findByFuelNameAndFuelTypeAndYearMonthContainingIgnoreCase(fuelName, fuelType,yearMonth);

            Optional<Scope1FactorData> scope1FactorData = scope1FactorRepository
                    .findByFuelTypeAndFuelName(fuelType.trim(), fuelName.trim());


            Scope1ActivityDataIngest existsDataIngest = null;
            Scope1FactorData emissionFactor = null;

            if (scope1ActivityDataIngest1.isPresent())
                existsDataIngest = scope1ActivityDataIngest1.get();//got ingested data

            if (scope1FactorData.isPresent())
                emissionFactor = scope1FactorData.get();//got factor  data
            else {
                existsDataIngest.setStatus(-5);
                existsDataIngest.setErrorMsg("No matching factor found for fuel "+scope1FactorData.get().getFuelName());
                scope1DataIngestRepository.save(existsDataIngest);
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

            scope1EmissionReport.setOutputUnit(emissionFactor.getConvertTo());

            scope1ReportRepository.insert(scope1EmissionReport);
            existsDataIngest.setStatus(10);
            scope1DataIngestRepository.save(existsDataIngest);


        }catch (Exception e){
            logger.error(e.getMessage());
        }


    }

}
