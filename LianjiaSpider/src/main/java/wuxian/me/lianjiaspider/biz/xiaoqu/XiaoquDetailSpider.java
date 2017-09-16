package wuxian.me.lianjiaspider.biz.xiaoqu;

import com.sun.istack.internal.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.biz.BaseLianjiaSpider;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.lianjiaspider.api.BaseUrls;
import wuxian.me.lianjiaspider.model.Xiaoqu;
import wuxian.me.lianjiaspider.util.SpringBeans;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import java.util.List;
import java.util.regex.Pattern;

import static wuxian.me.spidercommon.util.ParsingUtil.*;

/**
 * Created by wuxian on 26/6/2017.
 */
public class XiaoquDetailSpider extends BaseLianjiaSpider {

    private static List<String> urls;
    private static final String API = "/xiaoqu/";

    private Long xiaoquId;
    private int city;
    private String cityUrl;

    @Nullable
    public static HttpUrlNode toUrlNode(XiaoquDetailSpider spider) {
        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = spider.cityUrl + API + spider.xiaoquId + "/";
        return node;
    }

    @Nullable
    public static XiaoquDetailSpider fromUrlNode(HttpUrlNode node) {

        if (urls == null) {
            urls = BaseUrls.getUrls();
        }
        String findUrl = null;
        for (String url : urls) {
            if (node.baseUrl.contains(url)) {
                findUrl = url;
                break;
            }
        }

        if (findUrl == null) {
            return null;
        }

        String findUrl2 = findUrl + API;

        if (!node.baseUrl.startsWith(findUrl2)) {
            return null;
        }

        int len = findUrl2.length();

        String s = node.baseUrl.substring(len);
        if (s.contains("pg")) {  //Fixme:目前区分XiaoquListSpider和XiaoquDetailSpider用了这么挫的方法...
            return null;
        }

        Long id = Long.parseLong(node.baseUrl.substring(len, node.baseUrl.length() - 1));
        XiaoquDetailSpider spider = new XiaoquDetailSpider(findUrl, id);

        return spider;
    }

    public XiaoquDetailSpider(String cityUrl, Long xiaoquId) {
        this(BaseUrls.getCity(cityUrl), cityUrl, xiaoquId);
    }

    public XiaoquDetailSpider(int city, Long xiaoquId) {
        this(city, BaseUrls.getUrl(city), xiaoquId);
    }

    public XiaoquDetailSpider(int city, String cityUrl, Long xiaoquId) {
        this.xiaoquId = xiaoquId;
        this.city = city;
        this.cityUrl = cityUrl;
    }

    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(cityUrl + API + xiaoquId + "/")
                .newBuilder();

        String host = cityUrl.substring(8);

        Request request = new Request.Builder()
                .headers(Helper.getSpiderHeader(host, cityUrl + API))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private void parseData(String data) throws MaybeBlockedException, ParserException {
        Xiaoqu xiaoqu = new Xiaoqu();
        xiaoqu.xiaoqu_id = xiaoquId;

        String longlanti = matchedString(LONG_LAN_ITUDE_PATTERN, data);
        stringEmptyIfTrueThrow(longlanti);

        int index = longlanti.indexOf(",");
        xiaoqu.longitude = longlanti.substring(0, index);
        xiaoqu.lantitude = longlanti.substring(index + 1, longlanti.length());

        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "detailDesc");
        Node detailDes = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));
        xiaoqu.location = detailDes.toPlainTextString().trim();

        Parser parser1 = new Parser(data);
        parser1.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "detailTitle");
        Node detailTitle = firstChildIfNullThrow(parser1.extractAllNodesThatMatch(filter));
        xiaoqu.name = detailTitle.toPlainTextString().trim();


        Parser parser2 = new Parser(data);
        parser2.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "detailFollowedNum");
        Node detailFollower = firstChildIfNullThrow(parser2.extractAllNodesThatMatch(filter));
        xiaoqu.follower = matchedInteger(FOLLOWER_PATTERN, detailFollower.toPlainTextString().trim());

        Parser parser3 = new Parser(data);
        parser3.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "xiaoquInfo");
        Node infoNode = firstChildIfNullThrow(parser3.extractAllNodesThatMatch(filter));

        String info = infoNode.toPlainTextString().trim();

        xiaoqu.buildTime = matchedInteger(BUILD_TIME, info);

        xiaoqu.buildingType = matchedString(BUILDING_TYPE_PATTERN, info);

        xiaoqu.wuyefee = matchedString(WUYEFEE_PATTERN, info);

        xiaoqu.wuyeCompany = matchedString(WUYE_COMPANY_PATTERN, info);

        xiaoqu.kaifashang = matchedString(KAIFASHANG_PATTERN, info);

        xiaoqu.buildingNum = matchedInteger(BUILDING_NUM_PATTERN, info);

        xiaoqu.houseNum = matchedInteger(HOUSE_NUM_PATTERN, info);

        LogManager.info(xiaoqu.toString());

        SpringBeans.xiaoquMapper().insertXiaoqu(xiaoqu);
    }

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

    private static final String REG_LONG_LAN_ITUDE = "(?<=resblockPosition:')[0-9.,]+(?=')";
    public static final Pattern LONG_LAN_ITUDE_PATTERN = Pattern.compile(REG_LONG_LAN_ITUDE);

    private static final String REG_FOLLOWER_NUM = "[0-9]+";
    private static final Pattern FOLLOWER_PATTERN = Pattern.compile(REG_FOLLOWER_NUM);

    private static final String REG_BUILD_TIME = "(?<=建筑年代)[0-9]+";
    private static final Pattern BUILD_TIME = Pattern.compile(REG_BUILD_TIME);

    private static final String REG_BUILDING_TYPE = "(?<=建筑类型).+(?=物业费用)";
    private static final Pattern BUILDING_TYPE_PATTERN = Pattern.compile(REG_BUILDING_TYPE);

    private static final String REG_WUYEFEE = "(?<=物业费用).+(?=/平米/月)";
    private static final Pattern WUYEFEE_PATTERN = Pattern.compile(REG_WUYEFEE);

    private static final String REG_WUYE_COMPANY = "(?<=物业公司).+(?=开发商)";
    private static final Pattern WUYE_COMPANY_PATTERN = Pattern.compile(REG_WUYE_COMPANY);

    private static final String REG_KAIFASHANG = "(?<=开发商).+(?=楼栋总数)";
    private static final Pattern KAIFASHANG_PATTERN = Pattern.compile(REG_KAIFASHANG);

    private static final String REG_BUILDING_NUM = "(?<=楼栋总数)[0-9]+(?=栋)";
    private static final Pattern BUILDING_NUM_PATTERN = Pattern.compile(REG_BUILDING_NUM);

    private static final String REG_HOUSE_NUM = "(?<=房屋总数)[0-9]+(?=户)";
    private static final Pattern HOUSE_NUM_PATTERN = Pattern.compile(REG_HOUSE_NUM);

    public String name() {
        return "XiaoquDetailSpider: xiaoqu_id:" + xiaoquId;
    }
}
