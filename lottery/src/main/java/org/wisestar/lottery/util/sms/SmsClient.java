package org.wisestar.lottery.util.sms;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisestar.lottery.exception.ServiceException;

/**
 * @author zhangxu
 * @date 2017/11/15
 */
public class SmsClient {
    private static final Logger logger = LoggerFactory.getLogger(SmsClient.class);

    private static final String SIGN_NAME = "关二爷串串";
    private static final String SERVER_URL = "http://gw.api.taobao.com/router/rest";
    private static final String APP_KEY = "24574366";
    private static final String APP_SECRET = "2cce3bfbe9cca1773989d80811ba015b";
    private TaobaoClient client;

    private static class SmsClientHolder {
        private static final SmsClient instance = new SmsClient();
    }

    public static SmsClient getInstance() {
        return SmsClientHolder.instance;
    }

    private SmsClient() {
        client = new DefaultTaobaoClient(SERVER_URL, APP_KEY, APP_SECRET);
    }

    public void sendSms(String phoneNumber, String templateCode, String templateParam) {
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType("normal");
        req.setSmsFreeSignName(SIGN_NAME);
        req.setSmsTemplateCode(templateCode);
        req.setRecNum(phoneNumber);
        req.setSmsParamString(templateParam);
        try {
            AlibabaAliqinFcSmsNumSendResponse rsp = client.execute(req);
            if (rsp.getErrorCode() != null) {
                throw new ServiceException(rsp.getBody());
            }
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
