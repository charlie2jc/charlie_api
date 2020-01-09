package com.charlie.api.mas.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import com.charlie.api.mas.repository.MasInfoRepository;
import com.charlie.api.mas.repository.MunicipalRepository;

@TestPropertySource(
        properties = {
        		"spring.datasource.url=jdbc:h2:mem:unittestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        		"spring.datasource.driver-class-name= org.h2.Driver",
        		"spring.datasource.username=sa",
        		"spring.datasource.password=",
        		"spring.h2.console.enabled=true"
        }
)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MasCSVParserTest {

    @Autowired
    private MasInfoRepository masInfoRepository;
    
    @Autowired
    private MunicipalRepository municipalRepository;
    
    @Test
	@DisplayName("Test - Municipal Agreement Support Information Entity to Saved")
	public void masInfoDataSave() {
    	assertNotNull(masInfoRepository.findAll(), "MasInfo Entity is Null");
    	assertTrue(masInfoRepository.count() > 0, "Count of MasInfo Entity is zero or less");
	}
    
    @Test
	@DisplayName("Test - Municipal Entity to Saved")
	public void municipalDataSave() {
    	assertNotNull(municipalRepository.findAll(), "Municipal Entity is Null");
    	assertTrue(municipalRepository.count() > 0, "Count of Municipal Entity is zero or less");
	}

}
