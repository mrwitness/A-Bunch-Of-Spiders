package wuxian.me.lianjiaspider.biz.ershou;

import com.sun.istack.internal.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import wuxian.me.lianjiaspider.biz.BaseLianjiaSpider;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.lianjiaspider.api.BaseUrls;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidersdk.BaseSpider;

import java.util.List;

/**
 * Created by wuxian on 24/6/2017.
 */
public class ErshouSpider extends BaseLianjiaSpider {

    private static List<String> urls;

    private String cityUrl;
    private String distinct;
    private int page;

    private static String API = "/ershoufang/";

    @Nullable
    public static HttpUrlNode toUrlNode(ErshouSpider spider) {

        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = spider.cityUrl + API + spider.distinct + "/pg" + spider.page + "/";
        return node;
    }

    @Nullable
    public static ErshouSpider fromUrlNode(HttpUrlNode node) {

        if (urls == null) {
            urls = BaseUrls.getUrls();
        }

        String findUrl = null;
        for (String url : urls) {
            if (node.baseUrl.contains(url)) {
                findUrl = url;
                break;
            }
        }

        if (findUrl == null) {
            return null;
        }

        String findUrl2 = findUrl + API;

        if (!node.baseUrl.startsWith(findUrl2)) {
            return null;
        }

        int len = findUrl2.length();

        int index = node.baseUrl.indexOf("/", len);
        String distinct = node.baseUrl.substring(len, index);
        index = node.baseUrl.indexOf("pg", index);

        int pg = Integer.parseInt(node.baseUrl.substring(index + 2, node.baseUrl.length() - 1));

        ErshouSpider spider = new ErshouSpider(findUrl, distinct, pg);

        return spider;
    }

    public ErshouSpider(String cityUrl, String distinct, int page) {
        this(BaseUrls.getCity(cityUrl), cityUrl, distinct, page);
    }

    private int city;

    public ErshouSpider(int city, String cityUrl, String distinct, int page) {
        this.city = city;
        this.cityUrl = cityUrl;
        this.distinct = distinct;
        this.page = page;
    }

    public ErshouSpider(int city, String distinct, int page) {

        this(city, BaseUrls.getUrl(city), distinct, page);
    }

    protected Request buildRequest() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(cityUrl + API + distinct + "/pg" + page + "/")
                .newBuilder();

        String host = cityUrl.substring(8);

        Request request = new Request.Builder()
                .headers(Helper.getSpiderHeader(host, cityUrl + API + distinct + "/pg1/"))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    public int parseRealData(String s) {
        LogManager.info(s);
        return BaseSpider.RET_SUCCESS;
    }

    public String name() {
        return "ErshouSpider: cityCode:" + city + " distinct:" + distinct + " page:" + page;
    }
}
