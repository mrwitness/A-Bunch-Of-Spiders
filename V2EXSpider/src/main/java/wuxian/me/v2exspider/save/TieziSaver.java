package wuxian.me.v2exspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.v2exspider.mapper.TieziMapper;
import wuxian.me.v2exspider.model.BaseTiezi;
import wuxian.me.v2exspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class TieziSaver extends BaseSaver<BaseTiezi> {

    private static TieziSaver instance = null;
    private Map<Long, BaseTiezi> modelMap = new ConcurrentHashMap<Long, BaseTiezi>();
    private SaveModelThread thread;

    private TieziMapper mapper = SpringBeans.tieziMapper();

    public static TieziSaver getInstance() {
        if (instance == null) {
            instance = new TieziSaver();
        }
        return instance;
    }


    private TieziSaver() {
        thread = new SaveModelThread(modelMap, GroupConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<BaseTiezi>() {
            public void insert(BaseTiezi model) {
                mapper.insertTiezi(model);
            }

            public void update(BaseTiezi model) {
                //
            }
        });
        thread.setName("TieziSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull BaseTiezi model) {
        if (!isModelValid(model)) {
            return false;
        }
        modelMap.put(model.index(), model);
        return true;
    }

    public boolean isModelValid(@NotNull BaseTiezi model) {
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}
