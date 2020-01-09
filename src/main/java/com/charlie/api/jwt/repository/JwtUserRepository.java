package com.charlie.api.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.charlie.api.jwt.model.JwtUserInfo;

@RepositoryRestResource
public interface JwtUserRepository extends JpaRepository<JwtUserInfo, Long> {

	JwtUserInfo findByUsername(String username);

}