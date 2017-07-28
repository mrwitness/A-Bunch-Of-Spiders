package wuxian.me.xueqiuspider.model.today;

/**
 * Created by wuxian on 24/7/2017.
 */
public class TodayItem<T> {

    public Long id;

    public Integer category;

    public String column;

    @Override
    public String toString() {
        return "TodayItem{" +
                "id=" + id +
                ", category=" + category +
                ", column='" + column + '\'' +
                ", data=" + data +
                '}';
    }

    public T data;

}
