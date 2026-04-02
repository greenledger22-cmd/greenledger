package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.Scope1EmissionReport;
import redswitch.greenledger.project.model.Scope2ActivityDataIngest;

import java.util.Optional;

@Repository
public interface Scope2DataIngestRepository  extends MongoRepository<Scope2ActivityDataIngest,String> {

    //@Query()
    Optional<Scope2ActivityDataIngest> findByEmissionTypeAndYearMonth(String emissionType, String yearMonth);

}
