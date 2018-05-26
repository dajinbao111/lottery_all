package org.wisestar.lottery.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zhangxu
 * @date 2017/10/24
 */
@Data
public class VerifyCodeDto {

    @NotEmpty
    private String openId;
    private String nickName;
    private String avatarUrl;
    @Pattern(regexp = "[1][3|4|5|7|8][0-9]{9}")
    private String phoneNum;
    @Size(min = 4, max = 6)
    private String authCode;
}
