package org.wisestar.lottery.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/10/27
 */
@Data
@Table(name = "fourteenGamesResult")
public class FourteenGamesResult {

    @Id
    private Long id;
    @Column(name = "phaseId")
    private String phaseId;
    @Column(name = "result")
    private String result;
    @Column(name = "prizeTime")
    private Date prizeTime;
    @Column(name = "prize1")
    private String prize1;
    @Column(name = "prize2")
    private String prize2;
    @Column(name = "ren9")
    private String ren9;
}
