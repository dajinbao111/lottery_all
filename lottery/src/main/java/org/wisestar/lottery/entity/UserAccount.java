package org.wisestar.lottery.entity;

import lombok.Data;
import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.dto.UserAccountDto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户账户
 *
 * @author zhangxu
 * @date 2017/11/2
 */
@Data
@Table(name = "userAccount")
public class UserAccount {

    @Id
    private Long id;
    @Column(name = "openId")
    private String openId;
    @Column(name = "balance")
    private Double balance;
    @Column(name = "alipay")
    private String alipay;
    @Column(name = "bankName")
    private String bankName;
    @Column(name = "bankAccount")
    private String bankAccount;
    @Column(name = "bankCard")
    private String bankCard;
    @Column(name = "authCode")
    private String authCode;
    @Column(name = "authCodeExpires")
    private Date authCodeExpires;

    public UserAccountDto copyTo() {
        UserAccountDto userAccountDto = new UserAccountDto();
        BeanUtils.copyProperties(this, userAccountDto);
        return userAccountDto;
    }
}
