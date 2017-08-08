package wuxian.me.doubanspider.model;

/**
 * Created by wuxian on 26/7/2017.
 * Todo:阳台?短租?
 */
public class GroupTiezi extends BaseModel {

    public static String tableName = "tiezi";

    public String groupId;

    public Long id;

    public String title;

    public String content;

    public String author;

    public String authorId;

    public Long postTime;

    public Integer pictureNum  = 0;

    public Integer selfReplyNum = 0;

    public Integer otherReplyNum = 0;

    public String replyContent;

    public Integer guessPrice = 0;

    //比如1500-1600
    public String guessPrices ;

    public String guessWechat ;

    public String guessPhone ;

    public Long created;

    public Long updated;

    //几室
    public Integer shiNum = -1;

    //几厅
    public Integer tingNum = -1;

    public Integer weiNum = -1;

    //朝向
    public String chaoxiang;

    //几楼
    public Integer louceng = -1;

    //0:只限女生 1:只限男生 2:男女不限
    public Integer sex = -1;

    //sex不一定是准确的
    public String guessSex;

    //押几付几
    public Integer ya = -1;

    public Integer fu = -1;

    //0:出租 1:转租 2:求租 求合租
    public Integer rentType = -1;

    //0:正在出租 1:已租 已出
    public Integer rentStatus = 0;

    //1:长期 2:短期
    public Integer rentTime = 0;

    //入住时间
    public Integer guessLiveTime = -1;

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
