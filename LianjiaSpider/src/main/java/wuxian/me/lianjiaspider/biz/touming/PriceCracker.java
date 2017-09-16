package wuxian.me.lianjiaspider.biz.touming;

import com.sun.istack.internal.Nullable;
import org.htmlparser.Node;
import org.htmlparser.tags.Span;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

/**
 * Created by wuxian on 13/7/2017.
 */
public class PriceCracker {
    private PriceCracker() {
    }

    private static Map<String, String> convertMap;

    static {
        convertMap = new HashMap<String, String>();
        convertMap.put("numbbone", "1");
        convertMap.put("numbbtwo", "2");
        convertMap.put("numbbthree", "3");
        convertMap.put("numbbfour", "4");
        convertMap.put("numbbfive", "5");
        convertMap.put("numbbsix", "6");
        convertMap.put("numbbseven", "7");
        convertMap.put("numbbeight", "8");
        convertMap.put("numbbnight", "9");
        convertMap.put("numbbzero", "0");

    }

    public static Integer crack(String origin) {
        if (origin == null || origin.length() == 0 || !convertMap.containsKey(origin)) {
            return null;
        }

        return Integer.parseInt(convertMap.get(origin));
    }

    @Nullable
    public static Integer crackPriceNode(Node node) {

        if (node == null) {
            return null;
        }

        if (node instanceof Span && node.getText().trim().contains("class=")) {
            String p = matchedString(PRICE_PATTERN, node.getText().trim());
            return crack(p);
        }

        return null;
    }

    private static final String REG_PRICE = "(?<=class=\")[a-zA-Z]+";
    private static final Pattern PRICE_PATTERN = Pattern.compile(REG_PRICE);

}
