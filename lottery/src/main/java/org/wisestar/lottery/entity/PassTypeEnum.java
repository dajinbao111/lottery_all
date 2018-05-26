package org.wisestar.lottery.entity;

/**
 * @author zhangxu
 * @date 2017/11/27
 */
public enum PassTypeEnum {

    ONE(1, "单关"),
    TWO(2, "2串1"),
    THREE(3, "3串1"),
    FOUR(4, "4串1"),
    FIVE(5, "5串1"),
    SIX(6, "6串1"),
    SEVEN(7, "7串1"),
    EIGHT(8, "8串1");

    private int value;
    private String text;

    PassTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static int getValue(String text) {
        for (PassTypeEnum e : PassTypeEnum.values()) {
            if (e.text == text) {
                return e.value;
            }
        }
        return 0;
    }
}
