package wuxian.me.xueqiuspider.mapper;

import wuxian.me.xueqiuspider.model.Zhibo;

import java.util.List;

/**
 * Created by wuxian on 28/7/2017.
 */
public interface ZhiboMapper extends BaseMapper<Zhibo> {

    void insertZhibo(Zhibo zhibo);

    List<Zhibo> loadZhibo(Zhibo zhibo);
}
