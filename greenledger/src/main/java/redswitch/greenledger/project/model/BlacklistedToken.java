package redswitch.greenledger.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private String id;

    private String token;

    @Indexed(expireAfter = "0s")// auto delete after expiry
    private Instant expiry;

    public BlacklistedToken(String token, Instant expiry) {
        this.token = token;
        this.expiry = expiry;
    }



}
