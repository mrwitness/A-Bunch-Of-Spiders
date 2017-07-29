package wuxian.me.xueqiuspider.dataprocess.cut;

import com.sun.istack.internal.NotNull;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import wuxian.me.spidercommon.log.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 28/7/2017.
 * https://github.com/ysc/word
 */
public class ApdplatWordCutter implements IWordCutter {

    public ApdplatWordCutter() {
        this(true);
    }

    private boolean hasStopWord;

    public ApdplatWordCutter(boolean hasStopword) {

        this.hasStopWord = hasStopword;
    }

    private SegmentationAlgorithm algorithm = SegmentationAlgorithm.MaximumMatching;

    @NotNull
    @Override
    public List<String> cutWord(String content) {
        if (content == null) {
            return new ArrayList<String>();
        }
        List<Word> list = null;

        if (hasStopWord) {
            list = WordSegmenter.segWithStopWords(content, algorithm);
        } else {
            list = WordSegmenter.seg(content, algorithm);
        }

        List<String> wordList = new ArrayList<String>();
        for (Word w : list) {
            wordList.add(w.getText());
        }

        return wordList;
    }
}
