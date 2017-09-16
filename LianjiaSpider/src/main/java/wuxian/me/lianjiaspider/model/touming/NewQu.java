package wuxian.me.lianjiaspider.model.touming;

import wuxian.me.lianjiaspider.model.BaseModel;

/**
 * Created by wuxian on 10/7/2017.
 */
public class NewQu extends BaseModel {

    public static String tableName = "newqu";

    public Long quId;

    public String name;

    //推广名称
    public String tuiguangName;

    public String district;

    public String location;

    //可售
    public Integer canSell;

    //总套数
    public Integer allSell;

    public Integer sellType; //1:待售 2:在售 3:尾盘

    public String quType; //住宅 商铺 etc

    public String dongtai;  //动态

    public Integer price;

    @Override
    public String name() {
        return "NewQu{" +
                "quId=" + quId +
                ", name='" + name + '\'' +
                ", tuiguangName='" + tuiguangName + '\'' +
                ", district='" + district + '\'' +
                ", location='" + location + '\'' +
                ", canSell=" + canSell +
                ", allSell=" + allSell +
                ", sellType=" + sellType +
                ", quType=" + quType +
                ", dongtai='" + dongtai + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public long index() {
        return quId;
    }
}
