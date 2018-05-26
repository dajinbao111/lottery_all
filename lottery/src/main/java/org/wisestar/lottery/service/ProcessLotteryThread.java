package org.wisestar.lottery.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wisestar.lottery.config.RabbitMQConfig;
import org.wisestar.lottery.entity.*;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.mapper.BetRecordMapper;
import org.wisestar.lottery.mapper.FourteenGamesResultMapper;
import org.wisestar.lottery.mapper.GameInfoMapper;
import org.wisestar.lottery.util.MathUtils;

import java.util.List;

/**
 * 处理中奖情况
 *
 * @author zhangxu
 * @date 2017/11/27
 */
public class ProcessLotteryThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ProcessLotteryThread.class);
    private final GameInfoMapper gameInfoMapper;
    private final FourteenGamesResultMapper fourteenGamesResultMapper;
    private final BetRecordMapper betRecordMapper;
    private final AmqpService amqpService;
    private BetRecord betRecord;

    public ProcessLotteryThread(GameInfoMapper gameInfoMapper,
                                FourteenGamesResultMapper fourteenGamesResultMapper,
                                BetRecordMapper betRecordMapper,
                                AmqpService amqpService,
                                BetRecord betRecord) {
        this.gameInfoMapper = gameInfoMapper;
        this.fourteenGamesResultMapper = fourteenGamesResultMapper;
        this.betRecordMapper = betRecordMapper;
        this.amqpService = amqpService;
        this.betRecord = betRecord;
    }


    @Override
    public void run() {
        long s = System.currentTimeMillis();

        //订单修改成功数
        int count;
        //中奖标识
        boolean winFlag = false;
        //中奖金额
        Double winPrize = 0.0;

        if (betRecord.getLotteryType().equals(LotteryTypeEnum.FOURTEEN.getValue()) ||
                betRecord.getLotteryType().equals(LotteryTypeEnum.NINE.getValue())) {

            //example:17175-3,3,*,*,3,3,3,*,*,3,*,0,3,0
            String[] betInfo = StringUtils.splitPreserveAllTokens(betRecord.getBetDetail(), "-");
            if (betInfo.length != 2) {
                throw new ServiceException("14或9投注数据解析失败");
            }
            // phaseId = betInfo[0]
            FourteenGamesResult fourteenGamesResult = fourteenGamesResultMapper.getResultByPhaseId(betInfo[0]);

            if (fourteenGamesResult != null) {
                String[] resultArr = StringUtils.splitPreserveAllTokens(fourteenGamesResult.getResult(), ",");
                String[] betArr = StringUtils.splitPreserveAllTokens(betInfo[1], ",");

                int right = 0;
                for (int i = 0; i < 14; i++) {
                    if (StringUtils.contains(betArr[i], resultArr[i])) {
                        right++;
                    }
                }

                //14场全对一等奖，13场正确二等奖，否则未中奖
                boolean win14_1 = betRecord.getLotteryType().equals(LotteryTypeEnum.FOURTEEN.getValue()) && right == 14;
                boolean win14_2 = betRecord.getLotteryType().equals(LotteryTypeEnum.FOURTEEN.getValue()) && right == 13;
                boolean win9 = betRecord.getLotteryType().equals(LotteryTypeEnum.NINE.getValue()) && right == 9;

                if (win14_1) {
                    winPrize = Double.parseDouble(fourteenGamesResult.getPrize1());
                    winFlag = true;
                } else if (win14_2) {
                    winPrize = Double.parseDouble(fourteenGamesResult.getPrize2());
                    winFlag = true;
                } else if (win9) {
                    winPrize = Double.parseDouble(fourteenGamesResult.getRen9());
                    winFlag = true;
                }

            }
        } else if (betRecord.getLotteryType().equals(LotteryTypeEnum.FOOTBALL.getValue()) ||
                betRecord.getLotteryType().equals(LotteryTypeEnum.SINGLE.getValue())) {

            //过关方式数组
            String[] passTypeArr = StringUtils.splitPreserveAllTokens(betRecord.getPassType(), ",");
            //下注明细数组
            String[] dataList = StringUtils.splitPreserveAllTokens(betRecord.getBetDetail(), ",");

            //是否进行下一步处理中奖情况
            boolean nextStep = true;

            //先判断所选的场次是否都有比赛结果
            for (String betInfo : dataList) {
                String gameId = betInfo.substring(0, betInfo.indexOf("-"));

                GameInfo gameInfo = gameInfoMapper.getResultByCache(gameId);
                if (gameInfo == null) {
                    nextStep = false;
                    break;
                }
            }

            //所选场次都已经有了结果，继续判断中奖情况和奖金
            if (nextStep) {
                //总奖金
                double sumPrize = 0.0;

                //单关
                if (betRecord.getLotteryType().equals(LotteryTypeEnum.SINGLE.getValue())) {
                    //单关计算中奖情况
                    for (String oneGame : dataList) {
                        double onePiecePrize = 2.0;
                        String[] oneGameArr = StringUtils.splitPreserveAllTokens(oneGame, "-");
                        if (oneGameArr.length != 4) {
                            throw new ServiceException("单关投注数据解析失败");
                        }
                        GameInfo gameInfo = gameInfoMapper.getResultByCache(oneGameArr[0]);

                        if (gameInfo == null) {
                            throw new ServiceException("缓存数据异常");
                        }
                        boolean win = gameInfo.getHadResult().equals(oneGameArr[2]);
                        double ratio = NumberUtils.toDouble(oneGameArr[3], 0.0);
                        if (win) {
                            onePiecePrize *= ratio;
                        } else {
                            onePiecePrize = 0.0;
                        }
                        //每注奖金累加
                        sumPrize += onePiecePrize * betRecord.getBetTimes();
                    }
                } else {
                    //根据投注的过关方式，排列组合
                    for (String passType : passTypeArr) {
                        List<String[]> betList = MathUtils.combinationSelect(dataList, PassTypeEnum.getValue(passType));

                        for (String[] onePiece : betList) {
                            //一注的奖金
                            double onePiecePrize = 2.0;

                            for (String oneGame : onePiece) {
                                String[] oneGameArr = StringUtils.splitPreserveAllTokens(oneGame, "-");
                                // gameId = oneGameArr[0],rangqiu = oneGameArr[1],bet = oneGameArr[2],ratio = oneGameArr[3]
                                if (oneGameArr.length != 4) {
                                    throw new ServiceException("竞彩投注数据解析失败");
                                }
                                GameInfo gameInfo = gameInfoMapper.getResultByCache(oneGameArr[0]);

                                if (gameInfo == null) {
                                    throw new ServiceException("缓存数据异常");
                                }

                                //0表示不让球，1标识让球
                                boolean win = ("0".equals(oneGameArr[1]) && gameInfo.getHadResult().equals(oneGameArr[2])) ||
                                        ("1".equals(oneGameArr[1]) && gameInfo.getHhadResult().equals(oneGameArr[2]));

                                double ratio = NumberUtils.toDouble(oneGameArr[3], 0.0);

                                if (win) {
                                    onePiecePrize *= ratio;
                                } else {
                                    onePiecePrize = 0.0;
                                    break;
                                }
                            }

                            //每注奖金累加
                            sumPrize += onePiecePrize * betRecord.getBetTimes();
                        }
                    }
                }

                if (sumPrize > 0) {
                    winPrize = sumPrize;
                    winFlag = true;
                }
            }
        }


        if (winFlag) {
            count = betRecordMapper.updateState2WinPrize(betRecord.getBetNo(), winPrize);
            if (count != 1) {
                throw new ServiceException("修改订单状态失败");
            } else {
                //中奖数据放入兑奖队列
                amqpService.send(RabbitMQConfig.QUEUE_WIN_PRIZE, betRecord);
            }
        } else {
            count = betRecordMapper.updateState2NotWin(betRecord.getBetNo());
            if (count != 1) {
                throw new ServiceException("修改订单状态失败");
            }
        }
        logger.info("处理竞彩中奖情况单条耗时:{}s", (System.currentTimeMillis() - s) / 1000);
    }

}
