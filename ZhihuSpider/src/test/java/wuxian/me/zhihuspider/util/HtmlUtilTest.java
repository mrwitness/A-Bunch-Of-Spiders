package wuxian.me.zhihuspider.util;

import org.junit.Test;
import wuxian.me.spidercommon.util.FileUtil;

import static org.junit.Assert.*;

/**
 * Created by wuxian on 10/9/2017.
 */
public class HtmlUtilTest {

    @Test
    public void testRemove() {
        String path = "/Users/wuxian/Desktop/answer.txt";
        String outPath = "/Users/wuxian/Desktop/answer2.txt";
        String content = FileUtil.readFromFile(path);

        content = HtmlUtil.removeHtmlNode(content);
        if(content != null) {
            FileUtil.writeToFile(outPath,content);
        }
    }

}