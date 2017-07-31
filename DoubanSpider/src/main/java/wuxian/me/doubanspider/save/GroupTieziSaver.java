package wuxian.me.doubanspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.doubanspider.mapper.GroupTieziMapper;
import wuxian.me.doubanspider.model.GroupTiezi;
import wuxian.me.doubanspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class GroupTieziSaver extends BaseSaver<GroupTiezi> {

    private static GroupTieziSaver instance = null;
    private Map<Long, GroupTiezi> modelMap = new ConcurrentHashMap<Long, GroupTiezi>();
    private SaveModelThread thread;

    private GroupTieziMapper mapper = SpringBeans.groupTieziMapper();

    public static GroupTieziSaver getInstance() {
        if (instance == null) {
            instance = new GroupTieziSaver();
        }
        return instance;
    }


    private GroupTieziSaver() {
        thread = new SaveModelThread(modelMap, GroupConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<GroupTiezi>() {
            public void insert(GroupTiezi model) {
                mapper.insertTiezi(model);
            }

            public void update(GroupTiezi model) {
                //
            }
        });
        thread.setName("GroupTieziSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull GroupTiezi model) {
        if (!isModelValid(model)) {
            return false;
        }
        modelMap.put(model.index(), model);
        return true;
    }

    public boolean isModelValid(@NotNull GroupTiezi model) {
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}
