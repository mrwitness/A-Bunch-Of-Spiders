package wuxian.me.v2exspider.model;

/**
 * Created by wuxian on 26/7/2017.
 */
public class BaseTiezi {

    public static String tableName = "career";

    public Long id;

    public String title;

    public String author;

    public String authorId;

    public Long latestResponseTime;

    public Integer replyNum;

    public long index() {
        return id;
    }
}
