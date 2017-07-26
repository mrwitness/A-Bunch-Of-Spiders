package wuxian.me.doubanspider.model;

/**
 * Created by wuxian on 26/7/2017.
 */
public class GroupTiezi extends BaseModel {

    public static String tableName = "tiezi";

    public Long id;

    public String title;

    public String content;

    public String author;

    public Long authorId;

    public Long postTime;

    public Integer pictureNum;

    public Integer selfReplyNum;

    public Integer otherReplyNum;

    public String replyContent;

    public Integer guessPrice;

    //比如1500-1600
    public String guessPrices;

    public String guessWechat;

    public String guessPhone;

    @Override
    public String name() {
        return "GroupTiezi{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", authorId=" + authorId +
                ", postTime=" + postTime +
                ", pictureNum=" + pictureNum +
                ", selfReplyNum=" + selfReplyNum +
                ", otherReplyNum=" + otherReplyNum +
                ", replyContent='" + replyContent + '\'' +
                ", guessPrice=" + guessPrice +
                ", guessPrices='" + guessPrices + '\'' +
                ", guessWechat='" + guessWechat + '\'' +
                ", guessPhone='" + guessPhone + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public long index() {
        return id;
    }
}
