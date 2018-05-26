package org.wisestar.lottery.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.wisestar.lottery.service.BetService;
import org.wisestar.lottery.service.FootballService;

import java.util.List;

/**
 * @author zhangxu
 * @date 2017/7/26
 */
@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final FootballService footballService;
    private final BetService betService;

    @Autowired
    public ScheduledTasks(FootballService footballService,
                          BetService betService) {
        this.footballService = footballService;
        this.betService = betService;
    }

    /**
     * 过关数据抓取
     */
    @Scheduled(cron = "${cron.crawlGameInfo}")
    public void crawlGameInfo() {
        logger.info("crawlGameInfo...");
        footballService.crawlGameInfo();
    }

    /**
     * 14场数据抓取
     */
    @Scheduled(cron = "${cron.crawlFourteenGames}")
    public void crawlFourteenGames() {
        logger.info("crawlPhaseIds...");
        List<String> list = footballService.crawlPhaseIds();
        logger.info("crawlFourteenGames...", list);
        footballService.crawlFourteenGames(list);
    }

    /**
     * 过关彩果抓取
     */
    @Scheduled(cron = "${cron.crawlGameInfoResult}")
    public void crawlGameInfoResult() {
        logger.info("crawlGameInfo...");
        footballService.crawlGameInfoResult();
    }

    /**
     * 14场彩果抓取
     */
    @Scheduled(cron = "${cron.crawlFourteenGamesResult}")
    public void crawlFourteenGamesResult() {
        logger.info("crawlGameInfo...");
        footballService.crawlFourteenGamesResult();
    }

//    @Scheduled(cron = "0 * */1 * * ?")
//    public void task3() {
//        logger.info("getDigitalGeneralInfo...");
//        digitalGeneralInfoService.getDigitalGeneralInfo();
//    }

    /**
     * 处理超时未付款
     */
    @Scheduled(fixedDelay = 60000)
    public void modifyBetStateToNotPay() {
        betService.modifyBetStateToNotPay();
    }

}
