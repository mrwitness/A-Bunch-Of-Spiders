package wuxian.me.v2exspider.biz.career;

import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.v2exspider.biz.ListSpiders;
import wuxian.me.v2exspider.model.BaseTiezi;
import wuxian.me.v2exspider.model.Career;
import wuxian.me.v2exspider.save.TieziSaver;

/**
 * Created by wuxian on 5/8/2017.
 * https://www.v2ex.com/go/career?p=1
 */
public class CareerSpider extends ListSpiders {

    public static HttpUrlNode toUrlNode(CareerSpider spider) {
        return ListSpiders.toUrlNode(spider);
    }

    public static CareerSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains(API + TOPIC)) {
            return null;
        }
        return new CareerSpider(Integer.parseInt(node.httpGetParam.get("p")));
    }

    private static final String TOPIC = "career";
    public CareerSpider(int page) {
        super(TOPIC, page);
    }

    protected void saveTiezi(BaseTiezi tiezi) {
        TieziSaver.getInstance().saveModel(new Career(tiezi));
    }

    @Override
    public String name() {
        return "CareerSpider:{page:" + getPage() + "}";
    }
}
