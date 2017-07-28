package wuxian.me.xueqiuspider.dataprocess;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.xueqiuspider.dataprocess.cut.ApdplatWordCutter;
import wuxian.me.xueqiuspider.dataprocess.statistic.StatisticImpl;
import wuxian.me.xueqiuspider.model.Zhibo;
import wuxian.me.xueqiuspider.util.SpringBeans;

import java.util.List;

/**
 * Created by wuxian on 29/7/2017.
 * enter point of statistic
 */
public class StatisticMain {

    public static void main(String[] args) throws Exception {
        SpringBeans.init();


        List<Zhibo> list = SpringBeans.zhiboMapper().loadZhibo(new Zhibo());

        if (list == null || list.size() == 0) {
            return;
        }

        StringBuilder content = new StringBuilder("");
        for (int i = 0; i < list.size(); i++) {
            content.append(list.get(i).text);
        }

        LogManager.info("content:" + content.toString());
        new StatisticImpl().sortAndCount(new ApdplatWordCutter().cutWord(content.toString()));
    }
}
