package wuxian.me.xueqiuspider.biz.today.model;

/**
 * Created by wuxian on 25/7/2017.
 */
public class ZhiboItemData {

    //"{"id":183098,"text":"【传媒股临近午盘拉升】$华谊兄弟(SZ300027)$ 涨停，光线传媒涨近7%，华策影视、奥飞娱乐、中青宝等纷纷直线拉升。","mark":1,"target":"http://xueqiu.com/5124430882/89368918","created_at":1500953630000,"view_count":31327}"

    public Long id;

    public String text;

    public Integer mark;

    public String target;

    public Long created_at;

    public Integer view_count;

    @Override
    public String toString() {
        return "ZhiboItemData{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", mark=" + mark +
                ", target='" + target + '\'' +
                ", created_at=" + created_at +
                ", view_count=" + view_count +
                '}';
    }
}
