package wuxian.me.lianjiaspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.lianjiaspider.biz.touming.ToumingConfig;
import wuxian.me.lianjiaspider.mapper.NewQuDetailMapper;
import wuxian.me.lianjiaspider.mapper.NewQuPriceMapper;
import wuxian.me.lianjiaspider.model.touming.NewquPrice;
import wuxian.me.lianjiaspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 */
public class NewQuPriceSaver extends BaseSaver<NewquPrice> {

    private static NewQuPriceSaver instance = null;
    private Map<Long, NewquPrice> modelMap = new ConcurrentHashMap<Long, NewquPrice>();
    private SaveModelThread thread;

    private NewQuPriceMapper mapper = SpringBeans.newQuPriceMapper();

    public static NewQuPriceSaver getInstance() {
        if (instance == null) {
            instance = new NewQuPriceSaver();
        }
        return instance;
    }


    private NewQuPriceSaver() {
        thread = new SaveModelThread(modelMap, ToumingConfig.saveSellInternal * 1000, new SaveModelThread.IDatabaseOperator<NewquPrice>() {
            public void insert(NewquPrice model) {
                mapper.insertNewquPrice(model);
            }

            public void update(NewquPrice model) {
                //
            }
        });
        thread.setName("NewQuPriceSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull NewquPrice price) {
        if (!isModelValid(price)) {
            return false;
        }
        modelMap.put(price.index(), price);
        return true;
    }

    public boolean isModelValid(@NotNull NewquPrice model) {
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
