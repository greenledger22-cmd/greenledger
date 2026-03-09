package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import redswitch.greenledger.project.model.User;

public interface UserRepository extends MongoRepository<User,String> {
}
