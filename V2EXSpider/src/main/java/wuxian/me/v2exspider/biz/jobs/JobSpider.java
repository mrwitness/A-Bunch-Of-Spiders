package wuxian.me.v2exspider.biz.jobs;

import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.v2exspider.biz.ListSpiders;
import wuxian.me.v2exspider.biz.career.CareerSpider;
import wuxian.me.v2exspider.model.BaseTiezi;
import wuxian.me.v2exspider.model.Career;
import wuxian.me.v2exspider.model.Job;
import wuxian.me.v2exspider.save.TieziSaver;

/**
 * Created by wuxian on 9/8/2017.
 */
public class JobSpider extends ListSpiders {

    public static HttpUrlNode toUrlNode(JobSpider spider) {
        return ListSpiders.toUrlNode(spider);
    }

    public static JobSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains(API + TOPIC)) {
            return null;
        }
        return new JobSpider(Integer.parseInt(node.httpGetParam.get("p")));
    }

    private static final String TOPIC = "jobs";

    public JobSpider(int page) {
        super(TOPIC, page);
    }

    @Override
    protected void saveTiezi(BaseTiezi tiezi) {
        TieziSaver.getInstance().saveModel(new Job(tiezi));
    }

    @Override
    public String name() {
        return "JobSpider:{page:" + getPage() + "}";
    }
}
