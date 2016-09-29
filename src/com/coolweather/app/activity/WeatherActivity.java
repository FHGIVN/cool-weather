package com.coolweather.app.activity;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements android.view.View.OnClickListener {
	//public static final String WEATHER_ADDRESS ="http://wthrcdn.etouch.cn/weather_mini?city=";
	public static final String WEATHER_ADDRESS ="http://wthrcdn.etouch.cn/weather_mini?citykey=";
	String queryParam;
	
	private LinearLayout weatherInfoLayout;
	/**用于显示城市名*/
	private TextView cityNameText;
	/**用于显示天气描述信息*/
	private TextView weatherDespText;
	/**用于显示温度1*/
	private TextView temp1Text;
	/**用于显示温度2*/
	private TextView temp2Text;
	/**用于显示当前日期*/
	private TextView currentDateText;
	/**切换城市按钮*/
	private Button switchCity;
	/**更新天气按钮*/
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//初始化各个控件
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		
		queryParam = getIntent().getStringExtra("queryParam");  //获得城市的代号
		if(!TextUtils.isEmpty(queryParam)){
			//有县级代号就去查询天气
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryFromServer(queryParam);
		}else{
			//没有天气代号就直接显示本地天气
			showWeather();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			queryFromServer(queryParam);

		default:
			break;
		}
	}
	
	/**查询传入的地址和类型去向服务器查询天气代号或天气信息*/
	private void queryFromServer(String queryParam){
//		try {
//			queryParam = URLEncoder.encode(queryParam, "utf-8");
//		    // 如有中文一定要加上，在接收方用相应字符转码即可
//		  } catch (UnsupportedEncodingException e) {
//		       e.printStackTrace();
//		   } 
		
		String address = WEATHER_ADDRESS + queryParam;
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						showWeather();
					}
				});
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(WeatherActivity.this, "同步失败", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}
	
	/**从SharedPreferences文件中读取存储的天气信息，并显示到界面上*/
	private void showWeather(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		currentDateText.setText(prefs.getString("current_date",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

}
