package wuxian.me.lianjiaspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.lianjiaspider.biz.touming.ToumingConfig;
import wuxian.me.lianjiaspider.mapper.QuMapper;
import wuxian.me.lianjiaspider.model.touming.Qu;
import wuxian.me.lianjiaspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class QuSaver extends BaseSaver<Qu> {

    private static QuSaver instance = null;
    private Map<Long, Qu> companyMap = new ConcurrentHashMap<Long, Qu>();
    private SaveModelThread thread;

    private QuMapper mapper = SpringBeans.quMapper();

    public static QuSaver getInstance() {
        if (instance == null) {
            instance = new QuSaver();
        }
        return instance;
    }


    private QuSaver() {
        thread = new SaveModelThread(companyMap, ToumingConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<Qu>() {
            public void insert(Qu model) {
                mapper.insertQu(model);
            }

            public void update(Qu model) {
                //
            }
        });
        thread.setName("QuSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull Qu xiaoqu) {
        if (!isModelValid(xiaoqu)) {
            return false;
        }
        companyMap.put(xiaoqu.index(), xiaoqu);
        return true;
    }

    public boolean isModelValid(@NotNull Qu model) {

        if (model.quId == null) {
            return false;
        }
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}
