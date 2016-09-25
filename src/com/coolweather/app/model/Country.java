package com.coolweather.app.model;

public class Country {
	private int id;
	private String countryName;
	private String countryUrl;
	private String belongToCity;

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

	public String getBelongToCity() {
		return belongToCity;
	}

	public void setBelongToCity(String belongToCity) {
		this.belongToCity = belongToCity;
	}

}
