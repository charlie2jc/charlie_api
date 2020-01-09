package com.charlie.api.mas.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.charlie.api.mas.model.Geo;
import com.charlie.api.mas.model.MasInfo;
import com.charlie.api.mas.model.Municipal;
import com.charlie.api.mas.parser.GeoCSVParser;
import com.charlie.api.mas.repository.MasInfoRepository;
import com.charlie.api.mas.repository.MunicipalRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MasService {
	
	@Autowired
    private MasInfoRepository masInfoRepository;
    
    @Autowired
    private MunicipalRepository municipalRepository;
    
    @Autowired
    private GeoCSVParser geoCSVParser;

    public String getMunicipalAll() {
    	String rtnJsonStr = "{\"data\":\"No data\"}";
    	List<MasInfo> masInfoList = masInfoRepository.findAll();
    	if(masInfoList != null) {
    		setCustomDataMasInfoList(masInfoList);
    		rtnJsonStr = convertModelToJson(masInfoList);
    	}
    	return rtnJsonStr;
    }
    
    public String getMunicipalFindOne(MasInfo reqMasInfo) {
    	String rtnJsonStr = "{\"data\":\"No data\"}";
    	if(reqMasInfo != null && StringUtils.isNotEmpty(reqMasInfo.getRegion())) {
    		Municipal municipal = municipalRepository.findByRegion(reqMasInfo.getRegion());
        	if(municipal != null && StringUtils.isNotEmpty(municipal.getRegionCd())) {
        		MasInfo masInfo = masInfoRepository.findByRegionCd(municipal.getRegionCd());
    	    	if(masInfo != null && masInfo.getMunicipal() != null && masInfo.getMunicipal().getRegion() != null) {
    	    		masInfo.setRegion(masInfo.getMunicipal().getRegion());
    	    		rtnJsonStr = convertModelToJson(masInfo);
    	    	}
        	}
    	}
    	return rtnJsonStr;
    }
    
    public String putMunicipalOne(MasInfo reqMasInfo) throws IOException {
    	String rtnJsonStr = "";
    	if(reqMasInfo != null && StringUtils.isNotEmpty(reqMasInfo.getRegion())) {
    		Municipal municipal = municipalRepository.findByRegion(reqMasInfo.getRegion());
    		if(municipal != null && StringUtils.isNotEmpty(municipal.getRegionCd())) {
    			MasInfo origingMasInfo = masInfoRepository.findByRegionCd(municipal.getRegionCd());
    			origingMasInfo.setTarget(reqMasInfo.getTarget());
    			origingMasInfo.setUsage(reqMasInfo.getUsage());
    			origingMasInfo.setLimit(reqMasInfo.getLimit());
    			origingMasInfo.setRate(reqMasInfo.getRate());
    			origingMasInfo.setInstitute(reqMasInfo.getInstitute());
    			origingMasInfo.setMgmt(reqMasInfo.getMgmt());
    			origingMasInfo.setReception(reqMasInfo.getReception());
    			
    			MasInfo savedMasInfo = masInfoRepository.save(origingMasInfo);
    			if(savedMasInfo != null && savedMasInfo.getMunicipal() != null && savedMasInfo.getMunicipal().getRegion() != null) {
    				savedMasInfo.setRegion(savedMasInfo.getMunicipal().getRegion());
    	    		rtnJsonStr = convertModelToJson(savedMasInfo);
    	    	}else {
    	    		throw new IOException("Fail saved");
    	    	}
    		}else {
    			throw new IOException("Not found region");
    		}
    	}else {
    		throw new IOException("Invalid Request");
    	}
    	return rtnJsonStr;
    }
    
    public String deleteMunicipalOne(MasInfo reqMasInfo) throws IOException {
    	String rtnJsonStr = "";
    	if(reqMasInfo != null && StringUtils.isNotEmpty(reqMasInfo.getRegion())) {
    		Municipal municipal = municipalRepository.findByRegion(reqMasInfo.getRegion());
    		if(municipal != null && StringUtils.isNotEmpty(municipal.getRegionCd())) {
    			MasInfo origingMasInfo = masInfoRepository.findByRegionCd(municipal.getRegionCd());
    			masInfoRepository.delete(origingMasInfo);
    			rtnJsonStr = "deleted "+reqMasInfo.getRegion();
    		}else {
    			throw new IOException("Not found region in municipal list- " + reqMasInfo.getRegion());
    		}
    	}else {
    		throw new IOException("Invalid Request");
    	}
    	return rtnJsonStr;
    }
    
    public String getMunicipalFindByLimitDescTopCnt(int cnt) {
    	List<MasInfo> masInfoList = masInfoRepository.findAll();
    	if(masInfoList != null) {
    		setCustomDataMasInfoList(masInfoList);
    	}
    	masInfoList.sort(Comparator.comparing(MasInfo::getLimitLong).reversed().thenComparing(MasInfo::getRateAvg));
    	
    	int realCnt = masInfoList.size() < cnt ? masInfoList.size() : cnt;
    	StringBuilder sb = new StringBuilder(); 
    	sb.append("{");
    	for(int i=0; i<realCnt; i++) {
    		sb.append(masInfoList.get(i).getRegion());
    		if(i!=realCnt-1) {sb.append(", ");}
    	}
    	sb.append("}");
    	return sb.toString();
    }
    
    public String getMunicipalFindByRateMinTopInstitute() {
    	List<MasInfo> masInfoList = masInfoRepository.findAll();
    	if(masInfoList != null) {
    		setCustomDataMasInfoList(masInfoList);
    	}
    	masInfoList.sort(Comparator.comparing(MasInfo::getRateMin));
    	return "{" + masInfoList.get(0).getInstitute() +"}";
    }
    
    public String getRecommendMas(String aticle) {
    	MasInfo reqMasinfo = getAticlePickWordExtraction(aticle);
    	reqMasinfo.setLimitLong(convertLimitLong(reqMasinfo.getLimit()));
    	reqMasinfo.setRateMin(convertRateMin(reqMasinfo.getRate()));
    	reqMasinfo.setRateMax(convertRateMax(reqMasinfo.getRate()));

    	Geo reqGeo = getLocationGeo(reqMasinfo.getRegion());
    	String rtnJsonStr = "{\"data\":\"No data\"}";
    	List<MasInfo> rtnMasInfoList = new ArrayList<MasInfo>();
    	Map<String, Geo> rtnGeoMap = geoCSVParser.geoDataInit();
    	List<MasInfo> masInfoList = masInfoRepository.findByUsageContains(reqMasinfo.getUsage());
    	if(masInfoList != null) {
    		setCustomDataMasInfoList(masInfoList);
    	}
    	for(MasInfo item : masInfoList) {
    		if(item.getLimitLong() >= reqMasinfo.getLimitLong()) {
    			if(item.getRateMax() >= reqMasinfo.getRateMax() && item.getRateMin() <= reqMasinfo.getRateMin()) {
    				item.setDistance(getDistance(reqGeo.getLatitude(), reqGeo.getLongitude(), rtnGeoMap.get(item.getRegionCd()).getLatitude(), rtnGeoMap.get(item.getRegionCd()).getLongitude(), "km"));
    				rtnMasInfoList.add(item);
    			}
    		}
    	}
    	if(rtnMasInfoList.size() > 0) {
    		rtnMasInfoList.sort(Comparator.comparing(MasInfo::getDistance));
    		Map<String, String> rtnMap = new LinkedHashMap<String, String>();
    		rtnMap.put("region", rtnMasInfoList.get(0).getRegionCd());
    		rtnMap.put("usage", rtnMasInfoList.get(0).getUsage());
        	rtnMap.put("limit", rtnMasInfoList.get(0).getLimit());
        	rtnMap.put("rate", rtnMasInfoList.get(0).getRate());
    		rtnJsonStr = convertModelToJson(rtnMap);
    	}
		return rtnJsonStr;
    }
    
    public MasInfo getAticlePickWordExtraction(String aticle) {
    	//기사 분석해서 요소 겟 하는 API 개발 필요. 
    	
    	
    	//샘플-기사분석
    	MasInfo reqMasinfo = new MasInfo();
		reqMasinfo.setRegion("대천");
    	reqMasinfo.setUsage("시설");
    	reqMasinfo.setLimit("5억원");
    	reqMasinfo.setRate("2%");
    	return reqMasinfo; 
    }
    
    public Geo getLocationGeo(String location) {
    	//지역명으로 좌표 가져오는 API개발 필요
    	
    	
    	//샘플-충남 대천군 임시 좌표
    	Geo reqGeo = new Geo(location, "", 36.5184, 126.8);
    	return reqGeo;
    }
    
    public void setCustomDataMasInfoList(List<MasInfo> masInfoList) {
    	for(MasInfo masInfo : masInfoList) {
    		masInfo.setRegion(masInfo.getMunicipal().getRegion());
    		masInfo.setLimitLong(convertLimitLong(masInfo.getLimit()));
    		masInfo.setRateAvg(convertRateAvg(masInfo.getRate()));
    		masInfo.setRateMin(convertRateMin(masInfo.getRate()));
    		masInfo.setRateMax(convertRateMax(masInfo.getRate()));
    	}
    }
    
    public String convertModelToJson(Object model) {
    	String rtnJsonStr = "";
    	ObjectMapper mapper = new ObjectMapper();
		try {
			rtnJsonStr = mapper.writeValueAsString(model);
		} catch (JsonProcessingException e) {
			log.error("MasService.convertModelToJson.JsonProcessingException - ", e.getMessage());
		}
		return rtnJsonStr;
    }
    
    public Long convertLimitLong(String limitStr) {
		String digitStr = "";
		String unitStr = "";
		String convertStr = "0";
		if(limitStr.contains("원")) {
			char[] charArr = limitStr.split("원")[0].toCharArray();
			for(char c : charArr) {
				if(Character.isDigit(c)) {
					digitStr+=c;
				}else {
					switch (c) {
						case '십':
							unitStr+="0";
							break;
						case '백':
							unitStr+="00";
							break;
						case '천':
							unitStr+="000";
							break;
						case '만':
							unitStr+="0000";
							break;
						case '억':
							unitStr+="00000000";
							break;
						default:
							break;
					}
				}
			}
			convertStr = digitStr + unitStr;
		}
		return Long.parseLong(convertStr);
	}
    
    public Float convertRateAvg(String rateStr) {
		Float rateAvg = 0f;
		if(rateStr.contains("%")) {
			if(rateStr.contains("~")) {
				String[] strArr = rateStr.split("~");
				for(String tmpRate : strArr) {
					rateAvg += Float.parseFloat(tmpRate.split("%")[0]);
				}
				rateAvg /= strArr.length;
			}else {
				rateAvg = Float.parseFloat(rateStr.split("%")[0]);
			}
		}
		return rateAvg;
	}
	
    public Float convertRateMin(String rateStr) {
		Float rateMin = 0f;
		if(rateStr.contains("%")) {
			if(rateStr.contains("~")) {
				rateMin = Float.parseFloat(rateStr.split("~")[0].split("%")[0]);
			}else {
				rateMin = Float.parseFloat(rateStr.split("%")[0]);
			}
		}
		return rateMin;
	}
    
    public Float convertRateMax(String rateStr) {
		Float rateMax = 0f;
		if(rateStr.contains("%")) {
			if(rateStr.contains("~")) {
				rateMax = Float.parseFloat(rateStr.split("~")[1].split("%")[0]);
			}else {
				rateMax = Float.parseFloat(rateStr.split("%")[0]);
			}
		}
		return rateMax;
	}
    
    public double getDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
    	if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			if (unit.equalsIgnoreCase("km")) {
				dist = dist * 1.609344;
			} else if (unit.equalsIgnoreCase("m")) {
				dist = dist * 1609.344;
			}
			return (dist);
		}
	}

}
