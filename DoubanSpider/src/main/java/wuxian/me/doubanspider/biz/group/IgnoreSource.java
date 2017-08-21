package wuxian.me.doubanspider.biz.group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 21/8/2017.
 */
public class IgnoreSource {

    private IgnoreSource() {
    }

    private static List<String> sourceList = new ArrayList<String>();

    static {
        sourceList.add("荷花苑");
        sourceList.add("益乐新村");
        sourceList.add("余杭");
        sourceList.add("萧山");
        sourceList.add("下沙");
        sourceList.add("五联东苑");
        sourceList.add("五联西苑");

    }

    public static boolean shouldIgnore(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }

        for (String source : sourceList) {
            if (s.contains(source)) {
                return true;
            }
        }
        return false;
    }
}
