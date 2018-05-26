package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangxu
 * @date 2017/11/15
 */
@Data
public class BetDto {

    private Long betId;
    private String betNo;
    private String betTime;
    private Integer lotteryType;
    private String lotteryTypeText;
    private Integer betState;
    private String betStateText;
    private Date dutTime;
    private String passType;
    private String bonus;
    private String betAmount;
    private String winAmount;
    private String nickname;
    private Long piece;
    private Long times;
    private String phaseId;
    private List<BetDetailDto> detailList = new ArrayList<>();
}
