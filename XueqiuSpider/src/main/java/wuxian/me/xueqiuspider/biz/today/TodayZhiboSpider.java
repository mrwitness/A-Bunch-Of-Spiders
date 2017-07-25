package wuxian.me.xueqiuspider.biz.today;

import com.google.gson.reflect.TypeToken;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.xueqiuspider.biz.today.model.TodayResponse;
import wuxian.me.xueqiuspider.biz.today.model.TopItemData;
import wuxian.me.xueqiuspider.biz.today.model.ZhiboItemData;

/**
 * Created by wuxian on 25/7/2017.
 * https://xueqiu.com/#/livenews
 */
public class TodayZhiboSpider extends AbstractTodaySpider {

    public TodayZhiboSpider() {
        this(-1L);
    }

    @Override
    public int parseRealData(String data) {
        data = formatToRightJson(data);

        LogManager.info(data);
        TodayResponse res = GsonProvider.gson().fromJson(data
                , new TypeToken<TodayResponse<ZhiboItemData>>() {
                }.getType());

        if (res == null) {
            return BaseSpider.RET_MAYBE_BLOCK;
        }
        LogManager.info(res.toString());

        return BaseSpider.RET_SUCCESS;
    }

    public TodayZhiboSpider(Long max_id) {
        super(CategoryEnum.Zhibo.getType(), max_id);
    }

    @Override
    public String name() {
        return "TodayZhiboSpider:{maxId:" + getMaxId() + "}";
    }
}
