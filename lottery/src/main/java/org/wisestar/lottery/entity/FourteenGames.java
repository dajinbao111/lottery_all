package org.wisestar.lottery.entity;

import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.dto.FourteenGameDto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhangxu
 */
@Table(name = "fourteenGames")
public class FourteenGames {

    @Id
    private Long id;
    @Column(name = "gameId")
    private String gameId;
    @Column(name = "phaseId")
    private String phaseId;
    @Column(name = "serialId")
    private Integer serialId;
    @Column(name = "gameType")
    private String gameType;
    @Column(name = "startTime")
    private String startTime;
    @Column(name = "hostTeam")
    private String hostTeam;
    @Column(name = "guestTeam")
    private String guestTeam;
    @Column(name = "winRatio")
    private String winRatio;
    @Column(name = "drawRatio")
    private String drawRatio;
    @Column(name = "loseRatio")
    private String loseRatio;
    @Column(name = "result")
    private String result;
    @Column(name = "point")
    private String point;
    @Column(name = "startPostTime")
    private Date startPostTime;
    @Column(name = "endPostTime")
    private Date endPostTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    public Integer getSerialId() {
        return serialId;
    }

    public void setSerialId(Integer serialId) {
        this.serialId = serialId;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getHostTeam() {
        return hostTeam;
    }

    public void setHostTeam(String hostTeam) {
        this.hostTeam = hostTeam;
    }

    public String getGuestTeam() {
        return guestTeam;
    }

    public void setGuestTeam(String guestTeam) {
        this.guestTeam = guestTeam;
    }

    public String getWinRatio() {
        return winRatio;
    }

    public void setWinRatio(String winRatio) {
        this.winRatio = winRatio;
    }

    public String getDrawRatio() {
        return drawRatio;
    }

    public void setDrawRatio(String drawRatio) {
        this.drawRatio = drawRatio;
    }

    public String getLoseRatio() {
        return loseRatio;
    }

    public void setLoseRatio(String loseRatio) {
        this.loseRatio = loseRatio;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public Date getStartPostTime() {
        return startPostTime;
    }

    public void setStartPostTime(Date startPostTime) {
        this.startPostTime = startPostTime;
    }

    public Date getEndPostTime() {
        return endPostTime;
    }

    public void setEndPostTime(Date endPostTime) {
        this.endPostTime = endPostTime;
    }

    public FourteenGameDto copyTo() {
        FourteenGameDto fourteenGameDto = new FourteenGameDto();
        BeanUtils.copyProperties(this, fourteenGameDto);
        return fourteenGameDto;
    }
}
