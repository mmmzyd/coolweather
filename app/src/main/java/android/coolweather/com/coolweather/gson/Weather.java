package android.coolweather.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * 这是一个总的实体类，用于引用刚刚创建的各个实体类
 */
public class Weather {

    // 状态:成功返回ok;失败返回具体原因
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    // daily_forecast中包含的是一个数组，因此用List集合来引用Forecast类
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
