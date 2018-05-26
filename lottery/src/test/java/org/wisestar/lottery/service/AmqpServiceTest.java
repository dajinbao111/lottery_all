package org.wisestar.lottery.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wisestar.lottery.config.RabbitMQConfig;
import org.wisestar.lottery.entity.BetRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmqpServiceTest {
    @Autowired
    private AmqpService amqpService;

    @Test
    public void size() throws Exception {
        Map<String, Long> map = amqpService.size();
        System.out.println(map);
    }

    @Test
    public void send() throws Exception {
        for (int i = 0; i < 10; i++) {
//            BetRecord betRecord = new BetRecord();
//            betRecord.setBetNo(UUID.randomUUID().toString().replaceAll("-", ""));
//            amqpService.send(RabbitMQConfig.QUEUE_PENDING_TICKET, betRecord);

            BetRecord betRecord = new BetRecord();
            betRecord.setBetNo("AA201711291742000076");
            betRecord.setBetAmount(20D);
            betRecord.setBetDetail("101461-0-3-3.50,101462-0-3-3.20");
            betRecord.setBetPiece(1L);
            betRecord.setBetTime(new Date());
            betRecord.setBetTimes(10L);
            betRecord.setBetState(7);
            betRecord.setLotteryType(7);
            betRecord.setOpenId("oBUwh0TM-0blcVL72dvYmn2WdH6w");
            betRecord.setId(112L);
            betRecord.setPassType("2串1");
            betRecord.setWinAmount(118.0);
            betRecord.setUpdateTime(new Date());
            amqpService.send(RabbitMQConfig.QUEUE_WIN_PRIZE, betRecord);
        }

    }

    @Test
    public void receive() throws Exception {
        List<BetRecord> list = amqpService.receive(RabbitMQConfig.QUEUE_PENDING_TICKET, 4);
        for (BetRecord betRecord : list) {
            System.out.println(betRecord.getBetNo());
        }

    }

}