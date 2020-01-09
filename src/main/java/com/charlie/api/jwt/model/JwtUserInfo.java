package com.charlie.api.jwt.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USER_INFO")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class JwtUserInfo {

	@Id
	private String username;
	private String password;
	
	@Override
	public String toString() {
		return "{\"username\"=\"" + username + "\"}";
	}
	
}