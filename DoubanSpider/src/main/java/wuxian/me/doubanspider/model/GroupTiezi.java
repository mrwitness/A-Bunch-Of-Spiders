package wuxian.me.doubanspider.model;

/**
 * Created by wuxian on 26/7/2017.
 */
public class GroupTiezi extends BaseModel {

    public static String tableName = "tiezi";

    public Long id;

    public String title;

    public String author;

    public Long authorId;

    public Integer responseNum;

    public Long postTime;

    public Integer selfReplyNum;

    public Integer otherReplyNum;

    public String replyContent;

    public Integer guessPrice;

    public String guessWechat;

    public String guessPhone;

    @Override
    public String name() {
        return "GroupTiezi{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", authorId=" + authorId +
                ", responseNum=" + responseNum +
                ", postTime=" + postTime +
                '}';
    }

    @Override
    public long index() {
        return id;
    }
}
