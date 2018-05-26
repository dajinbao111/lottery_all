package org.wisestar.lottery.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zhangxu
 */
@Table(name = "digitalGeneralInfo")
public class DigitalGeneralInfo {

    @Id
    private Long id;
    @Column(name = "phaseId")
    private String phaseId;
    @Column(name = "typeId")
    private Integer typeId;
    @Column(name = "lastRoundResult")
    private String lastRoundResult;
    @Column(name = "totalPoolBalance")
    private String totalPoolBalance;
    @Column(name = "dueTime")
    private String dueTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getLastRoundResult() {
        return lastRoundResult;
    }

    public void setLastRoundResult(String lastRoundResult) {
        this.lastRoundResult = lastRoundResult;
    }

    public String getTotalPoolBalance() {
        return totalPoolBalance;
    }

    public void setTotalPoolBalance(String totalPoolBalance) {
        this.totalPoolBalance = totalPoolBalance;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }
}
