package wuxian.me.lianjiaspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.lianjiaspider.biz.BizConfig;
import wuxian.me.lianjiaspider.mapper.LoupanMapper;
import wuxian.me.lianjiaspider.model.Loupan;
import wuxian.me.lianjiaspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class LoupanSaver extends BaseSaver<Loupan> {

    private static LoupanSaver instance = null;
    private Map<Long, Loupan> loupanMap = new ConcurrentHashMap<Long, Loupan>();
    private SaveModelThread thread;

    private LoupanMapper mapper = SpringBeans.loupanMapper();

    public static LoupanSaver getInstance() {
        if (instance == null) {
            instance = new LoupanSaver();
        }
        return instance;
    }


    private LoupanSaver() {
        thread = new SaveModelThread(loupanMap, BizConfig.saveSellInternal * 1000,
                new SaveModelThread.IDatabaseOperator<Loupan>() {
                    public void insert(Loupan model) {
                        mapper.insertLoupan(model);
                    }

                    public void update(Loupan model) {
                        //
                    }
                });
        thread.setName("xiaoquSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull Loupan loupan) {
        if (!isModelValid(loupan)) {
            return false;
        }
        loupanMap.put(loupan.index(), loupan);
        return true;
    }

    public boolean isModelValid(@NotNull Loupan model) {
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}
