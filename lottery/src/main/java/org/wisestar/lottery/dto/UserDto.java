package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/10/23
 */
@Data
public class UserDto {

    private String openId;
    private String nickName;
    private String avatarUrl;
    private String phoneNum;
    private String authority;
    private String authCode;
    private Date authCodeExpires;
    private Double balance;

}
