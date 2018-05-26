package org.wisestar.lottery.entity;

import lombok.Data;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.util.DateUtils;
import org.wisestar.lottery.dto.GameInfoDto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.ParseException;
import java.util.Date;

/**
 * @author zhangxu
 */
@Data
@Table(name = "gameInfo")
public class GameInfo implements Comparable<GameInfo> {

    @Id
    private Long id;
    @Column(name = "gameId")
    private String gameId;
    @Column(name = "hostTeam")
    private String hostTeam;
    @Column(name = "guestTeam")
    private String guestTeam;
    @Column(name = "gameEventType")
    private String gameEventType;
    @Column(name = "dueTime")
    private Date dueTime;
    @Column(name = "weekday")
    private String weekday;
    @Column(name = "gameDate")
    private String gameDate;
    @Column(name = "point")
    private String point;
    @Column(name = "rangqiu")
    private String rangqiu;
    @Column(name = "hadResult")
    private String hadResult;
    @Column(name = "hhadResult")
    private String hhadResult;
    @Column(name = "lastUpdated")
    private Date lastUpdated;

    public GameInfoDto copyTo() {
        GameInfoDto gameInfoDto = new GameInfoDto();
        BeanUtils.copyProperties(this, gameInfoDto);
        return gameInfoDto;
    }

    @Override
    public int compareTo(GameInfo o) {
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
