package wuxian.me.lianjiaspider.biz.touming.xinfang;

import com.sun.istack.internal.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.api.touming.QuEnum;
import wuxian.me.lianjiaspider.biz.touming.BaseToumingSpider;
import wuxian.me.lianjiaspider.biz.touming.PriceCracker;
import wuxian.me.lianjiaspider.model.touming.NewQu;
import wuxian.me.lianjiaspider.save.NewQuSaver;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.StringUtil.*;
/**
 * Created by wuxian on 10/7/2017.
 * http://www.tmsf.com/newhouse/property_searchall.htm?sid=33&districtid=330102&page=2
 */
public class NewQuListSpider extends BaseToumingSpider {

    private QuEnum quEnum;
    private int page;

    public NewQuListSpider(QuEnum qu, int page) {

        this.quEnum = qu;

        this.page = page;
    }

    private static final String API = "http://www.tmsf.com/newhouse/property_searchall.htm?sid=33";

    @Nullable
    public static HttpUrlNode toUrlNode(NewQuListSpider spider) {

        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = "http://www.tmsf.com/newhouse/property_searchall.htm";
        node.httpGetParam.put("sid", String.valueOf(33));
        node.httpGetParam.put("districtid", String.valueOf(spider.quEnum.ordinal()));
        node.httpGetParam.put("page", String.valueOf(spider.page));
        return node;
    }

