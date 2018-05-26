package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/10/26
 */
@Data
public class GameRatioDto {

    private String gameId;
    private String hostTeam;
    private String guestTeam;
    private String gameEventType;
    private Date dueTime;
    private String weekday;
    private String gameDate;
    private Date lastUpdated;
    /**
     * 不让球的胜平负率
     */
    private String hadRangqiu;
    private String hadWinRatio;
    private String hadDrawRatio;
    private String hadLoseRatio;
    /**
     * 让球的胜平负率
     */
    private String hhadRangqiu;
    private String hhadWinRatio;
    private String hhadDrawRatio;
    private String hhadLoseRatio;



}
