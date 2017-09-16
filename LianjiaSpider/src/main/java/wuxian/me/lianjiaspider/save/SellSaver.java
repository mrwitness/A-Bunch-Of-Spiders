package wuxian.me.lianjiaspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.lianjiaspider.biz.BizConfig;
import wuxian.me.lianjiaspider.mapper.XiaoquSellMapper;
import wuxian.me.lianjiaspider.model.XiaoquSell;
import wuxian.me.lianjiaspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class SellSaver extends BaseSaver<XiaoquSell> {

    private static SellSaver instance = null;
    private Map<Long, XiaoquSell> companyMap = new ConcurrentHashMap<Long, XiaoquSell>();
    private SaveModelThread thread;

    private XiaoquSellMapper mapper = SpringBeans.sellMapper();

    public static SellSaver getInstance() {
        if (instance == null) {
            instance = new SellSaver();
        }
        return instance;
    }


    private SellSaver() {
        thread = new SaveModelThread(companyMap, BizConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<XiaoquSell>() {
            public void insert(XiaoquSell model) {
                mapper.insertSell(model);
            }

            public void update(XiaoquSell model) {
                //
            }
        });
        thread.setName("SellSaveThread");
        thread.start();
    }

    public boolean saveModel(@NotNull XiaoquSell sell) {
        if (!isModelValid(sell)) {
            return false;
        }
        companyMap.put(sell.index(), sell);
        return true;
    }

    public boolean isModelValid(@NotNull XiaoquSell model) {

        if (model.xiaoqu_id == null) {
            return false;
        }
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}
