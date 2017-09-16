package wuxian.me.lianjiaspider;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wuxian on 9/7/2017.
 */
public class MainTest {

    @Test
    public void testString() {
        Integer i = null;
        System.out.println(i);
    }

    @Test
    public void testBlank() {
        String s = "hell                     w";

        System.out.println(s.indexOf(" "));
    }

}