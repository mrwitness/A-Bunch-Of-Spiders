package wuxian.me.lianjiaspider.mapper;

import wuxian.me.lianjiaspider.model.XiaoquSell;

import java.util.List;

/**
 * Created by wuxian on 13/4/2017.
 */
public interface XiaoquSellMapper extends BaseMapper<XiaoquSell> {

    void insertSell(XiaoquSell sell);

    void updateSell(XiaoquSell sell);

    List<XiaoquSell> loadSells(Long xiaoqu_id);
}
