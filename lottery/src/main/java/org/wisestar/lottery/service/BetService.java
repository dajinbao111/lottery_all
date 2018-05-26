package org.wisestar.lottery.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wisestar.lottery.config.RabbitMQConfig;
import org.wisestar.lottery.dto.*;
import org.wisestar.lottery.entity.*;
import org.wisestar.lottery.exception.ErrorText;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.mapper.*;
import org.wisestar.lottery.util.DateUtils;

import java.util.*;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
@Service
public class BetService {
    private static final Logger logger = LoggerFactory.getLogger(BetService.class);

    private final BetRecordMapper betRecordMapper;
    private final UserMapper userMapper;
    private final GameInfoMapper gameInfoMapper;
    private final FourteenGamesMapper fourteenGamesMapper;
    private final UserAccountMapper userAccountMapper;
    private final UserAccountRecordMapper userAccountRecordMapper;
    private final AmqpService amqpService;

    @Autowired
    public BetService(BetRecordMapper betRecordMapper,
                      UserMapper userMapper,
                      GameInfoMapper gameInfoMapper,
                      FourteenGamesMapper fourteenGamesMapper,
                      UserAccountMapper userAccountMapper,
                      UserAccountRecordMapper userAccountRecordMapper,
                      AmqpService amqpService) {
        this.betRecordMapper = betRecordMapper;
        this.userMapper = userMapper;
        this.gameInfoMapper = gameInfoMapper;
        this.fourteenGamesMapper = fourteenGamesMapper;
        this.userAccountMapper = userAccountMapper;
        this.userAccountRecordMapper = userAccountRecordMapper;
        this.amqpService = amqpService;
    }

    /**
     * 插入下注记录
     *
     * @param confirmBetDto
     * @return 投注编号
     */
    @Transactional(rollbackFor = Exception.class)
    public String insertBet(ConfirmBetDto confirmBetDto) {
        Map<String, String> param = new HashMap<>();
        param.put("prefix", "AA");
        betRecordMapper.generateNo(param);
        String betNo = param.get("betNo");
        logger.debug("生成的订单号:{}", betNo);
        BetRecord betRecord = new BetRecord();
        betRecord.setBetNo(betNo);
        betRecord.setBetDetail(confirmBetDto.getBetDetail());
        betRecord.setBonus(confirmBetDto.getBonus());
        betRecord.setBetAmount(confirmBetDto.getBetAmount());
        betRecord.setBetPiece(confirmBetDto.getBetPiece());
        betRecord.setBetTimes(confirmBetDto.getBetTimes());
        betRecord.setLotteryType(confirmBetDto.getLotteryType());
        betRecord.setBetTime(new Date());
        betRecord.setPassType(confirmBetDto.getPassType());
        betRecord.setOpenId(confirmBetDto.getOpenId());
        betRecord.setBetState(BetStateEnum.PENDING_PAY.getValue());
        betRecordMapper.insertSelective(betRecord);
        logger.debug("最终的订单号:{}", betRecord.getBetNo());
        return betRecord.getBetNo();
    }

