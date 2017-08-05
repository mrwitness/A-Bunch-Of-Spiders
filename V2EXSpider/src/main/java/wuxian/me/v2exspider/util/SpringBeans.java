package wuxian.me.v2exspider.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.v2exspider.mapper.CareerTieziMapper;

/**
 * Created by wuxian on 24/6/2017.
 */
@Component
public class SpringBeans {

    private static ApplicationContext applicationContext;

    private static SpringBeans ins;

    @Autowired
    CareerTieziMapper careerTieziMapper;

    public static CareerTieziMapper careerTieziMapper() {
        return ins.careerTieziMapper;
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
