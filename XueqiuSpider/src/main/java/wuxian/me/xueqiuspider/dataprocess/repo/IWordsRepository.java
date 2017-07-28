package wuxian.me.xueqiuspider.dataprocess.repo;

/**
 * Created by wuxian on 29/7/2017.
 */
public interface IWordsRepository {

    void addWord(String word);

    void addIgnoreWord(String igWord);
}
