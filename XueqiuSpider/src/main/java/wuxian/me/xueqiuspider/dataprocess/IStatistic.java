package wuxian.me.xueqiuspider.dataprocess;

import java.util.List;

/**
 * Created by wuxian on 28/7/2017.
 */
public interface IStatistic<T> {

    //统计某个item的出现次数,并排序？
    void calTopItemCounts(List<T> dataList);
}
