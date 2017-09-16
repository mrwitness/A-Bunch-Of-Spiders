package wuxian.me.lianjiaspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.lianjiaspider.biz.touming.ToumingConfig;
import wuxian.me.lianjiaspider.mapper.NewQuDetailMapper;
import wuxian.me.lianjiaspider.model.touming.NewquDetail;
import wuxian.me.lianjiaspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class NewQuDetailSaver extends BaseSaver<NewquDetail> {

    private static NewQuDetailSaver instance = null;
    private Map<Long, NewquDetail> modelMap = new ConcurrentHashMap<Long, NewquDetail>();
    private SaveModelThread thread;

    private NewQuDetailMapper mapper = SpringBeans.newQuDetailMapper();

    public static NewQuDetailSaver getInstance() {
        if (instance == null) {
            instance = new NewQuDetailSaver();
        }
        return instance;
    }


    private NewQuDetailSaver() {
        thread = new SaveModelThread(modelMap, ToumingConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<NewquDetail>() {
            public void insert(NewquDetail model) {
                mapper.insertNewQuDetail(model);
            }

            public void update(NewquDetail model) {
                //
            }
        });
        thread.setName("NewQuDetailSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull NewquDetail xiaoqu) {
        if (!isModelValid(xiaoqu)) {
            return false;
        }
        modelMap.put(xiaoqu.index(), xiaoqu);
        return true;
    }

    public boolean isModelValid(@NotNull NewquDetail model) {

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
