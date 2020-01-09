package com.charlie.api.jwt.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.charlie.api.jwt.model.JwtUserInfo;
import com.charlie.api.jwt.repository.JwtUserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestPropertySource(
        properties = {
        		"jwt.secret=kakaopay",
        		"spring.datasource.url=jdbc:h2:mem:unittestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        		"spring.datasource.driver-class-name= org.h2.Driver",
        		"spring.datasource.username=sa",
        		"spring.datasource.password=",
        		"spring.h2.console.enabled=true"
        }
)

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class JwtAuthenticationControllerTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
    private JwtUserRepository jwtUserRepository;
	
    @Test
    @Order(1)
    @DisplayName("Test - sign up and user to saved")
    public void saveUserTest() throws Exception {
    	JwtUserInfo reqUser = new JwtUserInfo();
    	reqUser.setUsername("admin");
    	reqUser.setPassword("kakao");
    	
    	MvcResult result = mockMvc.perform(
    			MockMvcRequestBuilders.post("/api/signup")
	 				.contentType(MediaType.APPLICATION_JSON_VALUE)
	 				.content(objectMapper.writeValueAsString(reqUser)))
	    			.andExpect(status().isOk())
	    			.andDo(print())
	      			.andReturn();
    	JwtUserInfo userVo = jwtUserRepository.findByUsername("admin");
    	assertNotNull(userVo, "User is Null");
    	assertTrue(StringUtils.equals(userVo.getUsername(), "admin"), "Not equal user ID");
    	
    	assertNotNull(result.getResponse().getContentAsString(), "Token is not created");
		Map<String, String> map = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, String>>(){});
		assertNotNull(map, "Token is not created");
		assertNotNull(map.get("token"), "Token is not created");
		assertTrue(map.get("token").length() > 0, "Token is not created");
    }

    @Test
	@Order(2)
	@DisplayName("Test - sign in and create token")
	public void createAuthenticationTokenTest() throws Exception {
		JwtUserInfo reqUser = new JwtUserInfo();
		reqUser.setUsername("admin");
		reqUser.setPassword("kakao");
		
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/api/signin")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(objectMapper.writeValueAsString(reqUser)))
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn();
		assertNotNull(result.getResponse().getContentAsString(), "Token is not created");
		Map<String, String> map = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, String>>(){});
		assertNotNull(map, "Token is not created");
		assertNotNull(map.get("token"), "Token is not created");
		assertTrue(map.get("token").length() > 0, "Token is not created");
    }

	@Test
	@Order(3)
	@DisplayName("Test - refresh token")
	public void refreshAuthenticationTokenTest() throws Exception {
		String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTU3ODQ2MjI2NSwiaWF0IjoxNTc4NDU1MDY1fQ.y5sJuJ9536ADDCA8UHXNyExplnk5-7noWoJHaVySZ1_QpMNuV7KVDBzITz-saduFz2dm36nL1ADxNb2jXEbCFA";
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders.post("/api/refresh")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.header("Authorization", token))
					.andExpect(status().isOk())
					.andDo(print())
					.andReturn();
		
		assertNotNull(result.getResponse().getContentAsString(), "Token is not refresh");
		Map<String, String> map = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, String>>(){});
		assertNotNull(map, "Token is not refresh");
		assertNotNull(map.get("token"), "Token is not refresh");
		assertTrue(map.get("token").length() > 0, "Token is not refresh");
  }
    
}
