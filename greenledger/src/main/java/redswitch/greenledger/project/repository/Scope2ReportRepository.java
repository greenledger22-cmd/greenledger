package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.Scope1EmissionReport;
import redswitch.greenledger.project.model.Scope2EmissionReport;

@Repository
public interface Scope2ReportRepository extends MongoRepository<Scope2EmissionReport,String> {
}
