package org.wisestar.lottery.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangxu
 * @date 2017/11/10
 */
@Configuration
public class RabbitMQConfig {
    /**
     * 待出票队列
     */
    public final static String QUEUE_PENDING_TICKET = "pending-ticket-queue";
    /**
     * 待兑奖队列
     */
    public final static String QUEUE_WIN_PRIZE = "win-prize-queue";

    /**
     * 初始化队列
     *
     * @return
     */
    @Bean
    public Queue queuePendingTicket() {
        return new Queue(QUEUE_PENDING_TICKET);
    }

    @Bean
    public Queue queueWinPrize() {
        return new Queue(QUEUE_WIN_PRIZE);
    }

}
