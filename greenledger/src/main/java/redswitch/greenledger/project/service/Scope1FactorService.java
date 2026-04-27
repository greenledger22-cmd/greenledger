package redswitch.greenledger.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redswitch.greenledger.project.controller.UserController;
import redswitch.greenledger.project.model.*;
import redswitch.greenledger.project.repository.FileUploadRepository;
import redswitch.greenledger.project.repository.Scope1FactorRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class Scope1FactorService {
    private static final Logger logger = LoggerFactory.getLogger(Scope1FactorService.class);
    private final Scope1FactorRepository scope1FactorRepository;
    private final FileUploadRepository fileUploadRepository;
    public Scope1FactorService(Scope1FactorRepository scope1FactorRepository,FileUploadRepository fileUploadRepository){
        this.scope1FactorRepository=scope1FactorRepository;
        this.fileUploadRepository=fileUploadRepository;
    }

    public ResponseEntity<ApiResponse> addFactor(Scope1FactorData scope1FactorData){
        try {
             Optional<Scope1FactorData> existsScope=scope1FactorRepository.findByFuelTypeAndFuelNameAndUnitAndYear(scope1FactorData.getFuelType().trim(),scope1FactorData.getFuelName().trim(),scope1FactorData.getUnit().trim(),scope1FactorData.getYear().trim());
            if (!existsScope.isPresent()) {
                scope1FactorData.setCreationDateString(LocalDate.now().toString());
                scope1FactorData.setUpdateDateString(LocalDate.now().toString());
                //scope1FactorData.setYear(scope1FactorData.getEmissionStandard().getGwpBasis());
                scope1FactorRepository.insert(scope1FactorData);

            }
            else
                return ResponseEntity.status(CONFLICT)
                        .body(new ApiResponse("Failure", CONFLICT.value(), "Factor name already exists"));


        }catch (Exception e){
            logger.error(e.getMessage());
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failure", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unable to save data"));

        }

        return   ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", HttpStatus.CREATED.value(), "scope factor added successfully"));

    }
    public ResponseEntity<ApiResponse> uploadFactor(MultipartFile file, Authentication authentication) {
        List<Scope1FactorData> list = new ArrayList<>();

        int count=1;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            //  Read header
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",");

            Map<String, Integer> headerMap = new HashMap<>();

            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim(), i);
            }

            String line;

            String fuelType="";
            String fuelName="";
            String year="";

            EmissionStandard emissionStandard = new EmissionStandard();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                count++;
                String[] data = line.split(",");

                Scope1FactorData factor = new Scope1FactorData();

                if(data[headerMap.get("Activity")].trim()!= null && !data[headerMap.get("Activity")].trim().isBlank()){
                    fuelType=data[headerMap.get("Activity")].trim();
                }


                factor.setFuelType(fuelType);
                if(data[headerMap.get("Fuel")].trim()!= null && !data[headerMap.get("Fuel")].trim().isBlank()){
                    fuelName=data[headerMap.get("Fuel")].trim();
                }



                factor.setFuelName(fuelName);
                factor.setUnit(data[headerMap.get("Unit")].trim());
                factor.setCo2eTotal(Double.parseDouble(data[headerMap.get("kg CO2e")].trim()));
                factor.setCo2Factor(Double.parseDouble(data[headerMap.get("kg CO2e of CO2 per unit")].trim()));
                factor.setCh4Factor(Double.parseDouble(data[headerMap.get("kg CO2e of CH4 per unit")].trim()));
                factor.setN2oFactor(Double.parseDouble(data[headerMap.get("kg CO2e of N2O per unit")].trim()));
                if (emissionStandard.getSource()==null){
                emissionStandard.setSource(data[headerMap.get("Emission factor source")].trim());
                //emissionStandard.setSourceType(data[headerMap.get("sourceType")].trim());
                year=data[headerMap.get("version")].trim();
                emissionStandard.setVersion(year);
                emissionStandard.setGwpBasis(data[headerMap.get("gwp basis")].trim());

                }

                factor.setEmissionStandard(emissionStandard);
                factor.setYear(year);
                factor.setConvertTo(data[headerMap.get("output unit")].trim());
                factor.setCreationDateString(Instant.now().toString());
                factor.setUpdateDateString(Instant.now().toString());

                boolean exists=list.stream().anyMatch(f->
                                    f.getFuelName().equals(factor.getFuelName())&&
                                    f.getFuelType().equals(factor.getFuelType())&&
                                    f.getUnit().equalsIgnoreCase(factor.getUnit()) &&
                                    f.getYear().equalsIgnoreCase(factor.getYear())


                                );

                if (!exists)
                    list.add(factor);
                else   return ResponseEntity.status(CONFLICT).body(
                        new ApiResponse("Failure", CONFLICT.value(), "CSV duplicate data found"));


            }

            //  Bulk insert
            scope1FactorRepository.saveAll(list);
            FileUploadLog fileUploadLog=new FileUploadLog();
            fileUploadLog.setUploadedBy(  authentication.getName());
            fileUploadLog.setFileName(file.getOriginalFilename());
            //fileUploadLog.setUploadDate(new Date());
            fileUploadRepository.insert(fileUploadLog);

            return ResponseEntity.ok(
                    new ApiResponse("success", 200, "CSV uploaded successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("failure", 500, e.getMessage()+" line number "+count));
        }
    }

    public ResponseEntity<ApiResponse> updateFactor(Scope1FactorData scope1FactorData,Authentication authentication,String userId){
        try {
            Optional<Scope1FactorData> existsFactor= scope1FactorRepository.findById(userId);
            if (existsFactor.isPresent()){
                Scope1FactorData factor=existsFactor.get();
                factor.setCo2Factor(scope1FactorData.getCo2Factor());
                factor.setCh4Factor(scope1FactorData.getCh4Factor());
                factor.setN2oFactor(scope1FactorData.getN2oFactor());
                factor.setCo2eTotal(scope1FactorData.getCo2eTotal());
                factor.setEmissionStandard(scope1FactorData.getEmissionStandard());
                factor.setFuelType(scope1FactorData.getFuelType());
                factor.setYear(scope1FactorData.getYear());
                if (!factor.getFuelName().equals(scope1FactorData.getFuelName()))
                    return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse("success", HttpStatus.BAD_REQUEST.value(), "Can't change fuel name "+scope1FactorData.getFuelName()));

                factor.setUpdateDateString(LocalDate.now().toString());
                factor.setUpdatedby(authentication.getName());
                scope1FactorRepository.save(factor);
            }else return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("User not found", HttpStatus.NOT_FOUND.value(), "unable to find scope factor of "+scope1FactorData.getFuelName()));


        }catch (Exception e){

            logger.error(e.getMessage());
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("User not found", HttpStatus.BAD_REQUEST.value(), "Unable to save data"));

        }
        return   ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("success", HttpStatus.OK.value(), "scope factor  "+scope1FactorData.getFuelName()+"updated successfully"));


    }


    public ResponseEntity<ApiResponse> getFactorData(String fuelName,String fuelType) {
        List<Scope1FactorData> fueldata=new ArrayList<>();
        try {
            if (fuelName!=null && !fuelName.isEmpty())
                fueldata = scope1FactorRepository.findByFuelNameContainingIgnoreCase(fuelName);
            else if (fuelType!=null && !fuelType.isEmpty())
                fueldata = scope1FactorRepository.findByFuelTypeContainingIgnoreCase(fuelType);
            else
                fueldata = scope1FactorRepository.findAll();


        } catch (Exception e) {
            logger.error(e.getMessage());

        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("success", HttpStatus.OK.value(), fueldata));

    }




}
