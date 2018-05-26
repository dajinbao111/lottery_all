package org.wisestar.lottery.dto;

import lombok.Data;

/**
 * @author zhangxu
 * @date 2017/11/24
 */
@Data
public class NoticeDTO {

    private String nickname;
    private String lotteryType;
    private Double winAmount;
}
