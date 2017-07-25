package wuxian.me.xueqiuspider.biz.today.model;

import wuxian.me.xueqiuspider.model.User;

/**
 * Created by wuxian on 25/7/2017.
 */
public class TopItemData {

    public User user;

    public Long id;

    public String title;

    public String target;

    public Boolean promotion;

    public String answers;

    public String tag;

    public String score;

    public Integer reply_count;

    public Integer retweet_count;

    public String topic_title;

    public String topic_desc;

    public String topic_symbol;

    public String topic_pic;

    public Integer view_count;

    public Long created_at;

    @Override
    public String toString() {
        return "TopItemData{" +
                "user=" + user +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", target='" + target + '\'' +
                ", promotion=" + promotion +
                ", answers='" + answers + '\'' +
                ", tag='" + tag + '\'' +
                ", score='" + score + '\'' +
                ", reply_count=" + reply_count +
                ", retweet_count=" + retweet_count +
                ", topic_title='" + topic_title + '\'' +
                ", topic_desc='" + topic_desc + '\'' +
                ", topic_symbol='" + topic_symbol + '\'' +
                ", topic_pic='" + topic_pic + '\'' +
                ", view_count=" + view_count +
                ", created_at=" + created_at +
                '}';
    }
}
