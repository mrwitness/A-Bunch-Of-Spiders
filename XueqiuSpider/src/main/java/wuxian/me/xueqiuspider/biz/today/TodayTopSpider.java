package wuxian.me.xueqiuspider.biz.today;


import com.google.gson.reflect.TypeToken;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.xueqiuspider.model.today.TodayResponse;
import wuxian.me.xueqiuspider.model.today.TopItemData;

/**
 * Created by wuxian on 24/7/2017.
 * <p>
 * https://xueqiu.com/#/
 */
public class TodayTopSpider extends AbstractTodaySpider {

    public TodayTopSpider() {
        this(-1L);
    }

    public TodayTopSpider(Long max_id) {
        super(CategoryEnum.Top.getType(), max_id);
    }

    @Override
    public int parseRealData(String data) {

        data = formatToRightJson(data);

        LogManager.info(data);
        TodayResponse res = GsonProvider.gson().fromJson(data, new TypeToken<TodayResponse<TopItemData>>() {
        }.getType());
        if (res == null) {
            return BaseSpider.RET_MAYBE_BLOCK;
        }
        LogManager.info(res.toString());

        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "TodayTopSpider:{maxId:" + getMaxId() + "}";
    }
}
