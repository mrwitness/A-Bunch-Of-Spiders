package wuxian.me.lianjiaspider.biz.touming.ershou;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.biz.touming.BaseToumingSpider;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.spidercommon.util.NodeLogUtil;
import static wuxian.me.spidercommon.util.StringUtil.*;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;
import static wuxian.me.spidercommon.util.StringUtil.*;

/**
 * Created by wuxian on 10/7/2017.
 * Todo:http://www.tmsf.com/esf/xq_index_10003132.htm
 */
public class QuDetailSpider extends BaseToumingSpider {

    private static final String API = "http://www.tmsf.com/esf/xq_index_";
    private static final String API_POST = ".htm";

    private Long quId;

    public QuDetailSpider(Long quId) {
        this.quId = quId;
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + quId + API_POST)
                .newBuilder();

        String host = "www.tmsf.com";
        String ref = "http://www.tmsf.com/esf/esfnSearch_communityList.htm";
        Request request = new Request.Builder()
                .headers(Helper.getToumingSpiderHeader(host, ref))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private void parseData(String data) throws MaybeBlockedException, ParserException {

        LogManager.info("id:" + quId);
        LogManager.info("qu time:" + matchedString(MONTH3_TIME_PATTERN, data));
        LogManager.info("qu price:" + matchedString(MONTH3_PRICE_PATTERN, data));

        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "basedata");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        LogManager.info("-------------------base data------------------");
        LogManager.info(removeAllBlanks(node.toPlainTextString()));

        Parser parser1 = new Parser(data);
        parser1.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "etabwe");
        Node zhoubianNode = firstChildIfNullThrow(parser1.extractAllNodesThatMatch(filter));
        LogManager.info("-------------------zhoubian data------------------");
        LogManager.info(removeAllBlanks(zhoubianNode.toPlainTextString()));

        filter = new HasAttributeFilter("class", "etabwe");
        Node jieshaoNode = nextBrotherWhoMatch(zhoubianNode, filter);
        LogManager.info("-------------------jieshao data------------------");
        LogManager.info(removeAllBlanks(jieshaoNode.toPlainTextString()));

        filter = new HasAttributeFilter("class", "etabwe");
        Node peitaoNode = nextBrotherWhoMatch(jieshaoNode, filter);
        LogManager.info("--------------------peitao-----------------");
        LogManager.info(removeAllBlanks(peitaoNode.toPlainTextString()));

    }

    //3个月的数据
    private static final String REG_MONTH3_TIME = "(?<=ticks\":)\\[[0-9'.,\"-]+](?=,)";
    public static final Pattern MONTH3_TIME_PATTERN = Pattern.compile(REG_MONTH3_TIME);

    private static final String REG_MONTH3_PRICE = "(?<=line\":)\\[[0-9'.,]+]";
    public static final Pattern MONTH3_PRICE_PATTERN = Pattern.compile(REG_MONTH3_PRICE);


    @Override
    public int parseRealData(String data) {
        try {
            parseData(data);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;

        }
        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "QuDetailSpider:{quId:" + quId + "}";
    }
}
