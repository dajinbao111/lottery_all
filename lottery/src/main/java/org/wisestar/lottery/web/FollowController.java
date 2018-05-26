package org.wisestar.lottery.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wisestar.lottery.dto.BetDto;
import org.wisestar.lottery.entity.LotteryTypeEnum;
import org.wisestar.lottery.service.FollowService;

import java.util.List;

/**
 * @author zhangxu
 * @date 2017/11/15
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    /**
     * 获取竞足数据
     */
    @PostMapping("/football")
    public ResponseEntity<?> football() {
        Integer[] types = {LotteryTypeEnum.FOOTBALL.getValue(), LotteryTypeEnum.SINGLE.getValue()};
        List<BetDto> followList =  followService.getFootball(types);
        return ResponseEntity.ok(followList);
    }

    /**
     * 获取14场数据
     */
    @PostMapping("/fourteen")
    public ResponseEntity<?> fourteen() {
        Integer[] types = {LotteryTypeEnum.FOURTEEN.getValue()};
        List<BetDto> followList =  followService.getFootball(types);
        return ResponseEntity.ok(followList);
    }

    /**
     * 获取任9数据
     */
    @PostMapping("/nine")
    public ResponseEntity<?> nine() {
        Integer[] types = {LotteryTypeEnum.NINE.getValue()};
        List<BetDto> followList =  followService.getFootball(types);
        return ResponseEntity.ok(followList);
    }
}
