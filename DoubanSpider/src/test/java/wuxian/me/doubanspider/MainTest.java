package wuxian.me.doubanspider;

import org.junit.Test;
import wuxian.me.doubanspider.biz.group.GroupListSpider;
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