package wuxian.me.zhihuspider.biz.activity;

import com.google.gson.reflect.TypeToken;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.ParsingUtil;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidersdk.BaseSpider;
import wuxian.me.zhihuspider.biz.BaseZhihuSpider;
import wuxian.me.zhihuspider.model.FollowModel;
import wuxian.me.zhihuspider.model.VoteupModel;
import wuxian.me.zhihuspider.model.ret.ActivityRet;
import wuxian.me.zhihuspider.model.ret.ActivityVerb;
import wuxian.me.zhihuspider.model.ret.Behavior;
import wuxian.me.zhihuspider.save.FollowSaver;
import wuxian.me.zhihuspider.save.VoteupSaver;
import wuxian.me.zhihuspider.util.CustomGson;
import wuxian.me.zhihuspider.util.Helper;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by wuxian on 1/9/2017.
 * 知乎的用户日常行为
 */
public class UserActivitySpider extends BaseZhihuSpider {

    public static HttpUrlNode toUrlNode(UserActivitySpider spider) {
        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl = API + spider.userId + API_POST;
        node.httpGetParam.put("after_id", String.valueOf(spider.afterId));
        return node;
    }

    public static UserActivitySpider fromUrlNode(HttpUrlNode node) {
        if (!node.baseUrl.contains(API) || !node.baseUrl.contains(API_POST)) {
            return null;
        }

        String userId = ParsingUtil.matchedString(USERID_PATTERN, node.baseUrl);
        if (userId != null) {
            return new UserActivitySpider(userId, Long.parseLong(node.httpGetParam.get("after_id")));
        }

        return null;
    }

    private static String REG_USERID = "(?<=members/)[a-zA-Z0-9.-]+(?=/activities)";
    private static Pattern USERID_PATTERN = Pattern.compile(REG_USERID);

    private static final String API = "https://www.zhihu.com/api/v4/members/";
    private static final String API_POST = "/activities?limit=20&desktop=True";

    private String userId;
    private Long afterId;

    public UserActivitySpider(String userId, Long afterId) {

        this.userId = userId;
        this.afterId = afterId;
    }

    @Override
    protected Request buildRequest() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API + userId + API_POST)
                .newBuilder();
        urlBuilder.addQueryParameter("after_id", String.valueOf(afterId));

        String host = "www.zhihu.com";
        Request request = new Request.Builder()
                .headers(Helper.getSpiderHeader(host, API + userId))
                .url(urlBuilder.build().toString())
                .build();
        return request;
    }

    @Override
    public int parseRealData(String data) {
        //LogManager.info(data);

        ActivityRet activityRet = null;

        try {
            activityRet = CustomGson.gson().fromJson(data
                    , new TypeToken<ActivityRet>() {
                    }.getType());
        } catch (Exception e) {
            LogManager.error(e.getMessage());

            return BaseSpider.RET_MAYBE_BLOCK;
        }


        if (activityRet == null) {
            return BaseSpider.RET_MAYBE_BLOCK;
        }

        handleRet(activityRet);

        handlePaging(activityRet.paging);

        return BaseSpider.RET_SUCCESS;

    }

    private static String REG_NEXTID = "(?<=after_id=)\\d+";
    private static Pattern NEXTID_PATTERN = Pattern.compile(REG_NEXTID);

    private void handlePaging(ActivityRet.Paging paging) {
        if (paging == null) {
            return;
        }
        if (paging.is_end) {
            return;
        }
        Long afterid = ParsingUtil.matchedLong(NEXTID_PATTERN, paging.next);
        //LogManager.info("dispatchSpider nextId:" + afterid);

        Random r = new Random();
        if (r.nextBoolean() && false) {
            if (r.nextBoolean()) {
                LogManager.info("not dispatch anohter spider");
                return;
            }
        }

        if (afterid != null) {
            Helper.dispatchSpider(new UserActivitySpider(userId, afterid));
        }
    }

    private void handleRet(ActivityRet ret) {

        for (ActivityVerb vb : ret.data) {
            Behavior b = Behavior.fromDescription(vb.verb);
            if (b == null) {
                continue;
            }

            if (b == Behavior.ANSWER_VOTE_UP || b == Behavior.MEMBER_VOTEUP_ARTICLE || b == Behavior.MEMBER_COLLECT_ANSWER) {
                handleVoteup(vb);

            } else if (b == Behavior.QUESTION_FOLLOW || b == Behavior.TOPIC_FOLLOW || b == Behavior.MEMBER_FOLLOW_COLUMN) {

                handleFollow(vb);
            }
        }
    }


    private void handleVoteup(@NotNull ActivityVerb vb) {
        VoteupModel model = VoteupModel.from(vb);
        //LogManager.info("handle " + vb.verb);
        if (model != null) {
            LogManager.info(model.toString());
            VoteupSaver.getInstance().saveModel(model);
        } else {
            LogManager.info("decode handleVoteup error");
        }
    }


    private void handleFollow(@NotNull ActivityVerb vb) {
        FollowModel model = FollowModel.from(vb);

        //LogManager.info("handle " + vb.verb);
        if (model != null) {
            LogManager.info(model.toString());
            FollowSaver.getInstance().saveModel(model);
        } else {
            LogManager.info("decode handleFollow error");
        }
    }

    @Override
    public String name() {
        return "UserActivitySpider:{userId:" + userId + " afterId:" + afterId + "}";
    }
}
