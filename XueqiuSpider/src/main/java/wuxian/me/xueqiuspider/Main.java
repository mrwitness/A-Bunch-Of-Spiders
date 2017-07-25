package wuxian.me.xueqiuspider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.xueqiuspider.biz.today.TodayTopSpider;
import wuxian.me.xueqiuspider.biz.today.TodayZhiboSpider;
import wuxian.me.xueqiuspider.util.Helper;

/**
 * Created by wuxian on 24/7/2017.
 */
public class Main {

    public static void main(String[] args) {
        LogManager.info("start Jobmanager");
        JobManagerConfig.init();
        JobManagerFactory.getJobManager().start();  //Must be called before any biz!!

        try {
            Thread.sleep(1000);  //现在有个bug 若SpiderSdk没有start完成 putJob会失败... 后面需要Fixme:
        } catch (InterruptedException e) {
            ;
        }

        Helper.dispatchSpider(new TodayTopSpider());
    }
}
