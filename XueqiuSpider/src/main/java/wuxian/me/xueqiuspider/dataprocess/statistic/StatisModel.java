package wuxian.me.xueqiuspider.dataprocess.statistic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wuxian on 29/7/2017.
 */
public class StatisModel {

    public String key;

    public List<Integer> positions = new ArrayList<Integer>();

    @Override
    public String toString() {
        return "StatisModel{" +
                "key='" + key + '\'' +
                "apeartime=" + positions.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatisModel)) return false;

        StatisModel model = (StatisModel) o;

        if (key != null ? !key.equals(model.key) : model.key != null) return false;
        return positions != null ? positions.equals(model.positions) : model.positions == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (positions != null ? positions.hashCode() : 0);
        return result;
    }

    public static class Comparator implements java.util.Comparator<StatisModel> {

        @Override
        public int compare(StatisModel left, StatisModel right) {

            if (left.positions.size() != right.positions.size()) {
                return left.positions.size() > right.positions.size() ? -1 : 1;
            }

            return 0;
        }
    }
}
