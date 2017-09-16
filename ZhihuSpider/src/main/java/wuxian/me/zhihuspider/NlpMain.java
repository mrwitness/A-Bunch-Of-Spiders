package wuxian.me.zhihuspider;


import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.FileUtil;
import wuxian.me.wordstatistic.nlp.model.MidStatistics;
import wuxian.me.wordstatistic.nlp.model.Writings;
import wuxian.me.wordstatistic.statistic.Helper;
import wuxian.me.wordstatistic.statistic.StatisModel;
import wuxian.me.zhihuspider.nlp.NlpUtil;
import wuxian.me.zhihuspider.util.HtmlUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by wuxian on 10/9/2017.
 */
public class NlpMain {

    private static int MIN_FILE_LEN = 500;

    public static void main(String[] args) {
        generateCSVFiles();
    }

    private static void generateCSVFiles() {
        String path = "/Users/wuxian/Desktop/zhihu-me/zhihufile-after/";
        String toPath = "/Users/wuxian/Desktop/zhihu-me/zhihufile-csv/";
        File fromParentFile = new File(path);
        if (!fromParentFile.exists() || !fromParentFile.isDirectory()) {
            return;
        }
        File[] fromFiles = fromParentFile.listFiles();  //5307
        LogManager.info("files under file num:" + fromFiles.length);

        int i = 0;
        for (File f : fromFiles) {
            if (f.isFile()) {
                String s = FileUtil.readFromFile(f.getAbsolutePath());
                if (s != null && s.length() > MIN_FILE_LEN) {
                    File toFile = new File(toPath, f.getName());
                    NlpUtil.toCSV(toFile.getAbsolutePath(), s);
                    if(++i > 20) {
                        break;
                    }
                }

            }
        }
    }

    private static void removeHtmlNodes() {
        HtmlUtil.dealFiles("/Users/wuxian/Desktop/zhihu-me/zhihufile", "/Users/wuxian/Desktop/zhihu-me/zhihufile-after");
    }

    private static void unlistwordRecognize() {
        String path = "/Users/wuxian/Desktop/zhihu-me/zhihufile-after/";
        File fromParentFile = new File(path);
        if (!fromParentFile.exists() || !fromParentFile.isDirectory()) {
            return;
        }
        File[] fromFiles = fromParentFile.listFiles();  //5307
        LogManager.info("files under file num:" + fromFiles.length);

        int totalLen = 0;
        List<String> list = new ArrayList<String>();
        int start = 2500;
        int len = 500;
        int i = 0;
        for (File f : fromFiles) {
            if (f.isFile()) {
                String s = FileUtil.readFromFile(f.getAbsolutePath());
                if (s != null && s.length() > MIN_FILE_LEN) {
                    if (++i >= (start + len)) {
                        break;
                    }
                    if (i >= start && i < (start + len)) {
                        list.add(s);
                        totalLen += s.length();
                    }
                }

            }
        }
        LogManager.info("word to cut totalLen:" + totalLen);
        dealList(list);
    }

    private static void dealList(List<String> list) {
        long start = System.currentTimeMillis();
        Writings writings = NlpUtil.cutWord(list);
        LogManager.info("cutword cost " + (System.currentTimeMillis() - start) + " miliseconds");
        printFirst(writings, 100);

        start = System.currentTimeMillis();
        MidStatistics mid = NlpUtil.unlistwordWithCount(writings);
        LogManager.info("unlistwordWithCount cost " + (System.currentTimeMillis() - start) + " miliseconds");
        printFirst(mid, 200);

        start = System.currentTimeMillis();
        Map<MidStatistics.Word, Double> map = NlpUtil.unlistwordWithConcretion(mid);
        LogManager.info("calConcretion cost " + (System.currentTimeMillis() - start) + " miliseconds");
        wuxian.me.wordstatistic.statistic.Helper.sortAndPrint(map, 100);

        start = System.currentTimeMillis();
        map = NlpUtil.unlistwordWithFreedom(mid, 0);
        LogManager.info("calLeftFreedom cost " + (System.currentTimeMillis() - start) + " miliseconds");
        wuxian.me.wordstatistic.statistic.Helper.sortAndPrint(map, 200);

        start = System.currentTimeMillis();
        map = NlpUtil.unlistwordWithFreedom(mid, 1);
        LogManager.info("calRightFreedom cost " + (System.currentTimeMillis() - start) + " miliseconds");
        wuxian.me.wordstatistic.statistic.Helper.sortAndPrint(map, 200);

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
