package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/11/17
 */
@Data
public class UserAccountRecordDto {

    private Long id;
    private String openId;
    private String accountChange;
    private Double balance;
    private Double changeAmount;
    private String recordTime;
    private Integer approval;
}
