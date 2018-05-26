package org.wisestar.lottery.exception;

/**
 * 服务层通用异常
 * Created by zhangxu on 2017/7/15.
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
