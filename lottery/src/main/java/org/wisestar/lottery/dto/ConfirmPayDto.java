package org.wisestar.lottery.dto;

import lombok.Data;

/**
 * @author zhangxu
 * @date 2017/11/3
 */
@Data
public class ConfirmPayDto {

    private String lottery;
    private Double payAmount;
    private String nickName;
    private Double balance;

}
