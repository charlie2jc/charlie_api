package com.charlie.api.mas.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "MAS_INFO", indexes = {@Index(columnList="regionCd")})
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter @ToString
public class MasInfo extends BaseTimeEntity {

	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

	@Transient
	private String region;
	
	@JsonIgnore
	private String regionCd;
    
    private String target;
    
    private String usage;
    
    @Column(name = "limited")
    private String limit;
    
    @Transient
    @JsonIgnore
	private long limitLong;
    
    private String rate;
    
    @Transient
    @JsonIgnore
	private float rateAvg;
    
    @Transient
    @JsonIgnore
	private float rateMin;
    
    @Transient
    @JsonIgnore
	private float rateMax;
    
    private String institute;
    
    private String mgmt;
    
    private String reception;
    
    @Transient
    @JsonIgnore
    private double distance;
    
    @JsonIgnore
    @OneToOne(cascade = {CascadeType.ALL})
//    @JoinTable(name = "MUNICIPAL_INFO",
//      joinColumns = @JoinColumn(name = "regionCd"),
//      inverseJoinColumns = @JoinColumn(name = "regionCdChild"))
    @JoinColumn(name = "regionCdFk")
    private Municipal municipal;

    public MasInfo(String regionCd, String target, String usage, String limit, String rate, String institute, String mgmt, String reception, Municipal municipal) {
    	this.regionCd = regionCd;
    	this.target = target;
    	this.usage = usage;
    	this.limit = limit;
    	this.rate = rate;
    	this.institute = institute;
    	this.mgmt = mgmt;
    	this.reception = reception;
    	this.municipal = municipal;
    }
    
    public MasInfo(String region) {
    	this.region = region;
    }
}