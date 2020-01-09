package com.charlie.api.mas.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.charlie.api.mas.model.MasInfo;
import com.charlie.api.mas.parser.MasCSVParser;
import com.charlie.api.mas.service.MasService;

@RestController
public class MasController {
	
	@Autowired
	public MasService masService;
	
	@Autowired
	public MasCSVParser masCSVParser;
	
	@RequestMapping(value={"/api/municipal"}, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
    public String municipalPostDataInit() {
		masCSVParser.sampleDataInit();
		return masService.getMunicipalAll(); 
    }
	
	@RequestMapping(value={"/api/municipal-all"}, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
    public String municipalAll() {
        return masService.getMunicipalAll(); 
    }
	
	@RequestMapping(value={"/api/municipal"}, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	@ResponseBody
    public String municipalGet(@RequestBody MasInfo reqBody) {
        return masService.getMunicipalFindOne(reqBody); 
    }
	
	@RequestMapping(value={"/api/municipal"}, method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
	@ResponseBody
    public ResponseEntity<?> municipalPut(@RequestBody MasInfo reqBody) {
		try {
			return ResponseEntity.ok(masService.putMunicipalOne(reqBody));
		} catch (IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }
	
	@RequestMapping(value={"/api/municipal"}, method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
	@ResponseBody
    public ResponseEntity<?> municipalDelete(@RequestBody MasInfo reqBody) {
		try {
			return ResponseEntity.ok(masService.deleteMunicipalOne(reqBody));
		} catch (IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }
	
	@RequestMapping(value={"/api/municipal/limit-{cnt}"}, method = RequestMethod.GET)
	@ResponseBody
    public String municipalGetLimitDescTopCnt(@PathVariable(value="cnt", required = true) int cnt) {
        return masService.getMunicipalFindByLimitDescTopCnt(cnt); 
    }
	
	@RequestMapping(value={"/api/municipal/rate-min-top"}, method = RequestMethod.GET)
	@ResponseBody
    public String municipalGetRateMinTop() {
        return masService.getMunicipalFindByRateMinTopInstitute(); 
    }
	
	@RequestMapping(value={"/api/municipal/recommend"}, method = RequestMethod.GET)
	@ResponseBody
    public String municipalGetRecommend() {
		String aticle = "{\"input\":\"철수는 충남 대천에 살고 있는데, 은퇴하고 시설 관리 비즈니스를 하기를 원한다. " + 
				"시설 관리 관련 사업자들을 만나보니 관련 사업을 하려면 대체로 5 억은 필요하고, 이차보전 비율은 " + 
				"2% 이내가 좋다는 의견을 듣고 정부에서 운영하는 지자체 협약 지원정보를 검색한다.\"}";
        return masService.getRecommendMas(aticle); 
    }
		
}
