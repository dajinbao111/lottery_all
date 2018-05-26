package org.wisestar.lottery.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.Cacheable;
import org.wisestar.lottery.entity.GameInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @author zhangxu
 */
public interface GameInfoMapper extends Mapper<GameInfo> {

    @Select("select * from gameInfo where gameId = #{gameId} limit 1")
    GameInfo getByGameId(String gameId);

    @Cacheable(key = "#p0", value = "gameInfo")
    @Select("select * from gameInfo where gameId = #{gameId} limit 1")
    GameInfo getGameByCache(String gameId);

    @Cacheable(key = "#p0", value = "gameInfoWithResult")
    @Select("select * from gameInfo where gameId = #{gameId} and point is not null limit 1")
    GameInfo getResultByCache(String gameId);

    @Select("select max(lastUpdated) from gameInfo")
    Date getLastUpdated();

    List<GameInfo> findByLastUpdated(Date lastUpdated);

    List<GameInfo> findPointIsNull();
}
