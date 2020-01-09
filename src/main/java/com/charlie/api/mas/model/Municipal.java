package com.charlie.api.mas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MUNICIPAL_INFO")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
public class Municipal {

	@Id
	@Column(name = "regionCdFk")
    private String regionCd;

	private String region;
}