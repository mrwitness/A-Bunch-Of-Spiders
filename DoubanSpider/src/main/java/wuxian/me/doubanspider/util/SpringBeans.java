package wuxian.me.doubanspider.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import wuxian.me.doubanspider.mapper.GroupTieziMapper;
import wuxian.me.spidercommon.log.LogManager;

/**
 * Created by wuxian on 24/6/2017.
 */
@Component
public class SpringBeans {

    private static ApplicationContext applicationContext;

    private static SpringBeans ins;

    @Autowired
    GroupTieziMapper groupTieziMapper;

    public static GroupTieziMapper groupTieziMapper() {
        return ins.groupTieziMapper;
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
