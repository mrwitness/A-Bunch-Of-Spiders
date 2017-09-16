package wuxian.me.lianjiaspider;

import wuxian.me.lianjiaspider.api.touming.QuEnum;
import wuxian.me.lianjiaspider.biz.BizConfig;
import wuxian.me.lianjiaspider.biz.touming.BaseToumingSpider;
import wuxian.me.lianjiaspider.biz.touming.ToumingConfig;
import wuxian.me.lianjiaspider.biz.touming.ershou.QuDetailSpider;
import wuxian.me.lianjiaspider.biz.touming.ershou.QuListSpider;
import wuxian.me.lianjiaspider.biz.touming.xinfang.NewQuDetailSpider;
import wuxian.me.lianjiaspider.biz.touming.xinfang.NewQuListSpider;
import wuxian.me.lianjiaspider.model.Loupan;
import wuxian.me.lianjiaspider.model.Xiaoqu;
import wuxian.me.lianjiaspider.model.XiaoquSell;
import wuxian.me.lianjiaspider.model.touming.NewQu;
import wuxian.me.lianjiaspider.model.touming.NewquDetail;
import wuxian.me.lianjiaspider.model.touming.NewquPrice;
import wuxian.me.lianjiaspider.model.touming.Qu;
import wuxian.me.lianjiaspider.save.*;
import wuxian.me.lianjiaspider.util.Helper;
import static wuxian.me.spidercommon.util.ParsingUtil.*;
import wuxian.me.lianjiaspider.util.SpringBeans;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidersdk.JobManagerConfig;
import wuxian.me.spidersdk.anti.Fail;
import wuxian.me.spidersdk.anti.UserAgentManager;
import wuxian.me.spidersdk.manager.JobManagerFactory;


/**
 * Created by wuxian on 24/6/2017.
 */
public class Main {

    private static void initEnv() {

        SpringBeans.init();

        XiaoquSell sell = new XiaoquSell();
        SpringBeans.sellMapper().createNewTableIfNeed(sell);

        Xiaoqu xiaoqu = new Xiaoqu();
        SpringBeans.xiaoquMapper().createNewTableIfNeed(xiaoqu);

        SpringBeans.loupanMapper().createNewTableIfNeed(new Loupan());

        SpringBeans.quMapper().createNewTableIfNeed(new Qu());

        SpringBeans.newQuMapper().createNewTableIfNeed(new NewQu());

        SpringBeans.newQuDetailMapper().createNewTableIfNeed(new NewquDetail());

        SpringBeans.newQuPriceMapper().createNewTableIfNeed(new NewquPrice());

        BizConfig.init();
        ToumingConfig.init();

        SignalManager.registerOnSystemKill(SellSaver.getInstance());
        SignalManager.registerOnSystemKill(XiaoquSaver.getInstance());
        SignalManager.registerOnSystemKill(LoupanSaver.getInstance());
        SignalManager.registerOnSystemKill(QuSaver.getInstance());
        SignalManager.registerOnSystemKill(NewQuSaver.getInstance());
        SignalManager.registerOnSystemKill(NewQuDetailSaver.getInstance());
        SignalManager.registerOnSystemKill(NewQuPriceSaver.getInstance());

    }

    public static void main(String[] args) {

        LogManager.info("initEnv");
        initEnv();

        LogManager.info("start Jobmanager");
        JobManagerConfig.init();
        JobManagerFactory.getJobManager().start();  //Must be called before any biz!!

        try{
            Thread.sleep(1000);  //现在有个bug 若SpiderSdk没有start完成 putJob会失败... 后面需要Fixme:
        } catch (InterruptedException e){
            ;
        }

        //LogManager.info(NewQuDetailSpider.fromUrlNode(new NewQuDetailSpider(39067525L).toUrlNode()).toString());

        //Helper.dispatchSpider(new QuDetailSpider(10002814L));

        //Helper.dispatchSpider(new NewQuDetailSpider(39067525L));

        //Helper.dispatchSpider(new QuListSpider(QuEnum.Xihu, 1));

        UserAgentManager.switchIndex();

        //Helper.dispatchSpider(new NewQuListSpider(QuEnum.Yuhang, 1));

        JobManagerFactory.getJobManager().fail(new NewQuListSpider(QuEnum.Yuhang, 1), Fail.BLOCK);

        //Helper.dispatchSpider(new LoupanDetailSpider(CityEnum.Hangzhou.city(), "p_wkxdhaarkz"));
        //Helper.dispatchSpider(new LoupanListSpider(CityEnum.Hangzhou.city(), 1));
        //Helper.dispatchSpider(new QuListSpider(CityEnum.Hangzhou.city(), "xihu", 1));
    }
}
