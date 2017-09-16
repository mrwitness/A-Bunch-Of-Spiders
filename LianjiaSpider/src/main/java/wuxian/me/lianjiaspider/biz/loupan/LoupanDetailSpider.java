package wuxian.me.lianjiaspider.biz.loupan;

import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.api.BaseUrls;
import wuxian.me.lianjiaspider.biz.BaseLianjiaSpider;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

import static wuxian.me.spidercommon.util.StringUtil.*;
/**
 * Created by wuxian on 9/7/2017.
 */
public class LoupanDetailSpider extends BaseLianjiaSpider {

    private String loupanId;
    private int city;
    private String cityUrl;

    public LoupanDetailSpider(int city, String loupanId) {
        this(city, BaseUrls.getLoupanUrl(city), loupanId);
    }

    public LoupanDetailSpider(int city, String cityUrl, String loupanId) {
        this.city = city;
        this.loupanId = loupanId;
        this.cityUrl = cityUrl;
    }

    private static final String API = "/loupan/";


    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(cityUrl + API + loupanId + "/")
                .newBuilder();

        String host = cityUrl.substring(7);

        Request request = new Request.Builder()
                .headers(Helper.getSpiderHeader(host, cityUrl + API))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private void parseData(String data) throws MaybeBlockedException, ParserException {

        String lan = matchedString(LAN_ITUDE_PATTERN, data);
        String longitude = matchedString(LONG_ITUDE_PATTERN, data);
        LogManager.info("lan:" + lan + " long:" + longitude);

        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "breadcrumbs");
        Node districtDes = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        NodeList list = districtDes.getChildren();
        for (int i = 0; i < list.size(); i++) {
            Node child = list.elementAt(i);
            if (child instanceof LinkTag && containsPattern(DISTRICT_HREF_PATTERN, child.getText())) {
                String lou = child.toPlainTextString().trim();
                LogManager.info("district:" + lou.substring(0, lou.length() - 2));
                break;
            }
        }

