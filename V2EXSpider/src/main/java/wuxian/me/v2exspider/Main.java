package wuxian.me.v2exspider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.v2exspider.biz.BizConfig;
import wuxian.me.v2exspider.biz.career.CareerSpider;
import wuxian.me.v2exspider.biz.jobs.JobSpider;
import wuxian.me.v2exspider.model.Career;
import wuxian.me.v2exspider.model.Job;
import wuxian.me.v2exspider.save.TieziSaver;
import wuxian.me.v2exspider.save.GroupConfig;
import wuxian.me.v2exspider.util.Helper;
import wuxian.me.v2exspider.util.SpringBeans;

/**
 * Created by wuxian on 26/7/2017.
 */
public class Main {

    private static void initEnv() {
        SpringBeans.init();
        BizConfig.init();
        GroupConfig.init();

        SpringBeans.tieziMapper().createNewTableIfNeed(new Career());
        SpringBeans.tieziMapper().createNewTableIfNeed(new Job());

        SignalManager.registerOnSystemKill(TieziSaver.getInstance());
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
        //Helper.dispatchSpider(new CareerSpider(2));
        Helper.dispatchSpider(new JobSpider(2));

        //LogManager.info(CareerSpider.fromUrlNode(new CareerSpider(1).toUrlNode()).toString());
    }

}
