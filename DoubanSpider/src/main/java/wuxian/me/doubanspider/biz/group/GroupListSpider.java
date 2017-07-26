package wuxian.me.doubanspider.biz.group;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.doubanspider.biz.BaseDoubanSpider;
import wuxian.me.doubanspider.util.Helper;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.StringUtil;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;
import static wuxian.me.spidercommon.util.StringUtil.removeAllBlanks;

/**
 * Created by wuxian on 24/7/2017.
 * https://www.douban.com/group/145219/discussion?start=50
 */
public class GroupListSpider extends BaseDoubanSpider {

    private static final String API = "https://www.douban.com/group/";
    private static final String API_POST = "/discussion";

    private Long groupId;
    private int page;

    public GroupListSpider(Long groupId, int page) {
        this.groupId = groupId;
        this.page = page;
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + groupId + API_POST)
                .newBuilder();
        urlBuilder.addQueryParameter("start", String.valueOf(page * 25));

        String host = "www.douban.com";
        Request request = new Request.Builder()
                .headers(Helper.getDoubanSpiderHeader(host, API))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private void parseGroupList(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "olt");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        NodeList list = node.getChildren();
        nodelistEmptyIfTrueThrow(list);

        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);

            if (child instanceof TableRow && !child.getText().trim().contains("class=\"th\"")) {
                parseItem(child, (i + 1 == list.size()));

            }
        }
    }

    private void parseItem(Node node, boolean lastItem) throws MaybeBlockedException
            , ParserException {
        NodeList list = node.getChildren();
        nodelistEmptyIfTrueThrow(list);


        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof TableColumn && child.getText().trim().contains("title")) {
                LogManager.info("title:" + removeAllBlanks(child.toPlainTextString()));

                child = firstChildOfType(child.getChildren(), LinkTag.class);
                if (child == null) {
                    throw new MaybeBlockedException();
                }

                LogManager.info("topId:" + matchedLong(TOPIC_ID_PATTERN, removeAllBlanks(child.getText())));

            } else if (child instanceof TableColumn && child.getText().trim().contains("nowrap")) {

                if (child.getText().trim().contains("class=\"\"")) {
                    LogManager.info("response:" + child.toPlainTextString().trim());
                } else if (child.getText().trim().contains("class=\"time\"")) {
                    LogManager.info("time:" + child.toPlainTextString().trim());

                    String time = child.toPlainTextString().trim();
                    if (GroupListSpider.TIME_PATTERN.matcher(time).matches()) {

                        time = "2017-" + time;
                        Integer formatedTime = StringUtil.formatYYMMDD8(time.substring(0, 10)); //用于存入数据库

                        LogManager.info("time:" + formatedTime);
                        if (lastItem && false) {
                            try {
                                Date date = sdf.parse(time);
                                if (new Date().getTime() - date.getTime() < 1000 * 60 * 60 * 24 * 7 * 3) {
                                    Helper.dispatchSpider(new GroupListSpider(groupId, page + 1));
                                }
                            } catch (ParseException e) {
                                ;
                            }
                        }
                    }
                } else {
                    child = firstChildOfType(child.getChildren(), LinkTag.class);
                    if (child == null) {
                        throw new MaybeBlockedException();
                    }
                    LogManager.info("author:" + matchedLong(AUTHER_ID_PATTERN, removeAllBlanks(child.getText())));
                    LogManager.info(child.toPlainTextString().trim());
                }
            }
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public static final String REG_TIME = "\\d\\d-\\d\\d\\s\\d\\d:\\d\\d";
    public static final Pattern TIME_PATTERN = Pattern.compile(REG_TIME);


    public static final String REG_TOPIC_ID = "(?<=topic/)[0-9]+";
    public static final Pattern TOPIC_ID_PATTERN = Pattern.compile(REG_TOPIC_ID);


    public static final String REG_AUTHER_ID = "(?<=people/)[0-9]+";
    public static final Pattern AUTHER_ID_PATTERN = Pattern.compile(REG_AUTHER_ID);


    @Override
    public int parseRealData(String data) {

        try {
            parseGroupList(data);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;

        }

        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "GroupListSpider:{groupId:" + groupId + " page:" + page + "}";
    }
}
