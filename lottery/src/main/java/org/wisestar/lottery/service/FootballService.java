package org.wisestar.lottery.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wisestar.lottery.dto.FourteenGameDto;
import org.wisestar.lottery.dto.GameRatioDto;
import org.wisestar.lottery.dto.GameSingleInfoDto;
import org.wisestar.lottery.entity.*;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.mapper.*;
import org.wisestar.lottery.util.BeanUtils;
import org.wisestar.lottery.util.DateUtils;
import org.wisestar.lottery.util.GroovyEngine;
import org.wisestar.lottery.util.MailReporter;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangxu
 */
@Service
public class FootballService {
    private static final Logger logger = LoggerFactory.getLogger(FootballService.class);
    private GroovyEngine engine = GroovyEngine.getInstance();
    private final GameInfoMapper gameInfoMapper;
    private final GameRatioMapper gameRatioMapper;
    private final GameSingleRatioMapper gameSingleRatioMapper;
    private final FourteenGamesMapper fourteenGamesMapper;
    private final FourteenGamesResultMapper fourteenGamesResultMapper;
    private final MailReporter mailReporter;
    private final BetRecordMapper betRecordMapper;
    private final AmqpService amqpService;

    private ExecutorService executor = new ThreadPoolExecutor(5, 20, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("football-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    public FootballService(GameInfoMapper gameInfoMapper,
                           GameRatioMapper gameRatioMapper,
                           GameSingleRatioMapper gameSingleRatioMapper,
                           FourteenGamesMapper fourteenGamesMapper,
                           FourteenGamesResultMapper fourteenGamesResultMapper,
                           MailReporter mailReporter,
                           BetRecordMapper betRecordMapper,
                           AmqpService amqpService) {
        this.gameInfoMapper = gameInfoMapper;
        this.gameRatioMapper = gameRatioMapper;
        this.gameSingleRatioMapper = gameSingleRatioMapper;
        this.fourteenGamesMapper = fourteenGamesMapper;
        this.fourteenGamesResultMapper = fourteenGamesResultMapper;
        this.mailReporter = mailReporter;
        this.betRecordMapper = betRecordMapper;
        this.amqpService = amqpService;
    }

    /**
     * 爬取14场期号
     *
     * @return
     */
    public List<String> crawlPhaseIds() {
        List<String> ids = new ArrayList<>();
        redexPhaseId(ids, "");
        return ids;
    }

    /**
     * 递归当前可用期号
     *
     * @param ids
     * @param num
     */
    private void redexPhaseId(List<String> ids, String num) {
        try {
            String result = engine.executeScript("game_fourteen", "get_num", num);
            JSONObject rs = JSON.parseObject(result);
            String phaseId = rs.getString("num");
            String format = rs.getString("format");
            String start = rs.getString("start");
            Date startDate = DateUtils.string2Date(start, format);
            String prize = rs.getString("prize");
            Date prizeTime = DateUtils.string2Date(prize, format);
            String nextPhaseId = rs.getString("next");

            //14场开奖结果预存
            FourteenGamesResult found = fourteenGamesResultMapper.getByPhaseId(phaseId);
            if (found == null) {
                FourteenGamesResult fourteenGamesResult = new FourteenGamesResult();
                fourteenGamesResult.setPhaseId(phaseId);
                fourteenGamesResult.setPrizeTime(prizeTime);
                fourteenGamesResultMapper.insertSelective(fourteenGamesResult);
            }

            //未开售
            if (startDate.getTime() > System.currentTimeMillis()) {
                return;
            } else {
                ids.add(phaseId);
                redexPhaseId(ids, nextPhaseId);
            }
        } catch (Exception e) {
            mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 爬取14场数据
     *
     * @param list
     */
    public void crawlFourteenGames(List<String> list) {
        try {
            for (String num : list) {
                String result = engine.executeScript("game_fourteen", "get_data", num);
                JSONArray array = JSON.parseArray(result);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject item = array.getJSONObject(i);

                    //抓取回来的日期格式做转换
                    String format = item.getString("format");
                    String startPostTime = item.getString("startPostTime");
                    item.put("startPostTime", DateUtils.string2Date(startPostTime, format));
                    String endPostTime = item.getString("endPostTime");
                    item.put("endPostTime", DateUtils.string2Date(endPostTime, format));

                    FourteenGames fourteenGames = item.toJavaObject(FourteenGames.class);
                    //新增或更新fourteenGames
                    FourteenGames found = fourteenGamesMapper.getByGameId(fourteenGames.getGameId());
                    if (found == null) {
                        fourteenGamesMapper.insertSelective(fourteenGames);
                    } else {
                        Example example = new Example(FourteenGames.class);
                        example.createCriteria().andEqualTo("gameId", fourteenGames.getGameId());
                        fourteenGamesMapper.updateByExampleSelective(fourteenGames, example);
                    }
                }
            }
        } catch (Exception e) {
            mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 爬取14场结果
     */
    public void crawlFourteenGamesResult() {
        List<FourteenGamesResult> list = fourteenGamesResultMapper.findResultIsNull();

        if (!list.isEmpty()) {
            try {
                for (FourteenGamesResult fourteenGamesResult : list) {

                    String result = engine.executeScript("game_fourteen", "get_result", fourteenGamesResult.getPhaseId());

                    JSONObject rs = JSON.parseObject(result);
                    String phaseId = rs.getString("phaseId");
                    String right = rs.getString("result");
                    if (!fourteenGamesResult.getPhaseId().equals(phaseId)) {
                        continue;
                    }
                    if (StringUtils.isEmpty(right)) {
                        continue;
                    }
                    if (right.length() < 27) {
                        throw new ServiceException("fb_lottery_match result.length less 27");
                    }
                    String prize1 = rs.getString("prize1");
                    String prize2 = rs.getString("prize2");
                    String ren9 = rs.getString("ren9");
                    if (StringUtils.isEmpty(prize1) || StringUtils.isEmpty(prize2) || StringUtils.isEmpty(ren9)) {
                        continue;
                    }
                    fourteenGamesResult.setResult(right);
                    fourteenGamesResult.setPrize1(prize1);
                    fourteenGamesResult.setPrize2(prize2);
                    fourteenGamesResult.setRen9(ren9);
                    fourteenGamesResultMapper.updateByPrimaryKeySelective(fourteenGamesResult);
                }

                Integer[] types = {LotteryTypeEnum.FOURTEEN.getValue(), LotteryTypeEnum.NINE.getValue()};
                processLottery(types);
            } catch (Exception e) {
                mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
            }
        }
    }

    /**
     * 获取14场数据
     *
     * @return
     */
    public Map<String, Map<Integer, FourteenGameDto>> getFourteenGames() {
        List<String> phaseIds = fourteenGamesMapper.findCurrentPhaseId();

        //期号为key，14场数据为value
        Map<String, Map<Integer, FourteenGameDto>> group = new HashMap<>(phaseIds.size());

        for (String phaseId : phaseIds) {

            Map<Integer, FourteenGameDto> map = new HashMap<>(phaseIds.size());
            List<FourteenGames> fourteenGamesList = fourteenGamesMapper.findByPhaseId(phaseId);
            for (FourteenGames fourteenGames : fourteenGamesList) {

                FourteenGameDto fourteenGameDto = fourteenGames.copyTo();
                map.put(fourteenGameDto.getSerialId(), fourteenGameDto);
            }

            group.put(phaseId, map);
        }

        return group;
    }

    /**
     * 抓取竞彩数据刷新时间
     *
     * @return
     */
    public Date crawlLastUpdated() {
        try {
            String result = engine.executeScript("game_football", "get_updated");
            JSONObject rs = JSON.parseObject(result);
            String format = rs.getString("format");
            String lastUpdated = rs.getString("lastUpdated");
            return DateUtils.string2Date(lastUpdated, format);
        } catch (Exception e) {
            mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
        }
        return new Date();
    }

    /**
     * 抓取竞彩数据
     */
    public void crawlGameInfo() {
        Date dbLastUpdated = gameInfoMapper.getLastUpdated();
        //页面数据未更新，直接退出
        if (dbLastUpdated != null && !crawlLastUpdated().after(dbLastUpdated)) {
            return;
        }

        try {
            String result = engine.executeScript("game_football", "get_data");
            JSONArray array = JSON.parseArray(result);
            for (int i = 0; i < array.size(); i++) {
                JSONObject info = array.getJSONObject(i);
                GameInfo gameInfo = info.toJavaObject(GameInfo.class);
                saveOrUpdate(gameInfo);

                JSONArray ratios = info.getJSONArray("ratio");
                for (int j = 0; j < ratios.size(); j++) {
                    JSONObject ratio = ratios.getJSONObject(j);

                    GameRatio gameRatio = ratio.toJavaObject(GameRatio.class);
                    saveOrUpdate(gameRatio);

                    if (ratio.containsKey("single")) {
                        JSONObject single = ratio.getJSONObject("single");
                        GameSingleRatio gameSingleRatio = single.toJavaObject(GameSingleRatio.class);
                        saveOrUpdate(gameSingleRatio);
                    }

                }

            }

        } catch (Exception e) {
            mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
        }
    }

    private void saveOrUpdate(GameInfo gameInfo) {
        GameInfo found = gameInfoMapper.getByGameId(gameInfo.getGameId());
        if (found == null) {
            gameInfoMapper.insertSelective(gameInfo);
        } else {
            Example example = new Example(GameInfo.class);
            example.createCriteria().andEqualTo("gameId", gameInfo.getGameId());
            gameInfoMapper.updateByExampleSelective(gameInfo, example);
        }
    }

    private void saveOrUpdate(GameRatio gameRatio) {
        GameRatio found = gameRatioMapper.getByGameIdAndRangqiu(gameRatio.getGameId(), gameRatio.getRangqiu());
        if (found == null) {
            gameRatioMapper.insertSelective(gameRatio);
        } else {
            Example example = new Example(GameRatio.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("gameId", gameRatio.getGameId());
            criteria.andEqualTo("rangqiu", gameRatio.getRangqiu());
            gameRatioMapper.updateByExampleSelective(gameRatio, example);
        }
    }

    private void saveOrUpdate(GameSingleRatio gameSingleRatio) {
        GameSingleRatio found = gameSingleRatioMapper.getByGameId(gameSingleRatio.getGameId());
        if (found == null) {
            gameSingleRatioMapper.insertSelective(gameSingleRatio);
        } else {
            Example example = new Example(GameSingleRatio.class);
            example.createCriteria().andEqualTo("gameId", gameSingleRatio.getGameId());
            gameSingleRatioMapper.updateByExampleSelective(gameSingleRatio, example);
        }
    }

    /**
     * 抓取竞彩比赛结果
     */
    public void crawlGameInfoResult() {
        List<GameInfo> list = gameInfoMapper.findPointIsNull();

        if (!list.isEmpty()) {
            try {
                for (GameInfo gameInfo : list) {
                    String result = engine.executeScript("game_football", "get_rs", gameInfo.getGameId());

                    JSONObject rs = JSON.parseObject(result);
                    if (!rs.isEmpty()) {
                        gameInfo.setPoint(rs.getString("point"));
                        gameInfo.setRangqiu(rs.getString("rangqiu"));
                        gameInfo.setHadResult(rs.getString("hadResult"));
                        gameInfo.setHhadResult(rs.getString("hhadResult"));

                        updateGameResult(gameInfo);
                    }
                }
                Integer[] types = {LotteryTypeEnum.FOOTBALL.getValue(), LotteryTypeEnum.SINGLE.getValue()};
                processLottery(types);
            } catch (Exception e) {
                mailReporter.sendMail(e.getMessage(), Throwables.getStackTraceAsString(e));
            }
        }
    }

    @CachePut(value = "gameInfoWithResult", key = "#gameInfo.gameId")
    public void updateGameResult(GameInfo gameInfo) {
        gameInfoMapper.updateByPrimaryKeySelective(gameInfo);
    }

    /**
     * 多线程处理足彩中奖情况
     *
     * @param types
     */
    @Transactional(rollbackFor = Exception.class)
    public void processLottery(Integer[] types) {
        List<BetRecord> betRecordList = betRecordMapper.findByStateAndType(
                BetStateEnum.PENDING_OPEN.getValue(), Arrays.asList(types));
        for (BetRecord betRecord : betRecordList) {
            Runnable worker = new ProcessLotteryThread(
                    gameInfoMapper,
                    fourteenGamesResultMapper,
                    betRecordMapper,
                    amqpService,
                    betRecord);
            if (!executor.isShutdown()) {
                executor.execute(worker);
            }
        }
    }

    /**
     * 获取竞足数据
     *
     * @return
     */
    public Map<String, GameRatioDto> getGameInfo() {
        Date lastUpdated = gameInfoMapper.getLastUpdated();
        List<GameInfo> infoList = gameInfoMapper.findByLastUpdated(lastUpdated);

        Map<String, GameRatioDto> gameMap = new LinkedHashMap<>();
        if (!infoList.isEmpty()) {

            //按weekday排序
            Collections.sort(infoList);

            for (GameInfo gameInfo : infoList) {

                List<GameRatio> ratioList = gameRatioMapper.findByGameId(gameInfo.getGameId());

                GameRatioDto gameRatioDto = new GameRatioDto();
                gameRatioDto.setGameId(gameInfo.getGameId());
                /**
                 * todo
                 * 开赛时间大于24点,截止时间为23点50
                 */
                gameRatioDto.setDueTime(gameInfo.getDueTime());
                gameRatioDto.setGameDate(gameInfo.getGameDate());
                gameRatioDto.setGameEventType(gameInfo.getGameEventType());
                gameRatioDto.setGuestTeam(gameInfo.getGuestTeam());
                gameRatioDto.setHostTeam(gameInfo.getHostTeam());
                gameRatioDto.setWeekday(gameInfo.getWeekday());
                gameRatioDto.setLastUpdated(gameInfo.getLastUpdated());

                for (GameRatio gameRatio : ratioList) {
                    if ("0".equals(gameRatio.getRangqiu())) {
                        //不让球的胜平负率
                        gameRatioDto.setHadRangqiu(gameRatio.getRangqiu());
                        gameRatioDto.setHadDrawRatio(gameRatio.getDrawRatio());
                        gameRatioDto.setHadLoseRatio(gameRatio.getLoseRatio());
                        gameRatioDto.setHadWinRatio(gameRatio.getWinRatio());
                    } else {
                        //让球的胜平负率
                        gameRatioDto.setHhadRangqiu(gameRatio.getRangqiu());
                        gameRatioDto.setHhadDrawRatio(gameRatio.getDrawRatio());
                        gameRatioDto.setHhadLoseRatio(gameRatio.getLoseRatio());
                        gameRatioDto.setHhadWinRatio(gameRatio.getWinRatio());
                    }
                }
                gameMap.put(gameInfo.getWeekday(), gameRatioDto);
            }

        }
        return gameMap;
    }

    /**
     * 获取竞足单关数据
     */
    public Map<String, GameSingleInfoDto> getGameSingleInfo() {
        Date lastUpdated = gameInfoMapper.getLastUpdated();
        List<GameSingleRatio> singleRatioList = gameSingleRatioMapper.getByLastUpdated(lastUpdated);

        Map<String, GameSingleInfoDto> gameMap = new LinkedHashMap<>();
        if (!singleRatioList.isEmpty()) {
            List<GameSingleInfoDto> list = new ArrayList<>();

            for (GameSingleRatio gameSingleRatio : singleRatioList) {

                GameInfo gameInfo = gameInfoMapper.getByGameId(gameSingleRatio.getGameId());

                GameSingleInfoDto gameSingleInfoDto = new GameSingleInfoDto();

                BeanUtils.copyProperties(gameSingleRatio, gameSingleInfoDto);
                BeanUtils.copyProperties(gameInfo, gameSingleInfoDto);

                list.add(gameSingleInfoDto);
            }

            //按weekday排序
            Collections.sort(list);

            for (GameSingleInfoDto gameSingleInfoDto : list) {
                gameMap.put(gameSingleInfoDto.getWeekday(), gameSingleInfoDto);
            }
        }

        return gameMap;
    }
}
