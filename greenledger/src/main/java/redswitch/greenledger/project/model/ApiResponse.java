package redswitch.greenledger.project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String response;
    private int stsCode;
    private Object data;
    private Object error;

    public ApiResponse(String response, int stsCode, Object data) {
        this.response = response;
        this.stsCode = stsCode;
        this.data = data;
    }

    public ApiResponse(String response, int stsCode, Object data, Object error) {
        this.response = response;
        this.stsCode = stsCode;
        this.data = data;
        this.error = error;
    }
}


