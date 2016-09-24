package com.coolweather.app.model;

public class Country {
	private int id;
	private String countryName;
	private String countryUrl;
	private int cityId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getCountryUrl() {
		return countryUrl;
	}
	public void setCountryUrl(String countryUrl) {
		this.countryUrl = countryUrl;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	
}
