package redswitch.greenledger.project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.Scope1FactorData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface Scope1DataIngestRepository extends MongoRepository<Scope1ActivityDataIngest,String> {


    Optional<Scope1ActivityDataIngest> findByFuelNameAndFuelTypeAndYearMonthContainingIgnoreCase(String fuelName, String fuelType,String yearMont);


}
