package org.wisestar.lottery.entity;

import lombok.Data;
import org.wisestar.lottery.dto.UserAccountRecordDto;
import org.wisestar.lottery.util.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
@Data
@Table(name = "userAccountRecord")
public class UserAccountRecord {

    @Id
    private Long id;
    @Column(name = "openId")
    private String openId;
    @Column(name = "accountChange")
    private Integer accountChange;
    @Column(name = "balance")
    private Double balance;
    @Column(name = "changeAmount")
    private Double changeAmount;
    @Column(name = "recordTime")
    private Date recordTime;
    @Column(name = "approval")
    private Integer approval;

    public UserAccountRecordDto copyTo() {
        UserAccountRecordDto userAccountRecordDto = new UserAccountRecordDto();
        BeanUtils.copyProperties(this, userAccountRecordDto);
        return userAccountRecordDto;
    }
}
