package wuxian.me.lianjiaspider.mapper;

import org.apache.ibatis.annotations.Param;
import wuxian.me.lianjiaspider.model.Xiaoqu;

import java.util.List;

/**
 * Created by wuxian on 13/4/2017.
 */
public interface XiaoquMapper extends BaseMapper<Xiaoqu> {

    void insertXiaoqu(Xiaoqu xiaoqu);

    List<Xiaoqu> loadXiaoqu(@Param("tableName") String tableName, long xiaoqu_id);
}
