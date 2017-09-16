package wuxian.me.lianjiaspider.model;

/**
 * Created by wuxian on 9/7/2017.
 */
public class Loupan extends BaseModel {

    public static String tableName = "loupan";

    public String loupanId;

    public String name;

    //区
    public String district;

    public String location;

    //户数
    public String hushu;

    public Integer sizeMin;

    public Integer sizeMax;

    public String advantage;

    public String attribute;

    public Integer price;

    @Override
    public String name() {
        return "Loupan{" +
                "loupanId='" + loupanId + '\'' +
                ", name='" + name + '\'' +
                ", district='" + district + '\'' +
                ", location='" + location + '\'' +
                ", hushu='" + hushu + '\'' +
                ", sizeMin=" + sizeMin +
                ", sizeMax=" + sizeMax +
                ", advantage='" + advantage + '\'' +
                ", attribute='" + attribute + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public long index() {
        return loupanId.hashCode();
    }
}
