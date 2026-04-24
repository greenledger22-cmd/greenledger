package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.SuperUserAdmin;
import redswitch.greenledger.project.model.User;

import java.util.Optional;

@Repository
public interface SuperUserRepository extends MongoRepository<SuperUserAdmin,String> {
    public SuperUserAdmin findByName(String Name);
    //Optional<User> findByUserNameOrEmail(String username, String email);
    Optional<SuperUserAdmin> findByUserName(String username);
    public Optional<SuperUserAdmin> findByEmail(String email);
}
