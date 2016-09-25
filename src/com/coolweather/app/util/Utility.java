package com.coolweather.app.util;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

public class Utility {
	/**
	 * 解析和处理服务器返回的省级数据
	 * */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser xmlPullParser = factory.newPullParser();
				xmlPullParser.setInput(new StringReader(response));
				int eventType = xmlPullParser.getEventType();
				String quName = "";
				String pyName = "";
				
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String nodeName = xmlPullParser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG: {
						if("city".equals(nodeName)){
							quName = xmlPullParser.getAttributeValue(0);
							pyName = xmlPullParser.getAttributeValue(1);
						};
						break;
					}
					case XmlPullParser.END_TAG: {
						if("city".equals(nodeName)){
							Province province = new Province();
							province.setProvinceName(quName);
							province.setProvincePyName(pyName);
							//将解析出来的数据存储到Province表
							coolWeatherDB.saveProvince(province);
						}
						break;
					}
					default:
						break;
					}
					eventType = xmlPullParser.next();
				}
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 * */
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response ,String belongToProvince){
		if(!TextUtils.isEmpty(response)){
			try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser xmlPullParser = factory.newPullParser();
				xmlPullParser.setInput(new StringReader(response));
				int eventType = xmlPullParser.getEventType();
				String cityname = "";
				String pyName = "";
				String url = "";
				
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String nodeName = xmlPullParser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG: {
						if("city".equals(nodeName)){
							cityname = xmlPullParser.getAttributeValue(2);
							pyName = xmlPullParser.getAttributeValue(5);
							url = xmlPullParser.getAttributeValue(17);
						};
						break;
					}
					case XmlPullParser.END_TAG: {
						if("city".equals(nodeName)){
							City city = new City();
							city.setCityName(cityname);
							city.setCityPyName(pyName);
							city.setCityUrl(url);
							city.setBelongToProvince(belongToProvince);
							//将解析出来的数据存储到City表
							coolWeatherDB.saveCity(city);
						}
						break;
					}
					default:
						break;
					}
					eventType = xmlPullParser.next();
				}
				return true;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 * */
	public static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,String response,String belongToCity){
		if(!TextUtils.isEmpty(response)){
			try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				XmlPullParser xmlPullParser = factory.newPullParser();
				xmlPullParser.setInput(new StringReader(response));
				int eventType = xmlPullParser.getEventType();
				String cityname = "";
				String url = "";
				
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String nodeName = xmlPullParser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG: {
						if("city".equals(nodeName)){
							cityname = xmlPullParser.getAttributeValue(2);
							url = xmlPullParser.getAttributeValue(16);
						};
						break;
					}
					case XmlPullParser.END_TAG: {
						if("city".equals(nodeName)){
							Country country = new Country();
							country.setCountryName(cityname);
							country.setCountryUrl(url);
							country.setBelongToCity(belongToCity);
							//将解析出来的数据存储到Country表
							coolWeatherDB.saveCountry(country);
						}
						break;
					}
					default:
						break;
					}
					eventType = xmlPullParser.next();
				}
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
