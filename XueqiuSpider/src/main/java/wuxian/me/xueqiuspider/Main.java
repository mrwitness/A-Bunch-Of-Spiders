package wuxian.me.xueqiuspider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.ClassHelper;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.distribute.SpiderClassChecker;
import wuxian.me.spidersdk.distribute.SpiderMethodManager;
import wuxian.me.spidersdk.distribute.SpiderMethodTuple;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.xueqiuspider.biz.BizConfig;
import wuxian.me.xueqiuspider.biz.today.TodayTopSpider;
import wuxian.me.xueqiuspider.biz.today.TodayZhiboSpider;
import wuxian.me.xueqiuspider.model.Zhibo;
import wuxian.me.xueqiuspider.save.ZhiboConfig;
import wuxian.me.xueqiuspider.save.ZhiboSaver;
import wuxian.me.xueqiuspider.util.Helper;
import wuxian.me.xueqiuspider.util.SpringBeans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wuxian on 24/7/2017.
 */
public class Main {

    private static void initEnv() {
        SpringBeans.init();
        BizConfig.init();
        ZhiboConfig.init();

        SpringBeans.zhiboMapper().createNewTableIfNeed(new Zhibo());

        SignalManager.registerOnSystemKill(ZhiboSaver.getInstance());
    }

    public static void main(String[] args) throws Exception {

        initEnv();

        LogManager.info("start Jobmanager");
        JobManagerConfig.init();
        LogManager.info("scan:" + JobManagerConfig.spiderScan);
        JobManagerFactory.getJobManager().start();  //Must be called before any biz!!

        try {
            Thread.sleep(1000);  //现在有个bug 若SpiderSdk没有start完成 putJob会失败... 后面需要Fixme:
        } catch (InterruptedException e) {
            ;
        }

        //LogManager.info(TodayZhiboSpider.fromUrlNode(new TodayZhiboSpider(87L).toUrlNode()).toString());
        Helper.dispatchSpider(new TodayZhiboSpider());
    }
}
