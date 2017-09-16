package wuxian.me.zhihuspider.mapper;

import wuxian.me.zhihuspider.model.FollowModel;

/**
 * Created by wuxian on 4/9/2017.
 */
public interface Follow extends VerbMapper {

    void insertFollow(FollowModel follow);
}
