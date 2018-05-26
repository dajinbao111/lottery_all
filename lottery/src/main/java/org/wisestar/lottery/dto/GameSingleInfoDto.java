package org.wisestar.lottery.dto;

import lombok.Data;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.util.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/10/26
 */
@Data
public class GameSingleInfoDto implements Comparable<GameSingleInfoDto> {

    private Long id;
    private String gameId;
    private String hostTeam;
    private String guestTeam;
    private String gameEventType;
    private Date dueTime;
    private String weekday;
    private String gameDate;
    private Date lastUpdated;
    private String rangqiu;
    private String winRatio;
    private String drawRatio;
    private String loseRatio;

    @Override
    public int compareTo(GameSingleInfoDto o) {
        Date o1Date;
        Date o2Date;
        try {
            o1Date = DateUtils.string2Date(this.gameDate, "yyyy-MM-dd");
            o2Date = DateUtils.string2Date(o.gameDate, "yyyy-MM-dd");
        } catch (ParseException e) {
            throw new ServiceException("gameDate format error");
        }

        if (o1Date.after(o2Date)) {
            return 1;
        } else if (o1Date.before(o2Date)) {
            return -1;
        } else {
            Integer o1Week = Integer.valueOf(DateUtils.week2Num(this.getWeekday()));
            Integer o2Week = Integer.valueOf(DateUtils.week2Num(o.getWeekday()));

            if (o1Week > o2Week) {
                return 1;
            } else if (o1Week < o2Week) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
