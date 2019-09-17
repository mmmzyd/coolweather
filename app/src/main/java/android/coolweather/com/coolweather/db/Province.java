package android.coolweather.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 省的实体类
 */
public class Province extends DataSupport {

    // 省份id
    private int id;

    // 省份名称
    private String provinceName;

    // 省份代号
    private int proviceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProviceCode() {
        return proviceCode;
    }

    public void setProviceCode(int proviceCode) {
        this.proviceCode = proviceCode;
    }
}
