package xiaozhang.testuiautomator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by zhang on 2016/6/4.
 * 使用Ui automator来测试 MainActivity
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UiautomatorAutoCheckTest {
    private UiDevice uiDevice;
    private static final int LAUNCH_TIMEOUT = 5000;//运行的时间
    private static final String XIANYU_APPLICATION_PACKAGE = "com.taobao.idlefish";
    private static final String TEST_APPLICATION_PACKAGE = "xiaozhang.testuiautomator";
    private static final String TAOBAO_APPLICATION_PACKAGE = "com.taobao.taobao";
    private static String account = "";
    private static String password = "";

    private Map<String, String> orderList = new HashMap<>();
    // @Before代替setUp方法，有多个依次执行   @After 代替tearDown方法   //uiautomatorviewer  自动化界面分析工具
    @Before
    public void startUiautomatorMainActivityHomeScreen() {

        Log.i("UiautomatorMainActivityTest","");
        //login登陆

        //初始化UiDevice实例
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        //从主屏幕
//        uiDevice.pressHome();

        //等待运行
//        final String launcherPackage = getLauncherPackageName();
//        assertThat(launcherPackage, notNullValue());
//        uiDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
        orderList.put("start","null");
    }




    @Test
    public void testAutoXianyuCheck() {
        Log.i("UiautomatorMainActivityTest","testAutoXianyuCheck");
        Bundle para= InstrumentationRegistry.getArguments();
        account = para.getString("account");//此时paraString="value""小蜜蜂1";//
        password = para.getString("password");//此时paraString="value""20";//"Aa123456";//
        if(login(account, password) != 200){
            uiDevice.pressHome();
            return;
        }
        int x = (int) (uiDevice.getDisplayWidth() *0.625);
        int y = (int) (uiDevice.getDisplayHeight()*0.5875);
        while (true){
            checkNewOrder(x,y);
        }
    }
    @Test //检测测试条件是不是为空
    public void checkPreconditions() {
        assertThat(uiDevice, notNullValue());
    }


    @After
    public void finishUiautomatorMainActivityHomeScreen() {
        uiDevice = null;

    }

    /**
     * 检测是否有新的订单被交易
     *
     * @return
     */
    private void checkNewOrder(int x,int y){
        List<UiObject2> tempOrderListObj = getUiObject2ById("indicator_itmes").getChildren();
        tempOrderListObj.get(3).click();
        testSleep(1000);

        List<UiObject2> orderListObj;
        //获取订单列表
        try {
            orderListObj = getUiObject2ById("fnml_list").getChildren();
        }catch (StaleObjectException e){
            return;
        }

        for (UiObject2 orderObj:orderListObj){
            try {
                //获取基本信息控件
                UiObject2 temp = orderObj.getChildren().get(0);
                //判断是否是聊天项
                if (temp.getChildren().size() < 4) continue;

                //检查未读消息的标志
                UiObject2 unread = temp.findObject(By.res(XIANYU_APPLICATION_PACKAGE, "msg_tag_debug_text_id"));

                //检查商品状态
                UiObject2 statusObj = temp.findObject(By.res(XIANYU_APPLICATION_PACKAGE, "vmmici_status"));
                String status = statusObj != null ? statusObj.getText() : "";
                if (!status.equals("等待卖家发货")) {//清楚未读状态
                    if (unread != null){
                        orderObj.click();
                        testSleep(1000);
                        getUiObject2ById("bar_left").click();
                        testSleep(1000);
                    }
                    continue;
                }
                //进入聊天记录详情
                orderObj.click();
                //进入宝贝详情
                getUiObject2ById("ln_order").click();
                //获取宝贝title及价格
                String title = getUiObject2ById("price").getText();
                String amount = getUiObject2ById("item_content").getText();
                int res = autoFinish(title, "入账" + amount.substring(1) + "元");
                if ( res == 200 ){//自动确认发货) {// || res == 404
                    getUiObject2ById("right_op").click();
                    getUiObject2ById("right_text").click();
                    //todo 点击确定
                    testSleep(3000);
                    uiDevice.click(x,y);
                    testSleep(3000);
                    getUiObject2ById("bar_left").click();
                    testSleep(1000);
                    getUiObject2ById("bar_left").click();
                }else {
                    getUiObject2ById("bar_left").click();
                    testSleep(1000);
                    getUiObject2ById("bar_left").click();
                }
            }catch (IndexOutOfBoundsException | StaleObjectException e){
                Log.i("checkNewOrder","");
            }
        }
    }

    /**
     * 获取运行的包
     *
     * @return
     */
    private String getLauncherPackageName() {
        //创建启动intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        //使用manage获取运行的包名
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return resolveInfo.activityInfo.packageName;
    }
    /**
     * 启动应用
     *
     * @return
     */
    private void startApp(String packageName) {
        //启动测试应用
        Context context = InstrumentationRegistry.getContext();//获取上下文
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        //等待应用程序出现
        uiDevice.wait(//等待5000秒
                Until.hasObject(By.pkg(packageName)//找到满足条件的包，取第一个
                        .depth(0)), LAUNCH_TIMEOUT);
    }
    /**
     * 登陆请求
     *
     * @return
     */
    private int login(String username, String password) {
        return UiautomatorTestApiClient.getInstance().login(username, password);
    }
    /**
     * 自动确认请求
     *
     * @return 200成功 其余失败
     */
    private int autoFinish(String title, String amount) {
        int res = UiautomatorTestApiClient.getInstance().autoFinish(title, amount);
        if (res == 200)
            return res;
        if (res == 403 && login(account, password) == 200)
            return UiautomatorTestApiClient.getInstance().autoFinish(title, amount);
        return res;
    }
    /**
     * 获取对应元素
     *
     * @return
     */
    private UiObject2 getUiObject2ById(String objId) {

        UiObject2 temp = null;
        while (temp == null){
            Log.i("getUiObject2ById",objId);
            temp = uiDevice.findObject(By.res(XIANYU_APPLICATION_PACKAGE, objId));
            testSleep(1000);
        }
        return temp;
    }
    private UiObject2 getUiObject2ByText(String text) {

        UiObject2 temp = null;
        while (temp == null){
            Log.i("getUiObject2ByText",text);
            temp = uiDevice.findObject(By.text(text));
            testSleep(1000);
        }
        return temp;
    }
    private void untilUiObjectInit(String packageName, String text) {
        while (!uiDevice.hasObject(By.text(text)) && !uiDevice.hasObject(By.res(packageName, text))){
            Log.i("untilUiObjectInit",text);
        }
    }
    private void testSleep(int time){
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
