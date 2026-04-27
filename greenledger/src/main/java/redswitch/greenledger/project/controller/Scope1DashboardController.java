package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.repository.Scope1ReportRepository;
import redswitch.greenledger.project.service.Scope1DashBoardService;
import redswitch.greenledger.project.service.Scope1FactorService;

import java.time.YearMonth;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/scope1")
public class Scope1DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(Scope1DashboardController.class);

    private final Scope1DashBoardService scope1DashBoardService;


    public Scope1DashboardController(Scope1DashBoardService scope1DashBoardService) {
        this.scope1DashBoardService = scope1DashBoardService;

    }

    @PostMapping("/dashBoard/{startYrmth}/{endYrmth}")
    public ResponseEntity<ApiResponse> getData(String startYrmth,String endYrmth){
        YearMonth startYm= YearMonth.parse(startYrmth);
        YearMonth endYm= YearMonth.parse(endYrmth);
        if (startYm.isAfter(endYm)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("failure", HttpStatus.BAD_REQUEST.value(), "start year month can't be greater than end year month"));

        }


      return scope1DashBoardService.getDashboard();

    }




}
