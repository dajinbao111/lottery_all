package org.wisestar.lottery.exception.handle;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.util.MailReporter;


/**
 * Created by zhangxu on 2017/7/10.
 */
@ControllerAdvice
public class ExceptionHandle {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @Autowired
    private MailReporter mailReporter;

    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handle(ServiceException e) {
        logger.error(e.getMessage(), e);
        mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<?> handle(Exception e) {
        logger.error(e.getMessage(), e);
        mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
