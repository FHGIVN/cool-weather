package com.coolweather.app.model;

public class City {
	private int id;
	private String cityName;
	private String cityPyName;
	private String cityUrl;
	private int provinceId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityPyName() {
		return cityPyName;
	}

	public void setCityPyName(String cityPyName) {
		this.cityPyName = cityPyName;
	}

	public String getCityUrl() {
		return cityUrl;
	}

	public void setCityUrl(String cityUrl) {
		this.cityUrl = cityUrl;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

}
