package wuxian.me.zhihuspider.model;

/**
 * Created by wuxian on 17/4/2017.
 * <p>
 * 用来规范log的命名
 */
public abstract class BaseModel {

    public abstract String name();

    @Override
    public final String toString() {
        return name();
    }

    public abstract long index();
}
