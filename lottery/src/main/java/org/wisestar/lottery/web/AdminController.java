package org.wisestar.lottery.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wisestar.lottery.dto.AccountRecordPage;
import org.wisestar.lottery.dto.BetDto;
import org.wisestar.lottery.dto.BetRecordPage;
import org.wisestar.lottery.dto.RecommendDto;
import org.wisestar.lottery.exception.ErrorText;
import org.wisestar.lottery.exception.ServiceException;
import org.wisestar.lottery.service.AmqpService;
import org.wisestar.lottery.service.BetService;
import org.wisestar.lottery.service.FollowService;
import org.wisestar.lottery.service.UserService;
import org.wisestar.lottery.util.ValidatorUtils;
import org.wisestar.lottery.util.QiniuStorage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/10/30
 */
@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    private final BetService betService;
    private final AmqpService amqpService;
    private final FollowService followService;
    private final UserService userService;
    private final QiniuStorage qiniuStorage;

    @Autowired
    public AdminController(BetService betService,
                           AmqpService amqpService,
                           FollowService followService,
                           UserService userService,
                           QiniuStorage qiniuStorage) {
        this.betService = betService;
        this.amqpService = amqpService;
        this.followService = followService;
        this.userService = userService;
        this.qiniuStorage = qiniuStorage;
    }

    /**
     * 队列大小
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('tic','cash')")
    @PostMapping("/listQueueSize")
    public ResponseEntity<?> listQueueSize() {
        Map<String, Long> queueSizeMap = amqpService.size();
        return ResponseEntity.ok(queueSizeMap);
    }

    /**
     * 待出票
     *
     * @return
     */
    @PreAuthorize("hasAuthority('tic')")
    @PostMapping("/listRecordPaid/{count}")
    public ResponseEntity<?> listRecordPaid(@PathVariable Integer count) {
        List<BetDto> list = betService.listRecordPaid(count);
        return ResponseEntity.ok(list);
    }

    /**
     * 出票操作
     */
    @PreAuthorize("hasAuthority('tic')")
    @PostMapping("/handleTicket/{betNo}")
    public ResponseEntity<?> handleTicket(@PathVariable String betNo, @RequestParam("file") MultipartFile file) {
        String path = "";
        if (file != null) {
            try {
                path = qiniuStorage.upload(file.getInputStream(), betNo);
            } catch (IOException e) {
                throw new ServiceException("图片上传失败");
            }
        }
        betService.handleTicket(betNo);
        return ResponseEntity.ok(path);
    }

    /**
     * 出票操作
     */
    @PreAuthorize("hasAuthority('tic')")
    @PostMapping("/handleNoTicket/{betNo}")
    public ResponseEntity<?> handleNoTicket(@PathVariable String betNo) {
        betService.handleNoTicket(betNo);
        return ResponseEntity.ok("");
    }

    /**
     * 待兑奖
     *
     * @return
     */
    @PreAuthorize("hasAuthority('cash')")
    @PostMapping("/listRecordWin/{count}")
    public ResponseEntity<?> listRecordWin(@PathVariable Integer count) {
        List<BetDto> list = betService.listRecordWin(count);
        return ResponseEntity.ok(list);
    }

    /**
     * 兑奖操作
     */
    @PreAuthorize("hasAuthority('cash')")
    @PostMapping("/handleCash/{betNo}")
    public ResponseEntity<?> handleCash(@PathVariable String betNo, @RequestBody Double amount) {
        betService.handleCash(betNo, amount);
        return ResponseEntity.ok("");
    }


    /**
     * 全部记录
     *
     * @param page
     * @return
     */
    @PreAuthorize("hasAuthority('view')")
    @PostMapping("/listRecordAll/{limit}/{page}")
    public ResponseEntity<?> listRecordAll(@PathVariable Integer page, @PathVariable Integer limit) {
        BetRecordPage betRecordPage = betService.listRecordAll(page, limit);
        return ResponseEntity.ok(betRecordPage);
    }

    /**
     * 提现列表
     *
     * @param page
     * @param limit
     * @return
     */
    @PreAuthorize("hasAuthority('with')")
    @PostMapping("/listWithdraw/{limit}/{page}")
    public ResponseEntity<?> listWithdraw(@PathVariable Integer page, @PathVariable Integer limit) {
        AccountRecordPage accountRecordPage = userService.listWithdraw(page, limit);
        return ResponseEntity.ok(accountRecordPage);
    }

    /**
     * 提现处理
     *
     * @param recordId
     * @return
     */
    @PreAuthorize("hasAuthority('with')")
    @PostMapping("/handleWithdraw/{recordId}")
    public ResponseEntity<?> handleWithdraw(@PathVariable Long recordId) {
        userService.handleWithdraw(recordId);
        return ResponseEntity.ok("");
    }


    /**
     * 订单记录详情
     *
     * @param betNo
     * @return
     */
    @PreAuthorize("hasAuthority('view')")
    @PostMapping("/viewRecord/{betNo}")
    public ResponseEntity<?> viewRecord(@PathVariable String betNo) {
        BetDto betDto = betService.getBetRecordDetail(betNo, true);
        return ResponseEntity.ok(betDto);
    }


    /**
     * 推荐
     *
     * @param recommendDto
     * @return
     */
    @PreAuthorize("hasAuthority('rec')")
    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@RequestBody RecommendDto recommendDto) {
        ValidatorUtils.validateEntity(recommendDto);
        //2*注数*倍数=下注金额
        Double calAmount = 2.0 * recommendDto.getBetPiece() * recommendDto.getBetTimes();
        if (!calAmount.equals(recommendDto.getBetAmount())) {
            throw new ServiceException(ErrorText.ERROR_BET_AMOUNT);
        }
        followService.insertFollow(recommendDto);
        return ResponseEntity.ok("");
    }
}