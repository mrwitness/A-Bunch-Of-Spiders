package wuxian.me.xueqiuspider.biz.today;

/**
 * Created by wuxian on 24/7/2017.
 */
public enum CategoryEnum {

    Top(-1),
    Ganggu(102),
    Zhibo(6),
    Jijin(104),
    Meigu(101),
    Fangcha(111);

    private int type;
    CategoryEnum(int type) {
        this.type = type;
    }

    public int getType(){
        return type;
    }
}
