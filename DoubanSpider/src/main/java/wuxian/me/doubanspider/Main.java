package wuxian.me.doubanspider;

import wuxian.me.doubanspider.biz.BizConfig;
import wuxian.me.doubanspider.biz.group.GroupListSpider;
import wuxian.me.doubanspider.biz.group.GroupTopicSpider;
import wuxian.me.doubanspider.model.GroupTiezi;
import wuxian.me.doubanspider.save.GroupConfig;
import wuxian.me.doubanspider.save.GroupTieziSaver;
import wuxian.me.doubanspider.util.Helper;
import wuxian.me.doubanspider.util.SpringBeans;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.anti.Fail;
import wuxian.me.spidersdk.manager.JobManagerFactory;

/**
 * Created by wuxian on 12/7/2017.
 */
public class Main {

    private static void initEnv() {

        SpringBeans.init();
        BizConfig.init();
        GroupConfig.init();

        SpringBeans.groupTieziMapper().createNewTableIfNeed(new GroupTiezi());

        SignalManager.registerOnSystemKill(GroupTieziSaver.getInstance());
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

        //276209:杭州租房一族
        //145219 杭州出租 租房 中介免入
        //HZhome 杭州租房小组
        //shanghaizufang  上海租房
        Helper.dispatchSpider(new GroupListSpider("145219", 0));
        //Helper.dispatchSpider(new GroupListSpider("HZhome", 0));
        //Helper.dispatchSpider(new GroupListSpider("276209", 0));
        //Helper.dispatchSpider(new GroupTopicSpider("105921703",105921703L));

    }
}
