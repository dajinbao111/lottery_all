package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/10/23
 */
@Data
public class LoginDto {

    private String openId;
    private String jwtToken;
    private Date expiration;
    private String[] authorities;

}
