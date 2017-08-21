package wuxian.me.tongchengspider.biz;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.SpiderCallback;

/**
 * Created by wuxian on 24/6/2017.
 */
public abstract class BaseTongchengSpider extends BaseSpider {
    protected final SpiderCallback getCallback() {
        return new TongchengSpiderCallback(this);
    }


    protected boolean checkBlockAndFailThisSpider(String s) {
        LogManager.error(s);
        return true;
    }

    public String hashString() {
        return name();
    }

    protected boolean checkBlockAndFailThisSpider(int httpCode) {
        if(super.checkBlockAndFailThisSpider(httpCode)) {
            return true;
        }
        return httpCode == 403;
    }
}
