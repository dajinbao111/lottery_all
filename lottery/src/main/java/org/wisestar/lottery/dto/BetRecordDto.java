package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/11/3
 */
@Data
public class BetRecordDto {

    private String betNo;
    private String openId;
    private Date betTime;
    private Integer lotteryType;
    private Integer betState;
    private Double betAmount;
    private Long betPiece;
    private Long betTimes;
    private String passType;
    private Double winAmount;
    private String betDetail;
}
