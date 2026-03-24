package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponse<T> {
    private String response;
    private int stsCode;
    private T data;
    private List<String> error;
    public ApiResponse(String response,T data){
       // this.stsCode=stscode;
        this.response=response;
        this.data=data;
    }

}
