package wuxian.me.tongchengspider.biz.rent;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.util.ParserException;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;
import wuxian.me.tongchengspider.biz.BaseTongchengSpider;
import wuxian.me.tongchengspider.util.Helper;

/**
 * Created by wuxian on 21/8/2017.
 * http://hz.58.com/wenxin/hezu/pn3/
 */
public class RentListSpider extends BaseTongchengSpider {

    private String qu;
    private int page;

    public RentListSpider(String qu, int page) {
        this.qu = qu;
        this.page = page;
    }

    private static final String API = "http://hz.58.com/";
    private static final String API_POST = "/hezu/pn";

    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + qu + API_POST + page)
                .newBuilder();

        String host = "hz.58.com";
        Request request = new Request.Builder()
                .headers(Helper.getTongchengHeader(host, API))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    //Todo
    private void parseGroupList(String data) throws MaybeBlockedException, ParserException {

    }

    public int parseRealData(String data) {
        LogManager.info(data);
        try {
            parseGroupList(data);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;
        }

        return BaseSpider.RET_SUCCESS;
    }

    public String name() {
        return "RentListSpider:{qu:" + qu + ",page:" + page + "}";
    }
}
