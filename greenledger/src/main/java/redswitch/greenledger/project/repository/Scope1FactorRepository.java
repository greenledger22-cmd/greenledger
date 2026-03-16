package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import redswitch.greenledger.project.model.Scope1FactorData;
import redswitch.greenledger.project.model.User;

import java.util.List;

public interface Scope1FactorRepository extends MongoRepository<Scope1FactorData,String> {
    List<Scope1FactorData> findByFuelNameContainingIgnoreCase(String fuelName);
    List<Scope1FactorData> findByFuelTypeContainingIgnoreCase(String fuelType);
    boolean existsByFuelTypeAndFuelNameContainingIgnoreCase(String fuelType,String fuelName);
}

