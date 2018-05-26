package org.wisestar.lottery.entity;

/**
 * 下注状态
 * @author zhangxu
 * @date 2017/11/2
 */
public enum BetStateEnum {

    /**
     * 待支付
     */
    PENDING_PAY(1, "待支付"),
    /**
     * 支付不成功
     */
    NOT_PAY(2, "支付不成功"),
    /**
     * 待出票
     */
    PENDING_TICKET(3, "待出票"),
    /**
     * 出票不成功
     */
    NOT_TICKENT(4, "出票不成功"),

    /**
     * 待开奖
     */
    PENDING_OPEN(5, "待开奖"),
    /**
     * 未中奖
     */
    NOT_WIN(6, "未中奖"),
    /**
     * 中奖
     */
    WIN_PRIZE(7, "中奖");

    private int value;
    private String text;

    BetStateEnum(int value, String text) {
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
        for (BetStateEnum e : BetStateEnum.values()) {
            if (e.value == value) {
                return e.text;
            }
        }
        return null;
    }
}
