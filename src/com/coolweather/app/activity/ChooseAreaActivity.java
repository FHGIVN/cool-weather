package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final String PROVINCE_LIST_ADDRESS = "http://flash.weather.com.cn/wmaps/xml/china.xml";
	public static final String CITY_COUNTRY_LIST_ADDRESS = "http://flash.weather.com.cn/wmaps/xml/";
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	/**省列表*/
	private List<Province> provinceList;
	/**市列表*/
	private List<City> cityList;
	/**县列表*/
	private List<Country> countryList;
	/**选中的省份*/
	private Province selectedProvince;
	/**选中的城市*/
	private City selectedCity;
	/**当前选中的级别*/
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					if(isIsland(selectedProvince)){  //若为西沙、南沙或钓鱼岛，传递固定的代号
						String queryParam = "";
						String pyName = selectedProvince.getProvincePyName();
						if (pyName.equals("xisha")){
							queryParam = "101310217";
						}else if(pyName.equals("nansha")){
							queryParam = "101310220";
						}else if(pyName.equals("diaoyudao")){
							queryParam = "101231001";
						}
						Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
						intent.putExtra("queryParam", queryParam);
						startActivity(intent);
					} else{
						queryCities();
					}
					
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					if(isSpecialArea(selectedProvince)){   //若为直辖市或特别行政区   传递代号
						String queryParam = selectedCity.getCityUrl();
						Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
						intent.putExtra("queryParam", queryParam);
						startActivity(intent);
					}else{
						queryCountries();
					}
					
				} else if(currentLevel == LEVEL_COUNTRY){   //若为县或县级市  传递代号
					String queryParam = countryList.get(index).getCountryUrl();
					Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					intent.putExtra("queryParam", queryParam);
					startActivity(intent);
				}
			}
		});
		queryProvinces(); // 加载省级数据
	}

	/**
	 * 查询所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
	 * */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/**
	 * 查询所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
	 * */
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince
				.getProvincePyName());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvincePyName(), "city");
		}
	}

	/**
	 * 查询所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
	 * */
	private void queryCountries() {
		countryList = coolWeatherDB.loadCountries(selectedCity.getCityPyName());
		if (countryList.size() > 0) {
			dataList.clear();
			for (Country country : countryList) {
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		} else {
			queryFromServer(selectedCity.getCityPyName(), "country");
		}
	}

	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * */
	private void queryFromServer(String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = CITY_COUNTRY_LIST_ADDRESS + code + ".xml"; // 市 、县
		} else {
			address = PROVINCE_LIST_ADDRESS; // 省
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getProvincePyName());
				} else if ("country".equals(type)) {
					result = Utility.handleCountriesResponse(coolWeatherDB,
							response, selectedCity.getCityPyName());
				}

				if (result) {
					// 通过runOnUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("country".equals(type)) {
								queryCountries();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

	}

	/** 显示进度对话框 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/** 关闭进度对话框 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/** 捕获Back按键，根据当前的级别来判断，此时应返回市列表、省列表还是直接退出 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTRY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
	
	/**判断在省列表中是否选中了西沙、南沙或钓鱼岛*/
	public boolean isIsland(Province province) {
		String name = province.getProvincePyName();
		if (name.equals("xisha") || name.equals("nanshadao")
				|| name.equals("diaoyudao")) {
			return true;
		}
		return false;
	}
	
	/**判断是否在北京、天津、重庆、上海、香港、澳门、台湾等一级目录下的二级目录中*/
	public boolean isSpecialArea(Province province) {
		String name = province.getProvincePyName();
		if (name.equals("beijing") || name.equals("tianjin")
				|| name.equals("chongqing") || name.equals("shanghai")
				|| name.equals("xianggang") || name.equals("aomen")
				|| name.equals("taiwan")) {
			return true;
		}
		return false;
	}
}
