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

    public String authorId;

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

    public Long created;

    public Long updated;

    //几室
    public Integer shiNum;

    //几厅
    public Integer tingNum;

    public Integer weiNum;

    //朝向
    public String chaoxiang;

    //几楼
    public Integer louceng;

    //0:只限女生 1:只限男生 2:男女不限
    public Integer sex;

    //sex不一定是准确的
    public String guessSex;

    //押几付几
    public Integer ya;

    public Integer fu;

    @Override
    public String name() {
        return "GroupTiezi{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", authorId='" + authorId + '\'' +
                ", postTime=" + postTime +
                ", pictureNum=" + pictureNum +
                ", selfReplyNum=" + selfReplyNum +
                ", otherReplyNum=" + otherReplyNum +
                ", replyContent='" + replyContent + '\'' +
                ", guessPrice=" + guessPrice +
                ", guessPrices='" + guessPrices + '\'' +
                ", guessWechat='" + guessWechat + '\'' +
                ", guessPhone='" + guessPhone + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", shiNum=" + shiNum +
                ", tingNum=" + tingNum +
                ", weiNum=" + weiNum +
                ", chaoxiang='" + chaoxiang + '\'' +
                ", louceng=" + louceng +
                ", sex=" + sex +
                ", guessSex='" + guessSex + '\'' +
                ", ya=" + ya +
                ", fu=" + fu +
                '}';
    }

    @Override
    public long index() {
        return id;
    }
}
