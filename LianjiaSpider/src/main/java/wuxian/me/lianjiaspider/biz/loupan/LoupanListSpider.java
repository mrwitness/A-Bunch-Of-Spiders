package wuxian.me.lianjiaspider.biz.loupan;

import com.sun.istack.internal.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.api.BaseUrls;
import wuxian.me.lianjiaspider.biz.BaseLianjiaSpider;
import wuxian.me.lianjiaspider.model.Loupan;
import wuxian.me.lianjiaspider.save.LoupanSaver;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.util.List;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;
import static wuxian.me.spidercommon.util.StringUtil.*;
/**
 * Created by wuxian on 8/7/2017.
 */
public class LoupanListSpider extends BaseLianjiaSpider {

    private int city;
    private int page;
    private String cityUrl;

    public LoupanListSpider(int city, int pageNum) {
        this(city, BaseUrls.getLoupanUrl(city), pageNum);
    }

    public LoupanListSpider(String cityUrl, int pageNum) {
        this(BaseUrls.getLoupanCity(cityUrl), cityUrl, pageNum);
    }

    public LoupanListSpider(int city, String cityUrl, int pageNum) {
        this.city = city;
        this.page = pageNum;

        this.cityUrl = cityUrl;
    }

    private static final String API = "/loupan";

    private static List<String> urls;

    @Nullable
    public static HttpUrlNode toUrlNode(LoupanListSpider spider) {

        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = spider.cityUrl + API + "/pg" + spider.page + "/";
        return node;
    }

    @Nullable
    public static LoupanListSpider fromUrlNode(HttpUrlNode node) {
        if (urls == null) {
            urls = BaseUrls.getLoupanUrls();
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

        LoupanListSpider spider = new LoupanListSpider(findUrl, pg);

        return spider;
    }

    @Override
    protected Request buildRequest() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(cityUrl + API + "/pg" + page + "/")
                .newBuilder();

        String host = cityUrl.substring(7);

        Request request = new Request.Builder()
                .headers(Helper.getSpiderHeader(host, cityUrl + API + "/pg1/"))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private void parseLoupanList(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");

        HasAttributeFilter filter = new HasAttributeFilter("class", "house-lst");

        NodeList list = parser.extractAllNodesThatMatch(filter);

        try {
            nodelistEmptyIfTrueThrow(list);
        } catch (ParserException e) {
            throw new MaybeBlockedException();
        }

        Node node = firstChildIfNullThrow(list);

        list = node.getChildren();
        nodelistEmptyIfTrueThrow(list);

        for (int i = 0; i < list.size(); i++) {

            Node child = list.elementAt(i);
            if (child instanceof Bullet && child.getText().trim().contains("data-index")) {
                parseLoupan(list.elementAt(i));
            }
        }

    }

    private static final String REG_PRICE = "(?<=均价)\\s+[0-9]+";
    public static final Pattern PRICE_PATTERN = Pattern.compile(REG_PRICE);


    private static final String REG_LOUPANID = "(?<=loupan/)[a-zA-Z0-9_-]+";
    private static final Pattern LOUPANID_PATTERN = Pattern.compile(REG_LOUPANID);

    private static final String REG_LOUPANNUM = "(?<=为你找到)[0-9]+";
    private static final Pattern LOUPANNUM_PATTERN = Pattern.compile(REG_LOUPANNUM);

    private static final String REG_SIZE_MIN = "(?<=建面)[0-9]+";
    private static final Pattern SIZE_MIN_PATTERN = Pattern.compile(REG_SIZE_MIN);

    private static final String REG_SIZE_MAX = "(?<=~)[0-9]+";
    private static final Pattern SIZE_MAX_PATTERN = Pattern.compile(REG_SIZE_MAX);


    private void parseLoupan(Node node) throws MaybeBlockedException, ParserException {

        Loupan loupan = new Loupan();

        NodeList list = null;

        HasAttributeFilter filter = null;

        filter = new HasAttributeFilter("class", "col-1");
        if (node.getChildren() == null) {
            return;
        }
        Node infoNode = firstChild(node.getChildren().extractAllNodesThatMatch(filter, true));
        if (infoNode == null) {
            return;
        }

        list = infoNode.getChildren();
        for (int i = 0; i < list.size(); i++) {

            Node child = list.elementAt(i);
            if (child instanceof HeadingTag && child.getText().trim().contains("h2")) {
                String name = child.toPlainTextString().trim();
                int index = name.indexOf(" ");
                if (index != -1) {
                    name = name.substring(0, index);
                }
                loupan.name = name;

                NodeList nodeList = child.getChildren();
                for (int j = 0; j < nodeList.size(); j++) {
                    Node n = nodeList.elementAt(j);
                    if (n instanceof LinkTag) {
                        loupan.loupanId = matchedString(LOUPANID_PATTERN, n.getText().trim());
                        break;
                    }
                }
                continue;
            }

            if (child instanceof Div && child.getText().trim().contains("where")) {
                String location = child.toPlainTextString().trim();
                int index = location.indexOf("-");
                if (index == -1) {
                    continue;
                    //throw new MaybeBlockedException();
                }
                loupan.district = location.substring(0, index);

                loupan.location = location.substring(index + 1, location.length());


                continue;
            }

            if (child instanceof Div && child.getText().trim().contains("area")) {

                String area = removeAllBlanks(child.toPlainTextString());
                int index = area.indexOf("-");
                if (index == -1) {
                    continue;
                    //throw new MaybeBlockedException();
                }
                loupan.hushu = area.substring(0, index);

                loupan.sizeMin = matchedInteger(SIZE_MIN_PATTERN, area);
                loupan.sizeMax = matchedInteger(SIZE_MAX_PATTERN, area);
                continue;
            }

            if (child instanceof Div && child.getText().trim().contains("other")) {
                loupan.advantage = removeAllBlanks(child.toPlainTextString());
                continue;
            }

            if (child instanceof Div && child.getText().trim().contains("type")) {
                loupan.attribute = removeAllBlanks(child.toPlainTextString());
                continue;
            }

        }

        filter = new HasAttributeFilter("class", "col-2");
        Node priceNode = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));

        String price = matchedString(PRICE_PATTERN, priceNode.toPlainTextString());
        if (price != null) {
            loupan.price = Integer.parseInt(price.trim());
        } else {
            if (priceNode.toPlainTextString().contains("待定")) {
                loupan.price = -1;
            }
        }

        saveLoupan(loupan);
    }


    private void saveLoupan(Loupan loupan) {
        LoupanSaver.getInstance().saveModel(loupan);
    }

    private static final int XIAOQU_NUM_PER_PAGE = 10;

    private void parseLoupanNum(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");

        HasAttributeFilter filter = new HasAttributeFilter("class", "text");
        Node resultDes = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        Integer total = matchedInteger(LOUPANNUM_PATTERN, resultDes.toPlainTextString());

        if (total == null) {
            throw new MaybeBlockedException();
        }

        int pageTotal = 0;
        if (total % XIAOQU_NUM_PER_PAGE == 0) {
            pageTotal = total / XIAOQU_NUM_PER_PAGE;
        } else {
            pageTotal = total / XIAOQU_NUM_PER_PAGE + 1;
        }

        for (int j = 2; j <= pageTotal; j++) {
            LoupanListSpider spider = new LoupanListSpider(city, j);
            Helper.dispatchSpider(spider);
        }
    }

    @Override
    public int parseRealData(String data) {
        try {
            if (page == 1) {
                parseLoupanNum(data);
            }

            parseLoupanList(data);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;

        }
        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "LoupanListSpider: cityCode:" + city + " page:" + page;
    }
}
