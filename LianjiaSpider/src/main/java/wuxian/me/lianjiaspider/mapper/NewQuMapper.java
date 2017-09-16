package wuxian.me.lianjiaspider.mapper;

import org.apache.ibatis.annotations.Param;
import wuxian.me.lianjiaspider.model.touming.NewQu;

import java.util.List;

/**
 * Created by wuxian on 13/4/2017.
 */
public interface NewQuMapper extends BaseMapper<NewQu> {

    void insertNewQu(NewQu qu);

    List<NewQu> loadNewQu(@Param("tableName") String tableName, long qu_id);
}
