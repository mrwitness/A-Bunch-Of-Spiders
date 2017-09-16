package wuxian.me.lianjiaspider.biz.touming.xinfang;

import com.sun.istack.internal.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import wuxian.me.lianjiaspider.api.touming.QuEnum;
import wuxian.me.lianjiaspider.biz.touming.BaseToumingSpider;
import wuxian.me.lianjiaspider.biz.touming.PriceCracker;
import wuxian.me.lianjiaspider.model.touming.NewquDetail;
import wuxian.me.lianjiaspider.model.touming.NewquPrice;
import wuxian.me.lianjiaspider.save.NewQuDetailSaver;
import wuxian.me.lianjiaspider.save.NewQuPriceSaver;
import wuxian.me.lianjiaspider.util.Helper;
import wuxian.me.spidercommon.util.NodeLogUtil;
import wuxian.me.lianjiaspider.util.SpringBeans;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.anti.MaybeBlockedException;

import static wuxian.me.spidercommon.util.ParsingUtil.*;
import static wuxian.me.spidercommon.util.StringUtil.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by wuxian on 11/7/2017.
 * http://www.tmsf.com/newhouse/property_33_27508515_info.htm
 */
public class NewQuDetailSpider extends BaseToumingSpider {

    private static final String API = "http://www.tmsf.com/newhouse/property_33_";
    private static final String API_POST = "_info.htm";

    private Long quId;

    public NewQuDetailSpider(Long quId) {
        this.quId = quId;
    }

    @Nullable
    public static HttpUrlNode toUrlNode(NewQuDetailSpider spider) {

        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = API + spider.quId + API_POST;
        return node;
    }

    private static final String REG_QUID = "(?<=property_33_)\\d+";
    private static final Pattern QUID_PATTERN = Pattern.compile(REG_QUID);

