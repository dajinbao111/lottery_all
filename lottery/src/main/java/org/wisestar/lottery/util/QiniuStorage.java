package org.wisestar.lottery.util;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wisestar.lottery.exception.ServiceException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhangxu
 * @date 2017/11/18
 */
@Component
public class QiniuStorage {

    @Value("${qiniu.domain}")
    private String domain;
    @Value("${qiniu.access-key}")
    private String accessKey;
    @Value("${qiniu.secret-key}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;

    public String upload(byte[] data, String path) {
        UploadManager uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
        String token = Auth.create(accessKey, secretKey).uploadToken(bucket);
        try {
            Response res = uploadManager.put(data, path, token);
            if (!res.isOK()) {
                throw new ServiceException("上传七牛出错：" + res.toString());
            }
        } catch (Exception e) {
            throw new ServiceException("上传文件失败，请核对七牛配置信息", e);
        }

        return domain + "/" + path;
    }

    public String upload(InputStream inputStream, String path) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return this.upload(data, path);
        } catch (IOException e) {
            throw new ServiceException("上传文件失败", e);
        }
    }

}
