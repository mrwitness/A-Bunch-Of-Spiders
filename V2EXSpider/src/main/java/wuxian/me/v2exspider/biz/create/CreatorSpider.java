package wuxian.me.v2exspider.biz.create;

import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.v2exspider.biz.ListSpiders;
import wuxian.me.v2exspider.model.BaseTiezi;
import wuxian.me.v2exspider.model.Creator;
import wuxian.me.v2exspider.save.TieziSaver;

/**
 * Created by wuxian on 10/8/2017.
 */
public class CreatorSpider extends ListSpiders {

    public static HttpUrlNode toUrlNode(CreatorSpider spider) {
        return ListSpiders.toUrlNode(spider);
    }

    public static CreatorSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains(API + TOPIC)) {
            return null;
        }
        return new CreatorSpider(Integer.parseInt(node.httpGetParam.get("p")));
    }

    private static final String TOPIC = "create";

    public CreatorSpider(int page) {
        super(TOPIC, page);
    }

    @Override
    protected void saveTiezi(BaseTiezi tiezi) {
        TieziSaver.getInstance().saveModel(new Creator(tiezi));
    }

    @Override
    public String name() {
        return "JobSpider:{page:" + getPage() + "}";
    }
}

