package wuxian.me.zhihuspider.model.ret;

import com.sun.istack.internal.Nullable;

/**
 * Created by wuxian on 1/9/2017.
 */
public enum Behavior {

    MEMBER_VOTEUP_ARTICLE("赞了文章"),
    ANSWER_VOTE_UP("赞同了回答"),
    MEMBER_COLLECT_ANSWER("收藏了回答"),
    //上面三个能够大概描述价值观

    //这个可以单独训练
    ANSWER_CREATE("回答了问题"),

    TOPIC_FOLLOW("关注了话题"),
    QUESTION_FOLLOW("关注了问题"),
    MEMBER_FOLLOW_COLUMN("关注了专栏"),

    //分享的数量应该比较少
    MEMBER_LIKE_PIN("赞了分享"),
    ;

    private String des;

    Behavior(String description) {
        this.des = description;
    }

    @Nullable
    public static Behavior fromDescription(String des) {
        if (des == null || des.length() == 0) {
            return null;
        }

        for (Behavior b : Behavior.values()) {
            if (b.toString().equals(des)) {
                return b;
            }
        }
        return null;
    }
}
