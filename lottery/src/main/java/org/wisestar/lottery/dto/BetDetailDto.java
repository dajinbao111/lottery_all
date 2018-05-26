package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangxu
 * @date 2017/11/15
 */
@Data
public class BetDetailDto {

    private String gameId;
    private String weekday;
    private String hostTeam;
    private String guestTeam;
    private String point;
    private String rangqiu;
    private String hadResult;
    private String hhadResult;
    private Integer serialId;
    private String result;
    private String bet;

    private List<BetDetailRatioDto> ratioList = new ArrayList<>();
}
