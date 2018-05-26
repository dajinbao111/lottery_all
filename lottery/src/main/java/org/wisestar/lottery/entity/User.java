package org.wisestar.lottery.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/10/23
 */
@Data
@Table(name = "users")
public class User {

    @Id
    private Long id;
    @Column(name = "phoneNum")
    private String phoneNum;
    @Column(name = "openId")
    private String openId;
    @Column(name = "sessionKey")
    private String sessionKey;
    @Column(name = "unionId")
    private String unionId;
    @Column(name = "nickName")
    private String nickName;
    @Column(name = "avatarUrl")
    private String avatarUrl;
    @Column(name = "authority")
    private String authority;
    @Column(name = "createDate")
    private Date createDate;
    @Column(name = "authCode")
    private String authCode;
    @Column(name = "authCodeExpires")
    private Date authCodeExpires;

}
