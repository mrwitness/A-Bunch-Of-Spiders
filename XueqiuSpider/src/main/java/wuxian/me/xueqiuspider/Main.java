package wuxian.me.xueqiuspider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.xueqiuspider.biz.BizConfig;
import wuxian.me.xueqiuspider.biz.today.TodayTopSpider;
import wuxian.me.xueqiuspider.biz.today.TodayZhiboSpider;
import wuxian.me.xueqiuspider.model.Zhibo;
import wuxian.me.xueqiuspider.util.Helper;
import wuxian.me.xueqiuspider.util.SpringBeans;

/**
 * Created by wuxian on 24/7/2017.
 */
public class Main {

    private static void initEnv() {
        SpringBeans.init();
        BizConfig.init();
        //GroupConfig.init();

        SpringBeans.zhiboMapper().createNewTableIfNeed(new Zhibo());
    }

    public static void main(String[] args) {

        initEnv();

        LogManager.info("start Jobmanager");
        JobManagerConfig.init();
        JobManagerFactory.getJobManager().start();  //Must be called before any biz!!

        try {
            Thread.sleep(1000);  //现在有个bug 若SpiderSdk没有start完成 putJob会失败... 后面需要Fixme:
        } catch (InterruptedException e) {
            ;
        }

        //Helper.dispatchSpider(new TodayZhiboSpider());
    }
}
