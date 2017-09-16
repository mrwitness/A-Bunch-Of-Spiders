package wuxian.me.zhihuspider.model;

import wuxian.me.zhihuspider.model.ret.ActivityVerb;
import wuxian.me.zhihuspider.model.ret.Behavior;
import wuxian.me.zhihuspider.model.ret.Verb;

/**
 * Created by wuxian on 4/9/2017.
 */
public class FollowModel extends BaseModel {

    public static String tableName = "follow";

    public String target_id;
    public Integer actionType;

    public Integer follower_count;
    public Integer answer_count;
    public Integer comment_count;
    public Integer articles_count;

    public String title;
    public String authorUrl;
    public Long created_time;

    private static FollowModel fixAuthorUrl(FollowModel m) {
        if(m != null && m.authorUrl != null && m.authorUrl.length() == 0) {
            m.authorUrl =  null;
        }
        return m;
    }

    public static FollowModel from(ActivityVerb verb) {
        if (verb == null) {
            return null;
        }

        Behavior b = Behavior.fromDescription(verb.verb);
        if (b == null) {
            return null;
        }

        if (b == Behavior.TOPIC_FOLLOW) {
            FollowModel model = new FollowModel();

            model.created_time = verb.created_time;
            model.actionType = b.ordinal();
            Verb target = (Verb) verb.target;
            if (target != null) {
                model.comment_count = target.comment_count;
                model.answer_count = target.answer_count;
                model.title = target.name;
                model.target_id = String.valueOf(target.id);
            }
            return fixAuthorUrl(model);
        } else if (b == Behavior.QUESTION_FOLLOW) {
            FollowModel model = new FollowModel();

            model.created_time = verb.created_time;
            model.actionType = b.ordinal();
            Verb target = (Verb) verb.target;
            if (target != null) {
                model.answer_count = target.answer_count;
                model.comment_count = target.comment_count;
                model.follower_count = target.follower_count;
                model.title = target.title;
                model.target_id = String.valueOf(target.id);
            }
            return fixAuthorUrl(model);
        } else if (b == Behavior.MEMBER_FOLLOW_COLUMN) {  //专栏
            FollowModel model = new FollowModel();

            model.created_time = verb.created_time;
            model.actionType = b.ordinal();
            Verb target = (Verb) verb.target;
            if (target != null) {
                model.articles_count = target.articles_count;
                model.follower_count = target.followers;
                model.title = target.title;
                model.target_id = String.valueOf(target.id);

                Verb.Author author = target.author;
                if (author != null) {
                    model.authorUrl = author.url_token;
                }
            }
            return fixAuthorUrl(model);
        }

        return null;
    }

    @Override
    public String name() {
        return "FollowModel{" +
                "actionType=" + actionType +
                ", title='" + title + '\'' +
                ", target_id='" + target_id + '\'' +
                ", follower_count=" + follower_count +
                ", answer_count=" + answer_count +
                ", comment_count=" + comment_count +
                ", articles_count=" + articles_count +
                ", created_time=" + created_time +
                ", authorUrl='" + authorUrl + '\'' +
                '}';
    }


    @Override
    public long index() {

        return (target_id + actionType).hashCode();
    }
}
