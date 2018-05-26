package org.wisestar.lottery.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "gameSingleRatio")
public class GameSingleRatio {

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

}
