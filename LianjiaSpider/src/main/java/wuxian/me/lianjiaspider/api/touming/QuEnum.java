package wuxian.me.lianjiaspider.api.touming;

/**
 * Created by wuxian on 10/7/2017.
 */
public enum QuEnum {

    Shangcheng(330102),

    Xiacheng(330103),

    Jianggan(330104),

    Gongshu(330105),

    Xihu(330106),

    Binjiang(330108),

    Zhijiang(330110),

    Xiasha(330186),

    Xiaoshan(330181),

    Yuhang(330184),;

    int number;

    QuEnum(int number) {
        this.number = number;
    }

    public int number() {
        return number;
    }
}
