package org.wisestar.lottery.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;

public class MathUtilsTest {

    @Test
    public void test() {
        //2串1，各选择1场
        String[] dataList = {"101384-0-3-5.50", "101384-0-1-3.50", "101384-0-0-1.51", "101385-0-3-2.27", "101386-0-3-1.88", "101386-0-1-3.60"};
        List<String[]> resultList = MathUtils.combinationSelect(dataList, 3);
        for (String[] arr : resultList) {
            for (String a : arr) {
                System.out.println(a);
            }

            System.out.println();

        }

        double d = 0.0;
        System.out.println(d == 0);

        String[] a = StringUtils.splitPreserveAllTokens("单关", ",");
        System.out.println();
    }

}