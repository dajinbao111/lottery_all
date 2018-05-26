package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Select;
import org.wisestar.lottery.entity.GameSingleRatio;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author zhangxu
 */
public interface GameSingleRatioMapper extends Mapper<GameSingleRatio> {

    @Select("select * from gameSingleRatio where gameId = #{gameId}")
    GameSingleRatio getByGameId(String gameId);

    @Select("select * from gameSingleRatio where lastUpdated = #{lastUpdated}")
    List<GameSingleRatio> getByLastUpdated(Date lastUpdated);
}
