package org.wisestar.lottery.util.sms.template;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author zhangxu
 * @date 2017/11/15
 */
@Data
public class AuthCode {

    private String templateCode = "SMS_81115021";
    private String code;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
