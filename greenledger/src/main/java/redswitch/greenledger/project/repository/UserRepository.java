package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.User;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
}
