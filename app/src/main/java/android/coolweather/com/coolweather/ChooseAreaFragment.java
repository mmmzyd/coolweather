package android.coolweather.com.coolweather;

import android.app.ProgressDialog;
import android.coolweather.com.coolweather.db.City;
import android.coolweather.com.coolweather.db.County;
import android.coolweather.com.coolweather.db.Province;
import android.coolweather.com.coolweather.util.HttpUtil;
import android.coolweather.com.coolweather.util.LogUtils;
import android.coolweather.com.coolweather.util.Utility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    // 省的等级常量
    public static final int LEVEL_PROVINCE = 0;

    // 市的等级常量
    public static final int LEVEL_CITY = 1;

    // 县的等级常量
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);

        // 适配器
        // 要在哪个控件展示内容就设置哪个控件的适配器
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 给listView设置点击事件，点击查看各级省市县
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    // 如果选中等级为省，就查询选中省内的所有城市
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY){
                    // 如果选中等级为城市，就查询选中城市内的所有县
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });

        // 给返回按钮添加点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    // 点击后如果发现等级为县，就返回上一级市
                    queryCities();
                } else if(currentLevel == LEVEL_CITY){
                    // 点击后如果发现等级为市，就返回上一级省
                    queryProvinces();
                }
            }
        });
        // 初始化的时候加载省级数据
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有再到服务器上查询
     */
    private void queryProvinces(){
        // 首先将投布局的标题设置为中国
        titleText.setText("中国");
        // 将返回按钮隐藏起来
        backButton.setVisibility(View.GONE);
        // 调用LitePal的查询接口来从数据库中读取省级数据
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0){
            // 如果读取到了就直接将数据显示到界面上
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            // 设置等级常量为 省级
            currentLevel = LEVEL_PROVINCE;
        } else {
            // 如果没有读取到就组装出一个请求地址，调用queryFromServer()方法从服务器上查询数据
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有再到服务器上查询
     */
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProviceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有再到服务器上查询
     */
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?",
                String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProviceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        // 向服务器发送请求，响应的数据会回调到onResponse()方法中
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                // 调用Utility的handleProvincesResponse()方法解析和处理服务器返回的数据，并存储到数据库中
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                } else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            } else if("city".equals(type)){
                                queryCities();
                            } else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d("onFailure",e.getMessage());
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}