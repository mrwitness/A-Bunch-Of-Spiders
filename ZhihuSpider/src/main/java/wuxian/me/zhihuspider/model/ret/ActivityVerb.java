package wuxian.me.zhihuspider.model.ret;

/**
 * Created by wuxian on 1/9/2017.
 */
public class ActivityVerb<T> {

    public String verb;
    //public String id;      //其实就是时间 created_time
    public String type;    //这个值好像一直是feed
    public Long created_time;

    public T target;    //操作的对象

}