    @Nullable
    public static NewQuListSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains("http://www.tmsf.com/newhouse/property_searchall.htm")) {
            return null;
        }

        return new NewQuListSpider(QuEnum.values()[Integer.parseInt(node.httpGetParam.get("districtid"))]
                , Integer.parseInt(node.httpGetParam.get("page")));
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API)
                .newBuilder();
        urlBuilder.addQueryParameter("districtid", String.valueOf(quEnum.number()));
        urlBuilder.addQueryParameter("page", String.valueOf(page));

        String host = "www.tmsf.com";
        Request request = new Request.Builder()
                .headers(Helper.getToumingSpiderHeader(host, API))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private final static int XIAOQUNUM_PER_PAGE = 6;

    private void saveNewqu(NewQu newQu) {
        NewQuSaver.getInstance().saveModel(newQu);
    }

    private void parseNewQuNum(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");

        HasAttributeFilter filter = new HasAttributeFilter("class", "s_how01 fl");
        Node resultDes = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        Integer total = matchedInteger(NUM_PATTERN, removeAllBlanks(resultDes.toPlainTextString()));

        if (total != null) {
            if (total == 0) {
                throw new MaybeBlockedException();
            }
            int pageTotal = 0;
            if (pageTotal % XIAOQUNUM_PER_PAGE == 0) {
                pageTotal = total / XIAOQUNUM_PER_PAGE;
            } else {
                pageTotal = total / XIAOQUNUM_PER_PAGE + 1;
            }

            for (int j = 2; j <= pageTotal; j++) {
                NewQuListSpider spider = new NewQuListSpider(quEnum, j);
                Helper.dispatchSpider(spider);
            }
        }
    }

    private void parseXiaoquList(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("name", "propertyids");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        do {

            if (node instanceof InputTag && node.getText().trim().contains("propertyids")) {
                NewQu qu = new NewQu();
                qu.quId = matchedLong(ID_PATTERN, node.getText().trim());

                do {
                    node = node.getNextSibling();
                } while (node != null && !(node instanceof Bullet));

                if (node == null) {
                    break;
                }

                parseNewQu(node, qu);
                node = node.getNextSibling();

            } else {
                node = node.getNextSibling();
            }
        } while (node != null);

    }

    public static final String REG_ID = "(?<=value=\")[0-9]+";
    public static final Pattern ID_PATTERN = Pattern.compile(REG_ID);

    public static final String REG_KESHOU = "(?<=可售)[0-9]+(?=套)";
    public static final Pattern KESHOU_PATTERN = Pattern.compile(REG_KESHOU);

    public static final String REG_ZONGTAO = "(?<=总)[0-9]+(?=套)";
    public static final Pattern ZONGTAO_PATTERN = Pattern.compile(REG_ZONGTAO);


    public static final String REG_SELLTYPE = "在售|待售|尾盘|售完";
    public static final Pattern SELLTYPE_PATTERN = Pattern.compile(REG_SELLTYPE);

    public static final String REG_TUIGUANG = "(?<=推广名：).+(?=物业类型)";
    public static final Pattern TUIGUANG_PATTERN = Pattern.compile(REG_TUIGUANG);

    public static final String REG_WUYETYPE = "(?<=物业类型：).+(?=项目位置)";
    public static final Pattern WUYETYPE_PATTERN = Pattern.compile(REG_WUYETYPE);

    public static final String REG_LOCATION = "(?<=]).+(?=最新动态：)";
    public static final Pattern LOCATION_PATTERN = Pattern.compile(REG_LOCATION);

    public static final String REG_DISTRICT = "(?<=项目位置：\\[).+(?=])";
    public static final Pattern DISTRICT_PATTERN = Pattern.compile(REG_DISTRICT);

    public static final String REG_DONGTAI = "(?<=最新动态：).+(?=楼盘动态)";
    public static final Pattern DONGTAI_PATTERN = Pattern.compile(REG_DONGTAI);

    public static final String REG_NUM = "(?<=共有)[0-9]+(?=个)";
    public static final Pattern NUM_PATTERN = Pattern.compile(REG_NUM);

    private void parseNewQu(Node node, NewQu newQu) throws MaybeBlockedException
            , ParserException {
        //LogManager.info(removeAllBlanks(node.toPlainTextString()));
        String content = removeAllBlanks(node.toPlainTextString());

        newQu.canSell = matchedInteger(KESHOU_PATTERN, content);

        newQu.allSell = matchedInteger(ZONGTAO_PATTERN, content);

        String sellType = matchedString(SELLTYPE_PATTERN, content);
        if (sellType.equals("在售")) {
            newQu.sellType = 2;
        } else if (sellType.equals("待售")) {
            newQu.sellType = 1;
        } else if (sellType.equals("尾盘")) {
            newQu.sellType = 3;
        } else {
            newQu.sellType = 4;
        }

        int index = content.indexOf("套/总");
        int index2 = content.indexOf(sellType);
        if (index != -1 && index2 != -1) {

            String reg = "(?<=套).+(?=" + sellType + ")";
            Pattern pattern = Pattern.compile(reg);
            newQu.name = matchedString(pattern, content.substring(index + 3, index2 + 2));
            newQu.name = replaceHtmlDot(newQu.name);
        }

        newQu.tuiguangName = replaceHtmlDot(matchedString(TUIGUANG_PATTERN, content));
        newQu.quType = matchedString(WUYETYPE_PATTERN, content);

        int index3 = content.indexOf("最新动态");
        if (index3 == -1) {
            throw new ParserException();
        }
        newQu.district = matchedString(DISTRICT_PATTERN, content.substring(0, index3));

        int index4 = content.indexOf(newQu.district + "]");
        if (index4 == -1) {
            throw new ParserException();
        }

        newQu.location = matchedString(LOCATION_PATTERN, content.substring(index4));

        newQu.dongtai = replaceHtmlCharactors(matchedString(DONGTAI_PATTERN, content));

        HasAttributeFilter filter = new HasAttributeFilter("class", "word1");
        Node priceNode = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));

        String s = removeAllBlanks(priceNode.toPlainTextString());
        if (s.contains("无成交")) {
            //LogManager.info("近期无成交");
        } else {
            NodeList list = priceNode.getChildren();
            StringBuilder builder = new StringBuilder("");
            for (int i = 0; i < list.size(); i++) {
                Integer p = PriceCracker.crackPriceNode(list.elementAt(i));
                if (p != null) {
                    builder.append(p);
                }
            }
            if (builder.toString().length() != 0) {
                newQu.price = Integer.parseInt(builder.toString());
            }
        }
        //LogManager.info(newQu.toString());
        //LogManager.info("");

        saveNewqu(newQu);
    }

    @Override
    public int parseRealData(String data) {
        try {
            if (page == 1) {
                parseNewQuNum(data);
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
        return "NewQuListSpider:{qu:" + quEnum.ordinal() + " page:" + page + "}";
    }
}
