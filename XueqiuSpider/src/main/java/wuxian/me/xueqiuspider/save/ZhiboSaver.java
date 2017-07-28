package wuxian.me.xueqiuspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.xueqiuspider.mapper.ZhiboMapper;
import wuxian.me.xueqiuspider.model.Zhibo;
import wuxian.me.xueqiuspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class ZhiboSaver extends BaseSaver<Zhibo> {

    private static ZhiboSaver instance = null;
    private Map<Long, Zhibo> modelMap = new ConcurrentHashMap<Long, Zhibo>();
    private SaveModelThread thread;

    private ZhiboMapper mapper = SpringBeans.zhiboMapper();

    public static ZhiboSaver getInstance() {
        if (instance == null) {
            instance = new ZhiboSaver();
        }
        return instance;
    }


    private ZhiboSaver() {
        thread = new SaveModelThread(modelMap, ZhiboConfig.saveZhiboInternal * 1000, new SaveModelThread.IDatabaseOperator<Zhibo>() {
            public void insert(Zhibo model) {
                mapper.insertZhibo(model);
            }

            public void update(Zhibo model) {
                //
            }
        });
        thread.setName("ZhiboSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull Zhibo model) {
        if (!isModelValid(model)) {
            return false;
        }
        modelMap.put(model.index(), model);
        return true;
    }

    public boolean isModelValid(@NotNull Zhibo model) {
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}
