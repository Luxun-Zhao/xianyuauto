package xiaozhang.testuiautomator.util;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 执行命令
 */
public class JSONBuilder {

    private static final String TAG = "JSONBuilder";

    private JSONObject jsonObject = new JSONObject();

    public JSONBuilder append(String key, Object value) {
        try {
            jsonObject.put(key, value);
        } catch (Exception e) {
            Log.e("JSONBuilder#append", e.getMessage());
        }
        return this;
    }

    public JSONObject buildJSON() {
        return jsonObject;
    }

    public String buildJSONString() {
        return jsonObject.toString();
    }

    public RequestBody buildRequestBody() {
        return RequestBody.create(MediaType.parse("application/json"), buildJSONString());
    }



}