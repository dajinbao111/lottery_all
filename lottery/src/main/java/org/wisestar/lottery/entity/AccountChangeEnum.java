package org.wisestar.lottery.entity;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
public enum AccountChangeEnum {

    RECHARGE(1, "微信充值"),
    PAY(2, "购彩支付"),
    WITHDRAW(3, "账户提现"),
    DRAWBACK(4, "退款"),
    PRIZE(5, "奖金");

    private int value;
    private String text;

    AccountChangeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static String getText(int value) {
        for (AccountChangeEnum e : AccountChangeEnum.values()) {
            if (e.value == value) {
                return e.text;
            }
        }
        return null;
    }
}
