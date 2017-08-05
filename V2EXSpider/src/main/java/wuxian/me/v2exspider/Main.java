package wuxian.me.v2exspider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.v2exspider.biz.BizConfig;
import wuxian.me.v2exspider.biz.career.CareerSpider;
import wuxian.me.v2exspider.model.BaseTiezi;
import wuxian.me.v2exspider.util.Helper;
import wuxian.me.v2exspider.util.SpringBeans;

/**
 * Created by wuxian on 26/7/2017.
 */
public class Main {

    private static void initEnv() {
        SpringBeans.init();
        BizConfig.init();
        //GroupConfig.init();

        SpringBeans.careerTieziMapper().createNewTableIfNeed(new BaseTiezi());

        //SignalManager.registerOnSystemKill(GroupTieziSaver.getInstance());
    }

    public static void main(String[] args) {

        initEnv();
        LogManager.info("start Jobmanager");
        JobManagerConfig.init();
        JobManagerFactory.getJobManager().start();  //Must be called before any biz!!

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            ;
        }

        //Helper.dispatchSpider(new CareerSpider(3));
    }

}
