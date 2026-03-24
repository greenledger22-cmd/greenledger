package redswitch.greenledger.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import redswitch.greenledger.project.controller.UserController;
import redswitch.greenledger.project.model.Scope1FactorData;
import redswitch.greenledger.project.model.User;
import redswitch.greenledger.project.repository.Scope1FactorRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Scope1FactorService {
    private static final Logger logger = LoggerFactory.getLogger(Scope1FactorService.class);
    private final Scope1FactorRepository scope1FactorRepository;
    public Scope1FactorService(Scope1FactorRepository scope1FactorRepository){
        this.scope1FactorRepository=scope1FactorRepository;
    }

    public ResponseEntity<String > addFactor(Scope1FactorData scope1FactorData){
        try {
             Optional<Scope1FactorData> existsScope=scope1FactorRepository.findByFuelTypeAndFuelName(scope1FactorData.getFuelType().trim(),scope1FactorData.getFuelName().trim());
            if (!existsScope.isPresent()) {
                scope1FactorData.setCreationDateString(LocalDate.now().toString());
                scope1FactorData.setUpdateDateString(LocalDate.now().toString());
                scope1FactorRepository.insert(scope1FactorData);

            }
            else
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Fuel type/ fuel name already exists");;
        }catch (Exception e){
            logger.error(e.getMessage());
            return   ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save data");
        }

        return   ResponseEntity.status(HttpStatus.CREATED).body("scope factor added successfully");
    }


    public ResponseEntity<String > updateFactor(Scope1FactorData scope1FactorData){
        try {
            Optional<Scope1FactorData> existsFactor= scope1FactorRepository.findByFuelTypeAndFuelName(scope1FactorData.getFuelType().trim(), scope1FactorData.getFuelName().trim());
            if (existsFactor.isPresent()){
                Scope1FactorData factor=existsFactor.get();
                factor.setCo2Factor(scope1FactorData.getCo2Factor());
                factor.setCh4Factor(scope1FactorData.getCh4Factor());
                factor.setN2oFactor(scope1FactorData.getN2oFactor());
                factor.setCo2eTotal(scope1FactorData.getCo2eTotal());
                factor.setUpdateDateString(LocalDate.now().toString());
                scope1FactorRepository.save(factor);
            }else return   ResponseEntity.status(HttpStatus.NOT_FOUND).body("unable to find scope factor of "+scope1FactorData.getFuelName());

        }catch (Exception e){

            logger.error(e.getMessage());
            return   ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to save data");
        }
        return   ResponseEntity.status(HttpStatus.CREATED).body("scope factor  "+scope1FactorData.getFuelName()+"updated successfully");

    }









    public ResponseEntity<List<Scope1FactorData>> getFactorData(String fuelName,String fuelType) {
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

        return ResponseEntity.status(HttpStatus.FOUND).body(fueldata);
    }



}
