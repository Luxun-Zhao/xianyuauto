package xiaozhang.testuiautomator;

import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xiaozhang.testuiautomator.util.JSONBuilder;

/**
 * Created by zhang on 2016/6/4.
 * 使用Ui automator来测试 MainActivity
 *
 */
public class UiautomatorTestApiClient {
    private UiDevice uiDevice;
    private static final int TIMEOUT_CONNECTION = 60;
    private static final int TIMEOUT_READ = 60;
    private static UiautomatorTestApiClient instance = null;

    public static UiautomatorTestApiClient getInstance(){
        if (instance == null){
            instance = new UiautomatorTestApiClient();
        }
        return instance;
    }
    private String baseUrl = "https://api.usdt.com.co";//"https://holiday-uat.marbleio.tech";//

    private OkHttpClient okHttpClient = null;
    private UiautomatorTestApiModel apiModel = null;
    List<Cookie> staticCookies=new ArrayList<>();
    private String csrftoken = "";

    private UiautomatorTestApiClient(){

        okHttpClient = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                if (staticCookies != null)
                    return staticCookies;
                return new ArrayList<Cookie>();
            }

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                staticCookies.addAll(cookies);
                for (Cookie cookie:cookies){
                    if (cookie.name().equals("csrftoken")){
                        csrftoken = cookie.value();
                        break;
                    }
                }
            }
        }).addNetworkInterceptor(new LogInterceptor())
        .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        apiModel = retrofit.create(UiautomatorTestApiModel.class);
    }
    /**
     * 设置Header头
     * @param headersParams
     * @return
     */
    private Headers setHeaders(Map<String,Object> headersParams){
        Headers headers = null;

        Headers.Builder headerBuilder = new Headers.Builder();

        if(headersParams != null){
            for (String key:headersParams.keySet()){
                headerBuilder.add(key,headersParams.get(key).toString());
            }
        }

        headers = headerBuilder.build();

        return headers;
    }

    public int login(String username, String password){
        RequestBody request = new JSONBuilder().append("username", username).append("password", password).buildRequestBody();

        try {
            Call<ResponseBody> call = apiModel.login(request, csrftoken);
            Response<ResponseBody> response = call.execute();
//            Log.i("UiautomatorTestApiClient-login", response.code() + " " + response.body().string());
            return response.code();
        } catch (IOException e) {
            Log.e("UiautomatorTestApiClient-login", e.getMessage());
            return 0;
        }
    }
    public int autoFinish(String title, String amount){
        RequestBody request = new JSONBuilder().append("content", amount).append("remarks", title).buildRequestBody();

        try {
            Call<ResponseBody> call = apiModel.autoFinish(request, csrftoken);
            Response<ResponseBody> response = call.execute();
            Log.i("UiautomatorTestApiClient-autoFinish", response.code() + "");// + " " + response.body().string());
            return response.code();
        } catch (IOException e) {
            Log.e("UiautomatorTestApiClient-autoFinish", e.getMessage());
            return 0;
        }
    }
}
