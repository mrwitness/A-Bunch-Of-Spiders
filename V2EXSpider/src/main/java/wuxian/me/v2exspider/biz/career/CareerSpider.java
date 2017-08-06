package wuxian.me.v2exspider.biz.career;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.StringUtil;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;
import wuxian.me.v2exspider.biz.BaseV2EXSpider;
import wuxian.me.v2exspider.model.BaseTiezi;
import wuxian.me.v2exspider.save.CareerTieziSaver;
import wuxian.me.v2exspider.util.Helper;
import wuxian.me.v2exspider.util.SpringBeans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

/**
 * Created by wuxian on 5/8/2017.
 * https://www.v2ex.com/go/career?p=1
 */
public class CareerSpider extends BaseV2EXSpider {

    private static final String API = "https://www.v2ex.com/go/career";
    private int page;

    public static HttpUrlNode toUrlNode(CareerSpider spider) {
        HttpUrlNode node = new HttpUrlNode();

        node.baseUrl = API;
        node.httpGetParam.put("p", String.valueOf(spider.page));
        return node;
    }

    public static CareerSpider fromUrlNode(HttpUrlNode node) {

        if (!node.baseUrl.contains(API)) {
            return null;
        }
        return new CareerSpider(Integer.parseInt(node.httpGetParam.get("p")));
    }

    public CareerSpider(int page) {
        this.page = page;
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API)
                .newBuilder();
        urlBuilder.addQueryParameter("p", String.valueOf(page));

        String host = "www.v2ex.com";
        Request request = new Request.Builder()
                .headers(Helper.getCareerSpiderHeader(host, "https://www.v2ex.com/go/career?p=1"))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    //帖子id
    public static final String REG_TID = "(?<=t_)\\d+";
    public static final Pattern TID_PATTERN = Pattern.compile(REG_TID);

    //作者id
    public static final String REG_AID = "(?<=from_)\\d+";
    public static final Pattern AID_PATTERN = Pattern.compile(REG_AID);

    private void parseItem(Node node) throws MaybeBlockedException, ParserException {

        BaseTiezi tiezi = new BaseTiezi();
        //LogManager.info("tiezi id:" + matchedInteger(TID_PATTERN, node.getText()));
        //LogManager.info("author id:" + matchedInteger(AID_PATTERN, node.getText()));

        tiezi.id = matchedLong(TID_PATTERN, node.getText());
        tiezi.authorId = matchedString(AID_PATTERN, node.getText());

        node = firstChildOfType(node.getChildren(), TableTag.class);
        if (node == null) {
            throw new MaybeBlockedException();
        }
        node = firstChildOfType(node.getChildren(), TableRow.class);
        if (node == null) {
            throw new MaybeBlockedException();
        }

        NodeList list = node.getChildren();
        for (int i = 0; i < list.size(); i++) {

            Node child = list.elementAt(i);

            if (child instanceof TableColumn && child.getText().contains("right")) {

                Node c = firstChildOfType(child.getChildren(), LinkTag.class);
                if (c == null) {
                    //LogManager.info("response num:" + 0);

                    tiezi.replyNum = 0;
                } else {
                    //LogManager.info("response num:" + c.toPlainTextString().trim());
                    tiezi.replyNum = Integer.parseInt(c.toPlainTextString().trim());
                }

            } else if (child instanceof TableColumn && child.getText().contains("middle")) {
                parseItemMain(child, tiezi);
            }
        }

        //LogManager.info("paresed tiezi: "+tiezi.toString());
        saveTiezi(tiezi);
    }

    //作者id
    public static final String REG_ANAME = "[0-9a-zA-Z]+";
    public static final Pattern ANAME_PATTERN = Pattern.compile(REG_ANAME);

    private void parseItemMain(Node node, BaseTiezi tiezi) throws MaybeBlockedException, ParserException {
        NodeList list = node.getChildren();
        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);

            if (child instanceof Span && child.getText().contains("item_title")) {
                //LogManager.info("title:" + child.toPlainTextString().trim());
                tiezi.title = child.toPlainTextString().trim();
            } else if (child instanceof Span && child.getText().contains("small")) {
                //LogManager.info(StringUtil.removeAllBlanks(child.toPlainTextString()));
                //LogManager.info("author name:" + matchedString(ANAME_PATTERN, child.toPlainTextString()));

                tiezi.author = matchedString(ANAME_PATTERN, child.toPlainTextString());
                //LogManager.info("post time:" + matchedString(POSTTIME_PATTERN, StringUtil.removeAllBlanks(child.toPlainTextString())));
                String resposeTime = matchedString(POSTTIME_PATTERN, StringUtil.removeAllBlanks(child.toPlainTextString()));

                long t = System.currentTimeMillis();
                int index = -1;
                if (resposeTime.contains("秒")) {
                    //LogManager.info("reponse time:" + sdf.format(new Date(t)));
                    tiezi.latestReplyTime = t;
                } else if ((index = resposeTime.indexOf("天")) != -1) {
                    int day = Integer.parseInt(resposeTime.substring(0, index));
                    //LogManager.info("reponse time:" + sdf.format(new Date(t - day * 3600 * 1000 * 24)));
                    tiezi.latestReplyTime = t - day * 3600 * 1000 * 24;
                } else {
                    int m = 0;
                    int hour = resposeTime.indexOf("小时");

                    int minute = -1;
                    if (hour != -1) {
                        m += Integer.parseInt(resposeTime.substring(0, hour)) * 60;
                    }
                    minute = resposeTime.indexOf("分钟");
                    if (minute != -1) {
                        m += Integer.parseInt(resposeTime.substring(hour + 2, minute));
                    }
                    if (m != 0) {
                        //LogManager.info("reponse time:" + sdf.format(new Date(t - m * 60 * 1000)));
                        tiezi.latestReplyTime = t - m * 60 * 1000;
                    } else {
                        //LogManager.info("response time:" + resposeTime);
                        try {
                            tiezi.latestReplyTime = sdf2.parse(resposeTime).getTime();
                        } catch (ParseException e) {
                            ;
                        }
                    }
                }
            }
        }
    }

    private void saveTiezi(BaseTiezi tiezi) {
        CareerTieziSaver.getInstance().saveModel(tiezi);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");

    private void parseItemList(String data) throws MaybeBlockedException, ParserException {

        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("id", "TopicsNode");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        NodeList list = childrenOfTypeAndContent(node.getChildren(), Div.class, "class=\"cell");

        if (list == null || list.size() == 0) {
            throw new MaybeBlockedException();
        }

        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            parseItem(child);
        }

    }

    public static final String REG_NUM = "(?<=max=\")\\d+";
    public static final Pattern NUM_PATTERN = Pattern.compile(REG_NUM);

    public static final String REG_POSTTIME = "[0-9分钟小时月天年几秒:-]+(?=前)|[0-9:-]{18}";
    public static final Pattern POSTTIME_PATTERN = Pattern.compile(REG_POSTTIME);


    private void parseNum(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "page_input");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        //LogManager.info("total page:" + matchedInteger(NUM_PATTERN, node.getText()));

        Integer num = matchedInteger(NUM_PATTERN, node.getText());
        if (num == null) {
            throw new MaybeBlockedException();
        }

        for (int i = 2; i < num; i++) {
            Helper.dispatchSpider(new CareerSpider(i));
        }
    }

    @Override
    public int parseRealData(String data) {
        try {
            if (page == 1) {
                parseNum(data);
            }
            parseItemList(data);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;
        }

        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "CareerSpider:{page:" + page + "}";
    }
}