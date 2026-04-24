package redswitch.greenledger.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import redswitch.greenledger.project.model.ApiResponse;
import redswitch.greenledger.project.model.CsvResponse;
import redswitch.greenledger.project.service.Scope1EmissionReportService;
import redswitch.greenledger.project.service.Scope2EmissionReportService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/report")
public class ReportGenerateController {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerateController.class);

    private final Scope1EmissionReportService scope1EmissionReportService;
    private final Scope2EmissionReportService scope2EmissionReportService;
    public ReportGenerateController(Scope1EmissionReportService scope1EmissionReportService,
                                    Scope2EmissionReportService scope2EmissionReportService) {
        this.scope1EmissionReportService = scope1EmissionReportService;
        this.scope2EmissionReportService=scope2EmissionReportService;
    }

    @GetMapping("/getAllReport/{reportType}")
    public ResponseEntity<ApiResponse> getReport(@PathVariable String reportType,
                                                 @RequestParam(required = false) String startMonth,
                                                 @RequestParam(required = false) String endMonth
    ) {
        List<?> reportData;
        if (reportType.equalsIgnoreCase("scope1"))
            reportData=scope1EmissionReportService.getAllReport();
        else
            reportData=scope2EmissionReportService.getAllReport();

        return ResponseEntity.status(OK)
                .body(new ApiResponse("success", OK.value(),reportData ));
    }

    @GetMapping("/GenerateReport/{reportType}")
    public ResponseEntity<byte[]> getScope1Report(@PathVariable   String reportType,
                                                  @RequestParam(required = false,value = "startMonth")   String startMonth,
                                                  @RequestParam(required = false,value = "endMonth")   String endMonth) {

        if (startMonth.compareTo(endMonth) > 0) {
            throw new IllegalArgumentException("Invalid date range");
        }
        LocalDate today=LocalDate.now();
        if (startMonth.isBlank()){

            startMonth= today.getYear()+"-"+today.getMonth();

        }
        if (endMonth.isBlank()){
            today=today.plusMonths(6);
            endMonth= today.getYear()+"-"+today.getMonth();

        }




            ResponseEntity  response = null;
        if (reportType.equalsIgnoreCase("scope1")){
            CsvResponse csvData= scope1EmissionReportService.generateScope1ReportCsv(reportType,startMonth,endMonth);

            response=ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=" + csvData.getFileName())
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .body(csvData.getCsvData());
        }
        else if (reportType.equalsIgnoreCase("scope2")){
            CsvResponse csvData= scope2EmissionReportService.generateScope2ReportCsv(reportType,startMonth,endMonth);

            response=ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=" + csvData.getFileName())
                    .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                    .body(csvData.getCsvData());
        }
        return response;
    }

//    @GetMapping("/allReport")
//    public ResponseEntity<ApiResponse> getScope1Report() {
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//
//        return ;
//    }




}
