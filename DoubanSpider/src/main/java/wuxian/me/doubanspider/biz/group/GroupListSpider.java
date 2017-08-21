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
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.StringUtil;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;
import static wuxian.me.spidercommon.util.StringUtil.removeAllBlanks;

/**
 * Created by wuxian on 24/7/2017.
 * https://www.douban.com/group/145219/discussion?start=50
 */
public class GroupListSpider extends BaseDoubanSpider {

    public static final Long STOP_TIME_INTERNAL = 1000 * 60 * 60 * 12 * 1 * 1L;

    private static final String API = "https://www.douban.com/group/";
    private static final String API_POST = "/discussion";

    private String groupId;
    private int page;

    public static HttpUrlNode toUrlNode(GroupListSpider spider) {
        HttpUrlNode node = new HttpUrlNode();

        node.baseUrl = API + spider.groupId + API_POST;
        node.httpGetParam.put("start", String.valueOf(spider.page));
        return node;
    }

    public static GroupListSpider fromUrlNode(HttpUrlNode node) {

        if (!node.baseUrl.contains(API) || !node.baseUrl.contains(API_POST)) {
            return null;
        }
        String id = matchedString(NODE_GROUPID_PATTERN, node.baseUrl);
        return new GroupListSpider(id, Integer.parseInt(node.httpGetParam.get("start")));
    }

    private static final String REG_NODE_GROUPID = "(?<=group/)[0-9a-zA-Z]+";
    private static final Pattern NODE_GROUPID_PATTERN = Pattern.compile(REG_NODE_GROUPID);


    public GroupListSpider(String groupId, int page) {
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
        List<Long> topList = new ArrayList<Long>();

        NodeList list1 = new NodeList();
        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof TableRow && !child.getText().trim().contains("class=\"th\"")) {
                list1.add(child);
            }
        }

        for (int i = 0; i < list1.size(); i++) {
            Node child = list1.elementAt(i);
            Long id = parseItem(child, (i + 1 == list1.size()));
            if (id != null) {
                topList.add(id);
            }
        }

        for (Long topidId : topList) {
            Helper.dispatchSpider(new GroupTopicSpider(groupId, topidId));
        }
    }

    private Long parseItem(Node node, boolean lastItem) throws MaybeBlockedException
            , ParserException {

        Long topId = null;
        NodeList list = node.getChildren();

        if(list == null || list.size() == 0) {
            return null;  //允许为空
        }


        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof TableColumn && child.getText().trim().contains("title")) {
                child = firstChildOfType(child.getChildren(), LinkTag.class);
                if (child == null) {
                    throw new MaybeBlockedException();
                }
                String title = StringUtil.removeAllBlanks(child.toPlainTextString());
                if(IgnoreSource.shouldIgnore(title)) {  //直接忽略掉不要的源

                    LogManager.info("帖子标题为:"+title+" 这里进行忽略");
                    return null;
                }

                topId = matchedLong(TOPIC_ID_PATTERN, removeAllBlanks(child.getText()));

            } else if (child instanceof TableColumn && child.getText().trim().contains("nowrap")) {
                if (child.getText().trim().contains("class=\"time\"")) {
                    if (lastItem) {        //最后一条item用于判断是否应该dispatch下一页的spider。
                        String time = child.toPlainTextString().trim();
                        if (GroupListSpider.TIME_PATTERN.matcher(time).matches()) {
                            time = "2017-" + time;
                            try {
                                Date date = sdf.parse(time);
                                if (new Date().getTime() - date.getTime() < STOP_TIME_INTERNAL) {
                                    Helper.dispatchSpider(new GroupListSpider(groupId, page + 1));
                                }
                            } catch (ParseException e) {
                                ;
                            }
                        } else {
                            Helper.dispatchSpider(new GroupListSpider(groupId, page + 1));
                        }
                    }

                }
            }
        }

        return topId;
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
