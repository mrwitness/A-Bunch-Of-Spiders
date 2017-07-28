package wuxian.me.xueqiuspider.dataprocess;

import java.util.List;

/**
 * Created by wuxian on 28/7/2017.
 */
public interface WordsCorrector {

    //给定一个分词完成的字符集,给出一个纠正后的字符集
    //比如分词"我""们",应该纠正成"我们"
    void correctWithDistance(List<String> wordList, int distance);

    void correct(List<String> wordList);
}
