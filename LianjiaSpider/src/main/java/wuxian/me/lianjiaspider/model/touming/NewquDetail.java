package wuxian.me.lianjiaspider.model.touming;

import wuxian.me.lianjiaspider.model.BaseModel;

/**
 * Created by wuxian on 12/7/2017.
 */
public class NewquDetail extends BaseModel {

    public static String tableName = "newqu_detail";

    public Long quId;

    public String name;

    public String tuiguangName;

    public Integer status;

    public String longitude;

    public String lantitude;

    public String location;

    //主力户型
    public Integer huxingMin;

    //主力户型
    public Integer huxingMax;

    //物业类型
    public String wuyeType;

    //建筑形式
    public String jianzhuType;

    //最近开盘 yyyy-MM-dd --> yyyyMMdd
    public Integer newlyKaipan;

    //开发商
    public String kaifashang;

    //动态
    public String dongtai;

    //预售
    public String yushou;

    //容积率
    public String rongjilv;

    //绿化绿
    public Integer lvhualv;

    //装修
    public String zhuangxiu;

    //占地面积
    public Integer zhandi;

    //建筑面积
    public Integer jianzhu;

    //竣工时间
    public Integer jungongTime;

    //交付时间
    public String jiaofuTime;

    public Integer hushu;

    //车位数量
    public Integer chewei;

    public String wuyeCompany;

    public String wuyeFee;

    //产权年限
    public Integer chanquan;

    public String other;

    public Long created;

    public Long updated;

    @Override
    public String name() {
        return "NewquDetail{" +
                "quId=" + quId +
                ", name='" + name + '\'' +
                ", tuiguangName='" + tuiguangName + '\'' +
                ", status=" + status +
                ", longitude='" + longitude + '\'' +
                ", lantitude='" + lantitude + '\'' +
                ", location='" + location + '\'' +
                ", huxingMin=" + huxingMin +
                ", huxingMax=" + huxingMax +
                ", wuyeType='" + wuyeType + '\'' +
                ", jianzhuType='" + jianzhuType + '\'' +
                ", newlyKaipan=" + newlyKaipan +
                ", kaifashang='" + kaifashang + '\'' +
                ", dongtai='" + dongtai + '\'' +
                ", yushou='" + yushou + '\'' +
                ", rongjilv='" + rongjilv + '\'' +
                ", lvhualv=" + lvhualv +
                ", zhuangxiu='" + zhuangxiu + '\'' +
                ", zhandi=" + zhandi +
                ", jianzhu=" + jianzhu +
                ", jungongTime=" + jungongTime +
                ", jiaofuTime='" + jiaofuTime + '\'' +
                ", hushu=" + hushu +
                ", chewei=" + chewei +
                ", wuyeCompany='" + wuyeCompany + '\'' +
                ", wuyeFee='" + wuyeFee + '\'' +
                ", chanquan=" + chanquan +
                ", other='" + other + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    @Override
    public long index() {
        return quId;
    }


}
