package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.BlacklistedToken;

@Repository
public interface BlacklistedTokenRepository extends MongoRepository<BlacklistedToken, String> {
    boolean existsByToken(String token);
}
