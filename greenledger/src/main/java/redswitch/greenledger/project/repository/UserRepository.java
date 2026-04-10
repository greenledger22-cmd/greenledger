package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    public User findByName(String Name);
    //Optional<User> findByUserNameOrEmail(String username, String email);
    Optional<User> findByUserName(String username);
    public Optional<User> findByEmail(String email);
}
