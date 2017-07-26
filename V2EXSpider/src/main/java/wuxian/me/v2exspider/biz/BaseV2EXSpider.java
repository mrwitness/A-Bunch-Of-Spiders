package wuxian.me.v2exspider.biz;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.SpiderCallback;

/**
 * Created by wuxian on 24/6/2017.
 */
public abstract class BaseV2EXSpider extends BaseSpider {
    protected final SpiderCallback getCallback() {
        return new V2EXSpiderCallback(this);
    }

    protected boolean checkBlockAndFailThisSpider(String s) {
        LogManager.error(s);
        return true;
    }

    public String hashString() {
        return name();
    }
}
