package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.Cacheable;
import org.wisestar.lottery.entity.BetRecord;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author zhangxu
 * @date 2017/11/2
 */
public interface BetRecordMapper extends Mapper<BetRecord> {

    void generateNo(Map<String, String> paramMap);

    @Select("select * from betRecord where betNo = #{betNo}")
    BetRecord getByBetNo(String betNo);

    @Cacheable(key = "#p0", value = "betRecord")
    @Select("select * from betRecord where betNo = #{betNo}")
    BetRecord getByCache(String betNo);

    @Update("update betRecord set betState = #{betState} where betNo = #{betNo}")
    int updateBetState(@Param("betNo") String betNo,
                       @Param("betState") Integer betState);

    List<BetRecord> findByOpenIdAndStates(@Param("openId") String openId,
                                          @Param("states") List<Integer> states);

    /**
     * 更新状态为付款不成功
     */
    int updateState2NotPay();

    /**
     * 更新状态为中奖
     */
    int updateState2WinPrize(@Param("betNo") String betNo,
                             @Param("winAmount") Double winAmount);

    /**
     * 更新状态为未中奖
     */
    int updateState2NotWin(String betNo);

    /**
     * 更新为已出票
     * @param betNo
     * @return
     */
    int updateState2Ticket(String betNo);

    /**
     * 出票不成功
     * @param betNo
     * @return
     */
    int updateState2NoTicket(String betNo);

    /**
     * 当天中奖订单
     *
     * @return
     */
    List<BetRecord> findWinPrize();

    List<BetRecord> findByStateAndType(@Param("state") Integer state,
                                       @Param("types") List<Integer> types);
}
