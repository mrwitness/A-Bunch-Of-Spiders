package wuxian.me.doubanspider.biz.group;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.doubanspider.biz.BaseDoubanSpider;
import wuxian.me.doubanspider.model.GroupTiezi;
import wuxian.me.doubanspider.save.GroupTieziSaver;
import wuxian.me.doubanspider.util.Helper;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.StringUtil;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

/**
 * Created by wuxian on 24/7/2017.
 * https://www.douban.com/group/topic/105236658/
 */
public class GroupTopicSpider extends BaseDoubanSpider {

    private static final String API = "https://www.douban.com/group/topic/";

    private GroupTiezi tiezi = new GroupTiezi();

    private Long topicId;

    public static HttpUrlNode toUrlNode(GroupTopicSpider spider) {
        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = API + spider.topicId;
        node.httpGetParam.put("groupId", spider.groupId);
        return node;
    }

    public static GroupTopicSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains(API)) {
            return null;
        }
        return new GroupTopicSpider(node.httpGetParam.get("groupId"), matchedLong(NODE_GROUPID_PATTERN, node.baseUrl));
    }

    private static final String REG_NODE_GROUPID = "(?<=topic/)\\d+";
    private static final Pattern NODE_GROUPID_PATTERN = Pattern.compile(REG_NODE_GROUPID);

    private String groupId;

    public GroupTopicSpider(String groupId, Long topId) {
        this.groupId = groupId;
        this.topicId = topId;
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + topicId)
                .newBuilder();

        String host = "www.douban.com";
        Request request = new Request.Builder()
                .headers(Helper.getDoubanSpiderHeader(host, API))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private void parseTopicContent(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "topic-content");

        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        tiezi.content = StringUtil.removeAllBlanks(node.toPlainTextString());

        NodeList list = childrenOfTypeAndContent(node.getChildren(), Div.class, "topic-figure cc");
        tiezi.pictureNum = list.size();
    }

    private void parseCommentList(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "topic-reply");

        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        nodelistEmptyIfTrueThrow(node.getChildren());

        NodeList list = node.getChildren();
        List<String> replyList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {

            Node child = list.elementAt(i);
            if (child instanceof Bullet) {
                parseComment(list.elementAt(i), replyList);
            }
        }
        tiezi.replyContent = replyList.toString();
    }

    private void parseComment(Node node, List<String> replyList) throws MaybeBlockedException, ParserException {

        HasAttributeFilter filter = new HasAttributeFilter("class", "user-face");
        Node user = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));

        if ((user = firstChildOfType(user.getChildren(), LinkTag.class)) == null) {
            return;
        }

        if (!user.getText().trim().contains(tiezi.authorId)) {
            int num = tiezi.otherReplyNum == null ? 0 : tiezi.otherReplyNum;
            tiezi.otherReplyNum = ++num;
            return;//过滤掉非作者的评论
        } else {
            int num = tiezi.selfReplyNum == null ? 0 : tiezi.selfReplyNum;
            tiezi.selfReplyNum = ++num;
        }

        filter = new HasAttributeFilter("class", "reply-doc content");

        Node reply = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));


        filter = new HasAttributeFilter("class", "operation_div");
        reply = firstChildIfNullThrow(reply.getChildren().extractAllNodesThatMatch(filter, true));
        while ((reply = reply.getPreviousSibling()) != null) {

            if (reply instanceof ParagraphTag && reply.getText().trim().contains("class=\"\"")) {
                String rep = reply.toPlainTextString().trim();
                //长度太小 也不是price 抛弃
                if (rep.length() < 5 && matchedString(MAYBE_PRICE_PATTERN, rep) == null) {
                    ;
                } else {
                    if (!replyList.contains(rep)) {
                        replyList.add(rep);
                    }

                }
                break;
            }
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final String REG_REPLY = "(?<=\\d\\d:\\d\\d:\\d\\d).+(?=回应)";
    private static final Pattern REPLAY_PATTERN = Pattern.compile(REG_REPLY);

    private static final String REG_MAYBE_PRICE = "[1-9]?\\d{3}";
    public static final Pattern MAYBE_PRICE_PATTERN = Pattern.compile(REG_MAYBE_PRICE);

    private static final String REG_MAYBE_WECHAT = "微信(号)?(是)?[:：]?[0-9A-Za-z]{5,12}|[wW][eE][cC][hH][aA][tT](是)?[:：]?[0-9A-Za-z]{5,12}";
    public static final Pattern MAYBE_WECHAT_PATTERN = Pattern.compile(REG_MAYBE_WECHAT);

    private static final String REG_STANDARD_WECHAT = "[0-9A-Za-z]{5,12}";
    public static final Pattern STANDARD_WECHAT_PATTERN = Pattern.compile(REG_STANDARD_WECHAT);

    private static final String REG_MAYBE_PHONE = "(手机|电话)(号码)?(是)?[:：]?[0-9A-Za-z]{5,12}|[pP][hH][oO][nN][eE]([nN][uU][mM][bB][eE][rR])?(是)?[:：]?[0-9A-Za-z]{5,12}";
    public static final Pattern MAYBE_PHONE_PATTERN = Pattern.compile(REG_MAYBE_PHONE);

    private static final String REG_AUTHOR_ID = "(?<=people/)[0-9a-zA-Z]+";
    public static final Pattern AUTHOR_ID_PATTERN = Pattern.compile(REG_AUTHOR_ID);

    private static final String REG_RENTTIME = "长租|短租";
    public static final Pattern RENTTIME_PATTERN = Pattern.compile(REG_RENTTIME);

    private void parseTitle(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("id", "content");

        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        node = firstChildOfType(node.getChildren(), HeadingTag.class);
        if (node == null) {
            throw new MaybeBlockedException();
        }
        tiezi.title = node.toPlainTextString().trim();
    }


    private void parseAuthor(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "from");

        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        Node author = firstChildOfType(node.getChildren(), LinkTag.class);
        if (author == null) {
            throw new MaybeBlockedException();
        }
        tiezi.author = author.toPlainTextString().trim();

        tiezi.authorId = matchedString(AUTHOR_ID_PATTERN, author.getText().trim());
        if (tiezi.authorId == null) {
            throw new MaybeBlockedException();
        }

        while ((node = node.getNextSibling()) != null) {
            if (node instanceof Span && node.getText().trim().contains("color-green")) {
                try {
                    String time = node.toPlainTextString().trim();
                    tiezi.postTime = sdf.parse(time).getTime();
                } catch (ParseException e) {

                }
                break;
            }
        }
    }

    public void getGuessedPrice(String content, List<Integer> list) {
        if (content == null || content.length() == 0 || list == null) {
            return;
        }
        Matcher matcher = MAYBE_PRICE_PATTERN.matcher(content);
        while (matcher.find()) {
            Integer price = Integer.parseInt(matcher.group());
            if (price != null && !list.contains(price)) {

                int end = matcher.end();
                if (end < content.length()) {
                    char c = content.charAt(end);
                    if (c >= '0' && c <= '9' || c == '米' || c == '号' || c == '方' || c == '路') {  //要么是手机号码的中间值 要么距离xxx xxx米
                        continue;
                    }
                }
                int start = matcher.start() - 1;
                if (start >= 0) {
                    char c = content.charAt(start);
                    if (c >= '0' && c <= '9') {  //是手机号码的中间值
                        continue;
                    }
                }
                list.add(price);
            }
        }
    }

    private void getGuessedWechat(String content, List<String> list) {
        if (content == null || content.length() == 0 || list == null) {
            return;
        }
        Matcher matcher = MAYBE_WECHAT_PATTERN.matcher(content);
        while (matcher.find()) {
            list.add(matchedString(STANDARD_WECHAT_PATTERN, matcher.group()));
        }

    }

    private void getGuessedPhone(String content, List<String> list) {
        if (content == null || content.length() == 0 || list == null) {
            return;
        }
        Matcher matcher = MAYBE_PHONE_PATTERN.matcher(content);
        while (matcher.find()) {
            list.add(matchedString(STANDARD_WECHAT_PATTERN, matcher.group()));
        }

    }

    private void getGuessedSex(String content, List<String> list) {
        if (content == null || content.length() == 0 || list == null) {
            return;
        }
        Matcher matcher = SEX_PATTERN.matcher(content);
        while (matcher.find()) {

            String s = matcher.group();
            if (!list.contains(s)) {
                list.add(s);
            }
        }
    }

    @Override
    public int parseRealData(String data) {
        try {
            tiezi.id = topicId;
            parseTitle(data);
            parseAuthor(data);
            parseTopicContent(data);
            parseCommentList(data);

            processParsedTopic(tiezi);

            LogManager.info(tiezi.toString());
            tiezi.created = System.currentTimeMillis();
            tiezi.updated = tiezi.created;

            //SpringBeans.groupTieziMapper().insertTiezi(tiezi);

            saveTiezi(tiezi);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;

        }
        return BaseSpider.RET_SUCCESS;
    }

    private void saveTiezi(GroupTiezi tiezi) {

        //过滤掉一定不要的source
        if (IgnoreSource.shouldIgnore(tiezi.title + tiezi.content)) {
            return;
        }

        GroupTieziSaver.getInstance().saveModel(tiezi);
    }

    private void processParsedTopic(GroupTiezi tiezi) {

        List<String> wechatList = new ArrayList<String>();
        getGuessedWechat(tiezi.content, wechatList);
        getGuessedWechat(tiezi.replyContent, wechatList);
        if (wechatList.size() != 0) {
            tiezi.guessWechat = wechatList.get(0);
        }

        List<String> phoneList = new ArrayList<String>();
        getGuessedPhone(tiezi.content, phoneList);
        getGuessedPhone(tiezi.replyContent, phoneList);
        if (phoneList.size() != 0) {
            tiezi.guessPhone = phoneList.get(0);
        }

        List<Integer> priceList = new ArrayList<Integer>();
        getGuessedPrice(tiezi.title, priceList);
        getGuessedPrice(tiezi.content, priceList);
        getGuessedPrice(tiezi.replyContent, priceList);
        tiezi.guessPrices = priceList.toString();
        for (Integer p : priceList) {
            if (tiezi.guessWechat != null && tiezi.guessWechat.contains(String.valueOf(p))) {
                continue;
            } else if (tiezi.guessPhone != null && tiezi.guessPhone.contains(String.valueOf(p))) {
                continue;
            }
            tiezi.guessPrice = p;
            break;
        }

        tiezi.shiNum = findNumberIfExist(matchedString(SHI_NUM_PATTERN, tiezi.content));

        tiezi.tingNum = findNumberIfExist(matchedString(TING_NUM_PATTERN, tiezi.content));

        tiezi.weiNum = findNumberIfExist(matchedString(WEI_NUM_PATTERN, tiezi.content));

        tiezi.chaoxiang = matchedString(CHAOXIANG_PATTERN, tiezi.content);

        tiezi.louceng = findNumberIfExist(matchedString(LOUCENG_PATTERN, tiezi.content));

        String yafu = matchedString(YAFU_PATTERN, tiezi.content);
        tiezi.ya = findNumberIfExist(matchedString(YA_PATTERN, yafu));
        tiezi.fu = findNumberIfExist(matchedString(FU_PATTERN, yafu));

        List<String> sexList = new ArrayList<String>();
        getGuessedSex(tiezi.title, sexList);
        getGuessedSex(tiezi.content, sexList);
        getGuessedSex(tiezi.replyContent, sexList);
        if (sexList.size() != 0) {
            String sex = sexList.get(0);
            if (sex != null) {
                if (sex.equals("男生")) {
                    tiezi.sex = 1;
                } else if (sex.equals("女生") || sex.equals("妹纸") || sex.equals("妹子")) {
                    tiezi.sex = 0;
                } else {
                    tiezi.sex = 2;
                }
            }
        }
        tiezi.guessSex = sexList.toString();

        String yizu = matchedString(YIZU_PATTERN, tiezi.title + tiezi.content + tiezi.replyContent);
        if (yizu != null) {
            tiezi.rentStatus = 1;
        }

        String liveTime = matchedString(LIVE_TIME_PATTERN, tiezi.title + tiezi.content + tiezi.replyContent);

        if (liveTime != null) {
            if (liveTime.equals("月末") || liveTime.equals("月底")) {
                tiezi.guessLiveTime = 31;
            } else if (liveTime.equals("月初")) {
                tiezi.guessLiveTime = 1;
            } else if (liveTime.equals("月中")) {
                tiezi.guessLiveTime = 15;
            } else {
                tiezi.guessLiveTime = Integer.parseInt(liveTime);
            }
        }

        String rentType = matchedString(RENT_TYPE_PATTERN, tiezi.title + tiezi.content + tiezi.replyContent);
        if (rentType != null) {

            if (rentType.contains("出")) {
                tiezi.rentType = 0;
            } else if (rentType.contains("转")) {
                tiezi.rentType = 1;
            } else {
                tiezi.rentType = 2;
            }
        }

        String rentTime = matchedString(RENTTIME_PATTERN, tiezi.title + tiezi.content);

        if (rentTime != null) {
            if (rentTime.contains("短")) {
                tiezi.rentTime = 2;
            } else {
                tiezi.rentTime = 1;
            }
        }
    }

    private static final String REG_SEX = "男生|女生|男女|妹纸|妹子";
    public static final Pattern SEX_PATTERN = Pattern.compile(REG_SEX);


    private static final String REG_CHAOXIANG = "(?<=朝)[东南西北]{1,2}";
    public static final Pattern CHAOXIANG_PATTERN = Pattern.compile(REG_CHAOXIANG);

    private static final String REG_LOUCENG = "[0-9]*[零一二三四五六七八九]?(?=楼)";
    public static final Pattern LOUCENG_PATTERN = Pattern.compile(REG_LOUCENG);


    private static final String REG_SHI_NUM = "[0-9]*[零一二三四五六七八九]?(?=室)";
    public static final Pattern SHI_NUM_PATTERN = Pattern.compile(REG_SHI_NUM);

    private static final String REG_TING_NUM = "[0-9]*[零一二三四五六七八九]?(?=厅)";
    public static final Pattern TING_NUM_PATTERN = Pattern.compile(REG_TING_NUM);

    private static final String REG_WEI_NUM = "[0-9]*[零一二三四五六七八九]?(?=卫)";
    public static final Pattern WEI_NUM_PATTERN = Pattern.compile(REG_WEI_NUM);

    private static final String REG_NUM = "[0-9]+";
    public static final Pattern NUM_PATTERN = Pattern.compile(REG_NUM);

    private static final String REG_HAN_NUM = "[零一二三四五六七八九]";
    public static final Pattern HAN_NUM_PATTERN = Pattern.compile(REG_HAN_NUM);

    private static final String REG_YAFU = "[押压][0-9]?[零一二三四五六七八九]?[,，.。]?[付复负][0-9]?[零一二三四五六七八九]?|[付复负][0-9]?[零一二三四五六七八九]?[,，.。]?[押压][0-9]?[零一二三四五六七八九]?";
    public static final Pattern YAFU_PATTERN = Pattern.compile(REG_YAFU);

    private static final String REG_YA = "(?<=[押压])[0-9]?[零一二三四五六七八九]?";
    public static final Pattern YA_PATTERN = Pattern.compile(REG_YA);

    private static final String REG_FU = "(?<=[付复负])[0-9]?[零一二三四五六七八九]?";
    public static final Pattern FU_PATTERN = Pattern.compile(REG_FU);

    private static final String REG_YIZU = "已租|已出|已找";
    public static final Pattern YIZU_PATTERN = Pattern.compile(REG_YIZU);

    //入住时间
    private static final String REG_LIVE_TIME = "月末|月底|月初|月中|\\d+(?=号)";
    public static final Pattern LIVE_TIME_PATTERN = Pattern.compile(REG_LIVE_TIME);

    private static final String REG_RENT_TYPE = "出租|转租|求?[和合]租";
    public static final Pattern RENT_TYPE_PATTERN = Pattern.compile(REG_RENT_TYPE);

    public Integer findNumberIfExist(String content) {
        if (content == null) {
            return null;
        }

        String han = matchedString(HAN_NUM_PATTERN, content);
        return han != null ? transferFromHanzi(han)
                : matchedInteger(NUM_PATTERN, content);
    }

    private Integer transferFromHanzi(String content) {
        if (content == null) {
            return null;
        }

        if (content.equals("零")) {
            return 0;
        } else if (content.equals("一")) {
            return 1;
        } else if (content.equals("二")) {
            return 2;
        } else if (content.equals("三")) {
            return 3;
        } else if (content.equals("四")) {
            return 4;
        } else if (content.equals("五")) {
            return 5;
        } else if (content.equals("六")) {
            return 6;
        } else if (content.equals("七")) {
            return 7;
        } else if (content.equals("八")) {
            return 8;
        } else if (content.equals("九")) {
            return 9;
        }
        return null;
    }

    @Override
    public String name() {
        return "GroupTopicSpider:{topicId:" + topicId + "}";
    }
}
