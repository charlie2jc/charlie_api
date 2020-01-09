package com.charlie.api.mas.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
public class Geo {

	private String region;
	private String regionCd;
	
	private double latitude;
	private double longitude;
}