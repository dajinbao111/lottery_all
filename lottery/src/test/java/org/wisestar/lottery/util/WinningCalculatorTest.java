package org.wisestar.lottery.util;

import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WinningCalculatorTest {

    @Test
    public void winFourteenGames() throws Exception {

    }

    @Test
    public void winNineGames() throws Exception {
       String s = "admin";
       String[] ss = StringUtils.split(s, ",");
       System.out.println(ss);
    }

}