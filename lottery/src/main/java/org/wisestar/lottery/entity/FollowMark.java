package org.wisestar.lottery.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/11/14
 */
@Data
@Table(name = "followMark")
public class FollowMark {

    @Id
    private Long id;
    @Column(name = "lotteryType")
    private Integer lotteryType;    //彩种
    @Column(name = "together")
    private Integer together;       //合买
    @Column(name = "dutTime")
    private Date dutTime;           //截止时间，以最近一场的时间为准
    @Column(name = "passType")
    private String passType;    //过关类型
    @Column(name = "amount")
    private Double amount;      //下注金额
    @Column(name = "piece")
    private Long piece;         //下注注数或者份数
    @Column(name = "times")
    private Long times;         //下注倍数
    @Column(name = "detail")
    private String detail;      //推荐明细
    @Column(name = "createdDate")
    private Date createdDate;
}
