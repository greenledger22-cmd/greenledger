package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.Scope2Factor;

@Repository
public interface Scope2FactorRepository extends MongoRepository<Scope2Factor,String> {
    Scope2Factor findTopByOrderByYearDesc();
}
