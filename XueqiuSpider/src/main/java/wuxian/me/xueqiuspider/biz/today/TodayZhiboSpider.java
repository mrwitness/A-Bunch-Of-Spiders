package wuxian.me.xueqiuspider.biz.today;

/**
 * Created by wuxian on 25/7/2017.
 * https://xueqiu.com/#/livenews
 * Todo:
 */
public class TodayZhiboSpider extends AbstractTodaySpider {

    public TodayZhiboSpider() {
        this(-1L);
    }

    @Override
    public int parseRealData(String s) {
        return 0;
    }

    public TodayZhiboSpider(Long max_id) {
        super(CategoryEnum.Zhibo.getType(), max_id);
    }

    @Override
    public String name() {
        return "TodayZhiboSpider:{maxId:" + getMaxId() + "}";
    }
}
