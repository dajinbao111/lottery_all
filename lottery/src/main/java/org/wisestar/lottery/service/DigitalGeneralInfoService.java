package org.wisestar.lottery.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wisestar.lottery.entity.DigitalGeneralInfo;
import org.wisestar.lottery.mapper.DigitalGeneralInfoMapper;
import org.wisestar.lottery.util.GroovyEngine;
import org.wisestar.lottery.util.MailReporter;

/**
 * @author zhangxu
 * @date 2017/10/19
 */
@Service
public class DigitalGeneralInfoService {
    private final DigitalGeneralInfoMapper digitalGeneralInfoMapper;
    private final MailReporter mailReporter;

    @Autowired
    public DigitalGeneralInfoService(DigitalGeneralInfoMapper digitalGeneralInfoMapper,
                                     MailReporter mailReporter) {
        this.digitalGeneralInfoMapper = digitalGeneralInfoMapper;
        this.mailReporter = mailReporter;
    }

    public void getDigitalGeneralInfo() {
        GroovyEngine engine = GroovyEngine.getInstance();
        try {
            String result = engine.executeScript("lottery_result", "get_data");
            JSONArray array = JSON.parseArray(result);

            for (int i = 0; i < array.size(); i++) {
                JSONObject info = array.getJSONObject(i);
                DigitalGeneralInfo digital = info.toJavaObject(DigitalGeneralInfo.class);

                DigitalGeneralInfo found = digitalGeneralInfoMapper.getByTypeIdAndPhaseId(digital.getTypeId(), digital.getPhaseId());
                if (found == null) {
                    digitalGeneralInfoMapper.insertSelective(digital);
                }
            }
        } catch (Exception e) {
            mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
        }
    }
}
