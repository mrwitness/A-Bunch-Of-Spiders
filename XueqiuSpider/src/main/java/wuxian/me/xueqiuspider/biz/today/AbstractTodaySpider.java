package wuxian.me.xueqiuspider.biz.today;

import okhttp3.HttpUrl;
import okhttp3.Request;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.xueqiuspider.biz.BaseXueqiuSpider;
import wuxian.me.xueqiuspider.biz.today.model.TodayResponse;
import wuxian.me.xueqiuspider.util.Helper;

/**
 * Created by wuxian on 24/7/2017.
 * https://xueqiu.com/#/
 */
public abstract class AbstractTodaySpider extends BaseXueqiuSpider {

    //https://xueqiu.com/v4/statuses/public_timeline_by_category.json?since_id=-1&max_id=-1&count=5&category=-1
    protected AbstractTodaySpider(int category) {
        this(category, -1L);

    }

    protected AbstractTodaySpider(int category, Long max_id) {
        this(category, max_id, max_id == -1 ? 5 : 15);
    }

    private int category;
    private Long maxId;
    private int count;

    protected Long getMaxId(){return maxId;}

    //https://xueqiu.com/v4/statuses/public_timeline_by_category.json?since_id=-1&max_id=20192619&count=15&category=-1
    protected AbstractTodaySpider(int category, Long max_id, int count) {
        this.category = category;
        this.maxId = max_id;
        this.count = count;
    }

    private static final String API = "https://xueqiu.com/v4/statuses/public_timeline_by_category.json?since_id=-1";

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API)
                .newBuilder();
        urlBuilder.addQueryParameter("max_id", String.valueOf(maxId));
        urlBuilder.addQueryParameter("category", String.valueOf(category));
        urlBuilder.addQueryParameter("count", String.valueOf(count));

        String host = "xueqiu.com";
        Request request = new Request.Builder()
                .headers(Helper.getDoubanSpiderHeader(host, "https://xueqiu.com/"))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }
    @Override
    public abstract String name() ;
}
