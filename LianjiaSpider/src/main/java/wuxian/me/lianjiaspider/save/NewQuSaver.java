package wuxian.me.lianjiaspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.lianjiaspider.biz.touming.ToumingConfig;
import wuxian.me.lianjiaspider.mapper.NewQuMapper;
import wuxian.me.lianjiaspider.model.touming.NewQu;
import wuxian.me.lianjiaspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class NewQuSaver extends BaseSaver<NewQu> {

    private static NewQuSaver instance = null;
    private Map<Long, NewQu> modelMap = new ConcurrentHashMap<Long, NewQu>();
    private SaveModelThread thread;

    private NewQuMapper mapper = SpringBeans.newQuMapper();

    public static NewQuSaver getInstance() {
        if (instance == null) {
            instance = new NewQuSaver();
        }
        return instance;
    }


    private NewQuSaver() {
        thread = new SaveModelThread(modelMap, ToumingConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<NewQu>() {
            public void insert(NewQu model) {
                mapper.insertNewQu(model);
            }

            public void update(NewQu model) {
                //
            }
        });
        thread.setName("NewQuSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull NewQu xiaoqu) {
        if (!isModelValid(xiaoqu)) {
            return false;
        }
        modelMap.put(xiaoqu.index(), xiaoqu);
        return true;
    }

    public boolean isModelValid(@NotNull NewQu model) {

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
