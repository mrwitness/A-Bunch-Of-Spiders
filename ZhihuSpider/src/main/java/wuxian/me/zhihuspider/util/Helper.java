package wuxian.me.zhihuspider.util;

import com.sun.istack.internal.NotNull;
import okhttp3.Headers;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.FileUtil;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.spidersdk.IJobManager;
import wuxian.me.spidersdk.anti.UserAgentManager;
import wuxian.me.spidersdk.job.IJob;
import wuxian.me.spidersdk.job.JobProvider;
import wuxian.me.spidersdk.manager.JobManagerFactory;
import wuxian.me.spidersdk.util.CookieManager;

import static wuxian.me.spidercommon.util.FileUtil.getCurrentPath;

/**
 * Created by wuxian on 20/6/2017.
 */
public class Helper {

    private static final String HEADER_REFERER = "Referer";
    private static Headers.Builder builder;

    static {
        builder = new Headers.Builder();
        builder.add("Cookie", "");
        builder.add("Connection", "keep-alive");
        builder.add(HEADER_REFERER, "abd");
        builder.add("User-Agent", "ab");

        builder.add("X-UDID", "AEBAae4zoQmPTh_TXP4owAhRtunUxzWm_Xc=");
        builder.add("X-API-VERSION", "3.0.40");
        builder.add("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20");
    }

    private Helper() {
    }

    public static String getCookieFilePath(String spiderName) {
        return getCurrentPath() + "/cookie/cookies_" + spiderName;
    }

    public static Headers getHeaderBySpecifyRef(@NotNull String reference, @NotNull String spiderName) {
        if (!CookieManager.containsKey(spiderName)) {
            if (FileUtil.checkFileExist(getCookieFilePath(spiderName))) {
                String content = FileUtil.readFromFile(getCookieFilePath(spiderName));
                if (content != null && content.length() != 0) {
                    CookieManager.put(spiderName, content);
                }
            }
        }
        builder.set("Cookie", CookieManager.get(spiderName));
        builder.set(HEADER_REFERER, reference);
        builder.set("User-Agent", UserAgentManager.getAgent());
        return builder.build();
    }



    public static Headers getSpiderHeader(String host, @NotNull String reference) {
        builder.set("Host", host);
        return getHeaderBySpecifyRef(reference, "Zhihu");
    }

    public static void dispatchSpider(@NotNull BaseSpider spider) {
        IJob job = JobProvider.getJob();
        job.setRealRunnable(spider);

        boolean b = jobManager().putJob(job);

        LogManager.info("dispatch spider " + b);
    }

    private static IJobManager sJobManager;

    private static IJobManager jobManager() {

        if (sJobManager == null) {
            synchronized (Helper.class) {
                sJobManager = JobManagerFactory.getJobManager();
            }
        }

        return sJobManager;
    }
}
