package org.wisestar.lottery.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wisestar.lottery.config.RabbitMQConfig;
import org.wisestar.lottery.entity.*;
import org.wisestar.lottery.exception.ErrorText;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.mapper.BetRecordMapper;
import org.wisestar.lottery.mapper.UserAccountMapper;
import org.wisestar.lottery.mapper.UserAccountRecordMapper;

import java.util.Date;

/**
 * @author zhangxu
 * @date 2017/11/3
 */
@Service
public class PayService {

    private final UserAccountMapper userAccountMapper;
    private final UserAccountRecordMapper userAccountRecordMapper;
    private final BetRecordMapper betRecordMapper;
    private final AmqpService amqpService;

    @Autowired
    public PayService(UserAccountMapper userAccountMapper,
                      UserAccountRecordMapper userAccountRecordMapper,
                      BetRecordMapper betRecordMapper,
                      AmqpService amqpService) {
        this.userAccountMapper = userAccountMapper;
        this.userAccountRecordMapper = userAccountRecordMapper;
        this.betRecordMapper = betRecordMapper;
        this.amqpService = amqpService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Double pay(String betNo) {
        if (StringUtils.isEmpty(betNo)) {
            throw new ServiceException(ErrorText.ERROR_BET_NO);
        }
        BetRecord betRecord = betRecordMapper.getByBetNo(betNo);
        if (!betRecord.getBetState().equals(BetStateEnum.PENDING_PAY.getValue())) {
            throw new ServiceException(ErrorText.ERROR_BET_STATE);
        }

        Double payAmount = betRecord.getBetAmount();

        UserAccount userAccount = userAccountMapper.getByOpenId(betRecord.getOpenId());

        Double balance = userAccount.getBalance();

        //余额大于待支付的金额，扣除余额
        if (balance >= payAmount) {
            Double newBalance = balance - payAmount;

            //资金变动记录
            UserAccountRecord record = new UserAccountRecord();
            record.setOpenId(betRecord.getOpenId());
            record.setRecordTime(new Date());
            record.setAccountChange(AccountChangeEnum.PAY.getValue());
            record.setChangeAmount(-payAmount);

            record.setBalance(newBalance);
            //记录账户变动
            userAccountRecordMapper.insertSelective(record);
            //更新账户
            int count = userAccountMapper.updateBalance(betRecord.getOpenId(), newBalance);
            if (count != 1) {
                throw new ServiceException("更新账户余额失败");
            }
            //修改订单状态
            count = betRecordMapper.updateBetState(betNo, BetStateEnum.PENDING_TICKET.getValue());
            if (count != 1) {
                throw new ServiceException("修改订单状态失败");
            }
            //订单进入待出票队列
            amqpService.send(RabbitMQConfig.QUEUE_PENDING_TICKET, betRecordMapper.getByBetNo(betNo));
        } else {
            throw new ServiceException("余额不足");
        }

        return payAmount;
    }

}
