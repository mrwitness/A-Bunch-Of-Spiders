package wuxian.me.lianjiaspider.mapper;

import org.apache.ibatis.annotations.Param;
import wuxian.me.lianjiaspider.model.Loupan;
import wuxian.me.lianjiaspider.model.Xiaoqu;

import java.util.List;

/**
 * Created by wuxian on 13/4/2017.
 */
public interface LoupanMapper extends BaseMapper<Loupan> {

    void insertLoupan(Loupan loupan);

    List<Loupan> loadLoupan(@Param("tableName") String tableName, long loupanId);
}
