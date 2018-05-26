package org.wisestar.lottery.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.wisestar.lottery.auth.JwtTokenUtil;
import org.wisestar.lottery.dto.*;
import org.wisestar.lottery.entity.BetStateEnum;
import org.wisestar.lottery.exception.ErrorText;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.service.BetService;
import org.wisestar.lottery.service.UserService;
import org.wisestar.lottery.util.BankCardInfo;
import org.wisestar.lottery.util.ValidatorUtils;

import java.util.Arrays;

/**
 * @author zhangxu
 * @date 2017/10/24
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final BetService betService;

    @Autowired
    public UserController(UserService userService,
                          JwtTokenUtil jwtTokenUtil,
                          BetService betService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.betService = betService;
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @PostMapping("/getInfo")
    public ResponseEntity<?> getUserInfo() {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (!StringUtils.isEmpty(openId)) {
            UserDto userDto = userService.getUserInfo(openId);
            return ResponseEntity.ok(userDto);
        } else {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
    }

    /**
     * 获取用户账户
     */
    @PostMapping("/getAccount")
    public ResponseEntity<?> getAccount() {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (!StringUtils.isEmpty(openId)) {
            UserAccountDto userAccountDto = userService.getUserAccount(openId);
            return ResponseEntity.ok(userAccountDto);
        } else {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
    }

    /**
     * 用户全部购彩记录
     *
     * @param page
     * @return
     */
    @PostMapping("/listRecordAll/{limit}/{page}")
    public ResponseEntity<?> listBetRecordAll(@PathVariable Integer page, @PathVariable Integer limit) {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (!StringUtils.isEmpty(openId)) {
            BetRecordPage betRecordPage = betService.listBetRecord(page, limit, openId, null);
            return ResponseEntity.ok(betRecordPage);
        } else {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
    }

    /**
     * 用户待开奖记录
     *
     * @param page
     * @return
     */
    @PostMapping("/listRecordPending/{limit}/{page}")
    public ResponseEntity<?> listBetRecordPending(@PathVariable Integer page, @PathVariable Integer limit) {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (!StringUtils.isEmpty(openId)) {
            Integer[] states = {BetStateEnum.PENDING_OPEN.getValue()};
            BetRecordPage betRecordPage = betService.listBetRecord(page, limit, openId, Arrays.asList(states));
            return ResponseEntity.ok(betRecordPage);
        } else {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
    }

    /**
     * 用户中奖记录
     *
     * @param page
     * @return
     */
    @PostMapping("/listRecordWin/{limit}/{page}")
    public ResponseEntity<?> listBetRecordWin(@PathVariable Integer page, @PathVariable Integer limit) {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (!StringUtils.isEmpty(openId)) {
            Integer[] states = {BetStateEnum.WIN_PRIZE.getValue()};
            BetRecordPage betRecordPage = betService.listBetRecord(page, limit, openId, Arrays.asList(states));
            return ResponseEntity.ok(betRecordPage);
        } else {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
    }

    /**
     * 用户不成功记录
     *
     * @param page
     * @return
     */
    @PostMapping("/listRecordFail/{limit}/{page}")
    public ResponseEntity<?> listBetRecordFail(@PathVariable Integer page, @PathVariable Integer limit) {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (!StringUtils.isEmpty(openId)) {
            Integer[] states = {BetStateEnum.NOT_PAY.getValue(), BetStateEnum.NOT_TICKENT.getValue()};
            BetRecordPage betRecordPage = betService.listBetRecord(page, limit, openId, Arrays.asList(states));
            return ResponseEntity.ok(betRecordPage);
        } else {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
    }

    /**
     * 查看订单详情
     * @param betNo
     * @return
     */
    @PostMapping("/viewRecord/{betNo}")
    public ResponseEntity<?> viewRecord(@PathVariable String betNo) {
        BetDto betDto = betService.getBetRecordDetail(betNo, true);
        return ResponseEntity.ok(betDto);
    }

    /**
     * 账号记录
     *
     * @param page
     * @param limit
     * @return
     */
    @PostMapping("/listAccountRecord/{limit}/{page}")
    public ResponseEntity<?> listAccountRecord(@PathVariable Integer page, @PathVariable Integer limit) {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
        AccountRecordPage accountRecordPage = userService.listAccountRecord(page, limit, openId, null);
        return ResponseEntity.ok(accountRecordPage);
    }

    /**
     * 添加银行卡
     *
     * @param addBankCardDto
     * @return
     */
    @PostMapping("/addBankCard")
    public ResponseEntity<?> addBankCard(@RequestBody AddBankCardDto addBankCardDto) {
        ValidatorUtils.validateEntity(addBankCardDto);
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
        addBankCardDto.setOpenId(openId);
        String bankName = BankCardInfo.getBankInfo(addBankCardDto.getBankCard());
        addBankCardDto.setBankName(bankName);
        userService.updateBankInfo(addBankCardDto);
        return ResponseEntity.ok("");
    }

    /**
     * 提现操作
     *
     * @param amount
     * @return
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Double amount) {
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
        if (amount == null || amount <= 0) {
            throw new ServiceException("提现的金额有误");
        }
        userService.withdraw(openId, amount);
        return ResponseEntity.ok("");
    }

}
