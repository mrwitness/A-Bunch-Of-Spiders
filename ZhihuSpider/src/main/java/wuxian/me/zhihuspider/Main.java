package wuxian.me.zhihuspider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.zhihuspider.biz.BizConfig;
import wuxian.me.zhihuspider.biz.activity.UserActivitySpider;
import wuxian.me.zhihuspider.biz.behavior.PraiseAnswerSpider;
import wuxian.me.zhihuspider.model.FollowModel;
import wuxian.me.zhihuspider.model.VoteupModel;
import wuxian.me.zhihuspider.save.FollowSaver;
import wuxian.me.zhihuspider.save.VoteupSaver;
import wuxian.me.zhihuspider.util.Helper;
import wuxian.me.zhihuspider.util.SpringBeans;

/**
 * Created by wuxian on 1/9/2017.
 */
public class Main {

    private static void initEnv() {

        SpringBeans.init();
        BizConfig.init();

        SpringBeans.voteup().createNewTableIfNeed(new VoteupModel());
        SpringBeans.follow().createNewTableIfNeed(new FollowModel());

        SignalManager.registerOnSystemKill(VoteupSaver.getInstance());
        SignalManager.registerOnSystemKill(FollowSaver.getInstance());

    }

    public static void main(String[] args) {
        initEnv();
        LogManager.info("start Jobmanager");
        JobManagerConfig.init();
        JobManagerFactory.getJobManager().start();

        Helper.dispatchSpider(new UserActivitySpider("shen-mi-94", 1504633145L));
    }
}
