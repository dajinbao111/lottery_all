package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangxu
 * @date 2017/10/26
 */
@Data
public class GameInfoDto {

    private Long id;
    private String gameId;
    private String hostTeam;
    private String guestTeam;
    private String gameEventType;
    private Date dueTime;
    private String weekday;
    private String gameDate;
    private Date lastUpdated;
    private List<GameRatioDto> ratioList = new ArrayList<>();

}
