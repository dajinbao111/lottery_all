package org.wisestar.lottery.web;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.wisestar.lottery.auth.JwtTokenUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller基类
 *
 * @author zhangxu
 * @date 2017/11/2
 */
public class BaseController {
    /**
     * ThreadLocal确保高并发下每个请求的request，response都是独立的
     */
    private static ThreadLocal<ServletRequest> currentRequest = new ThreadLocal<>();
    private static ThreadLocal<ServletResponse> currentResponse = new ThreadLocal<>();

    @ModelAttribute
    protected void initReqAndResp(HttpServletRequest request, HttpServletResponse response) {
        currentRequest.set(request);
        currentResponse.set(response);
    }

    protected HttpServletRequest request() {
        return (HttpServletRequest) currentRequest.get();
    }

    protected HttpServletResponse response() {
        return (HttpServletResponse) currentResponse.get();
    }

    protected String getToken() {
        return request().getHeader(JwtTokenUtil.X_AUTHENTICATION_TOKEN);
    }
}
