package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.wisestar.lottery.entity.GameRatio;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author zhangxu
 */
public interface GameRatioMapper extends Mapper<GameRatio> {

    @Select("select * from gameRatio where gameId = #{gameId} and rangqiu = #{rangqiu}")
    GameRatio getByGameIdAndRangqiu(@Param("gameId") String gameId, @Param("rangqiu") String rangqiu);

    @Select("select * from gameRatio where gameId = #{gameId}")
    List<GameRatio> findByGameId(String gameId);
}