        Parser parser1 = new Parser(data);
        parser1.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "name-box");
        Node detailNode = firstChildIfNullThrow(parser1.extractAllNodesThatMatch(filter));
        LogManager.info("name:" + matchedString(NAME_PATTERN, removeAllBlanks(detailNode.toPlainTextString())));


        Parser parser2 = new Parser(data);
        parser2.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "jiage");
        Node priceNode = firstChildIfNullThrow(parser2.extractAllNodesThatMatch(filter));
        LogManager.info("price:" + matchedString(PRICE_PATTERN, removeAllBlanks(priceNode.toPlainTextString())));


        Parser parser3 = new Parser(data);
        parser3.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "more-wrap");
        try {
            Node kaipanNode = firstChildIfNullThrow(parser3.extractAllNodesThatMatch(filter));
            LogManager.info("kaipan:" + removeAllBlanks(kaipanNode.toPlainTextString()));

        } catch (ParserException e) {

        }

        Parser parser4 = new Parser(data);
        parser4.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "houselist");
        try {
            Node huxingNode = firstChildIfNullThrow(parser4.extractAllNodesThatMatch(filter));
            LogManager.info("huxing:" + removeAllBlanks(huxingNode.toPlainTextString()));

        } catch (ParserException e) {

        }

        Parser parser5 = new Parser(data);
        parser5.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "box-loupan");
        try {
            Node loupanNode = firstChildIfNullThrow(parser5.extractAllNodesThatMatch(filter));

            String detail = removeAllBlanks(loupanNode.toPlainTextString());

            LogManager.info("地址:" + matchedString(LOCATION_PATTERN, detail));

            LogManager.info("开发商:" + matchedString(KAIFASHANG_PATTERN, detail));

            LogManager.info("物业公司:" + matchedString(WUYE_PATTERN, detail));

            LogManager.info("最新开盘:" + matchedString(NEW_KAIPAN_PATTERN, detail));

            LogManager.info("物业类型:" + matchedString(WUYE_TYPE_PATTERN, detail));

            LogManager.info("交房时间:" + matchedString(JIAOFANG_PATTERN, detail));

            LogManager.info("容积率:" + matchedString(RONGJILV_PATTERN, detail));

            LogManager.info("绿化率:" + matchedString(LVHUA_PATTERN, detail));

            LogManager.info("规划户数:" + matchedString(HUSHU_PATTERN, detail));

            LogManager.info("物业费:" + matchedString(WUYEFEE_PATTERN, detail));

            LogManager.info("车位情况:" + matchedString(CHEWEI_PATTERN, detail));

            LogManager.info("占地面积:" + matchedString(ZHANDI_PATTERN, detail));

            LogManager.info("建筑面积:" + matchedString(JIANZHU_PATTERN, detail));


        } catch (ParserException e) {

        }

    }

    private static final String REG_DISTRICT_HREF = "(?<=loupan/)[a-zA-Z0-9]+(?=/)";
    private static final Pattern DISTRICT_HREF_PATTERN = Pattern.compile(REG_DISTRICT_HREF);

    private static final String REG_PRICE = "[0-9]+(?=元)";
    private static final Pattern PRICE_PATTERN = Pattern.compile(REG_PRICE);

    private static final String REG_NAME = "(?<=在售).+";
    private static final Pattern NAME_PATTERN = Pattern.compile(REG_NAME);

    private static final String REG_LAN_ITUDE = "(?<=parseFloat[(]')[0-9.]+(?='[)],parse)";
    public static final Pattern LAN_ITUDE_PATTERN = Pattern.compile(REG_LAN_ITUDE);

    private static final String REG_LONG_ITUDE = "(?<=parseFloat[(]')[0-9.]+(?='[)][]])";
    public static final Pattern LONG_ITUDE_PATTERN = Pattern.compile(REG_LONG_ITUDE);

    private static final String REG_LOCATION = "(?<=项目地址：).+(?=售楼处地址)";
    private static final Pattern LOCATION_PATTERN = Pattern.compile(REG_LOCATION);

    private static final String REG_KAIFASHANG = "(?<=开发商：).+(?=物业公司)";
    private static final Pattern KAIFASHANG_PATTERN = Pattern.compile(REG_KAIFASHANG);

    private static final String REG_WUYE = "(?<=物业公司：).+(?=最新开盘)";
    private static final Pattern WUYE_PATTERN = Pattern.compile(REG_WUYE);

    private static final String REG_NEW_KAIPAN = "(?<=最新开盘：).+(?=物业类型)";
    private static final Pattern NEW_KAIPAN_PATTERN = Pattern.compile(REG_NEW_KAIPAN);

    private static final String REG_WUYE_TYPE = "(?<=物业类型：).+(?=交房时间)";
    private static final Pattern WUYE_TYPE_PATTERN = Pattern.compile(REG_WUYE_TYPE);

    private static final String REG_JIAOFANG = "(?<=交房时间：).+(?=容积率)";
    private static final Pattern JIAOFANG_PATTERN = Pattern.compile(REG_JIAOFANG);

    private static final String REG_RONGJILV = "(?<=容积率：).+(?=产权年限)";
    private static final Pattern RONGJILV_PATTERN = Pattern.compile(REG_RONGJILV);

    private static final String REG_CHANQUAN = "(?<=产权年限：).+(?=产权年限)";
    private static final Pattern CHANQUAN_PATTERN = Pattern.compile(REG_CHANQUAN);

    private static final String REG_LVHUA = "(?<=绿化率：).+(?=规划户数)";
    private static final Pattern LVHUA_PATTERN = Pattern.compile(REG_LVHUA);

    private static final String REG_HUSHU = "(?<=规划户数：).+(?=物业费用)";
    private static final Pattern HUSHU_PATTERN = Pattern.compile(REG_HUSHU);

    private static final String REG_WUYEFEE = "(?<=物业费用：).+(?=车位情况)";
    private static final Pattern WUYEFEE_PATTERN = Pattern.compile(REG_WUYEFEE);

    private static final String REG_CHEWEI = "(?<=车位情况：).+(?=供暖方式)";
    private static final Pattern CHEWEI_PATTERN = Pattern.compile(REG_CHEWEI);

    //private static final String REG_GONGNUAN = "(?<=供暖方式：).+(?=供水方式)";
    //private static final Pattern GONGNUAN_PATTERN = Pattern.compile(REG_GONGNUAN);

    private static final String REG_ZHANDI = "(?<=占地面积：).+(?=建筑面积)";
    private static final Pattern ZHANDI_PATTERN = Pattern.compile(REG_ZHANDI);

    private static final String REG_JIANZHU = "(?<=建筑面积：).+(?=查看更多)";
    private static final Pattern JIANZHU_PATTERN = Pattern.compile(REG_JIANZHU);


    @Override
    public int parseRealData(String data) {
        //LogManager.info(data);

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
        return "LoupanDetailSpider: {city:" + city + " loupanId:" + loupanId + "}";
    }
}
