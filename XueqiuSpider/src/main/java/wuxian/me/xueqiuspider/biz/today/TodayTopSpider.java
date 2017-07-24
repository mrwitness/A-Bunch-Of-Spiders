package wuxian.me.xueqiuspider.biz.today;


import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.xueqiuspider.biz.today.model.TodayResponse;

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
        data = data.replaceAll(TodayTopSpider.REG_REMOVE_2
                        + "|" + TodayTopSpider.REG_REMOVE
                        + "|" + TodayTopSpider.REG_REMOVE_3
                        + "|" + TodayTopSpider.REG_REMOVE_4
                        + "|" + TodayTopSpider.REG_REMOVE_5
                        + "|" + TodayTopSpider.REG_REMOVE_6
                , "\"");
        data = data.replaceAll(TodayTopSpider.REG_REMOVE_7, "}");
        data = data.replaceAll(TodayTopSpider.REG_REMOVE_8, "{");
        LogManager.info(data);
        TodayResponse res = GsonProvider.gson().fromJson(data, TodayResponse.class);
        LogManager.info(res.toString());
        return BaseSpider.RET_SUCCESS;
    }

    public static String REG_REMOVE = "\\\\\"(?=:)";

    public static String REG_REMOVE_2 = "(?<=\\{)\\\\\"";

    public static String REG_REMOVE_3 = "(?<=,)\\\\\"";

    public static String REG_REMOVE_4 = "(?<=:)\\\\\"";

    public static String REG_REMOVE_5 = "\\\\\"(?=,)";

    public static String REG_REMOVE_6 = "\\\\\"(?=})";

    public static String REG_REMOVE_7 = "}\\\"";

    public static String REG_REMOVE_8 = "\\\"[{]";

    @Override
    public String name() {
        return "TodayTopSpider:{maxId:" + getMaxId() + "}";
    }
}
