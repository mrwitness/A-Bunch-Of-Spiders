package wuxian.me.xueqiuspider.dataprocess.corrector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 29/7/2017.
 */
public class WordsCorrectorImpl implements WordsCorrector {

    @Override
    public List<String> correctWithDistance(List<String> wordList, int distance) {
        if (wordList == null || wordList.size() == 0) {
            return wordList;
        }

        if (distance > wordList.size()) {
            return wordList;
        }
        List<String> list = new ArrayList<String>(wordList.size() - distance + 1);

        for (int i = 0; i < wordList.size() - distance + 1; i++) {
            StringBuilder b = new StringBuilder("");
            int num = 0;
            while (num < distance) {
                b.append(wordList.get(i + num));
                num++;
            }
            list.add(b.toString());
        }
        return list;
    }

    @Override
    public List<String> correct(List<String> wordList) {
        return correctWithDistance(wordList, 2);
    }
}
