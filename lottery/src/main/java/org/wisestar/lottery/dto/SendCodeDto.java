package org.wisestar.lottery.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author zhangxu
 * @date 2017/10/23
 */
@Data
public class SendCodeDto {

    @NotEmpty
    private String openId;
    @Pattern(regexp = "[1][3|4|5|7|8|9][0-9]{9}", message = "不正确的手机号码")
    private String phoneNum;
}
