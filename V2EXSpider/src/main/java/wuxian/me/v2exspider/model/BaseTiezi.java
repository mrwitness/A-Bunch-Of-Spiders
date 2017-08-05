package wuxian.me.v2exspider.model;

/**
 * Created by wuxian on 26/7/2017.
 */
public class BaseTiezi extends BaseModel {

    public static String tableName = "career";

    public Long id;

    public String title;

    public String author;

    public String authorId;

    public Long latestReplyTime;

    public Integer replyNum;

    @Override
    public String name() {
        return "BaseTiezi{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", authorId='" + authorId + '\'' +
                ", latestReplyTime=" + latestReplyTime +
                ", replyNum=" + replyNum +
                '}';
    }

    public long index() {
        return id;
    }


}
