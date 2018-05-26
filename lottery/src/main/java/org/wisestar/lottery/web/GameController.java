package org.wisestar.lottery.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wisestar.lottery.dto.FourteenGameDto;
import org.wisestar.lottery.dto.GameRatioDto;
import org.wisestar.lottery.dto.GameSingleInfoDto;
import org.wisestar.lottery.service.FootballService;

import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/10/26
 */
@RestController
@RequestMapping("/game")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final FootballService footballService;

    @Autowired
    public GameController(FootballService footballService) {
        this.footballService = footballService;
    }

    /**
     * 竞彩数据
     *
     * @return
     */
    @PostMapping("/getGameInfo")
    public ResponseEntity<?> getGameInfo() {
        Map<String, GameRatioDto> map = footballService.getGameInfo();
        return ResponseEntity.ok(map);
    }

    /**
     * 单关数据
     *
     * @return
     */
    @PostMapping("/getGameSingleInfo")
    public ResponseEntity<?> getGameSingleInfo() {
        Map<String, GameSingleInfoDto> map = footballService.getGameSingleInfo();
        return ResponseEntity.ok(map);
    }

    /**
     * 14场或任9数据
     *
     * @return
     */
    @PostMapping("/getGameFouteen")
    public ResponseEntity<?> getGameFouteen() {
        Map<String, Map<Integer, FourteenGameDto>> map = footballService.getFourteenGames();
        return ResponseEntity.ok(map);
    }

}
