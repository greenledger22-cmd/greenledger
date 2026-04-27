package redswitch.greenledger.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.model.Scope1EmissionReport;
import redswitch.greenledger.project.repository.Scope1ReportRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class Scope1DashBoardService {

    private static final Logger logger = LoggerFactory.getLogger(Scope1DashBoardService.class);

    private final Scope1ReportRepository scope1ReportRepository;
    public Scope1DashBoardService(Scope1ReportRepository scope1ReportRepository) {
        this.scope1ReportRepository = scope1ReportRepository;

    }


    public ResponseEntity<ApiResponse> getDashboard() {
        List<Scope1EmissionReport> sc1List= scope1ReportRepository.findAll();
        double totalEmission = 0;
        if(!sc1List.isEmpty()){

            totalEmission=sc1List.stream().mapToDouble(Scope1EmissionReport::getCo2eTotal).sum();

        }

        Map<String, Object> total = new HashMap<>();
        total.put("co2e", totalEmission);


        Map<String, Object> response = new HashMap<>();
        response.put("total_emission", total);
        return  ResponseEntity.status(OK)
                .body(new ApiResponse("success", OK.value(), response));


    }


}
