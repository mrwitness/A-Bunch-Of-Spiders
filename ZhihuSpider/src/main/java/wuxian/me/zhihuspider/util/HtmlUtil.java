package wuxian.me.zhihuspider.util;

import wuxian.me.spidercommon.util.FileUtil;

import javax.sound.midi.Track;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 4/9/2017.
 */
public class HtmlUtil {
    private HtmlUtil() {
    }

    private static List<String> nodes = new ArrayList<String>();

    static {
        nodes.add("blockquote");
        nodes.add("a");
        nodes.add("h2");
        nodes.add("em");
        nodes.add("img");
        nodes.add("br");
        nodes.add("b");
        nodes.add("p");
        nodes.add("i");
        nodes.add("strong");
        nodes.add("span");
        nodes.add("li");
        nodes.add("class");
        nodes.add("ul");
        nodes.add("gt");
        nodes.add("ol");
        nodes.add("lt");
    }

    /**
     * 将fromPath文件夹下的文件处理 然后放到toPath文件夹
     */
    public static void dealFiles(String fromPath, String toPath) {
        if (fromPath == null || fromPath.length() == 0) {
            return;
        }

        if (toPath == null || toPath.length() == 0) {
            return;
        }

        File fromParentFile = new File(fromPath);
        if (!fromParentFile.exists() || !fromParentFile.isDirectory()) {
            return;
        }

        File[] fromFiles = fromParentFile.listFiles();
        for (File f : fromFiles) {

            if (f.isFile()) {
                String s = removeHtmlNode(FileUtil.readFromFile(f.getAbsolutePath()));
                if (s != null) {
                    File toFile = new File(toPath, f.getName());
                    FileUtil.writeToFile(toFile.getAbsolutePath(), s);
                }
            }
        }

    }


    public static String removeHtmlNode(String origin) {
        if (origin == null) {
            return origin;
        }
        for (String node : nodes) {
            String n1 = "<" + node + "/>";
            origin = origin.replaceAll(n1, "");
            String n2 = "<" + node + ">";
            origin = origin.replaceAll(n2, "");
            String n3 = "</" + node + ">";
            origin = origin.replaceAll(n3, "");
        }

        for (String node : nodes) {
            String n = "<" + node;

            origin = removeNode(n, origin);
        }

        return origin;
    }


    private static String removeNode(String node, String origin) {

        StringBuilder ret = new StringBuilder("");
        int index = -1;
        int end = -1;
        String c = ">";
        while (true) {
            index = origin.indexOf(node, end);
            if (index == -1) {
                ret.append(origin.substring(end + 1, origin.length()));
                break;
            }
            ret.append(origin.substring(end + 1, index));
            end = origin.indexOf(c, index);
            if (end == -1) {
                ret.append(origin.substring(index, origin.length()));
                break;
            }
        }

        return ret.toString();
    }
}
