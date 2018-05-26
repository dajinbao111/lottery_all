package org.wisestar.lottery.dto;

import lombok.Data;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
@Data
public class UserAccountDto {

    private String openId;
    private Double balance;
    private String alipay;
    private String bankName;
    private String bankAccount;
    private String bankCard;
}