    /**
     * 订单详情
     *
     * @param betNo
     * @return
     */
    public BetDto getBetRecordDetail(String betNo, boolean isDetail) {
        if (StringUtils.isEmpty(betNo)) {
            throw new ServiceException(ErrorText.ERROR_BET_NO);
        }
        BetRecord betRecord = betRecordMapper.getByBetNo(betNo);
        if (betRecord != null) {

            BetDto betDto = new BetDto();
            betDto.setBetId(betRecord.getId());
            betDto.setBetNo(betRecord.getBetNo());
            betDto.setBetTime(DateUtils.date2String(betRecord.getBetTime(), "yyyy-MM-dd HH:mm"));
            betDto.setLotteryType(betRecord.getLotteryType());
            betDto.setLotteryTypeText(LotteryTypeEnum.getText(betRecord.getLotteryType()));
            betDto.setBetAmount(String.valueOf(betRecord.getBetAmount().longValue()));
            betDto.setWinAmount(String.valueOf(betRecord.getWinAmount()));
            betDto.setBetState(betRecord.getBetState());
            betDto.setBetStateText(BetStateEnum.getText(betRecord.getBetState()));
            betDto.setBonus(betRecord.getBonus());
            betDto.setPassType(betRecord.getPassType());
            betDto.setPiece(betRecord.getBetPiece());
            betDto.setTimes(betRecord.getBetTimes());
            User user = userMapper.getByOpenId(betRecord.getOpenId());
            betDto.setNickname(user.getNickName());

            if (isDetail) {
                // 竞彩和单关
                if (betRecord.getLotteryType().equals(LotteryTypeEnum.FOOTBALL.getValue()) ||
                        betRecord.getLotteryType().equals(LotteryTypeEnum.SINGLE.getValue())) {
                    Map<String, GameInfo> gameInfoMap = new HashMap<>();

                    String[] gameArr = StringUtils.splitPreserveAllTokens(betRecord.getBetDetail(), ",");
                    for (int i = 0; i < gameArr.length; i++) {
                        String game = gameArr[i];
                        String[] info = StringUtils.splitPreserveAllTokens(game, "-");

                        GameInfo gameInfo;
                        //已经查询过gameInfo信息直接从map取
                        if (gameInfoMap.containsKey(info[0])) {
                            for (BetDetailDto betDetailDto : betDto.getDetailList()) {
                                if (info[0].equals(betDetailDto.getGameId())) {
                                    //在已有的比赛场次上增加一种下注结果
                                    BetDetailRatioDto betDetailRatioDto = new BetDetailRatioDto();
                                    betDetailRatioDto.setBet(info[2]);
                                    betDetailRatioDto.setRangqiu(info[1]);
                                    betDetailRatioDto.setRatio(info[3]);
                                    betDetailDto.getRatioList().add(betDetailRatioDto);
                                    break;
                                }
                            }
                        } else {
                            gameInfo = gameInfoMapper.getGameByCache(info[0]);
                            gameInfoMap.put(info[0], gameInfo);

                            //新增下注比赛场次信息
                            BetDetailDto betDetailDto = new BetDetailDto();
                            betDetailDto.setGameId(info[0]);
                            betDetailDto.setGuestTeam(gameInfo.getGuestTeam());
                            betDetailDto.setHostTeam(gameInfo.getHostTeam());
                            betDetailDto.setWeekday(gameInfo.getWeekday());
                            betDetailDto.setPoint(gameInfo.getPoint());
                            betDetailDto.setRangqiu(gameInfo.getRangqiu());
                            betDetailDto.setHadResult(gameInfo.getHadResult());
                            betDetailDto.setHhadResult(gameInfo.getHhadResult());
                            //新增比赛场次的下注结果
                            BetDetailRatioDto betDetailRatioDto = new BetDetailRatioDto();
                            betDetailRatioDto.setBet(info[2]);
                            betDetailRatioDto.setRangqiu(info[1]);
                            betDetailRatioDto.setRatio(info[3]);
                            betDetailDto.getRatioList().add(betDetailRatioDto);

                            betDto.getDetailList().add(betDetailDto);
                        }

                    }
                } else if (betRecord.getLotteryType().equals(LotteryTypeEnum.FOURTEEN.getValue()) ||
                        betRecord.getLotteryType().equals(LotteryTypeEnum.NINE.getValue())) {

                    String[] info = StringUtils.splitPreserveAllTokens(betRecord.getBetDetail(), "-");
                    //期号
                    betDto.setPhaseId(info[0]);
                    //下注的结果
                    String[] betArr = StringUtils.splitPreserveAllTokens(info[1], ",");

                    List<FourteenGames> fourteenGames = fourteenGamesMapper.findListByCache(info[0]);
                    for (int i = 0; i < fourteenGames.size(); i++) {
                        FourteenGames game = fourteenGames.get(i);
                        BetDetailDto betDetailDto = new BetDetailDto();
                        betDetailDto.setSerialId(game.getSerialId());
                        betDetailDto.setHostTeam(game.getHostTeam());
                        betDetailDto.setGuestTeam(game.getGuestTeam());
                        betDetailDto.setGameId(game.getGameId());
                        betDetailDto.setPoint(game.getPoint());
                        betDetailDto.setResult(game.getResult());
                        betDetailDto.setBet(betArr[i]);
                        betDto.getDetailList().add(betDetailDto);
                    }
                }
            }
            return betDto;
        } else {
            throw new ServiceException(String.format("BetRecord not found, betNo:%s", betNo));
        }
    }

    /**
     * 根据编号
     *
     * @param betNo
     * @return
     */
    public BetRecordDto getBetRecord(String betNo) {
        BetRecord betRecord = betRecordMapper.getByBetNo(betNo);

        if (betRecord != null) {
            BetRecordDto betRecordDto = betRecord.copyTo();
            return betRecordDto;
        }
        return null;
    }

    /**
     * 清理超过20分钟未支付
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyBetStateToNotPay() {
        betRecordMapper.updateState2NotPay();
    }

    /**
     * 根据openid和状态列表查询购彩记录，
     *
     * @param page   当前页
     * @param limit  分页大小
     * @param openId 可以为null
     * @param state  可以为null
     * @return
     */
    public BetRecordPage listBetRecord(Integer page, Integer limit, String openId, List<Integer> state) {
        PageHelper.startPage(page, limit);
        List<BetRecord> betRecordList = betRecordMapper.findByOpenIdAndStates(openId, state);
        PageInfo<BetRecord> pageInfo = new PageInfo<>(betRecordList);

        BetRecordPage betRecordPage = new BetRecordPage();
        betRecordPage.setPageNum(pageInfo.getPageNum());
        betRecordPage.setPages(pageInfo.getPages());
        Map<String, Object> item;
        for (BetRecord betRecord : pageInfo.getList()) {
            BetDto betDto = getBetRecordDetail(betRecord.getBetNo(), false);
            betRecordPage.getList().add(betDto);
        }
        return betRecordPage;
    }

