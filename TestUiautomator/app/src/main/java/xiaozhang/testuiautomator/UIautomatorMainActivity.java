package xiaozhang.testuiautomator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import xiaozhang.testuiautomator.util.CMDUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by zhang on 2016/6/4.
 */
public class UIautomatorMainActivity extends Activity implements View.OnClickListener {
    private Button main_bt1;
    private EditText main_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_uiautomator_main_layout);
        main_bt1= findViewById(R.id.main_bt1);
        main_bt1.setOnClickListener(this);
        main_edit= findViewById(R.id.main_edit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_bt1:
                Log.i("main_bt1","1");
                new UiautomatorThread().start();
                Log.i("main_bt1","2");
//                main_edit.setText("这是点击按钮，输入的文字！");
                break;
        }
    }

    /**
     * 运行uiautomator是个费时的操作，不应该放在主线程，因此另起一个线程运行
     */
    class UiautomatorThread extends Thread {
        @Override
        public void run() {
            super.run();
            String command=generateCommand("xiaozhang.testuiautomator", "UiautomatorMainActivityTest", "testAutoXianyuCheck");
            CMDUtils.CMD_Result rs= CMDUtils.runCMD(command,true,true);
            Log.e(TAG, "run: " + rs.error + "-------" + rs.success);
        }

        /**
         * 生成命令
         * @param pkgName 包名
         * @param clsName 类名
         * @param mtdName 方法名
         * @return
         */
        public  String generateCommand(String pkgName, String clsName, String mtdName) {
            String command = "am instrument --user 0 -w -r   -e debug false -e class '"
                    + pkgName + "." + clsName + "#" + mtdName + "' "
                    + pkgName + ".test/android.support.test.runner.AndroidJUnitRunner";
//            String command = "am instrument  --user 0 -w -r   -e debug false -e class "
//                    + pkgName + "." + clsName + "#" + mtdName + " "
//                    + pkgName + ".test/android.support.test.runner.AndroidJUnitRunner";
            Log.i("test1: ", command);
            return command;
        }
    }
}
