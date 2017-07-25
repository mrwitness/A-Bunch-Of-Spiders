package wuxian.me.xueqiuspider.biz.today.model;

/**
 * Created by wuxian on 24/7/2017.
 */
public class TodayItem<T> {

    public Long id;

    public Integer category;

    public T data;

    @Override
    public String toString() {
        return "TodayItem{" +
                "id=" + id +
                ", category=" + category +
                ", data=" + data +
                '}';
    }
}
