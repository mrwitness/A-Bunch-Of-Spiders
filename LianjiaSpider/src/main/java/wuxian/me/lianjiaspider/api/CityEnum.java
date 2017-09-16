package wuxian.me.lianjiaspider.api;

/**
 * Created by wuxian on 24/6/2017.
 */
public enum CityEnum {

    Hangzhou(1),;
    private int city;

    CityEnum(int city) {
        this.city = city;
    }

    public int city() {
        return city;
    }
}
