package wuxian.me.doubanspider;

import org.junit.Test;
import wuxian.me.doubanspider.biz.group.GroupListSpider;
import wuxian.me.doubanspider.biz.group.GroupTopicSpider;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.ParsingUtil;
import wuxian.me.spidercommon.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

/**
 * Created by wuxian on 24/7/2017.
 */
public class MainTest {

    @Test
    public void testHan() {
        String s = "零室9厅";

        Integer i = new GroupTopicSpider(187L).getNum(ParsingUtil.matchedString(GroupTopicSpider.SHI_NUM_PATTERN, s));
        LogManager.info(String.valueOf(i));
    }

    @Test
    public void testPrice() {
        String s = "1980dfasdaffa987";

        Matcher matcher = GroupTopicSpider.MAYBE_PRICE_PATTERN.matcher(s);
        while (matcher.find()) {
            LogManager.info(matcher.group());
        }
    }

    @Test
    public void testWechat() {
        String s = "微信号是dafdafasf.........";

        ///s = "wechat:5343dfada....";
        LogManager.info(ParsingUtil.matchedString(GroupTopicSpider.MAYBE_WECHAT_PATTERN, s));

        s = "phonenumber:1768732";
        s = "电话8888777";
        LogManager.info(ParsingUtil.matchedString(GroupTopicSpider.MAYBE_PHONE_PATTERN, s));

    }

    @Test
    public void testTransform() {
        String s = "hellp";

        System.out.println(Integer.parseInt(s));
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    @Test
    public void testTimeReg() {
        String time = "12-09 09:09";
        LogManager.info(ParsingUtil.matchedString(GroupListSpider.TIME_PATTERN, time));

        Matcher m = GroupListSpider.TIME_PATTERN.matcher(time);

        LogManager.info(String.valueOf(m.matches()));

        time = "2017-" + time;

        try {
            Date date = sdf.parse(time);

            LogManager.info("unit time:" + date.getTime());

        } catch (ParseException e) {
            ;
        }

        LogManager.info("formated time:" + StringUtil.formatYYMMDD8(time.substring(0, 10)));
    }

}