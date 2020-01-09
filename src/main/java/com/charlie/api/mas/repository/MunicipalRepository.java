package com.charlie.api.mas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.charlie.api.mas.model.Municipal;

@RepositoryRestResource
public interface MunicipalRepository extends JpaRepository<Municipal, Long> {

	Municipal findByRegion(String region);

}