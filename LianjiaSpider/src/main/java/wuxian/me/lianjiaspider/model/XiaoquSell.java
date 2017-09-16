package wuxian.me.lianjiaspider.model;

/**
 * Created by wuxian on 27/6/2017.
 */
public class XiaoquSell extends BaseModel {

    public static String tableName = "xiaoqu_sell";

    public Long xiaoqu_id;

    public String chengjiaoTime;

    public Integer chengjiaoTao;

    public String url;

    public Integer rentTao;

    public Integer price;

    public String priceMonth;

    public Integer sellTao;

    public String name() {
        return "XiaoquSell{" +
                "xiaoqu_id=" + xiaoqu_id +
                ", chengjiaoTime='" + chengjiaoTime + '\'' +
                ", chengjiaoTao=" + chengjiaoTao +
                ", url='" + url + '\'' +
                ", rentTao=" + rentTao +
                ", price=" + price +
                ", priceMonth='" + priceMonth + '\'' +
                ", sellTao=" + sellTao +
                '}';
    }

    public long index() {
        return xiaoqu_id + priceMonth.hashCode();
    }
}
