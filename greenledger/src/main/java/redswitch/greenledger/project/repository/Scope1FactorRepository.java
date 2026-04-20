package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.Scope1FactorData;
import redswitch.greenledger.project.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface Scope1FactorRepository extends MongoRepository<Scope1FactorData,String> {
    List<Scope1FactorData> findByFuelNameContainingIgnoreCase(String fuelName);
    List<Scope1FactorData> findByFuelTypeContainingIgnoreCase(String fuelType);
    Optional<Scope1FactorData> findByFuelTypeAndFuelName(String fuelType, String fuelName);
    Optional<Scope1FactorData> findByFuelTypeAndFuelNameAndUnitAndYear(String fuelType, String fuelName,String unit,String year);
   // Scope1FactorData findByFuelTypeAndFuelNameContainingIgnoreCase(String fuelType,String fuelName);
}

