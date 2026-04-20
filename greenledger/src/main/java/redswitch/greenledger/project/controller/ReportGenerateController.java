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

import java.time.LocalDate;

@RestController
@RequestMapping("/report")
public class ReportGenerateController {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerateController.class);

    private final Scope1EmissionReportService scope1EmissionReportService;
    public ReportGenerateController(Scope1EmissionReportService scope1EmissionReportService) {
        this.scope1EmissionReportService = scope1EmissionReportService;
    }

    @GetMapping("/getAllReport/{reportType}")
    public ResponseEntity<ApiResponse> getReport(@PathVariable String reportType,
                                                 @RequestParam(required = false) String startMonth,
                                                 @RequestParam(required = false) String endMonth
    ) {
        //String csvData = scope1EmissionReportService.generateScope1ReportCsv();

        return scope1EmissionReportService.getAllReport();
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
        return response;
    }

//    @GetMapping("/allReport")
//    public ResponseEntity<ApiResponse> getScope1Report() {
//        var auth = SecurityContextHolder.getContext().getAuthentication();
//
//        return ;
//    }




}
