package org.wisestar.lottery.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * @author zhangxu
 * @date 2017/11/3
 */
@Data
public class ConfirmBetDto {

    private String openId;
    @Range(min = 1, max = 8)
    private Integer lotteryType;
    @Min(2)
    private Double betAmount;
    private String bonus;
    @NotEmpty
    private String betDetail;
    private String betExtra;
    @Min(1)
    private Long betTimes;
    @Min(1)
    private Long betPiece;
    @NotEmpty
    private String passType;
}
