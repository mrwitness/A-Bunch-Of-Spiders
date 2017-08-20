package wuxian.me.v2exspider.model;

/**
 * Created by wuxian on 10/8/2017.
 */
public class Creator extends BaseTiezi {

    public static String tableName = "creator";

    public Creator() {
        super();
    }

    public Creator(BaseTiezi tiezi) {
        this.author = tiezi.author;
        this.title = tiezi.title;
        this.authorId = tiezi.authorId;
        this.id = tiezi.id;
        this.latestReplyTime = tiezi.latestReplyTime;
        this.replyNum = tiezi.replyNum;
    }
}
