package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.Scope1EmissionReport;
import redswitch.greenledger.project.model.Scope1FactorData;

import java.util.List;
import java.util.Optional;

@Repository
public interface Scope1ReportRepository extends MongoRepository<Scope1EmissionReport,String> {
    Optional<Scope1EmissionReport> findByFuelTypeAndFuelNameContainingIgnoreCase(String fuelType, String fuelName);
    List<Scope1EmissionReport> findByReportDateBetween(String start, String end);
}
