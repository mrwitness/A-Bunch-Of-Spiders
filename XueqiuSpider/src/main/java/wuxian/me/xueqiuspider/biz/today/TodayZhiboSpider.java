package wuxian.me.xueqiuspider.biz.today;

import com.google.gson.reflect.TypeToken;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.xueqiuspider.model.today.TodayItem;
import wuxian.me.xueqiuspider.model.today.TodayResponse;
import wuxian.me.xueqiuspider.model.Zhibo;
import wuxian.me.xueqiuspider.save.ZhiboSaver;
import wuxian.me.xueqiuspider.util.Helper;

import java.util.List;

/**
 * Created by wuxian on 25/7/2017.
 * https://xueqiu.com/#/livenews
 */
public class TodayZhiboSpider extends AbstractTodaySpider {

    private static final Long STOP_TIME_INTERVAL = 1000 * 60 * 60 * 6 * 1L;  //只抓6小时以内的新闻

    public static HttpUrlNode toUrlNode(TodayZhiboSpider spider) {
        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = API;
        node.httpGetParam.put("max_id", String.valueOf(spider.getMaxId()));
        node.httpGetParam.put("category", String.valueOf(spider.getCategory()));
        return node;
    }

    public static TodayZhiboSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains(API)) {
            return null;
        }
        Integer category = Integer.parseInt(node.httpGetParam.get("category"));

        if (category != null && category == CategoryEnum.Zhibo.getType()) {
            return new TodayZhiboSpider(Long.parseLong(node.httpGetParam.get("max_id")));
        }

        return null;
    }

    public TodayZhiboSpider() {
        this(-1L);
    }

    @Override
    public int parseRealData(String data) {
        data = formatToRightJson(data);

        LogManager.info(data);
        TodayResponse res = GsonProvider.gson().fromJson(data
                , new TypeToken<TodayResponse<Zhibo>>() {
                }.getType());

        if (res == null) {
            return BaseSpider.RET_MAYBE_BLOCK;
        }
        //LogManager.info(res.toString());

        List<TodayItem> list = res.list;
        if (list == null || list.size() == 0) {
            return BaseSpider.RET_MAYBE_BLOCK;
        }

        for (TodayItem item : list) {
            if (item.data != null) {
                try {
                    Zhibo zhibo = (Zhibo) item.data;
                    zhibo.created = System.currentTimeMillis();
                    zhibo.updated = zhibo.created;
                    saveZhibo(zhibo);
                } catch (Exception e) {
                    ;
                }
            }
        }

        Zhibo zhibo = null;
        try {
            zhibo = (Zhibo) list.get(list.size() - 1).data;
        } catch (Exception e) {
            return BaseSpider.RET_MAYBE_BLOCK;
        }

        if (System.currentTimeMillis() - zhibo.created_at <= STOP_TIME_INTERVAL) {
            Helper.dispatchSpider(new TodayZhiboSpider(res.next_max_id));
        }

        return BaseSpider.RET_SUCCESS;
    }

    private void saveZhibo(Zhibo zhibo) {
        ZhiboSaver.getInstance().saveModel(zhibo);
    }

    public TodayZhiboSpider(Long max_id) {
        super(CategoryEnum.Zhibo.getType(), max_id);
    }

    @Override
    public String name() {
        return "TodayZhiboSpider:{maxId:" + getMaxId() + "}";
    }
}
