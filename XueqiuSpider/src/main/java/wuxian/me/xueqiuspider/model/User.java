package wuxian.me.xueqiuspider.model;

/**
 * Created by wuxian on 24/7/2017.
 */
public class User {

    public Long id;

    public String description;

    //其实就是name
    public String screen_name;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", screen_name='" + screen_name + '\'' +
                '}';
    }
}
