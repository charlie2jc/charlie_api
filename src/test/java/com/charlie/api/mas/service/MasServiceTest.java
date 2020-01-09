package com.charlie.api.mas.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.charlie.api.mas.model.MasInfo;
import com.charlie.api.mas.parser.MasCSVParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
@TestMethodOrder(OrderAnnotation.class)
public class MasServiceTest {

	@Autowired
	private MasService masService;
	
	@Autowired
	public MasCSVParser masCSVParser;
	
	@Test
	@Order(1)
	@DisplayName("Test - Initial Data saved in DB ")
	public void initData() {
		masCSVParser.sampleDataInit();
	}
	
    @Test
    @Order(2)
	@DisplayName("Test - Get All Municipal Agreement Support Information")
	public void getMunicipalAllTest() {
    	String rtnJsonStr = masService.getMunicipalAll();
    	
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		List<MasInfo> masInfoList= mapper.readValue(rtnJsonStr, new TypeReference<List<MasInfo>>(){});
    	
    		assertNotNull(masInfoList, "All MasInfo List is Null");
        	assertTrue(masInfoList.size() > 0, "Count of MasInfo Entity is zero or less");
        	
        	assertTrue(StringUtils.equals(masInfoList.get(0).getRegion(), "강릉시"));
        	assertTrue(StringUtils.equals(masInfoList.get(0).getTarget(), "강릉시 소재 중소기업으로서 강릉시장이 추천한 자"));
        	assertTrue(StringUtils.equals(masInfoList.get(0).getUsage(), "운전"));
        	assertTrue(StringUtils.equals(masInfoList.get(0).getLimit(), "추천금액 이내"));
        	assertTrue(StringUtils.equals(masInfoList.get(0).getRate(), "3%"));
        	assertTrue(StringUtils.equals(masInfoList.get(0).getInstitute(), "강릉시"));
        	assertTrue(StringUtils.equals(masInfoList.get(0).getMgmt(), "강릉지점"));
        	assertTrue(StringUtils.equals(masInfoList.get(0).getReception(), "강릉시 소재 영업점"));
        		
        	assertTrue(StringUtils.equals(masInfoList.get(97).getRegion(), "안양상공회의소"));
    	} catch (IOException e) {
			log.error("getMunicipalAllTest - " + e.getMessage());
		}
	}
    
    @Test
    @Order(3)
	@DisplayName("Test - Get One pick Municipal Agreement Support Information")
	public void getMunicipalFindOneTest() {
    	String reqJsonStr = "{\"region\": \"강릉시\"}";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		MasInfo reqMasInfo = mapper.readValue(reqJsonStr, MasInfo.class);
    		String rtnJsonStr = masService.getMunicipalFindOne(reqMasInfo);
    		MasInfo masInfo= mapper.readValue(rtnJsonStr, MasInfo.class);
    		assertNotNull(masInfo, "MasInfo is Null");
        	assertTrue(StringUtils.equals(masInfo.getRegion(), "강릉시"));
        	assertTrue(StringUtils.equals(masInfo.getTarget(), "강릉시 소재 중소기업으로서 강릉시장이 추천한 자"));
        	assertTrue(StringUtils.equals(masInfo.getUsage(), "운전"));
        	assertTrue(StringUtils.equals(masInfo.getLimit(), "추천금액 이내"));
        	assertTrue(StringUtils.equals(masInfo.getRate(), "3%"));
        	assertTrue(StringUtils.equals(masInfo.getInstitute(), "강릉시"));
        	assertTrue(StringUtils.equals(masInfo.getMgmt(), "강릉지점"));
        	assertTrue(StringUtils.equals(masInfo.getReception(), "강릉시 소재 영업점"));
    	} catch (IOException e) {
			log.error("getMunicipalFindOneTest - " + e.getMessage());
		}
	}
    
    @Test
    @Order(4)
	@DisplayName("Test - Put One pick Municipal Agreement Support Information")
	public void putMunicipalOneTest() {
    	String reqJsonStr = "{\n" + 
    			"    \"region\": \"강릉시\",\n" + 
    			"    \"target\": \"강릉시 소재 중소기업으로서 강릉시장이 추천한 자\",\n" + 
    			"    \"usage\": \"운전\",\n" + 
    			"    \"limit\": \"추천금액 이내\",\n" + 
    			"    \"limitLong\": 0,\n" + 
    			"    \"rate\": \"35%\",\n" + 
    			"    \"rateAvg\": 3,\n" + 
    			"    \"institute\": \"강릉시\",\n" + 
    			"    \"mgmt\": \"강릉지점\",\n" + 
    			"    \"reception\": \"강릉시 소재 영업점\"\n" + 
    			"  }";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		MasInfo reqMasInfo= mapper.readValue(reqJsonStr, MasInfo.class);
    		String rtnJsonStr = masService.putMunicipalOne(reqMasInfo);
    		MasInfo masInfo= mapper.readValue(rtnJsonStr, MasInfo.class);
    		assertNotNull(masInfo, "MasInfo is Null");
        	assertTrue(StringUtils.equals(masInfo.getRegion(), "강릉시"));
        	assertTrue(StringUtils.equals(masInfo.getTarget(), "강릉시 소재 중소기업으로서 강릉시장이 추천한 자"));
        	assertTrue(StringUtils.equals(masInfo.getUsage(), "운전"));
        	assertTrue(StringUtils.equals(masInfo.getLimit(), "추천금액 이내"));
        	assertTrue(StringUtils.equals(masInfo.getRate(), "35%"));
        	assertTrue(StringUtils.equals(masInfo.getInstitute(), "강릉시"));
        	assertTrue(StringUtils.equals(masInfo.getMgmt(), "강릉지점"));
        	assertTrue(StringUtils.equals(masInfo.getReception(), "강릉시 소재 영업점"));
    	} catch (IOException e) {
			log.error("putMunicipalOneTest - " + e.getMessage());
		}
	}
    
    @Test
    @Order(5)
	@DisplayName("Test - Delete One pick Municipal Agreement Support Information")
	public void deleteMunicipalOneTest() {
    	String reqJsonStr = "{\"region\": \"강릉시\"}";
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		MasInfo reqMasInfo = mapper.readValue(reqJsonStr, MasInfo.class);
    		masService.deleteMunicipalOne(reqMasInfo);
    		String rtnJsonStr = masService.getMunicipalFindOne(reqMasInfo);
        	assertTrue(StringUtils.equals(rtnJsonStr, "{\"data\":\"No data\"}"), "Delete failed");
    	} catch (IOException e) {
			log.error("deleteMunicipalOneTest - " + e.getMessage());
		}
	}
    
    @Test
    @Order(6)
	@DisplayName("Test - Get Limit Desc Top Count Municipal Agreement Support Information")
	public void getMunicipalFindByLimitDescTopCntTest() {
    	String rtnJsonStr = masService.getMunicipalFindByLimitDescTopCnt(3);
    	assertTrue(!StringUtils.equals(rtnJsonStr, "{}"), "No data");
    	String[] rtnArr = rtnJsonStr.split(",");
    	assertTrue(rtnArr.length == 3, "Count error");
    	
    	rtnJsonStr = masService.getMunicipalFindByLimitDescTopCnt(5);
    	assertTrue(!StringUtils.equals(rtnJsonStr, "{}"), "No data");
    	rtnArr = rtnJsonStr.split(",");
    	assertTrue(rtnArr.length == 5, "Count error");
    	
    	rtnJsonStr = masService.getMunicipalFindByLimitDescTopCnt(10);
    	assertTrue(!StringUtils.equals(rtnJsonStr, "{}"), "No data");
    	rtnArr = rtnJsonStr.split(",");
    	assertTrue(rtnArr.length == 10, "Count error");
	}
    
    @Test
    @Order(7)
	@DisplayName("Test - Get Rate Minimum Institute Municipal Agreement Support Information")
	public void getMunicipalFindByRateMinTopInstituteTest() {
    	String rtnJsonStr = masService.getMunicipalFindByRateMinTopInstitute();
    	assertTrue(!StringUtils.equals(rtnJsonStr, "{}"), "No data");
    	assertTrue(StringUtils.equals(rtnJsonStr, "{안산시}"), "No data");
	}

	@Test
	@Order(8)
	@DisplayName("Test - Get Recommends Minimum Institute Municipal Agreement Support Information")
	public void getRecommendMasTest() {
		String aticle = "";
		String rtnJsonStr = masService.getRecommendMas(aticle);
		assertNotNull(rtnJsonStr, "rtnJsonStr is Null");
	}
	
}
