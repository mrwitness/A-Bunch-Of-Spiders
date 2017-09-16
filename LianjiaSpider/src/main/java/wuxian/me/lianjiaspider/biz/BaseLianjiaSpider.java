package wuxian.me.lianjiaspider.biz;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.SpiderCallback;

/**
 * Created by wuxian on 24/6/2017.
 */
public abstract class BaseLianjiaSpider extends BaseSpider {
    protected final SpiderCallback getCallback() {
        return new LianjiaSpiderCallback(this);
    }

    //Todo
    protected boolean checkBlockAndFailThisSpider(String s) {
        LogManager.error(s);
        return true;
    }

    public String hashString() {
        return name();
    }
}
