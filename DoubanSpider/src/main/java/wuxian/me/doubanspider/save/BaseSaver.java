package wuxian.me.doubanspider.save;

import com.sun.istack.internal.NotNull;
import wuxian.me.spidercommon.util.SignalManager;

/**
 * Created by wuxian on 3/6/2017.
 */
public abstract class BaseSaver<T> implements IModelSaver<T>, SignalManager.OnSystemKill {

    @NotNull
    protected abstract Thread getSaverThread();

    public final void onSystemKilled() {
        Thread thread = getSaverThread();
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {

        }
    }
}
