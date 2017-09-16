package wuxian.me.lianjiaspider.biz.xiaoqu;

import com.sun.istack.internal.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.biz.BaseLianjiaSpider;
import wuxian.me.lianjiaspider.model.XiaoquSell;
import wuxian.me.lianjiaspider.save.SellSaver;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.lianjiaspider.api.BaseUrls;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;
import org.htmlparser.Node;
import org.htmlparser.util.NodeList;

import java.util.List;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;
/**
 * Created by wuxian on 24/6/2017.
 */
public class XiaoquListSpider extends BaseLianjiaSpider {

    private static List<String> urls;

    private String cityUrl;
    private String distinct;
    private int page;

    private static String API = "/xiaoqu/";

    @Nullable
    public static HttpUrlNode toUrlNode(XiaoquListSpider spider) {

        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = spider.cityUrl + API + spider.distinct + "/pg" + spider.page + "/";
        return node;
    }

    @Nullable
    public static XiaoquListSpider fromUrlNode(HttpUrlNode node) {

        if (urls == null) {
            urls = BaseUrls.getUrls();
        }

        String findUrl = null;
        for (String url : urls) {
            if (node.baseUrl.contains(url)) {
                findUrl = url;
                break;
            }
        }

        if (findUrl == null) {
            return null;
        }

        String findUrl2 = findUrl + API;

        if (!node.baseUrl.startsWith(findUrl2)) {
            return null;
        }

        int len = findUrl2.length();

        String s = node.baseUrl.substring(len);
        if (!s.contains("pg")) {
            return null;
        }

        int index = node.baseUrl.indexOf("/", len);
        String distinct = node.baseUrl.substring(len, index);
        index = node.baseUrl.indexOf("pg", index);

        int pg = Integer.parseInt(node.baseUrl.substring(index + 2, node.baseUrl.length() - 1));

        XiaoquListSpider spider = new XiaoquListSpider(findUrl, distinct, pg);

        return spider;
    }

    public XiaoquListSpider(String cityUrl, String distinct, int page) {
        this(BaseUrls.getCity(cityUrl), cityUrl, distinct, page);
    }

    private int city;

    public XiaoquListSpider(int city, String cityUrl, String distinct, int page) {
        this.city = city;
        this.cityUrl = cityUrl;
        this.distinct = distinct;
        this.page = page;
    }

    public XiaoquListSpider(int city, String distinct, int page) {

        this(city, BaseUrls.getUrl(city), distinct, page);
    }

    protected Request buildRequest() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(cityUrl + API + distinct + "/pg" + page + "/")
                .newBuilder();

        String host = cityUrl.substring(7);

        Request request = new Request.Builder()
                .headers(Helper.getSpiderHeader(host, cityUrl + API + distinct + "/pg1/"))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private static final int XIAOQU_NUM_PER_PAGE = 30;

    private void parseXiaoquNum(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");

        HasAttributeFilter filter = new HasAttributeFilter("class", "resultDes clear");
        Node resultDes = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        filter = new HasAttributeFilter("class", "total fl");

        Node totalNum = firstChildIfNullThrow(resultDes.getChildren().extractAllNodesThatMatch(filter, true));

        NodeList list = totalNum.getChildren();
        nodelistEmptyIfTrueThrow(list);

        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);

