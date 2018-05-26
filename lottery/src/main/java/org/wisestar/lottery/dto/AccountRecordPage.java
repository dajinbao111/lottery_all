package org.wisestar.lottery.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/11/17
 */
@Data
public class AccountRecordPage {

    private int pageNum;
    private int pages;
    private List<Map<String, Object>> list = new ArrayList<>();

}
