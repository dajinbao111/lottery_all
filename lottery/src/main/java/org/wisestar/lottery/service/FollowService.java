package org.wisestar.lottery.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wisestar.lottery.dto.BetDetailDto;
import org.wisestar.lottery.dto.BetDetailRatioDto;
import org.wisestar.lottery.dto.BetDto;
import org.wisestar.lottery.dto.RecommendDto;
import org.wisestar.lottery.entity.FollowMark;
import org.wisestar.lottery.entity.FourteenGames;
import org.wisestar.lottery.entity.GameInfo;
import org.wisestar.lottery.entity.LotteryTypeEnum;
import org.wisestar.lottery.mapper.FollowMarkMapper;
import org.wisestar.lottery.mapper.FourteenGamesMapper;
import org.wisestar.lottery.mapper.GameInfoMapper;

import java.util.*;

/**
 * @author zhangxu
 * @date 2017/11/15
 */
@Service
public class FollowService {

    private final FollowMarkMapper followMarkMapper;
    private final GameInfoMapper gameInfoMapper;
    private final FourteenGamesMapper fourteenGamesMapper;

    @Autowired
    public FollowService(FollowMarkMapper followMarkMapper,
                         GameInfoMapper gameInfoMapper,
                         FourteenGamesMapper fourteenGamesMapper) {
        this.followMarkMapper = followMarkMapper;
        this.gameInfoMapper = gameInfoMapper;
        this.fourteenGamesMapper = fourteenGamesMapper;
    }

    public List<BetDto> getFootball(Integer[] types) {
        List<FollowMark> markList = followMarkMapper.findCurrent(Arrays.asList(types));

        List<BetDto> followList = new ArrayList<>();
        if (!markList.isEmpty()) {
            for (FollowMark follow : markList) {

                BetDto betDto = new BetDto();
                betDto.setLotteryType(follow.getLotteryType());
                betDto.setBetAmount(String.valueOf(follow.getAmount().longValue()));
                betDto.setPassType(follow.getPassType());
                betDto.setPiece(follow.getPiece());
                betDto.setTimes(follow.getTimes());

                Date earliestTime = null;
                if (follow.getLotteryType().equals(LotteryTypeEnum.FOOTBALL.getValue()) ||
                        follow.getLotteryType().equals(LotteryTypeEnum.SINGLE.getValue())) {
                    Map<String, GameInfo> gameInfoMap = new HashMap<>();
                    /**
                     * detail格式示例 99166-1-3-1.25,99188-0-1-2.75
                     * 第一位gameId,第二位让球不让球,第三位胜平负,第四位赔率
                     */
                    String[] array = StringUtils.splitPreserveAllTokens(follow.getDetail(), ",");
                    for (int i = 0; i < array.length; i++) {
                        String str = array[i];
                        String[] arr = StringUtils.splitPreserveAllTokens(str, "-");

                        GameInfo gameInfo;
                        //已经查询过gameInfo信息直接从map取
                        if (gameInfoMap.containsKey(arr[0])) {
                            for (BetDetailDto betDetailDto : betDto.getDetailList()) {
                                if (arr[0].equals(betDetailDto.getGameId())) {
                                    //在已有的比赛场次上增加一种下注结果
                                    BetDetailRatioDto betDetailRatioDto = new BetDetailRatioDto();
                                    betDetailRatioDto.setBet(arr[2]);
                                    betDetailRatioDto.setRangqiu(arr[1]);
                                    betDetailRatioDto.setRatio(arr[3]);
                                    betDetailDto.getRatioList().add(betDetailRatioDto);
                                    break;
                                }
                            }
                        } else {
                            gameInfo = gameInfoMapper.getGameByCache(arr[0]);
                            gameInfoMap.put(arr[0], gameInfo);
                            //取所有场次里开赛时间最早的时间
                            if (earliestTime == null || earliestTime.after(gameInfo.getDueTime())) {
                                earliestTime = gameInfo.getDueTime();
                            }

                            //新增下注比赛场次信息
                            BetDetailDto betDetailDto = new BetDetailDto();
                            betDetailDto.setGameId(arr[0]);
                            betDetailDto.setGuestTeam(gameInfo.getGuestTeam());
                            betDetailDto.setHostTeam(gameInfo.getHostTeam());
                            betDetailDto.setWeekday(gameInfo.getWeekday());
                            betDetailDto.setRangqiu(gameInfo.getRangqiu());
                            //新增比赛场次的下注结果
                            BetDetailRatioDto betDetailRatioDto = new BetDetailRatioDto();
                            betDetailRatioDto.setBet(arr[2]);
                            betDetailRatioDto.setRangqiu(arr[1]);
                            betDetailRatioDto.setRatio(arr[3]);
                            betDetailDto.getRatioList().add(betDetailRatioDto);

                            betDto.getDetailList().add(betDetailDto);
                        }
                    }
                } else if (follow.getLotteryType().equals(LotteryTypeEnum.FOURTEEN.getValue()) ||
                        follow.getLotteryType().equals(LotteryTypeEnum.NINE.getValue())) {
                    String[] info = StringUtils.splitPreserveAllTokens(follow.getDetail(), "-");
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

                        earliestTime = game.getEndPostTime();
                    }
                }

                betDto.setDutTime(earliestTime);
                followList.add(betDto);
            }
        }
        return followList;
    }


    public void insertFollow(RecommendDto recommendDto) {
        FollowMark followMark = new FollowMark();
        followMark.setAmount(recommendDto.getBetAmount());
        followMark.setCreatedDate(new Date());
        followMark.setDetail(recommendDto.getBetDetail());
        followMark.setLotteryType(recommendDto.getLotteryType());
        followMark.setPassType(recommendDto.getPassType());
        followMark.setPiece(recommendDto.getBetPiece());
        followMark.setTimes(recommendDto.getBetTimes());
        followMarkMapper.insertSelective(followMark);
    }
}
