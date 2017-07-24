package wuxian.me.xueqiuspider.biz.today;


import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;

/**
 * Created by wuxian on 24/7/2017.
 * <p>
 * https://xueqiu.com/#/
 */
public class TodayTopSpider extends AbstractTodaySpider {

    public TodayTopSpider() {
        super(-1);
    }

    public TodayTopSpider(Long max_id) {
        super(CategoryEnum.Top.getType(), max_id);
    }

    @Override
    public int parseRealData(String data) {
        LogManager.info(data);
        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "TodayTopSpider:{maxId:" + getMaxId() + "}";
    }
}
