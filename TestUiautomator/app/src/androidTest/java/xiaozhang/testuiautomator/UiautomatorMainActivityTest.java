package xiaozhang.testuiautomator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
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
public class UiautomatorMainActivityTest {
    private UiDevice uiDevice;
    private static final int LAUNCH_TIMEOUT = 5000;//运行的时间
    private static final String XIANYU_APPLICATION_PACKAGE = "com.taobao.idlefish";
    private static final String TAOBAO_APPLICATION_PACKAGE = "com.taobao.taobao";
    private static final String account = "小蜜蜂";
    private static final String password = "20";
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
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        uiDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }




    @Test
    public void testAutoXianyuCheck() {
        Log.i("UiautomatorMainActivityTest","testAutoXianyuCheck");
        login();
//        startApp(XIANYU_APPLICATION_PACKAGE);
//        List<UiObject2> temp = getUiObject2ById("id_pager").getChildren();
//        List<UiObject2> temp1 = getUiObject2ById("id_indicator").getChildren();
//        getUiObject2ById("indicator_itmes").getChildren().get(4).click();
//        getUiObject2ByText("我卖出的").click();
        //刷新咸菜菜单列表
        uiDevice.swipe(500, 500, 500, 1000, 10);
        //等待刷新完成
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取订单列表
        List<UiObject2> orderListObj = getUiObject2ById("list_view").getChildren();
        orderListObj.remove(0);
        Map<String, String> orderList = new HashMap<>();
        for (UiObject2 orderObj:orderListObj){
            List<UiObject2> orderObjStatus = orderObj.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().get(1).getChildren();
            //获取订单内容，只有等待卖家发货的订单放入订单内容
            String title = orderObjStatus.get(0).getChildren().get(0).getText();
            String amount = orderObjStatus.get(1).getChildren().get(0).getChildren().get(0).getText();
            String status = orderObjStatus.get(2).getChildren().get(0).getText();
            if (status.equals("等待卖家发货")){
                orderList.put(title, amount);
                autoFinish(title, amount);
            }
        }
        UiObject2 temp2 = getUiObject2ById("list_view");
    }
    @Test
    public void testChangeText_sameActivity() {
//        //打开咸鱼
//        startApp(XIANYU_APPLICATION_PACKAGE);
//        //点开用户查询
//        getUiObject2ById("search_bar_text").click();
//        //选择搜索类型为用户
//        getUiObject2ById("type_entry").click();
//        getUiObject2ById("switch_user").click();
//        //输入用户
//        getUiObject2ById("search_term").setText("kadajzhao");
//        //点击搜索
//        getUiObject2ById("search_button").click();
//        //点击进入店主首页
//        getUiObject2ById("root").click();
//        //查看店主宝贝列表
//        getUiObject2ByText("宝贝").click();
//        //点击金额
//        getUiObject2ByText("100").click();
//        //点击我想要
//        getUiObject2ByText("我想要").click();
//        getUiObject2ByText("立即购买").click();
//        getUiObject2ByText("确定").click();
//        untilUiObjectInit(XIANYU_APPLICATION_PACKAGE,"flybird_layout");

        //打开淘宝
//        startApp(TAOBAO_APPLICATION_PACKAGE);
//        getUiObject2ByText("我的淘宝").click();
//        getUiObject2ByText("我的淘宝").click();

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
     * 登陆
     *
     * @return
     */
    private void login() {
        UiautomatorTestApiClient.getInstance().login();
    }
    /**
     * 自动确认
     *
     * @return
     */
    private void autoFinish(String title, String amount) {
        UiautomatorTestApiClient.getInstance().autoFinish(title, amount);
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
        }
        return temp;
    }
    private UiObject2 getUiObject2ByClass(String className) {

        UiObject2 temp = null;
        while (temp == null){
            Log.i("getUiObject2ByClass",className);
            temp = uiDevice.findObject(By.clazz(className));
        }
        return temp;
    }
    private UiObject2 getUiObject2ByText(String text) {

        UiObject2 temp = null;
        while (temp == null){
            Log.i("getUiObject2ByText",text);
            temp = uiDevice.findObject(By.text(text));
        }
        return temp;
    }
    private void untilUiObjectInit(String packageName, String text) {
        while (!uiDevice.hasObject(By.text(text)) && !uiDevice.hasObject(By.res(packageName, text))){
            Log.i("untilUiObjectInit",text);
        }
    }

}
