package wuxian.me.lianjiaspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.lianjiaspider.biz.BizConfig;
import wuxian.me.lianjiaspider.mapper.XiaoquMapper;
import wuxian.me.lianjiaspider.mapper.XiaoquSellMapper;
import wuxian.me.lianjiaspider.model.Xiaoqu;
import wuxian.me.lianjiaspider.model.Xiaoqu;
import wuxian.me.lianjiaspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class XiaoquSaver extends BaseSaver<Xiaoqu> {

    private static XiaoquSaver instance = null;
    private Map<Long, Xiaoqu> companyMap = new ConcurrentHashMap<Long, Xiaoqu>();
    private SaveModelThread thread;

    private XiaoquMapper mapper = SpringBeans.xiaoquMapper();

    public static XiaoquSaver getInstance() {
        if (instance == null) {
            instance = new XiaoquSaver();
        }
        return instance;
    }


    private XiaoquSaver() {
        thread = new SaveModelThread(companyMap, BizConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<Xiaoqu>() {
            public void insert(Xiaoqu model) {
                mapper.insertXiaoqu(model);
            }

            public void update(Xiaoqu model) {
                //
            }
        });
        thread.setName("xiaoquSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull Xiaoqu xiaoqu) {
        if (!isModelValid(xiaoqu)) {
            return false;
        }
        companyMap.put(xiaoqu.index(), xiaoqu);
        return true;
    }

    public boolean isModelValid(@NotNull Xiaoqu model) {

        if (model.xiaoqu_id != null) {
            return false;
        }
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}
