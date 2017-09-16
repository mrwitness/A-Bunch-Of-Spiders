package wuxian.me.lianjiaspider.mapper;

import org.apache.ibatis.annotations.Param;
import wuxian.me.lianjiaspider.model.touming.NewQu;
import wuxian.me.lianjiaspider.model.touming.NewquDetail;

import java.util.List;

/**
 * Created by wuxian on 13/4/2017.
 */
public interface NewQuDetailMapper extends BaseMapper<NewquDetail> {

    void insertNewQuDetail(NewquDetail detail);

    List<NewQu> loadNewQuDetail(@Param("tableName") String tableName, long qu_id);
}
