package org.wisestar.lottery.entity;

/**
 * 彩票类型
 * @author zhangxu
 * @date 2017/11/7
 */
public enum LotteryTypeEnum {

    LOTTERY(1, "大乐透"),
    RANK_THREE(2, "排列三"),
    RANK_FIVE(3, "排列五"),
    SEVEN_COLOR(4, "七星彩"),
    FOURTEEN(5, "胜负彩"),
    NINE(6, "任选九"),
    FOOTBALL(7, "竞彩足球"),
    SINGLE(8, "竞足单关");

    private int value;
    private String text;

    LotteryTypeEnum(int value, String text) {
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
        for (LotteryTypeEnum e : LotteryTypeEnum.values()) {
            if (e.value == value) {
                return e.text;
            }
        }
        return null;
    }
}
