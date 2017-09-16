package wuxian.me.zhihuspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.spidercommon.util.FileUtil;
import wuxian.me.zhihuspider.biz.BizConfig;
import wuxian.me.zhihuspider.mapper.Voteup;
import wuxian.me.zhihuspider.model.VoteupModel;
import wuxian.me.zhihuspider.util.SpringBeans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 4/9/2017.
 */
public class VoteupSaver extends BaseSaver<VoteupModel> {

    private static VoteupSaver instance = null;
    private Map<Long, VoteupModel> modelMap = new ConcurrentHashMap<Long, VoteupModel>();
    private SaveModelThread thread;

    private Voteup mapper = SpringBeans.voteup();

    public static VoteupSaver getInstance() {
        if (instance == null) {
            instance = new VoteupSaver();
        }
        return instance;
    }

    private VoteupSaver() {
        thread = new SaveModelThread(modelMap, BizConfig.saveVoteupInternal * 1000
                , new SaveModelThread.IDatabaseOperator<VoteupModel>() {
            public void insert(VoteupModel model) {
                String path = BizConfig.saveFilePath + model.getLocalPath();
                FileUtil.writeToFile(path, model.content);

                mapper.insertVoteup(model);
            }

            public void update(VoteupModel model) {
                //
            }
        });
        thread.setName("VoteupSaverThread");
        thread.start();
    }

    public boolean saveModel(@NotNull VoteupModel model) {
        if (!isModelValid(model)) {
            return false;
        }
        modelMap.put(model.index(), model);
        return true;
    }

    public boolean isModelValid(@NotNull VoteupModel model) {
        return true;
    }

    @Override
    protected Thread getSaverThread() {
        return thread;
    }
}

