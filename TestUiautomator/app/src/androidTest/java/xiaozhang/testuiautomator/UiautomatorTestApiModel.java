package xiaozhang.testuiautomator;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by zhang on 2016/6/4.
 * 使用Ui automator来测试 MainActivity
 *
 */
public interface UiautomatorTestApiModel {
    @POST("/user/login/")
    Call<ResponseBody> login(@Body RequestBody request, @Header("x-csrftoken") String csrftoken);


    @POST("/bill/auto_finish/")
    Call<ResponseBody> autoFinish(@Body RequestBody request, @Header("x-csrftoken") String csrftoken);

}
