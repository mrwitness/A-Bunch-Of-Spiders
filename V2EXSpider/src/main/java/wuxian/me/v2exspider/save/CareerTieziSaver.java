package wuxian.me.v2exspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.v2exspider.mapper.CareerTieziMapper;
import wuxian.me.v2exspider.model.BaseTiezi;
import wuxian.me.v2exspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class CareerTieziSaver extends BaseSaver<BaseTiezi> {

    private static CareerTieziSaver instance = null;
    private Map<Long, BaseTiezi> modelMap = new ConcurrentHashMap<Long, BaseTiezi>();
    private SaveModelThread thread;

    private CareerTieziMapper mapper = SpringBeans.careerTieziMapper();

    public static CareerTieziSaver getInstance() {
        if (instance == null) {
            instance = new CareerTieziSaver();
        }
        return instance;
    }


    private CareerTieziSaver() {
        thread = new SaveModelThread(modelMap, GroupConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<BaseTiezi>() {
            public void insert(BaseTiezi model) {
                mapper.insertTiezi(model);
            }

            public void update(BaseTiezi model) {
                //
            }
        });
        thread.setName("CareerTieziSaverThread");
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
