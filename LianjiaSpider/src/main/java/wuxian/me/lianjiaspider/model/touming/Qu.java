package wuxian.me.lianjiaspider.model.touming;

import wuxian.me.lianjiaspider.model.BaseModel;

/**
 * Created by wuxian on 10/7/2017.
 */
public class Qu extends BaseModel {

    public static String tableName = "qu";

    public Long quId;

    public String name;

    public String district;

    public String location;

    //出售
    public Integer sellNum;

    public Integer rentNum;

    public Integer price;

    public Float priceChange;

    //0:down 1:up
    public Integer changeType;

    @Override
    public String name() {
        return "Qu{" +
                "quId=" + quId +
                ", name='" + name + '\'' +
                ", district='" + district + '\'' +
                ", location='" + location + '\'' +
                ", sellNum=" + sellNum +
                ", rentNum=" + rentNum +
                ", price=" + price +
                ", priceChange=" + priceChange +
                ", changeType=" + changeType +
                '}';
    }

    @Override
    public long index() {
        return quId;
    }
}
