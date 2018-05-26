package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/10/27
 */
@Data
public class FourteenGameDto {

    private String gameId;
    private String phaseId;
    private Integer serialId;
    private String gameType;
    private String startTime;
    private String hostTeam;
    private String guestTeam;
    private String winRatio;
    private String drawRatio;
    private String loseRatio;
    private String result;
    private String point;
    private Date startPostTime;
    private Date endPostTime;
}
