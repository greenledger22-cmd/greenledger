package redswitch.greenledger.project.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsvResponse {

        private String fileName;
        private String csvData;

        public CsvResponse(String fileName, String csvData) {
            this.fileName = fileName;
            this.csvData = csvData;
        }


}
