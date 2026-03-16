package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import redswitch.greenledger.project.model.Scope1ActivityDataIngest;
import redswitch.greenledger.project.model.Scope1FactorData;

public interface Scope1DataIngestRepository extends MongoRepository<Scope1ActivityDataIngest,String> {


}
