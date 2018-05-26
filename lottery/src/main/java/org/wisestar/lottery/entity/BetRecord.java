package org.wisestar.lottery.entity;

import lombok.Data;
import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.dto.BetRecordDto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 购彩记录
 * @author zhangxu
 * @date 2017/11/1
 */
@Data
@Table(name = "betRecord")
public class BetRecord {

    @Id
    private Long id;
    @Column(name = "betNo")
    private String betNo;
    @Column(name = "openId")
    private String openId;
    @Column(name = "betTime")
    private Date betTime;
    @Column(name = "lotteryType")
    private Integer lotteryType;
    @Column(name = "betState")
    private Integer betState;
    @Column(name = "betAmount")
    private Double betAmount;
    @Column(name = "betPiece")
    private Long betPiece;
    @Column(name = "betTimes")
    private Long betTimes;
    @Column(name = "passType")
    private String passType;
    @Column(name = "winAmount")
    private Double winAmount;
    @Column(name = "bonus")
    private String bonus;
    @Column(name = "betDetail")
    private String betDetail;
    @Column(name = "updateTime")
    private Date updateTime;

    public BetRecordDto copyTo() {
        BetRecordDto betRecordDto = new BetRecordDto();
        BeanUtils.copyProperties(this, betRecordDto);
        return betRecordDto;
    }
}
