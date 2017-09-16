package wuxian.me.lianjiaspider.biz.touming.ershou;

import com.sun.istack.internal.Nullable;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.api.touming.QuEnum;
import wuxian.me.lianjiaspider.biz.touming.BaseToumingSpider;
import wuxian.me.lianjiaspider.model.touming.Qu;
import wuxian.me.lianjiaspider.save.QuSaver;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import static wuxian.me.spidercommon.util.ParsingUtil.*;
import static wuxian.me.spidercommon.util.StringUtil.*;
import java.util.regex.Pattern;

/**
 * Created by wuxian on 10/7/2017.
 * http://www.tmsf.com/esf/esfnSearch_communityList.htm
 */
public class QuListSpider extends BaseToumingSpider {

    private static final String API = "http://www.tmsf.com/esf/esfnSearch_communityList.htm";

    private QuEnum quEnum;

    private int page;

    public QuListSpider(QuEnum qu, int page) {
        this.quEnum = qu;
        this.page = page;
    }


    @Nullable
    public static HttpUrlNode toUrlNode(QuListSpider spider) {

        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = API;
        node.httpPostParam.put("aid", String.valueOf(spider.quEnum.ordinal()));
        node.httpPostParam.put("page", String.valueOf(spider.page));
        return node;
    }

    @Nullable
    public static QuListSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.equals(API)) {
            return null;
        }

        return new QuListSpider(QuEnum.values()[Integer.parseInt(node.httpPostParam.get("aid"))]
                , Integer.parseInt(node.httpPostParam.get("page")));
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API)
                .newBuilder();
        FormBody.Builder bodyBuilder = new FormBody.Builder();

        bodyBuilder.add("aid", String.valueOf(quEnum.number()));
        bodyBuilder.add("pr", "");
        bodyBuilder.add("age", "");
        bodyBuilder.add("ctype", "");
        bodyBuilder.add("imgcount", "");
        bodyBuilder.add("page", String.valueOf(page));
        bodyBuilder.add("keywords", "");

        String host = "www.tmsf.com";
        Request request = new Request.Builder()
                .headers(Helper.getToumingSpiderHeader(host, API))
                .url(urlBuilder.build().toString())
                .post(bodyBuilder.build())
                .build();
        return request;
    }

    private void parseXiaoquList(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "cslist_l fl");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        NodeList list = node.getChildren();
        nodelistEmptyIfTrueThrow(list);
        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof Div && child.getText().trim().contains("cslistfy")) {
                parseQu(child);
            }
        }

    }

    private void parseQu(Node node) throws MaybeBlockedException, ParserException {

        Qu qu = new Qu();
        qu.quId = matchedLong(ID_PATTERN, node.getText().trim());

        NodeList list = node.getChildren();
        nodelistEmptyIfTrueThrow(list);

        Node tableNode = null;
        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof TableTag) {
                tableNode = child;
                break;
            }
        }

        if (tableNode == null) {
            throw new MaybeBlockedException();
        }

        list = tableNode.getChildren();

        nodelistEmptyIfTrueThrow(list);

        Node rowNode = null;
        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof TableRow) {
                rowNode = child;
                break;
            }
        }

        if (rowNode == null) {
            throw new MaybeBlockedException();
        }
        LogManager.info(removeAllBlanks(rowNode.toPlainTextString()));
        String content = removeAllBlanks(rowNode.toPlainTextString());

        qu.name = matchedString(NAME_PATTERN, content);

        qu.district = matchedString(QU_PATTERN, content);

        qu.location = matchedString(LOCATION_PATTERN, content);

        qu.sellNum = matchedInteger(SELL_PATTERN, content);

        qu.rentNum = matchedInteger(RENT_PATTERN, content);

        Float price = matchedFloat(PRICE_PATTERN, content);
        if (price != null) {
            qu.price = Math.round(price);
        }

        if (matchedString(UPPRICE_PATTERN, content) != null) {
            qu.priceChange = matchedFloat(UPPRICE_PATTERN, content);
            qu.changeType = 1;
        } else if (matchedString(DOWNPRICE_PATTERN, content) != null) {
            qu.priceChange = matchedFloat(DOWNPRICE_PATTERN, content);
            qu.changeType = 0;
        }

        saveQu(qu);
    }

    private void saveQu(Qu qu) {
        QuSaver.getInstance().saveModel(qu);
    }

    public static final String REG_XIAOQU_NUM = "(?<=共搜到)[0-9]+";
    private static final Pattern XIAOQU_NUM_PATTERN = Pattern.compile(REG_XIAOQU_NUM);

    public static final String REG_ID = "(?<=id=\")[0-9]+";
    public static final Pattern ID_PATTERN = Pattern.compile(REG_ID);

    public static final String REG_NAME = "(?<=图).+(?=\\[)";
    public static final Pattern NAME_PATTERN = Pattern.compile(REG_NAME);

    private static final String REG_QU = "(?<=\\[).+(?=])";
    private static final Pattern QU_PATTERN = Pattern.compile(REG_QU);

    private static final String REG_LOCATION = "(?<=]).+(?=地图)";
    private static final Pattern LOCATION_PATTERN = Pattern.compile(REG_LOCATION);

    private static final String REG_CELL = "[0-9]+(?=套出租房源)";
    private static final Pattern SELL_PATTERN = Pattern.compile(REG_CELL);

    private static final String REG_RENT = "[0-9]+(?=套房价走势)";
    private static final Pattern RENT_PATTERN = Pattern.compile(REG_RENT);

    private static final String REG_PRICE = "[0-9.]+(?=元/㎡)";
    private static final Pattern PRICE_PATTERN = Pattern.compile(REG_PRICE);


    private static final String REG_UPPRICE = "[0-9.]+(?=%↑)";
    private static final Pattern UPPRICE_PATTERN = Pattern.compile(REG_UPPRICE);

    private static final String REG_DOWNPRICE = "[0-9.]+(?=%↓)";
    private static final Pattern DOWNPRICE_PATTERN = Pattern.compile(REG_DOWNPRICE);

    private final static int XIAOQUNUM_PER_PAGE = 10;

    private void parseQuNum(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");

        HasAttributeFilter filter = new HasAttributeFilter("class", "filter");
        Node resultDes = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        LogManager.info("xiaoqu num:" + matchedInteger(XIAOQU_NUM_PATTERN, resultDes.toPlainTextString().trim()));

        Integer total = matchedInteger(XIAOQU_NUM_PATTERN, resultDes.toPlainTextString().trim());
        if (total != null) {
            int pageTotal = 0;
            if (pageTotal % XIAOQUNUM_PER_PAGE == 0) {
                pageTotal = total / XIAOQUNUM_PER_PAGE;
            } else {
                pageTotal = total / XIAOQUNUM_PER_PAGE + 1;
            }

            for (int j = 2; j <= pageTotal; j++) {
                QuListSpider spider = new QuListSpider(quEnum, j);
                Helper.dispatchSpider(spider);
            }
        }
    }

    @Override
    public int parseRealData(String data) {
        try {
            if (page == 1) {
                parseQuNum(data);
            }

            parseXiaoquList(data);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;

        }

        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "QuListSpider:{quEnum:" + quEnum.ordinal() + " page:" + page + "}";
    }
}
