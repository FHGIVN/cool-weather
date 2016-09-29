package com.coolweather.app.util;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
							url = xmlPullParser.getAttributeValue(17);
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
	
	/**
	 * 解析服务器返回的JSON数据,并将解析的数据存储到本地
	 * */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject object = new JSONObject(response);
			JSONObject data = object.getJSONObject("data");
			String cityName = data.getString("city"); 
			JSONArray forecast = data.getJSONArray("forecast");
			String temp1 = ((JSONObject)(forecast.get(0))).getString("high");
			String temp2 = ((JSONObject)(forecast.get(0))).getString("low");
			String weatherDesp = ((JSONObject)(forecast.get(0))).getString("type");
			saveWeatherInfo(context,cityName,temp1,temp2,weatherDesp);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 * */
	public static void saveWeatherInfo(Context context,String cityName,String temp1,String temp2,String weatherDesp){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		//editor.putString("url_code",urlCode);
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("temp1", "最"+temp2);  //低温在左
		editor.putString("temp2", "最"+temp1);  //高温在右
		editor.putString("weather_desp", weatherDesp);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
