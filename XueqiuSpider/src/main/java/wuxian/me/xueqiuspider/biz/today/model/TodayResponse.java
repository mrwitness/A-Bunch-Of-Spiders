package wuxian.me.xueqiuspider.biz.today.model;

import java.util.List;

/**
 * Created by wuxian on 24/7/2017.
 */
public class TodayResponse {

    public Long next_max_id;

    public Long next_id;

    public List<TodayItem> list;

    @Override
    public String toString() {
        return "TodayResponse{" +
                "next_max_id=" + next_max_id +
                ", next_id=" + next_id +
                ", list=" + list +
                '}';
    }
}
