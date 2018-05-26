package org.wisestar.lottery.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * @author zhangxu
 * @date 2017/11/20
 */
@Data
public class RecommendDto {

    @Range(min = 1, max = 8)
    private Integer lotteryType;
    @Min(2)
    private Double betAmount;
    @NotEmpty
    private String betDetail;
    @Min(1)
    private Long betTimes;
    @Min(1)
    private Long betPiece;
    @NotEmpty
    private String passType;
}
