package wuxian.me.lianjiaspider.mapper;

import org.apache.ibatis.annotations.Param;
import wuxian.me.lianjiaspider.model.Xiaoqu;
import wuxian.me.lianjiaspider.model.touming.Qu;

import java.util.List;

/**
 * Created by wuxian on 13/4/2017.
 */
public interface QuMapper extends BaseMapper<Qu> {

    void insertQu(Qu qu);

    List<Qu> loadQu(@Param("tableName") String tableName, long qu_id);
}
