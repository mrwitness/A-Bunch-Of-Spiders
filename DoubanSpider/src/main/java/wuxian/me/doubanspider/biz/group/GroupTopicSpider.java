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
import wuxian.me.doubanspider.util.Helper;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.NodeLogUtil;
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

    public GroupTopicSpider(Long topId) {
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

        if (!user.getText().trim().contains(String.valueOf(tiezi.authorId))) {
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
                    replyList.add(rep);
                }
                break;
            }
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final String REG_REPLY = "(?<=\\d\\d:\\d\\d:\\d\\d).+(?=回应)";
    private static final Pattern REPLAY_PATTERN = Pattern.compile(REG_REPLY);

    private static final String REG_MAYBE_PRICE = "[1234]?\\d{3}";
    public static final Pattern MAYBE_PRICE_PATTERN = Pattern.compile(REG_MAYBE_PRICE);

    private static final String REG_MAYBE_WECHAT = "微信(号)?(是)?[:：]?[0-9A-Za-z]{5,12}|[wW][eE][cC][hH][aA][tT](是)?[:：]?[0-9A-Za-z]{5,12}";
    public static final Pattern MAYBE_WECHAT_PATTERN = Pattern.compile(REG_MAYBE_WECHAT);

    private static final String REG_STANDARD_WECHAT = "[0-9A-Za-z]{5,12}";
    public static final Pattern STANDARD_WECHAT_PATTERN = Pattern.compile(REG_STANDARD_WECHAT);

    private static final String REG_MAYBE_PHONE = "(手机|电话)(号码)?(是)?[:：]?[0-9A-Za-z]{5,12}|[pP][hH][oO][nN][eE]([nN][uU][mM][bB][eE][rR])?(是)?[:：]?[0-9A-Za-z]{5,12}";
    public static final Pattern MAYBE_PHONE_PATTERN = Pattern.compile(REG_MAYBE_PHONE);


    private static final String REG_AUTHOR_ID = "(?<=people/)\\d+";
    public static final Pattern AUTHOR_ID_PATTERN = Pattern.compile(REG_AUTHOR_ID);


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
        tiezi.authorId = matchedLong(AUTHOR_ID_PATTERN, author.getText().trim());
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

    private void getGuessedPrice(String content, List<Integer> list) {
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
                    if (c >= '0' && c <= '9' || c == '米') {  //要么是手机号码的中间值 要么距离xxx xxx米
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

    @Override
    public int parseRealData(String data) {
        try {
            tiezi.id = topicId;
            parseTitle(data);
            parseAuthor(data);
            parseTopicContent(data);
            parseCommentList(data);

            List<Integer> priceList = new ArrayList<Integer>();
            getGuessedPrice(tiezi.content, priceList);
            getGuessedPrice(tiezi.replyContent, priceList);
            tiezi.guessPrices = priceList.toString();
            if (priceList.size() != 0) {
                tiezi.guessPrice = priceList.get(0);
            }

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

            LogManager.info(tiezi.toString());

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;

        }
        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "GroupTopicSpider:{topicId:" + topicId + "}";
    }
}
