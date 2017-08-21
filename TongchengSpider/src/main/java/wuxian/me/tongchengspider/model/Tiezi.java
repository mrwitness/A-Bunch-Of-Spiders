package wuxian.me.tongchengspider.model;

/**
 * Created by wuxian on 26/7/2017.
 */
public class Tiezi extends BaseModel {

    public static String tableName = "tiezi";

    public String id;

    public String title;

    public String content;

    public Long postTime;

    public Integer pictureNum = 0;

    public Long created;

    public Long updated;

    public Integer size;

    public String location;

    //几室几厅几卫 单间 隔断
    public String shitingwei;

    public String live;

    //几室
    public Integer shiNum;

    //几厅
    public Integer tingNum;

    public Integer weiNum;

    public Integer price;


    //0:只限女生 1:只限男生 2:男女不限
    public Integer sex = -1;

    public String generateShitingwei() {
        StringBuilder b = new StringBuilder("");
        if (shiNum != null) {
            b.append(shiNum + "室");
        }

        if (tingNum != null) {
            b.append(tingNum + "厅");
        }

        if (weiNum != null) {
            if (weiNum != 111) {
                b.append(weiNum + "卫");
            } else {
                b.append("独卫");
            }
        }

        b.append("+" + live);

        return b.toString();
    }

    public String name() {
        return "Tiezi{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", postTime=" + postTime +
                ", size=" + size +
                ", location='" + location + '\'' +
                ", shitingwei='" + generateShitingwei() + '\'' +
                ", price=" + price +
                ", sex=" + sex +
                '}';
    }

    @Override
    public long index() {
        return id.hashCode();
    }
}
