package wuxian.me.zhihuspider.biz.behavior;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.ParserException;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;
import wuxian.me.zhihuspider.biz.BaseZhihuSpider;
import wuxian.me.zhihuspider.util.Helper;

/**
 * Created by wuxian on 1/9/2017.
 * <p>
 * 给某个答案点赞了
 */
public class PraiseAnswerSpider extends BaseZhihuSpider {

    private Long questionId;
    private Long answerId;

    public PraiseAnswerSpider(Long questionId, Long answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

    private static final String API = "https://www.zhihu.com/question/";
    private static final String API_POST = "/answer/";

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + questionId + API_POST + answerId)
                .newBuilder();

        String host = "www.zhihu.com";
        Request request = new Request.Builder()
                .headers(Helper.getSpiderHeader(host, "https://www.zhihu.com"))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    //Todo
    private void parseAnswer(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "olt");
    }

    //Todo
    private void parseTopics(String data) throws MaybeBlockedException, ParserException {
        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "olt");
    }

    @Override
    public int parseRealData(String data) {
        LogManager.info(data);
        try {
            parseTopics(data);   //Todo:先检查下该topic是否被check了？？？
            parseAnswer(data);

        } catch (MaybeBlockedException e) {
            return BaseSpider.RET_MAYBE_BLOCK;

        } catch (ParserException e) {
            return BaseSpider.RET_PARSING_ERR;
        }

        return BaseSpider.RET_SUCCESS;
    }

    @Override
    public String name() {
        return "PraiseAnswerSpider:{question_id:" + questionId + "answer_id:" + answerId + "}";
    }
}
