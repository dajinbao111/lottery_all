package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangxu
 * @date 2017/11/7
 */
@Data
public class BetRecordPage {

    private int pageNum;
    private int pages;
    private List<BetDto> list = new ArrayList<>();
}
