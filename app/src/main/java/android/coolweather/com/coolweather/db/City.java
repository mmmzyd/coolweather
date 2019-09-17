package android.coolweather.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 城市的实体类
 */
public class City extends DataSupport {

    // 城市id
    private int id;

    // 城市名称
    private String cityName;

    // 城市代号
    private int cityCode;

    // 当前城市所属省份的id
    private int provinceID;

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

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }
}