    @Nullable
    public static NewQuDetailSpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains(API) || !node.baseUrl.contains(API_POST)) {
            return null;
        }

        Long quId = matchedLong(QUID_PATTERN, node.baseUrl);

        return quId == null ? null : new NewQuDetailSpider(quId);
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + quId + API_POST)
                .newBuilder();

        String host = "www.tmsf.com";
        String ref = "http://www.tmsf.com/newhouse/";
        Request request = new Request.Builder()
                .headers(Helper.getToumingSpiderHeader(host, ref))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    private void parseData(String data) throws MaybeBlockedException, ParserException {

        NewquPrice price = new NewquPrice();
        price.quId = quId;
        price.zhuzhai7day = matchedString(DAY7_ZHUZHAI_TIME_PATTERN, data);
        price.zhuzhai7price = matchedString(DAY7_ZHUZHAI_PRICE_PATTERN, data);
        price.zhuzhaiLastDay = formatYYMMDD4(matchedString(LAST_DAY_PATTERN, price.zhuzhai7day));
        price.zhuzhaiLastPrice = matchedInteger(LAST_PRICE_PATTERN, price.zhuzhai7price);

        price.shangye7day = matchedString(DAY7_SHANGYE_TIME_PATTERN, data);
        price.shangye7price = matchedString(DAY7_SHANGYE_PRICE_PATTERN, data);
        price.shangyeLastDay = formatYYMMDD4(matchedString(LAST_DAY_PATTERN, price.shangye7day));
        price.shangyeLastPrice = matchedInteger(LAST_PRICE_PATTERN, price.shangye7price);

        NewquDetail qu = new NewquDetail();
        qu.quId = quId;

        String lanlong = removeAllBlanks(matchedString(LONG_LAN_ITUDE_PATTERN, data));
        qu.longitude = matchedString(LONGITUDE_PATTERN, lanlong);
        qu.lantitude = matchedString(LANTITUDE_PATTERN, lanlong);

        Parser parser = new Parser(data);
        parser.setEncoding("utf-8");
        HasAttributeFilter filter = new HasAttributeFilter("class", "buildtxtbox classones fl famwei");
        Node node = firstChildIfNullThrow(parser.extractAllNodesThatMatch(filter));

        String s = removeAllBlanks(node.toPlainTextString());
        LogManager.info("id:" + quId);
        LogManager.info("origin url:" + API + quId + API_POST);
        LogManager.info("基本信息1:" + s);
        qu.location = matchedString(LOCATION_PATTERN, s);
        qu.huxingMin = matchedInteger(HUXING_MIN_PATTERN, s);
        qu.huxingMax = matchedInteger(HUXING_MAX_PATTERN, s);
        qu.wuyeType = matchedString(WUYE_TYPE_PATTERN, s);
        qu.jianzhuType = matchedString(JIANZHU_TYPE_PATTERN, s);

        //format 开盘信息
        String newlyKaipan = null;
        newlyKaipan = matchedString(NEWLY_KAIPAN_PATTERN, s);
        if (newlyKaipan == null) {
            newlyKaipan = matchedString(NEWLY_KAIPAN_PATTERN2, s);
        }

        qu.newlyKaipan = formatYYMMDD8(newlyKaipan);

        qu.kaifashang = matchedString(KAIFASHANG_PATTERN, s);
        if (qu.kaifashang == null) {
            qu.kaifashang = matchedString(KAIFASHANG2_PATTERN, s);
        }

        filter = new HasAttributeFilter("class", "fl");
        node = firstChildIfNullThrow(node.getChildren().extractAllNodesThatMatch(filter, true));

        Node paragraphTag = firstChildOfType(node.getChildren(), ParagraphTag.class);
        if (paragraphTag != null) {
            NodeList list = paragraphTag.getChildren();
            StringBuilder builder = new StringBuilder("");
            for (int j = 0; j < list.size(); j++) {
                Integer p = PriceCracker.crackPriceNode(list.elementAt(j));
                if (p != null) {
                    builder.append(p);
                }
            }
            if (builder.toString().length() != 0) {
                price.currentPrice = Integer.parseInt(builder.toString());
            }
        }


        Parser parser1 = new Parser(data);
        parser1.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "fl lpact");
        Node dongtaiNode = firstChildIfNullThrow(parser1.extractAllNodesThatMatch(filter));
        filter = new HasAttributeFilter("class", "news_left clearfix");
        dongtaiNode = firstChildIfNullThrow(dongtaiNode.getChildren().extractAllNodesThatMatch(filter, true));

        LogManager.info("动态:" + removeAllBlanks(dongtaiNode.toPlainTextString()));

        Node bulletList = firstChildOfType(dongtaiNode.getChildren(), BulletList.class);
        if (bulletList != null) {
            StringBuilder dongtai = new StringBuilder("");
            NodeList list = childrenOfType(bulletList.getChildren(), Bullet.class);
            for (int j = 0; j < list.size(); j++) {
                String s2 = removeAllBlanks(list.elementAt(j).toPlainTextString().trim());
                String dtime = matchedString(DONGTAI_TIME_PATTERN, s2);
                if (dtime != null && dtime.length() == 9) {
                    dtime = dtime.substring(5) + dtime.substring(0, 5);
                }

                String dcontent = matchedString(DONGTAI_CONTENT_PATTERN, s2);
                dongtai.append(formatYYMMDD8(dtime));
                dongtai.append(replaceHtmlCharactors(dcontent));
                //dongtai.append(";");
            }
            qu.dongtai = dongtai.toString();
        }


        Parser parser2 = new Parser(data);
        parser2.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "buildshow_contant colordg ft14");

        //预售信息有可能为空
        Node yushouNode = firstChild(parser2.extractAllNodesThatMatch(filter));
        if (yushouNode != null) {
            LogManager.info("预发:" + removeAllBlanks(yushouNode.toPlainTextString()));
            NodeList list = yushouNode.getChildren();
            Node tableTag = firstChildOfType(list, TableTag.class);
            if (tableTag != null) {
                StringBuilder builder = new StringBuilder("");
                list = childrenOfTypeAndContent(tableTag.getChildren(), TableRow.class, "trmouseon");
                for (int i = 0; i < list.size(); i++) {
                    String s2 = removeAllBlanks(list.elementAt(i).toPlainTextString());
                    String time = matchedString(YUSHOU_TIME_PATTERN, s2);
                    String number = matchedString(YUSHOU_NUMBER_PATTERN, s2);

                    if (time != null && number != null) {
                        int index = s2.indexOf(time);
                        if (index != -1) {
                            String content = s2.substring(number.length(), index);

                            builder.append(formatYYMMDD8(time));
                            builder.append(replaceHtmlCharactors(content));
                            //builder.append(";");
                        }
                    }
                }
                qu.yushou = builder.toString();
            }
        }


        Parser parser3 = new Parser(data);
        parser3.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "buildshowin_contant ft14 colordg");
        Node loupanNode = firstChildIfNullThrow(parser3.extractAllNodesThatMatch(filter));
        s = removeAllBlanks(loupanNode.toPlainTextString());
        LogManager.info("基本信息2:" + s);
        qu.rongjilv = matchedString(RONGJILV_PATTERN, s);
        qu.lvhualv = matchedInteger(LVHUALV_PATTERN, s);
        qu.zhuangxiu = matchedString(ZHUANGXIU_PATTERN, s);
        qu.zhandi = matchedInteger(ZHANDI_PATTERN, s);
        qu.jianzhu = matchedInteger(JIANZHU_PATTERN, s);
        qu.jungongTime = formatYYMMDD8(matchedString(JUNGONG_TIME_PATTERN, s));
        qu.jiaofuTime = matchedString(JIAOFU_TIME_PATTERN, s);
        qu.hushu = matchedInteger(HUSHU_PATTERN, s);
        qu.chewei = matchedInteger(CHEWEI_PATTERN, s);
        qu.wuyeCompany = matchedString(WUYE_COMPANY_PATTERN, s);
        qu.wuyeFee = matchedString(WUYE_FEE_PATTERN, s);
        qu.chanquan = matchedInteger(CHANQUAN_PATTERN, s);

        if (qu.chanquan != null) {
            String chan = "产权年限：" + qu.chanquan + "年";
            int index = s.indexOf(chan);
            if (index != -1) {
                qu.other = replaceHtmlCharactors(s.substring(index + chan.length()));
            }
        }

        Parser parser4 = new Parser(data);
        parser3.setEncoding("utf-8");
        filter = new HasAttributeFilter("class", "build_name fl");
        Node nameNode = firstChildIfNullThrow(parser4.extractAllNodesThatMatch(filter));

        filter = new HasAttributeFilter("class", "build01 mgb15");
        Node name = firstChildIfNullThrow(nameNode.getChildren().extractAllNodesThatMatch(filter, true));
        s = removeAllBlanks(name.toPlainTextString());
        LogManager.info("名字:" + s);
        qu.name = matchedString(NAME_PATTERN, s);
        if (qu.name == null) {
            qu.name = s;
        }
        qu.tuiguangName = matchedString(TUIGUANG_NAME_PATTERN, s);

        filter = new HasAttributeFilter("class", "ofl");
        Node type = firstChildIfNullThrow(nameNode.getChildren().extractAllNodesThatMatch(filter, true));
        LogManager.info("状态:" + removeAllBlanks(type.toPlainTextString()));
        qu.status = fromSellStatus(matchedString(STATE_PATTERN, removeAllBlanks(type.toPlainTextString())));

        qu.created = System.currentTimeMillis();
        qu.updated = qu.created;

        price.created = System.currentTimeMillis();
        LogManager.info("price:" + price.toString());
        LogManager.info("qu:" + qu.toString());

        saveNewquDetail(qu);
        saveNewquPrice(price);

    }

    private Integer fromSellStatus(String status) {
        if (status == null || status.length() == 0) {
            return null;
        }

        if (status.equals("竞得")) {
            return 1;
        } else if (status.equals("在售")) {
            return 2;
        } else if (status.equals("尾盘")) {
            return 3;
        } else if (status.equals("售完")) {
            return 4;
        }

        return 5;
    }

    private void saveNewquDetail(NewquDetail qu) {
        NewQuDetailSaver.getInstance().saveModel(qu);
    }

    private void saveNewquPrice(NewquPrice price) {
        NewQuPriceSaver.getInstance().saveModel(price);
    }

    private static final String REG_STATE = "竞得|在售|尾盘|售完";
    private static final Pattern STATE_PATTERN = Pattern.compile(REG_STATE);

    private static final String REG_NAME = ".+(?=推广名)";
    private static final Pattern NAME_PATTERN = Pattern.compile(REG_NAME);

    private static final String REG_TUIGUANG_NAME = "(?<=推广名).+";
    private static final Pattern TUIGUANG_NAME_PATTERN = Pattern.compile(REG_TUIGUANG_NAME);

    //楼盘住宅7成交日数据
    private static final String REG_DAY7_ZHUZHAI_TIME = "(?<=p_zz_time=)\\[[0-9'.,]+](?=;)";
    public static final Pattern DAY7_ZHUZHAI_TIME_PATTERN = Pattern.compile(REG_DAY7_ZHUZHAI_TIME);

    private static final String REG_DAY7_ZHUZHAI_PRICE = "(?<=p_zz_list=)\\[[0-9'.,]+](?=;)";
    public static final Pattern DAY7_ZHUZHAI_PRICE_PATTERN = Pattern.compile(REG_DAY7_ZHUZHAI_PRICE);

    //楼盘商业7成交日数据
    private static final String REG_DAY7_SHANGYE_TIME = "(?<=p_sy_time=)\\[[0-9'.,]+](?=;)";
    public static final Pattern DAY7_SHANGYE_TIME_PATTERN = Pattern.compile(REG_DAY7_SHANGYE_TIME);

    private static final String REG_DAY7_SHANGYE_PRICE = "(?<=p_sy_list=)\\[[0-9'.,]+](?=;)";
    public static final Pattern DAY7_SHANGYE_PRICE_PATTERN = Pattern.compile(REG_DAY7_SHANGYE_PRICE);

    private static final String REG_LAST_DAY = "(?<=')[0-9.]+(?='])";
    public static final Pattern LAST_DAY_PATTERN = Pattern.compile(REG_LAST_DAY);

    private static final String REG_LAST_PRICE = "(?<=')[0-9]+(?='])";
    public static final Pattern LAST_PRICE_PATTERN = Pattern.compile(REG_LAST_PRICE);


    private static final String REG_LONG_LAN_ITUDE = "(?<=BMap.Point\\()[0-9'.,\\s]+(?=\\);)";
    public static final Pattern LONG_LAN_ITUDE_PATTERN = Pattern.compile(REG_LONG_LAN_ITUDE);

    private static final String REG_LONGITUDE = "(?<=')[0-9.]+(?=',)";
    public static final Pattern LONGITUDE_PATTERN = Pattern.compile(REG_LONGITUDE);

    private static final String REG_LANTITUDE = "(?<=,')[0-9.]+(?=')";
    public static final Pattern LANTITUDE_PATTERN = Pattern.compile(REG_LANTITUDE);

    private static final String REG_LOCATION = "(?<=地址：).+(?=查看地图)";
    public static final Pattern LOCATION_PATTERN = Pattern.compile(REG_LOCATION);

    private static final String REG_HUXING_MIN = "(?<=户型：)[0-9]+(?=-)";
    public static final Pattern HUXING_MIN_PATTERN = Pattern.compile(REG_HUXING_MIN);

    private static final String REG_HUXING_MAX = "(?<=-)[0-9]+(?=㎡\\[全部户型图)";
    public static final Pattern HUXING_MAX_PATTERN = Pattern.compile(REG_HUXING_MAX);

    private static final String REG_WUYE_TYPE = "(?<=物业类型：).+(?=建筑形式：)";
    public static final Pattern WUYE_TYPE_PATTERN = Pattern.compile(REG_WUYE_TYPE);

    private static final String REG_JIANZHU_TYPE = "(?<=建筑形式：)(多层|高层|超高层)";
    public static final Pattern JIANZHU_TYPE_PATTERN = Pattern.compile(REG_JIANZHU_TYPE);

    private static final String REG_NEWLY_KAIPAN = "(?<=最新开盘：).+(?=交房时间)";
    public static final Pattern NEWLY_KAIPAN_PATTERN = Pattern.compile(REG_NEWLY_KAIPAN);

    private static final String REG_NEWLY_KAIPAN2 = "(?<=最新开盘：).+(?=项目公司)";
    public static final Pattern NEWLY_KAIPAN_PATTERN2 = Pattern.compile(REG_NEWLY_KAIPAN2);

    private static final String REG_KAIFASHANG = "(?<=项目公司：).+(?=更多详细信息)";
    public static final Pattern KAIFASHANG_PATTERN = Pattern.compile(REG_KAIFASHANG);

    private static final String REG_KAIFASHANG2 = "(?<=项目公司：).+(?=收藏)";
    public static final Pattern KAIFASHANG2_PATTERN = Pattern.compile(REG_KAIFASHANG2);


    private static final String REG_DONGTAI_TIME = "[0-9-]+(?=动态)";
    public static final Pattern DONGTAI_TIME_PATTERN = Pattern.compile(REG_DONGTAI_TIME);

    private static final String REG_DONGTAI_CONTENT = "(?<=动态).+";
    public static final Pattern DONGTAI_CONTENT_PATTERN = Pattern.compile(REG_DONGTAI_CONTENT);

    private static final String REG_YUSHOU_TIME = "20\\d\\d-\\d\\d-\\d\\d";
    public static final Pattern YUSHOU_TIME_PATTERN = Pattern.compile(REG_YUSHOU_TIME);

    private static final String REG_YUSHOU_NUMBER = "[0-9]+";
    private static final Pattern YUSHOU_NUMBER_PATTERN = Pattern.compile(REG_YUSHOU_NUMBER);

    private static final String REG_RONGJILV = "(?<=容积率：)[<>0-9.]+(?=绿化率：)";
    private static final Pattern RONGJILV_PATTERN = Pattern.compile(REG_RONGJILV);

    private static final String REG_LVHULV = "(?<=绿化率：).+(?=%装修情况：)";
    private static final Pattern LVHUALV_PATTERN = Pattern.compile(REG_LVHULV);

    private static final String REG_ZHUANGXIU = "(?<=装修情况：).+(?=占地面积：)";
    private static final Pattern ZHUANGXIU_PATTERN = Pattern.compile(REG_ZHUANGXIU);

    private static final String REG_ZHANDI = "(?<=占地面积：)\\d+(?=㎡总建筑面积：)";
    private static final Pattern ZHANDI_PATTERN = Pattern.compile(REG_ZHANDI);

    private static final String REG_JIANZHU = "(?<=总建筑面积：)\\d+(?=㎡)";
    private static final Pattern JIANZHU_PATTERN = Pattern.compile(REG_JIANZHU);

    private static final String REG_JUNGONG_TIME = "(?<=竣工时间：)[0-9-]+";
    private static final Pattern JUNGONG_TIME_PATTERN = Pattern.compile(REG_JUNGONG_TIME);

    private static final String REG_JIAOFU_TIME = "(?<=预计交付时间：).+(?=总户数：)";
    private static final Pattern JIAOFU_TIME_PATTERN = Pattern.compile(REG_JIAOFU_TIME);

    private static final String REG_HUSHU = "(?<=总户数：)\\d+(?=户)";
    private static final Pattern HUSHU_PATTERN = Pattern.compile(REG_HUSHU);

    private static final String REG_CHEWEI = "(?<=车位信息：)\\d+(?=个)";
    private static final Pattern CHEWEI_PATTERN = Pattern.compile(REG_CHEWEI);

    private static final String REG_WUYE_COMPANY = "(?<=物业公司：).+(?=物业费：)";
    private static final Pattern WUYE_COMPANY_PATTERN = Pattern.compile(REG_WUYE_COMPANY);

    private static final String REG_WUYE_FEE = "(?<=物业费：)[0-9.]+(?=元)";
    private static final Pattern WUYE_FEE_PATTERN = Pattern.compile(REG_WUYE_FEE);

    private static final String REG_CHANQUAN = "(?<=产权年限：)\\d+(?=年)";
    private static final Pattern CHANQUAN_PATTERN = Pattern.compile(REG_CHANQUAN);


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
        return "NewQuDetailSpider:{quId:" + quId + "}";
    }
}
