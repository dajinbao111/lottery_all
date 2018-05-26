package org.wisestar.lottery.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wisestar.lottery.auth.JwtTokenUtil;
import org.wisestar.lottery.dto.BetRecordDto;
import org.wisestar.lottery.dto.ConfirmBetDto;
import org.wisestar.lottery.dto.ConfirmPayDto;
import org.wisestar.lottery.dto.UserAccountDto;
import org.wisestar.lottery.entity.BetStateEnum;
import org.wisestar.lottery.entity.LotteryTypeEnum;
import org.wisestar.lottery.exception.ErrorText;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.service.BetService;
import org.wisestar.lottery.service.PayService;
import org.wisestar.lottery.service.UserService;
import org.wisestar.lottery.util.ValidatorUtils;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
@RestController
@RequestMapping("/bet")
public class BetController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(BetController.class);
    private final BetService betService;
    private final UserService userService;
    private final PayService payService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public BetController(BetService betService,
                         UserService userService,
                         PayService payService,
                         JwtTokenUtil jwtTokenUtil) {
        this.betService = betService;
        this.userService = userService;
        this.payService = payService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * 投注明细
     */
    @PostMapping("/detail/{betNo}")
    public ResponseEntity<?> detail(@PathVariable String betNo) {
        if (StringUtils.isEmpty(betNo)) {
            throw new ServiceException(ErrorText.ERROR_BET_NO);
        }
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
        BetRecordDto betRecordDto = betService.getBetRecord(betNo);
        if (!openId.equals(betRecordDto.getOpenId())) {
            throw new ServiceException(ErrorText.ERROR_USER_BET);
        }

        return null;
    }

    /**
     * 确认下注
     *
     * @param confirmBetDto 下注内容
     * @return 下注订单编号
     */
    @PostMapping("/confirmBet")
    public ResponseEntity<?> confirmBet(@RequestBody ConfirmBetDto confirmBetDto) {
        ValidatorUtils.validateEntity(confirmBetDto);
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
        //2*注数*倍数=下注金额
        Double calAmount = 2.0 * confirmBetDto.getBetPiece() * confirmBetDto.getBetTimes();
        if (!calAmount.equals(confirmBetDto.getBetAmount())) {
            throw new ServiceException(ErrorText.ERROR_BET_AMOUNT);
        }
        confirmBetDto.setOpenId(openId);
        String betNo = betService.insertBet(confirmBetDto);
        logger.debug("发送给客户端的订单号:{}", betNo);
        return ResponseEntity.ok(betNo);
    }

    /**
     * 确认支付
     *
     * @param betNo 下注订单编号
     * @return 用户余额，所购彩种类型期号，已经要支付金额
     */
    @PostMapping("/confirmPay")
    public ResponseEntity<?> confirmPay(@RequestBody String betNo) {
        logger.debug("确认支付页, 请求参数: {}", betNo);
        if (StringUtils.isEmpty(betNo)) {
            throw new ServiceException(ErrorText.ERROR_BET_NO);
        }
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
        String nickname = jwtTokenUtil.getNicknameFromToken(getToken());

        BetRecordDto betRecordDto = betService.getBetRecord(betNo);
        if (!betRecordDto.getBetState().equals(BetStateEnum.PENDING_PAY.getValue())) {
            throw new ServiceException(ErrorText.ERROR_BET_STATE);
        }

        UserAccountDto userAccountDto = userService.getUserAccount(openId);

        ConfirmPayDto confirmPayDto = new ConfirmPayDto();
        confirmPayDto.setBalance(userAccountDto.getBalance());

        //十四场或者任九
        if (betRecordDto.getLotteryType().equals(5) || betRecordDto.getLotteryType().equals(6)) {
            String phaseId = StringUtils.substring(betRecordDto.getBetDetail(), 0, betRecordDto.getBetDetail().indexOf("-"));
            confirmPayDto.setLottery(String.format("%s第%s期", LotteryTypeEnum.getText(betRecordDto.getLotteryType()), phaseId));
        }
        //竞彩或者单关
        if (betRecordDto.getLotteryType().equals(7) || betRecordDto.getLotteryType().equals(8)) {
            confirmPayDto.setLottery(LotteryTypeEnum.getText(betRecordDto.getLotteryType()));
        }

        confirmPayDto.setNickName(nickname);
        confirmPayDto.setPayAmount(betRecordDto.getBetAmount());

        return ResponseEntity.ok(confirmPayDto);
    }

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody String betNo) {
        logger.info("确认支付，订单号:{}", betNo);
        String openId = jwtTokenUtil.getUsernameFromToken(getToken());
        if (StringUtils.isEmpty(openId)) {
            throw new ServiceException(ErrorText.ERROR_OPENID);
        }
        Double amount = payService.pay(betNo);
        return ResponseEntity.ok(amount);
    }
}