            if (child instanceof Span) {
                int total = Integer.parseInt(child.toPlainTextString().trim());

                int pageTotal = 0;
                if (total % XIAOQU_NUM_PER_PAGE == 0) {
                    pageTotal = total / XIAOQU_NUM_PER_PAGE;
                } else {
                    pageTotal = total / XIAOQU_NUM_PER_PAGE + 1;
                }

                for (int j = 2; j <= pageTotal; j++) {
                    XiaoquListSpider spider = new XiaoquListSpider(city, distinct, j);
                    Helper.dispatchSpider(spider);
                }
                break;
            }
        }

    }

    private void parseSell(Node node) throws MaybeBlockedException, ParserException {

        XiaoquSell sell = new XiaoquSell();

        NodeList list = null;

        HasAttributeFilter filter = null;

        filter = new HasAttributeFilter("class", "img");
        Node imgNode = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));
        sell.xiaoqu_id = matchedLong(XIAOQUID_PATTERN, imgNode.getText().trim());

        list = imgNode.getChildren();
        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof ImageTag) {
                sell.url = matchedString(URL_PATTERN, child.getText());
            }
        }

        filter = new HasAttributeFilter("class", "houseInfo");
        Node houseInfoNode = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));
        String houseinfo = houseInfoNode.toPlainTextString().trim();

        sell.chengjiaoTime = matchedInteger(DAY_PATTERN, houseinfo) + "天";
        sell.chengjiaoTao = matchedInteger(TAO_PATTERN, houseinfo);
        sell.rentTao = matchedInteger(RENT_PATTERN, houseinfo);

        filter = new HasAttributeFilter("class", "xiaoquListItemPrice");
        Node priceNode = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));
        String priceInfo = priceNode.toPlainTextString().trim();
        sell.price = matchedInteger(PRICE_PATTERN, priceInfo);

        if (sell.price == null) {
            sell.price = -1;
        }

        Integer price = matchedInteger(PRICE_MONTH_PATTERN, priceInfo);
        if (price != null) {
            sell.priceMonth = matchedInteger(PRICE_MONTH_PATTERN, priceInfo) + "月";
        } else {
            sell.priceMonth = "x月";
        }


        filter = new HasAttributeFilter("class", "xiaoquListItemSellCount");
        Node sellNode = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));
        sell.sellTao = matchedInteger(SELL_PATTERN, sellNode.toPlainTextString().trim());

        //LogManager.info("parse sell: " + sell.toString());

        if (sell.price == null && sell.rentTao == 0 && sell.sellTao == 0) {
            throw new MaybeBlockedException();
        } else {
            saveXiaoquSell(sell);
        }
    }

    private void saveXiaoquSell(XiaoquSell sell) {
        SellSaver.getInstance().saveModel(sell);
    }

    public static final String REG_URL = "(?<=data-original=\")[0-9a-zA-Z:/.?_x-]+(?=\")";
    public static final Pattern URL_PATTERN = Pattern.compile(REG_URL);

    private static final String REG_XIAOQUID = "(?<=xiaoqu/)[0-9]+";
    private static final Pattern XIAOQUID_PATTERN = Pattern.compile(REG_XIAOQUID);

    private static final String REG_BUILD = "[0-9]+(?=年建成)";
    private static final Pattern BUILD_PATTERN = Pattern.compile(REG_BUILD);

    private static final String REG_DAY = "[0-9]+(?=天)";
    private static final Pattern DAY_PATTERN = Pattern.compile(REG_DAY);

    private static final String REG_TAO = "(?<=成交)[0-9]+(?=套)";
    private static final Pattern TAO_PATTERN = Pattern.compile(REG_TAO);

    private static final String REG_RENT = "[0-9]+(?=套正在出租)";
    private static final Pattern RENT_PATTERN = Pattern.compile(REG_RENT);

    private static final String REG_PRICE = "[0-9]+(?=元/m2)";
    private static final Pattern PRICE_PATTERN = Pattern.compile(REG_PRICE);

    private static final String REG_PRICE_MONTH = "[0-9]+(?=月二手房参考)";
    private static final Pattern PRICE_MONTH_PATTERN = Pattern.compile(REG_PRICE_MONTH);

    private static final String REG_SELL = "[0-9]+(?=套)";
    private static final Pattern SELL_PATTERN = Pattern.compile(REG_SELL);

    private void parseXiaoquList(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");

        HasAttributeFilter filter = new HasAttributeFilter("class", "clear xiaoquListItem");

        NodeList list = parser.extractAllNodesThatMatch(filter);

        nodelistEmptyIfTrueThrow(list);

        for (int i = 0; i < list.size(); i++) {
            parseSell(list.elementAt(i));

        }

    }

    public int parseRealData(String s) {

        try {
            if (page == 1) {
                parseXiaoquNum(s);
            }

            parseXiaoquList(s);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;

        }

        return BaseSpider.RET_SUCCESS;
    }

    public String name() {
        return "XiaoquListSpider: cityCode:" + city + " distinct:" + distinct + " page:" + page;
    }
}
