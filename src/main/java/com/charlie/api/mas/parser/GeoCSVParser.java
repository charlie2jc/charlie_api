package com.charlie.api.mas.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.charlie.api.mas.model.Geo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GeoCSVParser {

    //private List<Geo> geoList = new ArrayList<Geo>();
    
    private Map<String, Geo> geoMap = new HashMap<String, Geo>();
    
    private String fileNm = "geo.csv";

    public Map<String, Geo> geoDataInit(){
        CSVParser csvParser = null;
        try {
        	FileInputStream fis = new FileInputStream(fileNm);
            csvParser = CSVParser.parse(fis, Charset.forName("UTF-8"), CSVFormat.DEFAULT.withFirstRecordAsHeader());
            if(csvParser != null) {
            	geoParseData(csvParser.getRecords());
            }
        }catch(IOException e) {
            log.error("geoDataInit.IOException - ", e.getMessage());
        }finally {
        	if(csvParser != null && !csvParser.isClosed()){
                try {csvParser.close();} catch (IOException e) {log.error("geoDataInit.CSVParser.close.IOException - ", e.getMessage());}
            }
		}
        return geoMap;
    }
    
    private void geoParseData(List<CSVRecord> csvRecordList) throws IOException{
		for(CSVRecord csvRecord : csvRecordList){
			geoMap.put("reg"+csvRecord.get(0), new Geo("reg"+csvRecord.get(0), csvRecord.get(1), Double.parseDouble(csvRecord.get(2)), Double.parseDouble(csvRecord.get(3))));
		}
    }

}
