package org.wisestar.lottery.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author zhangxu
 * @date 2017/11/17
 */
@Data
public class AddBankCardDto {

    private String openId;
    private String bankName;
    @NotEmpty
    private String bankAccount;
    @NotEmpty
    private String bankCard;
}
