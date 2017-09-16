package wuxian.me.lianjiaspider.mapper;

import org.apache.ibatis.annotations.Param;
import wuxian.me.lianjiaspider.model.touming.NewQu;
import wuxian.me.lianjiaspider.model.touming.NewquDetail;
import wuxian.me.lianjiaspider.model.touming.NewquPrice;

import java.util.List;

/**
 * Created by wuxian on 13/4/2017.
 */
public interface NewQuPriceMapper extends BaseMapper<NewquPrice> {

    void insertNewquPrice(NewquPrice price);

    List<NewQu> loadNewquPrice(@Param("tableName") String tableName, long qu_id);
}
