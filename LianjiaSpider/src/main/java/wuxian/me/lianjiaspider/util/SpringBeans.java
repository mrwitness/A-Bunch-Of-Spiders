package wuxian.me.lianjiaspider.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.stereotype.Component;
import wuxian.me.lianjiaspider.biz.touming.xinfang.NewQuListSpider;
import wuxian.me.lianjiaspider.mapper.*;
import wuxian.me.spidercommon.log.LogManager;

/**
 * Created by wuxian on 24/6/2017.
 */
@Component
public class SpringBeans {

    private static ApplicationContext applicationContext;

    private static SpringBeans ins;

    @Autowired
    XiaoquSellMapper sellMapper;

    public static XiaoquSellMapper sellMapper() {
        return ins.sellMapper;
    }

    @Autowired
    XiaoquMapper xiaoquMapper;

    public static XiaoquMapper xiaoquMapper() {
        return ins.xiaoquMapper;
    }

    @Autowired
    LoupanMapper loupanMapper;

    public static LoupanMapper loupanMapper() {
        return ins.loupanMapper;
    }

    @Autowired
    QuMapper quMapper;

    public static QuMapper quMapper() {
        return ins.quMapper;
    }

    @Autowired
    NewQuMapper newQuMapper;

    public static NewQuMapper newQuMapper() {
        return ins.newQuMapper;
    }

    @Autowired
    NewQuDetailMapper newQuDetailMapper;

    public static NewQuDetailMapper newQuDetailMapper() {
        return ins.newQuDetailMapper;
    }

    @Autowired
    NewQuPriceMapper newQuPriceMapper;

    public static NewQuPriceMapper newQuPriceMapper() {
        return ins.newQuPriceMapper;
    }

    private SpringBeans() {
    }

    public static void init() {

        try {
            applicationContext = new ClassPathXmlApplicationContext("spider.xml");
        } catch (Exception e) {
            LogManager.info(e.toString());
        }

        ins = applicationContext.getBean(SpringBeans.class);

    }
}
