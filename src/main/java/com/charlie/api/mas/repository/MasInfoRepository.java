package com.charlie.api.mas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.charlie.api.mas.model.MasInfo;

@RepositoryRestResource
public interface MasInfoRepository extends JpaRepository<MasInfo, Long> {

	MasInfo findByRegionCd(String regionCd);
	
	List<MasInfo> findByUsageContains(String usage);

}