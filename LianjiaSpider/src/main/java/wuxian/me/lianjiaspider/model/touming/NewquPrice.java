package wuxian.me.lianjiaspider.model.touming;

import wuxian.me.lianjiaspider.model.BaseModel;

/**
 * Created by wuxian on 12/7/2017.
 */
public class NewquPrice extends BaseModel {

    public static String tableName = "newqu_price";

    public Long id;

    public Long quId;

    public Integer currentPrice;

    //住宅7日
    public String zhuzhai7day;

    //住宅7日价格
    public String zhuzhai7price;

    //住宅最近一次的时间
    public Integer zhuzhaiLastDay;

    //住宅最近一次的价格
    public Integer zhuzhaiLastPrice;

    //商用7日
    public String shangye7day;

    //商用最近一次的时间
    public Integer shangyeLastDay;

    //商用7日价格
    public String shangye7price;

    //商用最近一次的价格
    public Integer shangyeLastPrice;

    public Long created;

    @Override
    public String name() {
        return "NewquPrice{" +
                "quId=" + quId +
                ", currentPrice=" + currentPrice +
                ", zhuzhai7day='" + zhuzhai7day + '\'' +
                ", zhuzhai7price='" + zhuzhai7price + '\'' +
                ", zhuzhaiLastDay='" + zhuzhaiLastDay + '\'' +
                ", zhuzhaiLastPrice=" + zhuzhaiLastPrice +
                ", shangye7day='" + shangye7day + '\'' +
                ", shangyeLastDay='" + shangyeLastDay + '\'' +
                ", shangye7price='" + shangye7price + '\'' +
                ", shangyeLastPrice=" + shangyeLastPrice +
                ", created=" + created +
                '}';
    }

    @Override
    public long index() {
        return quId + created;
    }
}
