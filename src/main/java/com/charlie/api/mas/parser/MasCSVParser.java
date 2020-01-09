package com.charlie.api.mas.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.charlie.api.mas.model.MasInfo;
import com.charlie.api.mas.model.Municipal;
import com.charlie.api.mas.repository.MasInfoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MasCSVParser {

    @Autowired
    private MasInfoRepository masInfoRepository;
    
    private List<Municipal> municipalList = new ArrayList<Municipal>();
    
    private List<MasInfo> masInfoList = new ArrayList<MasInfo>();
    
    private String fileNm = "sample1.csv";

    //@PostConstruct
    public void sampleDataInit(){
    	masInfoRepository.deleteAll();

        CSVParser csvParser = null;
        try {
        	FileInputStream fis = new FileInputStream(fileNm);
            csvParser = CSVParser.parse(fis, Charset.forName("UTF-8"), CSVFormat.DEFAULT.withFirstRecordAsHeader());
            if(csvParser != null) {
            	parseData(csvParser.getRecords());
            	masInfoRepository.saveAll(masInfoList);
            }
        }catch(IOException e) {
            log.error("sampleDataInit.IOException - ", e.getMessage());
        }finally {
        	if(csvParser != null && !csvParser.isClosed()){
                try {csvParser.close();} catch (IOException e) {log.error("sampleDataInit.CSVParser.close.IOException - ", e.getMessage());}
            }
		}
    }
    
    private void parseData(List<CSVRecord> csvRecordList) throws IOException{
		for(CSVRecord csvRecord : csvRecordList){
			Municipal municipal = new Municipal("reg"+csvRecord.get(0), csvRecord.get(1)); 
			MasInfo masInfo = new MasInfo("reg"+csvRecord.get(0), csvRecord.get(2), csvRecord.get(3), csvRecord.get(4),
										csvRecord.get(5), csvRecord.get(6), csvRecord.get(7), csvRecord.get(8), municipal);
			masInfoList.add(masInfo);
			municipalList.add(municipal);
		}
    }

}
