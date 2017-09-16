package wuxian.me.lianjiaspider.biz.touming;

import com.sun.istack.internal.NotNull;
import okhttp3.Call;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.SpiderCallback;
import wuxian.me.spidersdk.anti.Fail;
import wuxian.me.spidersdk.manager.JobManagerFactory;

import java.io.IOException;

/**
 * Created by wuxian on 20/6/2017.
 */
public class ToumingSpiderCallback extends SpiderCallback {

    public ToumingSpiderCallback(@NotNull BaseSpider spider) {
        super(spider);
    }

    public void onFailure(Call call, IOException e) {
        LogManager.error("onFailure: " + getSpider().name());
        JobManagerFactory.getJobManager().fail(getSpider(), Fail.MAYBE_BLOCK);
        getSpider().serializeFullLog();
    }
}
