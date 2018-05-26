package org.wisestar.lottery.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.wisestar.lottery.auth.JwtTokenUtil;
import org.wisestar.lottery.dto.*;
import org.wisestar.lottery.exception.ErrorText;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.service.BetService;
import org.wisestar.lottery.service.UserService;
import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.util.ValidatorUtils;
import org.wisestar.lottery.util.sms.SmsClient;
import org.wisestar.lottery.util.sms.template.AuthCode;

import java.util.Date;
import java.util.List;

/**
 * @author zhangxu
 * @date 2017/10/20
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String RS_KEY_OPENID = "openid";
    private static final String RS_KEY_SESSION_KEY = "session_key";
    private RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('0', '9').build();

    @Value("${wechat.appid}")
    private String appid;
    @Value("${wechat.secret}")
    private String secret;
    @Value("${qiniu.domain}")
    private String domain;

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final BetService betService;
    private final ResourceLoader resourceLoader;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthController(UserService userService,
                          JwtTokenUtil jwtTokenUtil,
                          UserDetailsService userDetailsService,
                          BetService betService,
                          ResourceLoader resourceLoader,
                          RestTemplate restTemplate) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.betService = betService;
        this.resourceLoader = resourceLoader;
        this.restTemplate = restTemplate;
    }

    /**
     * 获取openid
     *
     * @param code
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody String code) {
        logger.debug("获取openid, 请求参数: {}", code);
        StringBuilder authUrl = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session");

        authUrl.append(String.format("?appid=%s", appid));
        authUrl.append(String.format("&secret=%s", secret));
        authUrl.append(String.format("&js_code=%s", code));
        authUrl.append("&grant_type=authorization_code");

        String response = restTemplate.getForObject(authUrl.toString(), String.class);
        logger.debug("获取openid, 响应内容: {}", response);

        JSONObject result = JSON.parseObject(response);
        if (result.containsKey(RS_KEY_OPENID)) {
            String openId = result.getString(RS_KEY_OPENID);
            String sessionKey = result.getString(RS_KEY_SESSION_KEY);
            userService.saveUserSessionKey(openId, sessionKey);

            UserDetails userDetails = userDetailsService.loadUserByUsername(openId);
            String jwtToken = jwtTokenUtil.generateToken(userDetails);

            LoginDto loginDto = new LoginDto();
            loginDto.setOpenId(openId);
            loginDto.setJwtToken(jwtToken);
            //过期时间
            loginDto.setExpiration(jwtTokenUtil.getExpirationDateFromToken(jwtToken));
            //权限
            String authorityStr = jwtTokenUtil.getAuthorityFromToken(jwtToken);
            loginDto.setAuthorities(StringUtils.split(authorityStr, ","));
            return ResponseEntity.ok(loginDto);
        } else {
            return ResponseEntity.badRequest().body(result.getString("errmsg"));
        }
    }

    /**
     * 获取短信验证码
     *
     * @param sendCodeDto
     * @return
     */
    @PostMapping("/sendCode")
    public ResponseEntity<?> sendCode(@RequestBody SendCodeDto sendCodeDto) {
        ValidatorUtils.validateEntity(sendCodeDto);
        logger.debug("发送短信验证码, 请求参数: {}", sendCodeDto);

        UserDto userDto = userService.getUserInfo(sendCodeDto.getOpenId());
        if (userDto == null) {
            throw new ServiceException(ErrorText.ERROR_OPERATION);
        }

        String code = generator.generate(6);

        userDto.setAuthCode(code);
        userDto.setPhoneNum(sendCodeDto.getPhoneNum());
        //验证码有效时间3分钟
        userDto.setAuthCodeExpires(DateUtils.addMinutes(new Date(), 3));
        userService.updateUserInfo(userDto);

        AuthCode authCode = new AuthCode();
        authCode.setCode(code);
        SmsClient.getInstance().sendSms(sendCodeDto.getPhoneNum(), authCode.getTemplateCode(), authCode.toString());

        return ResponseEntity.ok("");
    }

    /**
     * 验证
     *
     * @param verifyCodeDto
     * @return
     */
    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeDto verifyCodeDto) {
        ValidatorUtils.validateEntity(verifyCodeDto);
        logger.debug("验证短信验证码, 请求参数: {}", verifyCodeDto);

        UserDto userDto = userService.getUserInfo(verifyCodeDto.getOpenId());
        if (userDto == null || StringUtils.isEmpty(userDto.getAuthCode()) || userDto.getAuthCodeExpires() == null) {
            throw new ServiceException(ErrorText.ERROR_OPERATION);
        }
        if (!userDto.getPhoneNum().equals(verifyCodeDto.getPhoneNum())) {
            throw new ServiceException(ErrorText.ERROR_USER_PHONE);
        }
        if (!userDto.getAuthCode().equals(verifyCodeDto.getAuthCode())) {
            throw new ServiceException(ErrorText.ERROR_AUTHCODE);
        }
        if (System.currentTimeMillis() > userDto.getAuthCodeExpires().getTime()) {
            throw new ServiceException(ErrorText.EXPIRE_AUTHCODE);
        }
        BeanUtils.copyProperties(verifyCodeDto, userDto);
        userService.updateUserInfo(userDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh() {
        String token = getToken();
        String openId = jwtTokenUtil.getUsernameFromToken(token);
        if (openId != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(openId);
            String refreshToken = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(refreshToken);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 中奖公告
     *
     * @return
     */
    @GetMapping("/notice")
    public ResponseEntity<?> notice() {
        List<NoticeDTO> noticeList = betService.listWinNotice();
        return ResponseEntity.ok(noticeList);
    }

    /**
     * 票样
     *
     * @param betNo
     * @return
     */
    @GetMapping("/ticket/{betNo}")
    public ResponseEntity<?> ticket(@PathVariable String betNo) {
        try {
            Resource resource = resourceLoader.getResource(String.format("url:http://%s/%s", domain, betNo));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
