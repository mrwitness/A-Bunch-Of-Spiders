package wuxian.me.tongchengspider.biz;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.tongchengspider.biz.rent.Rent;
import wuxian.me.tongchengspider.biz.rent.RentListSpider;
import wuxian.me.tongchengspider.util.Helper;

/**
 * Created by wuxian on 21/8/2017.
 */
public class Main {

    private static void initEnv() {
        /*
        SpringBeans.init();
        BizConfig.init();
        GroupConfig.init();

        SpringBeans.groupTieziMapper().createNewTableIfNeed(new GroupTiezi());
        SignalManager.registerOnSystemKill(GroupTieziSaver.getInstance());
        */
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

        Helper.dispatchSpider(new RentListSpider(Rent.wenerlu.name(), 1));


    }
}
