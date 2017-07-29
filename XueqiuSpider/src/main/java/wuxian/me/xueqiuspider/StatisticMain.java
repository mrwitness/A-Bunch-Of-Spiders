package wuxian.me.xueqiuspider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.wordstatistic.corrector.WordsCorrectorImpl;
import wuxian.me.wordstatistic.cut.ApdplatWordCutter;
import wuxian.me.wordstatistic.statistic.IStatistic;
import wuxian.me.wordstatistic.statistic.StatisticImpl;
import wuxian.me.xueqiuspider.model.Zhibo;
import wuxian.me.xueqiuspider.util.SpringBeans;

import java.util.Iterator;
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

        List<String> wordList = new ApdplatWordCutter().cutWord(content.toString());
        LogManager.info("after cut:" + wordList.toString());


        for (int i = 1; i < 10; i++) {
            printWordWithDistance(wordList, i);
        }
    }

    private static void printWordWithDistance(List<String> wordList, int distance) {
        if (wordList == null || wordList.size() == 0 || distance <= 0) {
            return;
        }
        LogManager.info("printWordWithDistance distance:" + distance);
        List<String> list = new WordsCorrectorImpl().correctWithDistance(wordList, distance);
        //LogManager.info("after correct with distance" + distance + ":" + list.toString());
        Iterator<IStatistic.IModel<String>> iterator = new StatisticImpl().sortAndCount(list);
        int cur = 0;
        int max = 50;
        while (iterator.hasNext()) {
            IStatistic.IModel<String> model = iterator.next();

            if (model.getSize() <= 3) {
                break;
            }
            LogManager.info("key:" + model.getKey() + " apperance:" + model.getSize());

            if (++cur >= max) {
                break;
            }
        }
    }
}
