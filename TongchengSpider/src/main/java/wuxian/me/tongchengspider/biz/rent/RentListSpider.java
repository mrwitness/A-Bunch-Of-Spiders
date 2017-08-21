package wuxian.me.tongchengspider.biz.rent;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.NodeLogUtil;
import wuxian.me.spidercommon.util.ParsingUtil;
import wuxian.me.spidercommon.util.StringUtil;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;
import wuxian.me.tongchengspider.biz.BaseTongchengSpider;
import wuxian.me.tongchengspider.model.Tiezi;
import wuxian.me.tongchengspider.util.Helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

/**
 * Created by wuxian on 21/8/2017.
 * http://hz.58.com/wenxin/hezu/pn3/
 */
public class RentListSpider extends BaseTongchengSpider {

    private String qu;
    private int page;

    public RentListSpider(String qu, int page) {
        this.qu = qu;
        this.page = page;
    }

    private static final String API = "http://hz.58.com/";
    private static final String API_POST = "/hezu/0/pn";

    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + qu + API_POST + page)
                .newBuilder();

        String host = "hz.58.com";
        Request request = new Request.Builder()
                .headers(Helper.getTongchengHeader(host, API))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    //最后一条item用来判断是否需要post下一个spider
    //去掉那些不靠谱的 比如时间太老的 发布地点在我黑名单的 等。
    private void parseGroupList(String data) throws MaybeBlockedException, ParserException {

        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "listUl");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        List<Tiezi> topList = new ArrayList<Tiezi>();

        NodeList list1 = null;
        list1 = childrenOfTypeAndContent(node.getChildren(), Bullet.class, "logr");

        for (int i = 0; i < list1.size(); i++) {
            Node child = list1.elementAt(i);
            Tiezi id = parseItem(child, (i + 1 == list1.size()));
            if (id != null) {
                topList.add(id);
            }
        }

        LogManager.info(topList.toString());
    }

    private Tiezi parseItem(Node node, boolean lastItem) throws MaybeBlockedException
            , ParserException {

        Tiezi tiezi = new Tiezi();

        Node des = ParsingUtil.firstChildOfTypeAndContent(node.getChildren(), Div.class, "des");
        if (des == null) {
            throw new MaybeBlockedException();
        }
        Node info = ParsingUtil.firstChildOfTypeAndContent(des.getChildren(), HeadingTag.class, "h2");
        if (info != null) {

            tiezi.title = StringUtil.removeAllBlanks(info.toPlainTextString());
            Node link = ParsingUtil.firstChildOfType(info.getChildren(), LinkTag.class);
            if (link == null) {
                throw new MaybeBlockedException();
            }

            tiezi.id = matchedString(TOPIC_ID_PATTERN, link.getText());
        } else {
            throw new MaybeBlockedException();
        }

        HasAttributeFilter filter = new HasAttributeFilter("class", "money");
        Node price = ParsingUtil.firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));
        tiezi.price = matchedInteger(PRICE_PATTERN, price.toPlainTextString());

        String in = StringUtil.removeAllBlanks(node.toPlainTextString());

        Integer picnum = matchedInteger(PICNUM_PATTERN, in);
        tiezi.pictureNum = picnum;

        Integer shi = matchedInteger(SHINUM_PATTERN, in);
        tiezi.shiNum = shi;

        Integer ting = matchedInteger(TINGNUM_PATTERN, in);
        tiezi.tingNum = ting;

        Integer wei = matchedInteger(WEINUM_PATTERN, in);
        if (wei == null && matchedString(DUWEI_PATTERN, in) != null) {
            wei = 111; //独卫
        }
        tiezi.weiNum = wei;

        Integer size = matchedInteger(SIZE_PATTERN, in);
        tiezi.size = size;

        String location = matchedString(LOCATION_PATTERN, in);
        location = location.replaceAll("&nbsp;", "");
        tiezi.location = location;

        String live = matchedString(LIVE_PATTERN, in);
        tiezi.live = live;

        tiezi.shitingwei = tiezi.generateShitingwei();

        String sex = matchedString(SEX_PATTERN, in);
        if (sex != null) {
            if (sex.equals("男女")) {
                tiezi.sex = 2;
            } else if (sex.equals("女")) {
                tiezi.sex = 0;
            } else {
                tiezi.sex = 1;
            }
        }


        String time = matchedString(TIME_PATTERN, in);

        if (time != null) {
            int index = -1;
            if ((index = time.indexOf("分钟")) != -1) {
                try {
                    tiezi.postTime = System.currentTimeMillis() - Integer.parseInt(time.substring(0, index)) * 1000 * 60;
                } catch (Exception e) {
                    LogManager.error(e.toString());
                    e.printStackTrace();
                }
            } else if ((index = time.indexOf("小时")) != -1) {
                try {
                    tiezi.postTime = System.currentTimeMillis() - Integer.parseInt(time.substring(0, index)) * 1000 * 60 * 60;
                } catch (Exception e) {
                    LogManager.error(e.toString());
                    e.printStackTrace();
                }
            } else if ((index = time.indexOf("天")) != -1) {

                try {
                    Integer i = Integer.parseInt(time.substring(0, index));
                    if (i <= 2) {  //Todo:这里只看两天的数据 超过这个值为空

                        tiezi.postTime = System.currentTimeMillis() - Integer.parseInt(time.substring(0, index)) * 1000 * 60 * 60 * 24;
                    } else {
                        if (lastItem) {
                            tiezi.postTime = System.currentTimeMillis() - Integer.parseInt(time.substring(0, index)) * 1000 * 60 * 60 * 24;
                        }
                    }

                } catch (Exception e) {
                    LogManager.error(e.toString());
                    e.printStackTrace();
                }
            } else {
                //08-12

                if (time.length() == 5) {
                    try {

                        Date date = sdf.parse(time);
                        tiezi.postTime = date.getTime();
                        if (!lastItem && System.currentTimeMillis() - tiezi.postTime >= 3 * 24 * 3600 * 1000) {
                            tiezi.postTime = null;
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }

        //LogManager.info(tiezi.toString());

        if (lastItem && tiezi.postTime != null) {
            if (System.currentTimeMillis() - tiezi.postTime >= 3 * 24 * 3600 * 1000) {
                //3天后了 不post下一个spider
            } else {
                Helper.dispatchSpider(new RentListSpider(qu, page + 1));
            }
        }
        //LogManager.info("time:" + time);

        return tiezi.postTime == null ? null : tiezi;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

    public static final String REG_SEX = "限女|限男|男女";
    public static final Pattern SEX_PATTERN = Pattern.compile(REG_SEX);

    public static final String REG_TIME = "\\d+小时前|\\d+分钟前|\\d+天前|\\d\\d\\d\\d-\\d\\d-\\d\\d|\\d\\d-\\d\\d";
    public static Pattern TIME_PATTERN = Pattern.compile(REG_TIME);

    public static final String REG_PRICE = "\\d+(?=元/月)";
    public static final Pattern PRICE_PATTERN = Pattern.compile(REG_PRICE);

    public static final String REG_LIVE = "(隔断|主卧|次卧)\\(\\d室\\)";
    public static final Pattern LIVE_PATTERN = Pattern.compile(REG_LIVE);

    public static final String REG_WEINUM = "\\d+(?=卫)";
    public static final Pattern WEINUM_PATTERN = Pattern.compile(REG_WEINUM);

    public static final String REG_DUWEI = "独卫";
    public static final Pattern DUWEI_PATTERN = Pattern.compile(REG_DUWEI);

    public static final String REG_SHINUM = "\\d+(?=室)";
    public static final Pattern SHINUM_PATTERN = Pattern.compile(REG_SHINUM);

    public static final String REG_TINGNUM = "\\d+(?=厅)";
    public static final Pattern TINGNUM_PATTERN = Pattern.compile(REG_TINGNUM);

    public static final String REG_PICNUM = "\\d+(?=图)";
    public static final Pattern PICNUM_PATTERN = Pattern.compile(REG_PICNUM);

    public static final String REG_TOPIC_ID = "(?<=hezu/)[a-zA-Z0-9]+(?=.)";
    public static final Pattern TOPIC_ID_PATTERN = Pattern.compile(REG_TOPIC_ID);

    public static final String REG_SIZE = "\\d+(?=㎡)";
    public static final Pattern SIZE_PATTERN = Pattern.compile(REG_SIZE);

    public static final String REG_LOCATION = "(?<=㎡).+(?=来自个人房源)";
    public static final Pattern LOCATION_PATTERN = Pattern.compile(REG_LOCATION);


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

    public String name() {
        return "RentListSpider:{qu:" + qu + ",page:" + page + "}";
    }
}
