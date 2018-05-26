package org.wisestar.lottery.entity;

import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.dto.GameRatioDto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhangxu
 */
@Table(name = "gameRatio")
public class GameRatio {

    @Id
    private Long id;
    @Column(name = "gameId")
    private String gameId;
    @Column(name = "rangqiu")
    private String rangqiu;
    @Column(name = "winRatio")
    private String winRatio;
    @Column(name = "drawRatio")
    private String drawRatio;
    @Column(name = "loseRatio")
    private String loseRatio;
    @Column(name = "lastUpdated")
    private Date lastUpdated;

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

    public String getRangqiu() {
        return rangqiu;
    }

    public void setRangqiu(String rangqiu) {
        this.rangqiu = rangqiu;
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

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public GameRatioDto copyTo() {
        GameRatioDto gameRatioDto = new GameRatioDto();
        BeanUtils.copyProperties(this, gameRatioDto);
        return gameRatioDto;
    }
}