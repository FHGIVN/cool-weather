# cool-weather
# 各个省：省-市-县 三级
# 北京、天津、重庆、上海、香港、澳门、台湾、海南： 二级
# 西沙、南沙、钓鱼岛：一级

		Entity          DB
省  id					id
    provinceName		province_name
	provincePyName      province_py_name
	
市	id					id
	cityName			city_name
	cityPyName			city_py_name
	cityUrl				city_url
	belongToProvince	belong_to_province
	
县  id					id
    countryName			country_name
	countryUrl			country_url
	belongToCity		belong_to_city