package wuxian.me.xueqiuspider;

import org.apdplat.word.util.WordConfTools;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.wordstatistic.nlp.cut.ConcreCalculator;
import wuxian.me.wordstatistic.nlp.cut.FreedomCalculator;
import wuxian.me.wordstatistic.nlp.model.MidStatistics;
import wuxian.me.wordstatistic.nlp.model.Sentence;
import wuxian.me.wordstatistic.nlp.model.Writings;
import wuxian.me.wordstatistic.statistic.Helper;
import wuxian.me.wordstatistic.statistic.StatisModel;
import wuxian.me.xueqiuspider.model.Zhibo;
import wuxian.me.xueqiuspider.util.SpringBeans;

import java.util.*;

import static wuxian.me.wordstatistic.statistic.Helper.sortAndPrint;

/**
 * Created by wuxian on 29/7/2017.
 * enter point of statistic
 */
public class StatisticMain {

    private static void calRightFreedom(MidStatistics mid, int maxNum) {
        FreedomCalculator calculator = new FreedomCalculator();
        //calculator.setWordAppearanceLimit(3);
        Map<MidStatistics.Word, Double> map = calculator.calRightFreedom(mid);
        sortAndPrint(map, maxNum);
    }

    private static void calConcretion(MidStatistics mid, int maxNum) {
        ConcreCalculator calculator = new ConcreCalculator();
        //calculator.setWordAppearanceLimit(3);
        Map<MidStatistics.Word, Double> map = calculator.calConcretion(mid);

        sortAndPrint(map, maxNum);
    }

    private static void calLeftFreedom(MidStatistics mid, int maxNum) {
        FreedomCalculator calculator = new FreedomCalculator();
        //calculator.setWordAppearanceLimit(3);
        Map<MidStatistics.Word, Double> map = calculator.calLeftFreedom(mid);

        sortAndPrint(map, maxNum);
    }

    public static void main(String[] args) throws Exception {
        SpringBeans.init();
        List<Zhibo> list = SpringBeans.zhiboMapper().loadZhibo(new Zhibo());
        if (list == null || list.size() == 0) {
            return;
        }
        int sentenceNum = 900;  //先测试一百句
        if (list.size() > sentenceNum) {
            //list = new ArrayList<Zhibo>(list.subList(0, sentenceNum));
        }

        WordConfTools.set("dic.path", "classpath:custom_dic.txt,classpath:dic.txt");
        WordConfTools.set("stopwords.path", "classpath:custom_stopwords.txt");
        //WordConfTools.set("stopwords.path", "classpath:stopwords.txt,classpath:custom_stopwords.txt");

        Writings writings = new Writings(false);  //入参:hasStopWord 默认文件在stopwords.txt
        StringBuilder content = new StringBuilder("");
        for (int i = 0; i < list.size(); i++) {
            content.append(list.get(i).text);
            writings.addSentence(new Sentence(list.get(i).text));
        }
        LogManager.info("we get sentence total: " + list.size());

        long start = System.currentTimeMillis();
        writings.generateWordsMap(writings);
        LogManager.info("writings generateWordsMap cost " + (System.currentTimeMillis() - start) + " miliseconds");
        printFirst(writings, 5000);

        MidStatistics mid = new MidStatistics(2);
        mid.setSingleWordLenLimit(6);  //设置单个词的最大长度
        start = System.currentTimeMillis();
        mid.generateWordsMap(writings);
        LogManager.info("MidStatistics generateWordsMap cost " + (System.currentTimeMillis() - start) + " miliseconds");

        LogManager.info("--------------------2dimension appearance------------------");
        printFirst(mid, 100);


        LogManager.info("--------------------concretion------------------");
        start = System.currentTimeMillis();
        calConcretion(mid, 100);
        LogManager.info("calConcretion cost " + (System.currentTimeMillis() - start) + " miliseconds");


        LogManager.info("--------------------left freedom------------------");
        start = System.currentTimeMillis();
        calLeftFreedom(mid, 100);
        LogManager.info("calLeftFreedom cost " + (System.currentTimeMillis() - start) + " miliseconds");

        LogManager.info("--------------------right freedom------------------");
        start = System.currentTimeMillis();
        calRightFreedom(mid, 100);
        LogManager.info("CalRightFreedom cost " + (System.currentTimeMillis() - start) + " miliseconds");



    }

    private static void printFirst(MidStatistics mid, int num) {
        if (mid == null || !mid.ready()) {
            return;
        }

        List<StatisModel> modelList = Helper.toModelList(mid.getWordPostionMap());
        Collections.sort(modelList, new StatisModel.Comparator());

        int cur = 0;
        num = num >= modelList.size() ? modelList.size() : num;
        for (int i = 0; i < num; i++) {
            LogManager.info("key:" + modelList.get(i).getKey() + " apperance:" + modelList.get(i).getSize());
        }

    }
}
