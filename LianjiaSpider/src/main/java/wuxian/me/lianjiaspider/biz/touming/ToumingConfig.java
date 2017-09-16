package wuxian.me.lianjiaspider.biz.touming;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.util.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wuxian on 30/5/2017.
 */
public class ToumingConfig {

    public static int saveCompanyInternal;
    public static int saveSellInternal;
    public static int savePositionInternal;

    private ToumingConfig() {
        ;
    }

    public static void init() {
        Properties pro = new Properties();
        FileInputStream in = null;
        boolean success = false;
        try {
            in = new FileInputStream(FileUtil.getCurrentPath()
                    + "/conf/biz.properties");
            pro.load(in);
            success = true;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            ;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    ;
                }

            }
        }

        if (!success) {
            pro = null; //确保一定会初始化
        }

        saveCompanyInternal = parse(pro, "saveCompanyInternal", 180);

        saveSellInternal = parse(pro, "saveSellInternal", 180);

        savePositionInternal = parse(pro, "savePositionInternal", 180);

    }

    private static String parse(@NotNull Properties pro, String key, String defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return pro.getProperty(
                    key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    private static long parse(@Nullable Properties pro, String key, long defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return Long.parseLong(pro.getProperty(
                    key, String.valueOf(defValue)));
        } catch (Exception e) {
            return defValue;
        }
    }

    private static int parse(@Nullable Properties pro, String key, int defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return Integer.parseInt(pro.getProperty(
                    key, String.valueOf(defValue)));
        } catch (Exception e) {
            return defValue;
        }
    }

    private static boolean parse(@Nullable Properties pro, String key, boolean defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return Boolean.parseBoolean(pro.getProperty(
                    key, String.valueOf(defValue)));
        } catch (Exception e) {
            return defValue;
        }
    }

}
