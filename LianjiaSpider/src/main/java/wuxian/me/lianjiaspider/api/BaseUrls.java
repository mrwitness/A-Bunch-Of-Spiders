package wuxian.me.lianjiaspider.api;

import com.sun.istack.internal.Nullable;

import java.util.*;

/**
 * Created by wuxian on 24/6/2017.
 */
public class BaseUrls {

    private static Map<Integer, String> urlMap = new HashMap<Integer, String>(CityEnum.values().length);

    private static Map<Integer, String> loupanUrlMap = new HashMap<Integer, String>(CityEnum.values().length);

    private BaseUrls() {
    }

    static {
        urlMap.put(CityEnum.Hangzhou.city(), "https://hz.lianjia.com");

        loupanUrlMap.put(CityEnum.Hangzhou.city(), "http://hz.fang.lianjia.com");
    }

    public static boolean containUrl(String url) {
        return urlMap.containsValue(url);
    }

    public static int getCity(String url) {

        Iterator<Map.Entry<Integer, String>> iterator = urlMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();

            if (entry.getValue().equals(url)) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public static List<String> getUrls() {
        List<String> urls = new ArrayList<String>();
        urls.addAll(urlMap.values());

        return urls;
    }

    @Nullable
    public static String getUrl(int city) {
        if (urlMap.containsKey(city)) {
            return urlMap.get(city);
        }

        return null;
    }

    public static String getLoupanUrl(int city) {
        if (loupanUrlMap.containsKey(city)) {
            return loupanUrlMap.get(city);
        }

        return null;
    }

    public static List<String> getLoupanUrls() {
        List<String> urls = new ArrayList<String>();
        urls.addAll(loupanUrlMap.values());

        return urls;
    }

    public static int getLoupanCity(String url) {

        Iterator<Map.Entry<Integer, String>> iterator = loupanUrlMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();

            if (entry.getValue().equals(url)) {
                return entry.getKey();
            }
        }

        return -1;
    }

}