    public BetRecordPage listRecordAll(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<BetRecord> betRecordList = betRecordMapper.findByOpenIdAndStates(null, null);
        PageInfo<BetRecord> pageInfo = new PageInfo<>(betRecordList);

        BetRecordPage betRecordPage = new BetRecordPage();
        betRecordPage.setPageNum(pageInfo.getPageNum());
        betRecordPage.setPages(pageInfo.getPages());
        for (BetRecord betRecord : pageInfo.getList()) {
            BetDto betDto = getBetRecordDetail(betRecord.getBetNo(), false);
            betRecordPage.getList().add(betDto);
        }
        return betRecordPage;
    }

    public List<BetDto> listRecordWin(Integer count) {
        List<BetRecord> betRecordList = amqpService.receive(RabbitMQConfig.QUEUE_WIN_PRIZE, count);
        List<BetDto> list = new ArrayList<>();
        for (BetRecord betRecord : betRecordList) {
            BetDto betDto = getBetRecordDetail(betRecord.getBetNo(), true);
            list.add(betDto);
        }
        return list;
    }

    public List<BetDto> listRecordPaid(Integer count) {
        List<BetRecord> betRecordList = amqpService.receive(RabbitMQConfig.QUEUE_PENDING_TICKET, count);
        List<BetDto> list = new ArrayList<>();
        for (BetRecord betRecord : betRecordList) {
            BetDto betDto = getBetRecordDetail(betRecord.getBetNo(), true);
            list.add(betDto);
        }
        return list;
    }

    public List<NoticeDTO> listWinNotice() {
        List<BetRecord> betRecordList = betRecordMapper.findWinPrize();

        List<NoticeDTO> noticeList = new ArrayList<>();
        for (BetRecord betRecord : betRecordList) {
            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setLotteryType(LotteryTypeEnum.getText(betRecord.getLotteryType()));
            noticeDTO.setWinAmount(betRecord.getWinAmount());
            User user = userMapper.getByOpenId(betRecord.getOpenId());
            noticeDTO.setNickname(user.getNickName());
            noticeList.add(noticeDTO);
        }
        return noticeList;
    }

    /**
     * 出票处理
     *
     * @param betNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleTicket(String betNo) {
        if (StringUtils.isEmpty(betNo)) {
            throw new ServiceException(ErrorText.ERROR_BET_NO);
        }
        int count = betRecordMapper.updateState2Ticket(betNo);
        if (count != 1) {
            throw new ServiceException("修改订单状态失败");
        }
    }

    /**
     * 出票处理
     *
     * @param betNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleNoTicket(String betNo) {
        if (StringUtils.isEmpty(betNo)) {
            throw new ServiceException(ErrorText.ERROR_BET_NO);
        }
        BetRecord betRecord = betRecordMapper.getByBetNo(betNo);
        //退款到余额
        //更新余额
        UserAccount userAccount = userAccountMapper.getByOpenId(betRecord.getOpenId());

        Double balance = userAccount.getBalance();

        Double newBalance = balance + betRecord.getBetAmount();

        int count = userAccountMapper.updateBalance(betRecord.getOpenId(), newBalance);
        if (count != 1) {
            throw new ServiceException("更新账户余额失败");
        }

        //账户记录变动
        UserAccountRecord record = new UserAccountRecord();
        record.setOpenId(betRecord.getOpenId());
        record.setRecordTime(new Date());
        record.setAccountChange(AccountChangeEnum.DRAWBACK.getValue());
        record.setChangeAmount(betRecord.getBetAmount());
        record.setBalance(newBalance);
        //记录账户变动
        userAccountRecordMapper.insertSelective(record);

        count = betRecordMapper.updateState2NoTicket(betNo);
        if (count != 1) {
            throw new ServiceException("修改订单状态失败");
        }
    }

    /**
     * 兑奖处理
     *
     * @param betNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleCash(String betNo, Double amount) {
        if (StringUtils.isEmpty(betNo)) {
            throw new ServiceException(ErrorText.ERROR_BET_NO);
        }
        if (amount < 0) {
            throw new ServiceException("错误的调整金额");
        }
        BetRecord betRecord = betRecordMapper.getByBetNo(betNo);

        //更新余额
        UserAccount userAccount = userAccountMapper.getByOpenId(betRecord.getOpenId());

        Double balance = userAccount.getBalance();

        Double newBalance = balance + amount;

        int count = userAccountMapper.updateBalance(betRecord.getOpenId(), newBalance);
        if (count != 1) {
            throw new ServiceException("更新账户余额失败");
        }
        //账户记录变动
        UserAccountRecord record = new UserAccountRecord();
        record.setOpenId(betRecord.getOpenId());
        record.setRecordTime(new Date());
        record.setAccountChange(AccountChangeEnum.PRIZE.getValue());
        record.setChangeAmount(+amount);
        record.setBalance(newBalance);
        //记录账户变动
        userAccountRecordMapper.insertSelective(record);
    }
}
