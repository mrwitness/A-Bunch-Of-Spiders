package wuxian.me.xueqiuspider.model.today;

import java.util.List;

/**
 * Created by wuxian on 24/7/2017.
 */
public class TodayResponse<T> {

    public Long next_max_id;

    public Long next_id;

    public List<TodayItem<T>> list;

    @Override
    public String toString() {
        return "TodayResponse{" +
                "next_max_id=" + next_max_id +
                ", next_id=" + next_id +
                ", list=" + list +
                '}';
    }
}
