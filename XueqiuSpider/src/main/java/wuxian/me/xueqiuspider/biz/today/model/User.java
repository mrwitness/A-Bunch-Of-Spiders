package wuxian.me.xueqiuspider.biz.today.model;

/**
 * Created by wuxian on 24/7/2017.
 */
public class User {

    public Long id;

    public String profile;

    public String description;

    public String screen_name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", profile='" + profile + '\'' +
                ", description='" + description + '\'' +
                ", screen_name='" + screen_name + '\'' +
                '}';
    }
}
