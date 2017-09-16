package wuxian.me.zhihuspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.zhihuspider.biz.BizConfig;
import wuxian.me.zhihuspider.mapper.Follow;
import wuxian.me.zhihuspider.model.FollowModel;
import wuxian.me.zhihuspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 4/9/2017.
 */
public class FollowSaver extends BaseSaver<FollowModel> {

    private static FollowSaver instance = null;
    private Map<Long, FollowModel> modelMap = new ConcurrentHashMap<Long, FollowModel>();
    private SaveModelThread thread;

    private Follow mapper = SpringBeans.follow();

    public static FollowSaver getInstance() {
        if (instance == null) {
            instance = new FollowSaver();
        }
        return instance;
    }

    private FollowSaver() {
        thread = new SaveModelThread(modelMap, BizConfig.saveFollowInternal * 1000
                , new SaveModelThread.IDatabaseOperator<FollowModel>() {
            public void insert(FollowModel model) {
                mapper.insertFollow(model);
            }

            public void update(FollowModel model) {
                //
            }
        });
        thread.setName("FollowSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull FollowModel model) {
        if (!isModelValid(model)) {
            return false;
        }
        modelMap.put(model.index(), model);
        return true;
    }

    public boolean isModelValid(@NotNull FollowModel model) {
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}

