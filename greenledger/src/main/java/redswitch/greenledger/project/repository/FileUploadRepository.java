package redswitch.greenledger.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import redswitch.greenledger.project.model.BlacklistedToken;
import redswitch.greenledger.project.model.FileUploadLog;

@Repository
public interface FileUploadRepository extends MongoRepository<FileUploadLog, String> {

}
