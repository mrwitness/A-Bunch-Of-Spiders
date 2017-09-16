package wuxian.me.zhihuspider.nlp;

import com.sun.istack.internal.NotNull;
import org.apdplat.word.util.WordConfTools;
import wuxian.me.spidercommon.util.FileUtil;
import wuxian.me.wordstatistic.nlp.cut.ConcreCalculator;
import wuxian.me.wordstatistic.nlp.cut.FreedomCalculator;
import wuxian.me.wordstatistic.nlp.model.MidStatistics;
import wuxian.me.wordstatistic.nlp.model.Sentence;
import wuxian.me.wordstatistic.nlp.model.Writings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuxian on 5/9/2017.
 */
public class NlpUtil {

    private NlpUtil() {
    }

    static {
        WordConfTools.set("dic.path", "classpath:custom_dic.txt,classpath:dic.txt");
        WordConfTools.set("stopwords.path", "classpath:stopwords.txt,classpath:custom_stopwords.txt");
    }


    public static void toCSV(String path, String content) {
        if (path == null || path.length() == 0) {
            return;
        }
        if (content == null || content.length() == 0) {
            return;
        }

        Writings writings = cutWord(content, false);
        if (writings != null) {
            List<String> wordList = writings.getBaseWordList();
            StringBuilder builder = new StringBuilder("");
            for (String s : wordList) {
                builder.append(s + ",");
            }
            FileUtil.writeToFile(path, builder.toString());
        }
    }


    public static Writings cutWord(@NotNull String s, boolean multiThread) {
        List<String> list = new ArrayList<String>(1);
        list.add(s);
        return cutWord(list, multiThread);
    }

    //Todo: 量大的时候 单线程效率太低 --> 确定一下本身切词是否已经使用多线程实现了？
    public static Writings cutWord(@NotNull List<String> list, boolean multiThread) {
        Writings writings = new Writings(false);  //入参:hasStopWord 默认文件在stopwords.txt

        for (int i = 0; i < list.size(); i++) {
            writings.addSentence(new Sentence(list.get(i)));
        }
        writings.generateWordsMap(writings);
        return writings;
    }

    public static Writings cutWord(@NotNull List<String> list) {
        return cutWord(list, false);
    }


    public static MidStatistics unlistwordWithCount(@NotNull Writings writings) {
        MidStatistics mid = new MidStatistics(2);
        mid.setSingleWordLenLimit(6);  //设置单个词的最大长度
        mid.generateWordsMap(writings);

        return mid;
    }


    public static Map<MidStatistics.Word, Double> unlistwordWithConcretion(@NotNull MidStatistics mid) {
        ConcreCalculator calculator = new ConcreCalculator();
        Map<MidStatistics.Word, Double> map = calculator.calConcretion(mid);

        return map;
    }

    //direction: 0:left 1:right
    public static Map<MidStatistics.Word, Double> unlistwordWithFreedom(@NotNull MidStatistics mid, int direction) {
        if (direction == 0) {
            FreedomCalculator calculator = new FreedomCalculator();
            Map<MidStatistics.Word, Double> map = calculator.calLeftFreedom(mid);

            return map;
        } else if (direction == 1) {
            FreedomCalculator calculator = new FreedomCalculator();
            Map<MidStatistics.Word, Double> map = calculator.calRightFreedom(mid);
            return map;
        }
        return null;
    }

}
