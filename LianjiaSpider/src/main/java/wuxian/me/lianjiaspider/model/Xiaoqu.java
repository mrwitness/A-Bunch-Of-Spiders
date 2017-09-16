package wuxian.me.lianjiaspider.model;

/**
 * Created by wuxian on 26/6/2017.
 */
public class Xiaoqu extends BaseModel {

    public static String tableName = "xiaoqu";

    public Long xiaoqu_id;

    public String name;

    public Integer buildTime;

    public String longitude;

    public String lantitude;

    public String location;

    public Integer follower; //关注用户 --> Fixme:这个值是会变的

    public String buildingType;

    public String wuyefee;

    public String wuyeCompany;

    public String kaifashang;

    //易于排序
    public Integer buildingNum;

    public Integer houseNum;

    public String name() {
        return "Xiaoqu{" +
                "xiaoqu_id=" + xiaoqu_id +
                ", name='" + name + '\'' +
                ", buildTime=" + buildTime +
                ", longitude='" + longitude + '\'' +
                ", lantitude='" + lantitude + '\'' +
                ", location='" + location + '\'' +
                ", follower=" + follower +
                ", buildingType='" + buildingType + '\'' +
                ", wuyefee='" + wuyefee + '\'' +
                ", wuyeCompany='" + wuyeCompany + '\'' +
                ", kaifashang='" + kaifashang + '\'' +
                ", buildingNum=" + buildingNum +
                ", houseNum=" + houseNum +
                '}';
    }

    public long index() {
        return xiaoqu_id;
    }
}
