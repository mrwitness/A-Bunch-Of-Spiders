package wuxian.me.lianjiaspider.biz.touming;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.SpiderCallback;

/**
 * Created by wuxian on 10/7/2017.
 */
public abstract class BaseToumingSpider extends BaseSpider {
    protected final SpiderCallback getCallback() {
        return new ToumingSpiderCallback(this);
    }

    protected boolean checkBlockAndFailThisSpider(String s) {
        //LogManager.error(s);
        return true;
    }

    public String hashString() {
        return name();
    }

}