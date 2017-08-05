package wuxian.me.v2exspider.mapper;

/**
 * Created by wuxian on 6/5/2017.
 */
public interface BaseMapper<T> {

    void createNewTableIfNeed(T model);

    void createIndex(T model);
}
