package wuxian.me.v2exspider.model;


/**
 * Created by wuxian on 9/8/2017.
 */
public class Job extends BaseTiezi {

    public static String tableName = "job";

    public Job() {
        super();
    }

    public Job(BaseTiezi tiezi) {
        this.author = tiezi.author;
        this.title = tiezi.title;
        this.authorId = tiezi.authorId;
        this.id = tiezi.id;
        this.latestReplyTime = tiezi.latestReplyTime;
        this.replyNum = tiezi.replyNum;
    }
}
