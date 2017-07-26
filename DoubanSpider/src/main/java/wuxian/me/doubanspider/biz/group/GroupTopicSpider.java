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
import wuxian.me.doubanspider.util.Helper;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.NodeLogUtil;
import wuxian.me.spidercommon.util.StringUtil;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

/**
 * Created by wuxian on 24/7/2017.
 * https://www.douban.com/group/topic/105236658/
 */
public class GroupTopicSpider extends BaseDoubanSpider {

    private static final String API = "https://www.douban.com/group/topic/";

    private Long topicId;

    private Long authorId;

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
        LogManager.info(StringUtil.removeAllBlanks(node.toPlainTextString()));

        NodeList list = childrenOfTypeAndContent(node.getChildren(), Div.class, "topic-figure cc");
        LogManager.info("picture num:" + list.size());

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
        LogManager.info(replyList.toString());
    }

    private void parseComment(Node node, List<String> replyList) throws MaybeBlockedException, ParserException {

        HasAttributeFilter filter = new HasAttributeFilter("class", "user-face");

        Node user = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));

        if ((firstChildOfTypeAndContent(user.getChildren(), LinkTag.class, String.valueOf(authorId))) == null) {
            return;   //过滤掉非作者的评论
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

    private static final String REG_REPLY = "(?<=\\d\\d:\\d\\d:\\d\\d).+(?=回应)";
    private static final Pattern REPLAY_PATTERN = Pattern.compile(REG_REPLY);

    private static final String REG_MAYBE_PRICE = "[1234]?\\d{3}";
    public static final Pattern MAYBE_PRICE_PATTERN = Pattern.compile(REG_MAYBE_PRICE);

    private static final String REG_MAYBE_WECHAT = "[0-9A-Za-z]{5,12}";
    public static final Pattern MAYBE_WECHAT_PATTERN = Pattern.compile(REG_MAYBE_WECHAT);


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
        LogManager.info("title:" + node.toPlainTextString().trim());
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
        LogManager.info("author:" + author.toPlainTextString().trim());
        LogManager.info("author id:" + matchedString(AUTHOR_ID_PATTERN, author.getText().trim()));
        authorId = matchedLong(AUTHOR_ID_PATTERN, author.getText().trim());
        if (authorId == null) {
            throw new MaybeBlockedException();
        }

        while ((node = node.getNextSibling()) != null) {
            if (node instanceof Span && node.getText().trim().contains("color-green")) {
                LogManager.info("postTime:" + StringUtil.removeAllBlanks(node.toPlainTextString()));
                break;
            }
        }

    }

    @Override
    public int parseRealData(String data) {

        try {
            parseTitle(data);
            parseAuthor(data);
            parseTopicContent(data);
            parseCommentList(data);

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
