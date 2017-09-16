package wuxian.me.zhihuspider.model.ret;

import java.util.List;

/**
 * Created by wuxian on 1/9/2017.
 */
public class ActivityRet {

    public List<ActivityVerb> data;

    public Paging paging;

    public static class Paging {
        public Boolean is_end;

        public String next;

        public String previous;
    }
}
