package wuxian.me.zhihuspider.model;

import wuxian.me.zhihuspider.model.ret.ActivityVerb;
import wuxian.me.zhihuspider.model.ret.Behavior;
import wuxian.me.zhihuspider.model.ret.Verb;
import wuxian.me.zhihuspider.util.HtmlUtil;

/**
 * Created by wuxian on 4/9/2017.
 */
public class VoteupModel extends BaseModel {

    public static String tableName = "voteup";

    public static final Integer MAX_LOCAL_PATH_LENGTH = 40;
    public static final Integer MAX_CONTENT_LENGTH = 96;

    public Integer actionType;
    public Long answer_id;
    public Long question_id;
    public Long article_id;
    public String authorUrl;  //author的url_token
    public String title;
    public Integer thanks_count;
    public Integer voteup_count;
    public Integer comment_count;
    public Long created_time;
    public String content;

    public String getLocalPath() {
        String path = "" + created_time + "-" + actionType + "-" + title;
        if (path.length() >= MAX_LOCAL_PATH_LENGTH) {
            path = path.substring(0, MAX_LOCAL_PATH_LENGTH);
        }

        return path;
    }

    private static VoteupModel fixAuthorUrl(VoteupModel m) {
        if (m != null && m.authorUrl != null && m.authorUrl.length() == 0) {
            m.authorUrl = null;
        }

        return m;
    }

    public static VoteupModel from(ActivityVerb verb) {
        if (verb == null) {
            return null;
        }

        Behavior b = Behavior.fromDescription(verb.verb);
        if (b == null) {
            return null;
        }

        if (b == Behavior.ANSWER_VOTE_UP || b == Behavior.MEMBER_COLLECT_ANSWER) {  //给回答点赞
            VoteupModel model = new VoteupModel();
            model.created_time = verb.created_time;
            model.actionType = b.ordinal();
            Verb target = (Verb) verb.target;
            if (target != null) {
                model.comment_count = target.comment_count;
                model.thanks_count = target.thanks_count;
                model.voteup_count = target.voteup_count;
                model.title = target.name;
                model.answer_id = Long.parseLong(target.id);   //回答的id
                model.content = HtmlUtil.removeHtmlNode(target.content);

                Verb.Author author = target.author;
                if (author != null) {
                    model.authorUrl = author.url_token;
                }

                Verb.Question question = target.question;
                if (question != null) {
                    model.question_id = question.id;  //问题的id
                    model.title = question.title;
                }
            }
            return fixAuthorUrl(model);

        } else if (b == Behavior.MEMBER_VOTEUP_ARTICLE) {

            VoteupModel model = new VoteupModel();
            model.created_time = verb.created_time;
            model.actionType = b.ordinal();
            Verb target = (Verb) verb.target;
            if (target != null) {
                model.comment_count = target.comment_count;
                model.voteup_count = target.voteup_count;
                model.title = target.title;
                model.article_id = Long.parseLong(target.id);
                model.content = HtmlUtil.removeHtmlNode(target.content);
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
        return "VoteupModel{" +
                "actionType=" + actionType +
                ", answer_id='" + answer_id + '\'' +
                ", question_id=" + question_id +
                ", title='" + title + '\'' +
                ", article_id='" + article_id + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", thanks_count=" + thanks_count +
                ", voteup_count=" + voteup_count +
                ", comment_count=" + comment_count +
                ", created_time=" + created_time +
                ", local_path='" + getLocalPath() + '\'' +
                ", content='" + (content.length() >= MAX_CONTENT_LENGTH ? content.substring(0, MAX_CONTENT_LENGTH) : content) + '\'' +
                '}';
    }

    @Override
    public long index() {
        return ("" + actionType + answer_id
                + question_id + article_id).hashCode();
    }
}
