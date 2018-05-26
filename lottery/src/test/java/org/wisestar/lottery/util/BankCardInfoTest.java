package org.wisestar.lottery.util;

import org.junit.Test;

public class BankCardInfoTest {
    @Test
    public void getBankInfo() throws Exception {
        String content = BankCardInfo.getBankInfo("6214850270509158");
        System.out.println(content);
    }

}